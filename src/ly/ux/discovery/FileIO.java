package ly.ux.discovery;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;


public class FileIO {

	private String m_lineString; //In function, String is passed by value, not pass by reference
	private static Collection <String> stopwords;
	private static String taggerpath = "lib/left3words-wsj-0-18.tagger";

	private String contentSection = "Title:@BACKGROUND@FIELD@SUMMARY@";

	public void setContentSection(String contentSection) {
		this.contentSection = contentSection;
	}

	public String TreeSettoString(TreeSet<String> tree){
		String text = "";
		Iterator<String> itr = tree.iterator();
		while(itr.hasNext()){
			String tmp = itr.next();
			text = text + tmp + "\r\n";
		}
		return text;
	}

	public void InitialStopwordList(String docStopword, int stemFlag)
	{//store the stopwords in the Collection
		stopwords = new TreeSet <String> ();
		String stopw = readContent(docStopword);
		String[] term = stopw.split("\r\n");
		for (int kk = 0; kk < term.length; kk++){
			String t = term[kk].toString().trim();
			if(t.length() > 0){
				t = stemWord(t, stemFlag);
				stopwords.add(t);
			}
		}

		//		try {
		//			File file = new File(docStopword);
		//			FileInputStream fis = new FileInputStream(file);
		//			DataInputStream dis = new DataInputStream(fis);
		//			BufferedReader br = new BufferedReader(new InputStreamReader(dis));
		//			String lineString = null;
		//
		//			// Read each line from the document
		//			while ((lineString = br.readLine()) != null) {
		//				lineString = lineString.toLowerCase();
		//				StringTokenizer tk = new StringTokenizer(lineString);
		//				String a = null;
		//				while(tk.hasMoreTokens())
		//				{
		//					a = tk.nextToken();
		//					a = a.trim();
		//					stopwords.add(a);
		//				}
		//			}	
		//			br.close();
		//			dis.close();
		//			fis.close();
		//		}			
		//		catch(Exception e) {
		//			e.printStackTrace();
		//		}
	}

	public String getSection(String docPath, String patentNum, String outputFolder){

		//get the contents needed to be processed 
		//put it in "sentenceList" orderly and count the number of sentences
		//the number of sentences
		//new matrix sim
		String contents = "";

		//String savePath = outputFolder + "/-I-" + patentNum + ".txt";
		String saveSentence = outputFolder + "-Sentence-" + patentNum + ".txt";
		String saveMFS = outputFolder + "-termlist-" + patentNum + ".txt";;
		//String patentNum = ListOfFiles[i].substring(0, ListOfFiles[i].length()-4);

		contents = readContent(docPath);
		ContentSplit splitContent = new ContentSplit();

		setContentSection("Title:@Claims:@");//for claim section

		//		setContentSection("BACKGROUND@FIELD@SUMMARY@TECHNICAL@"); // for issue section

		splitContent.itemListCreator( contentSection );
		////System.out.println("Content: " + content);

		//the specified sections
		String refBlockTxt = splitContent.contentSplitandSave(contents, "", patentNum)+ "\r\n";

		String firstLine = contents.substring(0, contents.indexOf("-"));
		refBlockTxt = "Title.\r\n" + firstLine + ".\r\n" + refBlockTxt;
		String conSentence = toSentence(refBlockTxt, "");

		WriteContent(contents, saveSentence);
		return saveSentence;//return the path of the save file

	}
	
	
	public String toSentence(String str, String savePath){
		//process the "str" to sentences and remove the duplicate. 

		Set<String> hashSet = new HashSet<String>();
		String content = ""; //each line each sentence

		try {
			MaxentTagger tagger = new MaxentTagger(taggerpath);
			//release the space.!!!!!!!!!!!!!!!!!!!1
			List<Sentence<? extends HasWord>> sentences = MaxentTagger
					.tokenizeText(new StringReader(str));
			List<Sentence<TaggedWord>> taggedSentences = new ArrayList<Sentence<TaggedWord>>();
			for (int k = 0; k < sentences.size(); k++) {
				Sentence<? extends HasWord> sentence = sentences.get(k);
				////System.out.println(sentence.toString(true));
				String sent = sentence.toString(true);
				sent = sent.replaceAll("-LRB-", " ");
				sent = sent.replaceAll("-RRB-", " ");

				//2010.11.17 check whether sent is a sentence 
				if(!hashSet.contains(sent) && isSentence(sent)){
					if(sent.trim().length()> 15 ){
						content = content + sent + "\r\n";
						hashSet.add(sent);
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		if(savePath.length()>0)
			WriteContent(content, savePath);
		return content; 
	}


	public Set<String> getWordSet(String fileName) {//
		Set<String> hashSet = new HashSet<String>();
		try {
			String stopw = readContent(fileName);
			String[] term = stopw.split("\r\n");
			for (int kk = 0; kk < term.length; kk++){
				String t = term[kk].toString().trim();
				hashSet.add(t);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hashSet;
	}

	/**
	 * read the body of a patent (only get parts afert "Claims:" sections)
	 * @param DocPath
	 * @return
	 */
	public String readBody(String DocPath) {
		String content = "";
		try {
			File file = new File(DocPath);
			if(file.exists()){
				FileInputStream fis = new FileInputStream(file);
				DataInputStream dis = new DataInputStream(fis);
				BufferedReader br = new BufferedReader(new InputStreamReader(dis));
				String lineString = null;

				int flag = 0;
				// Read each line from the document
				while ((lineString = br.readLine()) != null) {
					//					System.out.println(lineString);
					if(flag == 0){
						if(lineString.trim().indexOf("Claims:") >= 0){
							flag = 1;
						}
					}
					if(flag > 0 )
						content = content + lineString + "\r\n"; 
				}
				br.close();
				dis.close();
				fis.close();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}		
		return content;
	}

	public String readContent(String DocPath)
	{
		String content = "";
		try {
			File file = new File(DocPath);
			if(file.exists()){
				FileInputStream fis = new FileInputStream(file);
				DataInputStream dis = new DataInputStream(fis);
				BufferedReader br = new BufferedReader(new InputStreamReader(dis));
				String lineString = null;

				// Read each line from the document
				while ((lineString = br.readLine()) != null) {
					content = content + lineString + "\r\n"; 
				}
				br.close();
				dis.close();
				fis.close();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}		
		return content;
	}


	public String outputArrayList(ArrayList<String> list){
		String str = "";
		if(list.size()>0){
			for(int i = 0; i < list.size(); i++ ){
				String ei = list.get(i);
				str = str + ei + "\r\n";
			}
			//			System.out.println(str);
		}
		return str;
	}


	public boolean isStopword(String str)
	{//true for stopword

		boolean flag = false;
		if(stopwords.size() > 0){
			Iterator <String> itr = stopwords.iterator();

			while(itr.hasNext()){
				String stop = (String)itr.next();
				if(stop.equals(str)){ 
					flag = true;
					break;
				}
			}
		}
		// check whether the term contains number
		Pattern   r   =   Pattern.compile("[0-9]+"); 
		Matcher   m3   =   r.matcher(str);
		if(m3.find()) {
			flag = true;
		}

		// check whether contains '.', '-'
		Pattern   s   =   Pattern.compile("\\p{Punct}+");   
		Matcher   m4   =   s.matcher(str);
		if(m4.find(0)) {
			flag = true;
		}

		return flag;
	}


	public String removeESorS(String strCont)
	{
		String content = "";
		Hashtable<String, Integer> ht = new Hashtable<String, Integer>();
		ArrayList<String> al = new ArrayList<String>();

		String[] linesOfContents = strCont.split("\r\n");
		for (int kk = 0; kk<linesOfContents.length; kk++){
			String str = linesOfContents[kk].toString();
			//str = stemSentence(str);
			StringTokenizer st = new StringTokenizer(str);

			while (st.hasMoreTokens()) {
				String tmp = st.nextToken().toLowerCase().trim();

				//is tk a stop word? remove the stopword
				boolean flag = isStopword(tmp);
				if(!flag)
				{
					content = content + tmp + " ";
					if(!ht.containsKey(tmp))
					{
						al.add(tmp);
						ht.put(tmp, 0);
					}
				}
			}
			content = content + "\r\n";
		}
		// Get alphabetic terms with lower case

		//remove es and s
		for (int i=0; i<al.size(); i++) {
			String term = al.get(i);
			Pattern p = Pattern.compile("[a-zA-Z]+");
			Matcher m = p.matcher(term);
			boolean b = m.matches();

			if(!b)
				continue;

			// the plural candidate
			if(term.substring(term.length()-1).trim().equals("s"))
			{
				String candTerm = term.substring(0, term.length()-1);
				if(ht.containsKey(candTerm))
				{
					////System.out.println("term: " + term + "    candTerm:" + candTerm);
					content = content.replaceAll(term, candTerm);
				}
				else
					if(term.substring(term.length()-2).trim().equals("es"))
					{
						candTerm = term.substring(0, term.length()-2);
						if(ht.containsKey(candTerm))
							content = content.replaceAll(term, candTerm);
					}
			}
		}
		return content;
	}


	public void WriteContent(String content, String DocPath)
	{
		// BufferedWriter
		File f1 = new File(DocPath);
		String t = "";
		if(f1.exists()){
			t = readContent(DocPath);
			content = t + "\r\n" + content;
		}

		try{
			FileWriter output = new FileWriter(DocPath);
			BufferedWriter bw = new BufferedWriter(output);
			bw.write(content);
			bw.close();
			output.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}


	//	public void WriteContent(String content, String DocPath)
	//	{
	//		// BufferedWriter
	//		try{
	//			FileWriter output = new FileWriter(DocPath);
	//			BufferedWriter bw = new BufferedWriter(output);
	//			bw.write(content);
	//			bw.close();
	//			output.close();
	//		}catch(Exception e) {
	//			e.printStackTrace();
	//		}
	//	}

	public void totalWord(String docPath)
	{
		try {
			String text = readContent(docPath);
			StringTokenizer st = new StringTokenizer(text);
			ConstantValue.NUM_OF_WORD = st.countTokens(); 												
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isNumeric(char s)	{
		if( (s >= '0')&&(s <= '9'))
			return true;
		else
			return false;
	} 	

	public boolean isLineXExist(String docPath,int lineX)
	{
		//get the sentence in lineX. if lineX exist, return true and the corresponding sentence is stored in m_lineSentence;
		//otherwise return false;
		m_lineString = null;
		try {
			String text = readContent(docPath);
			String[] line = text.split("\r\n");

			if(line.length > lineX ){
				m_lineString = line[lineX].toString();
				//				System.out.println( " -----line" + Integer.toString(lineX -1));
				return true;
			}

			//			
			//			File file = new File(docPath);
			////			FileReader fileReader = new FileReader(file);
			////			BufferedReader bufferedReader = new BufferedReader(fileReader);
			////			this is the common way in reading a file
			////			bufferedReader.close();
			//			
			//			FileInputStream fis = new FileInputStream(file);
			//			DataInputStream dis = new DataInputStream(fis);
			//			BufferedReader br = new BufferedReader(new InputStreamReader(dis));
			//			String lineString = null;
			//			int xLine = 0;
			//
			//			// Read each line from the document
			//			while ((lineString = br.readLine()) != null) {
			//				xLine = xLine + 1;
			//				if (lineX == xLine)
			//				{
			//					m_lineString = lineString;
			//					return true;
			//				}
			//			}
			//			br.close();
			//			dis.close();
			//			fis.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}


	public String GetLineXsentence(){
		return m_lineString;
	}


	public void WritePMI(String docPath, ArrayList<PMIforDoc> PMIDoc)
	{
		// BufferedWriter
		try{
			double totalPMIofdataset = 0;
			String PMIstr = "";

			for (int i = 0; i < PMIDoc.size(); i++ ){
				String doc = PMIDoc.get(i).getDocName();
				double pmi = PMIDoc.get(i).getPmi();
				totalPMIofdataset = totalPMIofdataset + pmi;
				PMIstr = PMIstr + doc + " " + Double.toString(pmi) + "\r\n";
			}
			PMIstr = PMIstr + Double.toString(totalPMIofdataset) + " " + Double.toString(totalPMIofdataset/24) 
					+ "\r\n";

			WriteContent( PMIstr, docPath);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<PMIforDoc> CalculatePMI(String docPath, Hashtable <String, ArrayList<TermInfo>> ht_MFS, ArrayList<PMIforDoc> PMIDoc, int win)	{
		double totalPMI = 0;
		int termsN = 0; //the number of terms (according to the MFS list) that appear in the doc

		int tN = 0; //the number of terms pairs when calculate p(word1, word2)
		if(ht_MFS.size()>0)	{
			Enumeration <String> e_MFS = ht_MFS.keys();
			List<String> list_MFS = new ArrayList<String>();

			while(e_MFS.hasMoreElements()) {
				String key_MFS = (String)e_MFS.nextElement();
				list_MFS.add((key_MFS));
			}
			Collections.sort(list_MFS);


			if(ht_MFS.size()>1)//if there are more that 2 MFS in the list, then do
			{			
				for (int i = 0; i < list_MFS.size(); i++)
				{
					String t1 = list_MFS.get(i);		
					ArrayList<TermInfo> i1 = (ArrayList<TermInfo>)ht_MFS.get(t1);
					int length1 = i1.size(); //the length of position list
					int idex1 = ConstantValue.TERM_POSITION_LINE;//index of position
					int x1 = i1.get(idex1).getIndex();//line of t1
					int p1 = i1.get(ConstantValue.TERM_DOCFREQ).getInfor();

					for(int j = i; j < list_MFS.size(); j++)
					{		
						idex1 = ConstantValue.TERM_POSITION_LINE;//index of position
						try{
							String t2 = list_MFS.get(j);	

							//if t1 == t2,break
							if (t1.compareTo(t2)==0)
								continue;

							ArrayList<TermInfo> i2 = (ArrayList<TermInfo>)ht_MFS.get(t2);
							int length2 = i2.size(); //the length of position list
							int idex2 = ConstantValue.TERM_POSITION_LINE;//index of position
							int x2 = i2.get(idex2).getIndex();//line of t2

							//compare the two position list
							int coocur = 0; //the coocur times of two terms.

							while(idex1<length1 && idex2<length2)
							{
								x1 = i1.get(idex1).getIndex();//line of t1
								x2 = i2.get(idex2).getIndex();//line of t2
								if (x1-x2 >win)// larger than "win"
								{
									idex2++;
									continue;
								}
								if (x2-x1>win)
								{
									idex1++;
									continue;
								}
								if(Math.abs(x1-x2)<=win ) // not larger that "win",count the coocur
								{
									coocur++;
									idex1++;
									idex2++;
								}
							}

							int p2 = i2.get(ConstantValue.TERM_DOCFREQ).getInfor();
							double temp =0;
							if (coocur>0)
							{
								temp = (Math.log10(coocur) - Math.log10(p1)- Math.log10(p2));
								////System.out.print("***P("+t1+","+t2+"):  ");
								////System.out.print(Integer.toString(p1));
								////System.out.print("  "+Integer.toString(p2));
								////System.out.print("  "+Integer.toString(coocur)+" \n");
								////System.out.print(" ----temp******"+Double.toString(temp)+"----\n");
								tN = tN + 1;
								//////System.out.println("------PMI-----tN-"+Integer.toString(tN)+"----");
							}
							totalPMI = totalPMI + temp;
						}catch (Exception e) {
							////System.out.println("--------------");
							////System.out.println("------PMI------"+Integer.toString(j)+"----log");
							System.exit(0);
						}
					}
					termsN = termsN + p1; //
				}
			}

			////System.out.print("----PMI***********"+ Double.toString(totalPMI));
			totalPMI = totalPMI + Math.log10(ConstantValue.NUM_OF_WORD)*tN;
			////System.out.print("----log10(termsN)*tN***********"+ Double.toString(Math.log10(ConstantValue.NUM_OF_WORD)*tN));
		}
		else
		{
			double avgPMI = 0;
			avgPMI = 0;
			PMIDoc.add(new PMIforDoc("000", avgPMI));
			return PMIDoc;
		}
		double avgPMI = 0;
		avgPMI = totalPMI/(ht_MFS.size()*Math.log10(2));//ht_MFS.size() the total number of features in the DP model
		PMIDoc.add(new PMIforDoc(docPath, avgPMI));

		////System.out.println("----MFS list***********"+ Integer.toString(ht_MFS.size()));
		////System.out.println("----ConstantValue.NUM_OF_WORD***********"+ Integer.toString(ConstantValue.NUM_OF_WORD));
		////System.out.println("----tN***********"+ Integer.toString(tN));
		////System.out.println("------PMI----total********"+Double.toString(avgPMI)+"----log");

		return PMIDoc;
	}

	public String stemWord(String word, int stemFlag){
		String stemW = "";
		if(stemFlag > 0){
			char[] chArr = word.toCharArray();
			Stemmer s = new Stemmer();
			s.add(chArr,chArr.length);
			s.stem();
			stemW = s.toString();
		}
		else{
			stemW = word;
		}
		return stemW;
	}


	public  String stemSentence(String sentence, int flag) {
		//word stem and stop word removing
		//not remove stopword
		sentence = sentence.toLowerCase();
		String sequence = "";
		if(flag > 0){
			StringTokenizer st = new StringTokenizer(sentence);
			while(st.hasMoreTokens()) {
				String tk = st.nextToken();
				if(!isStopword(tk))
					sequence = sequence + " " + stemWord(tk, flag);
			}
			sequence = sequence.trim();
			return sequence;
		}
		else {
			sequence = sentence;
			return sequence;
		}
	}

	public void WriteWithouSingleHashtable(Hashtable <String, ArrayList<TermInfo>> ht, String DocPath){
		//dont change the context in the ht 
		// BufferedWriter
		try{
			String htStr = "";
			Enumeration <String> e = ht.keys();
			List<String> akey = new ArrayList<String>();

			while(e.hasMoreElements()) {
				String key = (String)e.nextElement();
				akey.add((key));
			}
			Collections.sort(akey);
			for (int i = 0; i < akey.size(); i++) { 
				String t = akey.get(i);
				if(t.indexOf(" ") > 0) {// it is single word. withou " "
					htStr = htStr + t + "\r\n";
				}
			}
			WriteContent(htStr, DocPath);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void WriteHashtable(Hashtable <String, ArrayList<TermInfo>> ht, String DocPath){
		// BufferedWriter
		try{
			String printInfor = "";
			Enumeration <String> e = ht.keys();
			List<String> akey = new ArrayList<String>();

			while(e.hasMoreElements()) {
				String key = (String)e.nextElement();
				akey.add((key));
			}
			Collections.sort(akey);

			for (int i = 0; i < akey.size(); i++) { 
				String t = akey.get(i);
				ArrayList<TermInfo> infor = (ArrayList<TermInfo>)ht.get(t);
				printInfor = printInfor + t + ", ";
				for(int j = 0;j<infor.size();j++) {
					String tm = infor.get(j).toString();
					printInfor = printInfor + tm ;
				} 
				printInfor = printInfor + "\r\n";
			}
			WriteContent(printInfor, DocPath);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}


	public void WriteHashtableWithoutFreq(Hashtable <String, ArrayList<TermInfo>> ht, String DocPath)
	{
		// BufferedWriter
		try{
			String htStr = "";
			Enumeration <String> e = ht.keys();
			List<String> akey = new ArrayList<String>();

			while(e.hasMoreElements()) {
				String key = (String)e.nextElement();
				akey.add((key));
			}
			Collections.sort(akey);
			for (int i = 0; i < akey.size(); i++) { 
				String t = akey.get(i);
				htStr = htStr + t + "\r\n";
			}
			WriteContent(htStr, DocPath);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}


	public void readHashtable(String text, Hashtable <String, ArrayList<TermInfo>> ht) {

		String[] lines = StringUtils.split( text, "\r\n");
		for(int i = 0; i < lines.length; i++){
			String str = lines[i].toString();
			String[] items = StringUtils.split( str.trim(), ",");
			String key = items[0].toString();
			ArrayList<TermInfo> array = new ArrayList<TermInfo>();

			for(int j = 1; j < items.length; j++){
				String item_j = items[j].toString().trim();
				String separator = "-";
				String index_str = item_j.substring(0, item_j.indexOf(separator));
				String infor_str = item_j.substring(item_j.indexOf(separator)+1, item_j.length());

				//check whether the "index" is correct
				int index_int = Integer.parseInt(index_str);
				int infor_int = Integer.parseInt(infor_str);
				TermInfo tmp = new TermInfo(index_int, infor_int);
				array.add(tmp);
			}
			ht.put(key, array);
		}
	}


	public void OutputHashtable(Hashtable <String, ArrayList<TermInfo>>	ht) {
		Enumeration <String> e = ht.keys();
		List<String> akey = new ArrayList<String>();

		String printInfor = "";

		while(e.hasMoreElements()) {
			String key = (String)e.nextElement();
			akey.add((key));
		}
		Collections.sort(akey);
		for (int i = 0; i < akey.size(); i++) { 
			String t = akey.get(i);
			ArrayList<TermInfo> infor = (ArrayList<TermInfo>)ht.get(t);
			printInfor = printInfor + t + ", ";
			for(int j = 0;j<infor.size();j++) {
				String tm = infor.get(j).toString();
				printInfor = printInfor + tm ;
			} 
			printInfor = printInfor + "\r\n";
		}
		//		System.out.println(printInfor);
	}

	public void OutputHashtableDetail(Hashtable <String, ArrayList<TermInfo>> ht, String DocPath) {
		Enumeration <String> e = ht.keys();
		List<String> akey = new ArrayList<String>();

		String str = "";
		while(e.hasMoreElements()) {
			String key = (String)e.nextElement();
			akey.add((key));
		}
		Collections.sort(akey);
		try{
			for (int i = 0; i < akey.size(); i++) { 
				String t = akey.get(i);
				ArrayList<TermInfo> infor = (ArrayList<TermInfo>)ht.get(t);

				String tmp = "";
				tmp = tmp + t + " " + infor.get(0).getInfor() + " ";
				////System.out.print (t + " "); 
				////System.out.print(infor.get(0).getInfor()); 
				////System.out.print(" ");
				for(int j = 0;j<infor.size();j++){
					tmp = tmp + infor.get(j).getIndex() + "-" + infor.get(j).getInfor() + " ";
					////System.out.print(infor.get(j).getIndex()); 
					////System.out.print("-"); 
					////System.out.print(infor.get(j).getInfor()); 
					////System.out.print(" ");
				} 
				tmp = tmp + "\r\n";
				str = str + tmp;
				////System.out.println();
			}
			WriteContent(str, DocPath);
		}catch(Exception ee) {
			ee.printStackTrace();
		}
	}

	////////////////////2010.11.17 use the whole text as a input///////////////////////////////////////////////////////
	/**
	 * check whether a "str" (a line of str) is sentence
	 * function: try to remove the structue inforamtion of a patent
	 */
	public Boolean isSentence(String str){
		Boolean flag = false;
		if(str.indexOf(',') >0 || str.indexOf('.')>0)
			flag = true;
		return flag; 
	}

	public String getSentencefromText(String docPath, String patentNum, String outputFolder, int stemFlag){

		//read text from docPath and separate them into sentences and save
		String contents = "";

		String saveSentence = outputFolder + "-Sentence-" + patentNum + ".txt";
		String save2 = outputFolder + "-stem-" + patentNum + ".txt";

		contents = readContent(docPath);
		String conSentence = filterSentence(contents);

		WriteContent(conSentence, saveSentence);

		String[] lines = StringUtils.split( conSentence, "\r\n");
		String stemStr = "";
		for(int i = 0; i < lines.length; i++){
			String line = lines[i].toString();
			stemStr = stemStr + stemSentence(line, stemFlag) + "\r\n";
		}

		WriteContent(stemStr, save2);
		return saveSentence;//return the path of the save file
	}

	public String getallText(String docPath){
		String contents = "";
		contents = readContent(docPath);
		String conSentence = filterSentence(contents);
		return conSentence;
	}


	public String preprocessSent(String text, int stemFlag){
		String str = "";
		String[] lines = StringUtils.split( text, "\r\n");

		for(int i = 0; i < lines.length; i++){
			String line = lines[i].toString();
			line = stemSentence(line, stemFlag);
			str = str + line + "\r\n";
		}
		return str; 
	}

	public String getSentencefromText(String conSentence){

		//read text from docPath and separate them into sentences and save
		String contents = "";

		String[] lines = StringUtils.split( conSentence, "\r\n");
		for(int i = 0; i < lines.length; i++){
			String line = lines[i].toString();
			if(line.indexOf("---")<0){
				String sen = toSentence(line, "");
				contents = contents + sen + "\r\n";
			}
		}
		return contents;//return the path of the save file
	}

	public void getDocSizeandSentencenumber(String conSentence, ArrayList<Double> infor){
		double sentencNum = 0.0;
		double wordCount = 0.0;
		String sentences = getSentencefromText(conSentence);
		String[] sentence = sentences.split("\r\n");
		sentencNum = sentence.length;
		for(int i = 0; i < sentence.length; i++){
			String senten_i = sentence[i].toString();
			String[] items = senten_i.split(" ");
			wordCount = wordCount + items.length;
		}
		infor.add(wordCount);
		infor.add(sentencNum);
	}

	//20101119 
	/** separate text into sentences 
	 * 1. preprocessing: remove the lines without sentence
	 * 2. read each line, do the stemming, and save the sentence in an arraylist
	 * 3. find the mfs
	 */

	/** 
	 * 1. preprocessing: remove the lines without sentence
	 */
	public String filterSentence(String inputStr){
		//process the "str" to sentences and remove the duplicate. 
		String text = "";
		//1. read each line
		inputStr = inputStr.replaceAll("-LRB-", " ");
		inputStr = inputStr.replaceAll("-RRB-", " ");

		String[] lines = StringUtils.split( inputStr, "\r\n");
		for(int i = 0; i < lines.length; i++){
			String line = lines[i].toString();
			// is sentence?
			if(line.indexOf(".") >0 ){
				//				text = text + line + "\r\n";
				// separate the paragraph into sentence
				try {
					MaxentTagger tagger = new MaxentTagger(taggerpath);
					//release the space.!!!!!!!!!!!!!!!!!!!1
					List<Sentence<? extends HasWord>> sentences = MaxentTagger
							.tokenizeText(new StringReader(line));
					List<Sentence<TaggedWord>> taggedSentences = new ArrayList<Sentence<TaggedWord>>();
					for (int k = 0; k < sentences.size(); k++) {
						Sentence<? extends HasWord> sentence = sentences.get(k);
						////System.out.println(sentence.toString(true));


						String sent = sentence.toString(true);
						if(sent.length()>15)// if the sentence length reach a certain length
							text = text + sent + "\r\n";
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return text;
	}

	public void createFolder(String folderName){
		File f1 = new File(folderName);
		if(!f1.isDirectory()){
			f1.mkdir();
		}
	}

	public String getName(String path){
		String name = "";
		File f1 = new File(path);
		name = f1.getName();
		return name;
	}

}
