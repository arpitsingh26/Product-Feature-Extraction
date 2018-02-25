package ly.ux.discovery;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Scanner;

import rita.RiWordNet;

public class FeatureOpinionValue2 {

	// get the features based on the given list. 
	public static void main(String[] args) throws IOException {  
		//input////////////////////////////////////////////////////////////////
		String stopWordPath = "C:/Users/Arpit.Singh/workspace/artifactextraction/Input/StopWords.txt";
		String inputFolder = "C:/Users/Arpit.Singh/workspace/OriginalReviews/"; // the review text
		String positive_List = "C:/Users/Arpit.Singh/workspace/artifactextraction/Input/opinionFeatureStemed.txt"; // the given list
		String productFeature_list = "C:/Users/Arpit.Singh/workspace/artifactextraction/Input/TermList.txt";
		String productFeature_list2 = "C:/Users/Arpit.Singh/workspace/Annotation/ProductFeature.txt";
		//output////////////////////////////////////////////////////////////////
		String csvPath = "C:/Users/Arpit.Singh/workspace/artifactextraction/Output/Features.csv"; // the saved csv path
		/////////////////////////////////////////////////////////////////////////
		double sign = -1.0; // 1.0: positive value -1.0: negative value
				
		int stemFlag = 1;
		File folder = new File(inputFolder);
		String[] docs = folder.list();
		FileIO fileIO = new FileIO();

		fileIO.InitialStopwordList(stopWordPath, stemFlag);

		String csv_data = "";
		// read the given list
		//		ArrayList<String> productFeatureList = getWordList(productFeature_list);
		Hashtable<String, termWeight> h_productF = new Hashtable<String, termWeight> ();
		getListWordFreq(productFeature_list, h_productF);
		ArrayList<String> positiveList = getWordList(positive_List);

		int numFeatures = h_productF.size();

		// get the row name
		csv_data = csv_data + attributes(positiveList); 

		if (docs == null) {
		} else {
			// Read each document from the document collection
			for (int i = 0; i < docs.length; i++) {
				String sourceFilePath = inputFolder + docs[i];

				String contents = fileIO.readContent(sourceFilePath);
				//				String sentences = fileIO.toSentence(contents, "");

				String[] each_sent = contents.split("\r\n"); // for each sentence, calculate the f and Y
				//String csv_each = "";
				for(int j = 0; j < 100; j++ ){
					// 1. read the text, segment text into sentences and stem terms
					String line = each_sent[j].toString();
					line = fileIO.stemSentence(line, stemFlag);   

					calculateWeight(line, h_productF,  positiveList);
					calPhraseWeight(line, h_productF,  positiveList);
					System.out.println(j);
				}
			}
		}
		sortWeight( h_productF,  csvPath);
		
	}

	private static Set<String> getWordSet(String fileName) {
		FileIO fileIO = new FileIO();
		Set<String> hashSet = new HashSet<String>();
		try {
			String stopw = fileIO.readContent(fileName);  
			String[] term = stopw.split("\r\n");
			for (int kk = 0; kk < term.length; kk++){
				String t = fileIO.stemWord(term[kk].toString().trim(), 1);
				hashSet.add(t);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hashSet;
	}

	private static ArrayList<String> getWordList(String fileName) {
		ArrayList<String> wordList = new ArrayList<String>();
		Set<String> hashSet = getWordSet(fileName);
		Iterator<String> it = hashSet.iterator();
		while (it.hasNext()) {
			String str = it.next().toString();
			wordList.add(str);
		}
		return wordList;
	}


	private static void getListWordFreq(String fileName, Hashtable<String, termWeight> TWeight){
		FileIO fileIO = new FileIO();
		Set<String> hashSet = new HashSet<String>();
		try {
			String stopw = fileIO.readContent(fileName);
			String[] term = stopw.split("\r\n");
			for (int kk = 0; kk < term.length; kk++){
				String str = term[kk].toString().trim();
				if(str.indexOf(",") >= 0){
					String t = str.substring(0, str.lastIndexOf(",") );
					if(t.indexOf(",") >=0 ){
						continue;
					}
					double f = Double.parseDouble(str.substring( str.lastIndexOf(",") + 1, str.length()).trim());
   
					termWeight tw = new termWeight(t, f);
					TWeight.put(t, tw);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void getSituationterms(String fileName, Hashtable<String, String> h_pTerms){

		FileIO fileIO = new FileIO();
		Set<String> hashSet = new HashSet<String>();
		try {
			String stopw = fileIO.readContent(fileName);
			String[] term = stopw.split("\r\n");
			for (int kk = 0; kk < term.length; kk++){
				String str = term[kk].toString().trim().toLowerCase();
				h_pTerms.put(str, "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String attributes(Set<String> synonymySet, String count){
		String str = "";
		str = str + "id" + ", ";
		Iterator<String> it = synonymySet.iterator();
		while (it.hasNext()) {
			String syn = it.next().toString();
			str = str + syn + ", ";
		}
		str = str + count + "\r\n";
		return str;
	} 

	private static String attributes(ArrayList<String> feature){
		String str = "";
		str = str + "id" + ", ";
		for(int i = 0; i < feature.size(); i++){
			String f = feature.get(i);
			str = str + f + ","+ f+ "_value, ";
		}
		str = str + "feature_flag," + "sentence_value" + "\r\n";
		return str;
	}

	private static double calOpinionValue(String line, String feature, ArrayList<String> positiveList){
		double FeatureValue = -1000;
		// line already stemed. 
		// 1. does not contain feature
		ArrayList<String> terms = getTerms(line);
		Hashtable <String, ArrayList<TermInfo>> ht_TextTerms = new Hashtable <String, ArrayList<TermInfo>>();
		for(int i = 0; i<terms.size();i++){
			String tmp = terms.get(i);
			invertedIndex(tmp, 1, i, ht_TextTerms);
		}
		if(!ht_TextTerms.containsKey(feature)){
			return FeatureValue;
		}
		// contain feature
		else{
			FeatureValue = 0.0;
			// get the invert index
			if(ht_TextTerms.containsKey(feature)){
				ArrayList<TermInfo> infor = (ArrayList<TermInfo>)ht_TextTerms.get(feature);
				for (int j = ConstantValue.TERM_POSITION_LINE; j < infor.size() ; j++){
					int p_f = infor.get(j).getInfor();
					for(int sen = 0; sen < positiveList.size(); sen++){
						String opinion = positiveList.get(sen);
				     		if(ht_TextTerms.containsKey(opinion)){
							ArrayList<TermInfo> infor_sen = (ArrayList<TermInfo>)ht_TextTerms.get(opinion);
							for(int sen_pos = ConstantValue.TERM_POSITION_LINE; sen_pos < infor_sen.size() ; sen_pos++){
								int p_s = infor_sen.get(sen_pos).getInfor();
								double dis = Math.abs( p_s - p_f );
								if(dis > 0 ){
									FeatureValue = FeatureValue + (1/dis);
								}
							}
						}
					}
				}
			}		
		}
		return FeatureValue;
	}

	private static String getFeatureStr(String line, String feature, ArrayList<String> positiveList,
			double sign){
		double featureVal = calOpinionValue(line, feature, positiveList);
		StringBuilder str = new StringBuilder();
		if(featureVal < 0 ){ // does not have feature
			str.append("0,0,");
		}
		else{ // contain feature
			featureVal = featureVal * sign;
			str.append("1," + featureVal + ",");
		}
		return str.toString();
	}

	private static void sortWeight(Hashtable<String, termWeight> TWeight, String savePath) throws IOException{
		String txt = "";
		//System.out.println(TWeight);
		List<String> list_term = new ArrayList<String>();
		if(TWeight.size()>0)	{
			Enumeration e_MFS = TWeight.keys();
			while(e_MFS.hasMoreElements()) {
				String key_MFS = (String)e_MFS.nextElement();
				list_term.add((key_MFS));   
			}
			Collections.sort(list_term);
		}
		for(int ti = 0; ti < list_term.size(); ti++ ){
			String feature = list_term.get(ti);
			termWeight tmp = TWeight.get(feature);
			if(tmp.getNfy() < 0){
				tmp.setFinal_w(-1);
				tmp.setFinal_wd(-1);
			}
			else{
				double xx = 0;
				double xxd = 0;
				if(tmp.getN_nf_y()*tmp.getN_f_ny()> 0){
					xx = Math.log( 1+ tmp.getNfy()* tmp.getNfy()/((1+tmp.getN_nf_y())*(1+tmp.getN_f_ny())));
				}
				if(tmp.getN_nf_y()*tmp.getN_f_ny() > 0){
					//xxd = Math.log( 1+ tmp.getNfy_value()* tmp.getNfy_value()/(Math.sqrt(tmp.getN_nf_y()+1)*Math.sqrt(1+tmp.getN_f_ny())));
					xxd = Math.log( 1+ tmp.getNfy_value()* tmp.getNfy_value()
							/((1+tmp.getN_nf_y())*(1+tmp.getN_f_ny())));
				}
				tmp.setFinal_w(tmp.getTermFreq() * xx);
				tmp.setFinal_wd(tmp.getTermFreq() * xxd);
			}
					//TWeight.put(feature, tmp);

			txt = txt + tmp.getTerm()    + ", " + tmp.getTermFreq() + ", " + tmp.getFinal_w() + ", " + tmp.getFinal_wd() + "\r\n";

		}
		//System.out.println(txt);
		List<String> list = new ArrayList<String>();
        for(String line: txt.split("\r\n")) {
            list.add(line);  
        }
        Collections.sort(list, new Comparator<String>() {
            public int compare(String s1, String s2) {
                Float f1 = Float.parseFloat(s1.split(",")[3].trim());
                Float f2 = Float.parseFloat(s2.split(",")[3].trim());
                return f2.compareTo(f1);
            }
        });
        StringBuilder result = new StringBuilder();
        int i1=0;
        for(String line: list) {
            //System.out.println(line);
            if(i1!=0) {
            	//if(i1<=10) System.out.println(line);
            	result.append(line+"\r\n");
            }
            i1++;
        }
        i1=0;
      //  System.out.println(result.toString());

        String res=result.toString();
        int testcases=50;//22 and 49
		int testcases2=22;
        RiWordNet rw = new RiWordNet("C:/Users/Arpit.Singh/workspace/WordNet-3.1");
		rw.randomizeResults(false); 
		HashMap<String,String[]> smap = new HashMap<String,String[]>();
		for(int j1=1;j1<=testcases;j1++){
			//System.out.println("Line: "+list.get(j1).split(",")[0].trim());
			String[] str1=rw.getAllSynonyms(list.get(j1).split(",")[0].trim(), "n");
			smap.put(list.get(j1), str1);
		}   
		List<String> slist = new ArrayList<String>();
		List<String> templist = new ArrayList<String>();
		List<String> rlist = new ArrayList<String>();
		List<String> tempkey = new ArrayList<String>();   
		for (HashMap.Entry<String, String[]> entry : smap.entrySet())   
		{
			if(tempkey==null){
				tempkey.add(entry.getKey());
				continue;
			}
		    boolean b=false;
		    for(int j1=0;j1<tempkey.size();j1++){
			    String[] s1=smap.get(tempkey.get(j1));
			    for(int i2=0;i2<(s1.length);i2++){
			    	//System.out.println(s1[i2]+"/"+entry.getKey().split(",")[0].trim());
			    	File folder = new File("C:/Users/Arpit.Singh/workspace/artifactextraction/Output/Features.csv");
					String[] docs = folder.list();
					FileIO fileIO = new FileIO();
			    	String t = fileIO.stemWord(entry.getKey().split(",")[0].trim(), 1);
			    	b=b||((s1[i2].toLowerCase()).equals(t));
			    }
		    }
		        tempkey.add(entry.getKey());
			    if(b){
			    	templist.add(entry.getKey().split(",")[0].trim());
			    }
			    else{
			    	slist.add(entry.getKey().split(",")[0].trim());
			    }
			    
			    
			}
			for(int i2=0;i2<slist.size();i2++){
				rlist.add(slist.get(i2));
			}
			/*
			for(int i2=0;i2<templist.size();i2++){
				rlist.add(templist.get(i2));
			}
		*/
		System.out.println(rlist.size());

		FileIO fileIO = new FileIO();  
		fileIO.WriteContent(result.toString(), savePath);
		//calculation of precision and recall
				
				
				int i=1;
				/*
				List<String> list2 = new ArrayList<String>();
		        for(String line: res.split("\r\n")) {
		        	if (i>testcases) break;
		        	i++;
		        	String s1 = line.split(",")[0].trim();
		        	//String s1 = line.split(",")[0].trim()+",";
		        	//Float f1 = Float.parseFloat(line.split(",")[1].trim());
		        	//int f2 = Math.round(f1);
		        	//s1+=Integer.toString(f2);
		            list2.add(s1);
		        }
		        */
		        i=1;
		        
		        String productFeature_list2 = "C:/Users/Arpit.Singh/workspace/Annotation/ProductFeature.txt";
		        BufferedReader in;
		        List<String> list3 = new ArrayList<String>();
				try {
					in = new BufferedReader(new FileReader(productFeature_list2));
					String str3;
			        while((str3 = in.readLine()) != null){
			        	if (i>testcases2) break;
			        	i++;
			            list3.add(str3.trim());
			        }
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        i=0;
		        for(String str: rlist) {
		        	for(String str2: list3) {
		        		if(str.trim().equals(str2.trim())){
		        			System.out.println("Line: "+str.trim()+"/"+str2.trim());
		        			i++;
		        		}
		        	}
		        }
		        double precision=(i*1.0)/ rlist.size();
		        double recall=(i*1.0)/testcases2;
		        System.out.println("Precision: "+ precision);
		        System.out.println("Recall: "+ recall);   
		        
		        
	}

	private static void calculateWeight(String line, Hashtable<String, termWeight> TWeight, ArrayList<String> positiveList){

		// line already stemed. 
		// 1. does not contain feature
		ArrayList<String> terms = getTerms(line);
		Hashtable <String, ArrayList<TermInfo>> ht_TextTerms = new Hashtable <String, ArrayList<TermInfo>>();
		for(int i = 0; i<terms.size();i++){
			String tmp = terms.get(i);
			invertedIndex(tmp, 1, i, ht_TextTerms);
		}

		List<String> list_term = new ArrayList<String>();
		if(TWeight.size()>0)	{
			Enumeration e_MFS = TWeight.keys();
			while(e_MFS.hasMoreElements()) {
				String key_MFS = (String)e_MFS.nextElement();
				list_term.add((key_MFS));
			}
			Collections.sort(list_term);
		}

		boolean setimentFlag = isContainSentiment(ht_TextTerms, positiveList);

		// 1. do not have sentiment word, update n_f_ny
		if(!setimentFlag){
			for(int ti = 0; ti < list_term.size(); ti++ ){
				String feature = list_term.get(ti);
				if(ht_TextTerms.containsKey(feature)){
					termWeight tmp = TWeight.get(feature);
					double n_f_ny = tmp.getN_f_ny() + 1;
					tmp.setN_f_ny(n_f_ny);
					TWeight.put(feature, tmp);
				}
			}
		}

		else{//2. have sentiment word, update nfy, n_nf_y, nfy_value
			for(int ti = 0; ti < list_term.size(); ti++ ){
				String feature = list_term.get(ti);
				if(ht_TextTerms.containsKey(feature)){
					double dis = 0;
					double num = 0;
					ArrayList<TermInfo> infor = (ArrayList<TermInfo>)ht_TextTerms.get(feature);
					for (int j = ConstantValue.TERM_POSITION_LINE; j < infor.size() ; j++){
						int p_f = infor.get(j).getInfor();
						for(int sen = 0; sen < positiveList.size(); sen++){
							String opinion = positiveList.get(sen);
							if(ht_TextTerms.containsKey(opinion)){
								ArrayList<TermInfo> infor_sen = (ArrayList<TermInfo>)ht_TextTerms.get(opinion);
								for(int sen_pos = ConstantValue.TERM_POSITION_LINE; sen_pos < infor_sen.size() ; sen_pos++){
									int p_s = infor_sen.get(sen_pos).getInfor();
									if(p_s - p_f == 0){
										dis = -1; break; //the feature is a sentiment word
									}else
										dis = dis + 1.0/Math.abs( p_s - p_f );
								}
							}
						}
					}
					if(dis > 0)
						num = 1;
					termWeight tmp = TWeight.get(feature);
					double nfy = 0; 
					double nfy_value = 0;
					if(tmp.getNfy() >= 0 && dis > 0){
						nfy = tmp.getNfy() + num;
						nfy_value = tmp.getNfy_value() + dis;

					}else{
						if(dis < 0){
							nfy = -1; 
							nfy_value = -1;
						}
					}
					tmp.setNfy(nfy);
					tmp.setNfy_value(nfy_value);
					TWeight.put(feature, tmp);
				}
				else{// n_nf_y
					termWeight tmp = TWeight.get(feature);
					double n_nf_y = tmp.getN_nf_y() + 1;
					tmp.setN_nf_y(n_nf_y);
					TWeight.put(feature, tmp);
				}
			}
		}

	}

	/**
	 * if feature is a phrase AB, then use A with the larger term frequency to calculate the dis
	 * @param line
	 * @param TWeight
	 * @param positiveList
	 */
	private static void calPhraseWeight(String line, Hashtable<String, termWeight> TWeight, ArrayList<String> positiveList){

		// line already stemed. 
		// 1. does not contain feature
		ArrayList<String> terms = getTerms(line);
		Hashtable <String, ArrayList<TermInfo>> ht_TextTerms = new Hashtable <String, ArrayList<TermInfo>>();
		for(int i = 0; i<terms.size();i++){
			String tmp = terms.get(i);
			invertedIndex(tmp, 1, i, ht_TextTerms);
		}

		List<String> list_term = new ArrayList<String>();
		if(TWeight.size()>0)	{
			Enumeration e_MFS = TWeight.keys();
			while(e_MFS.hasMoreElements()) {
				String key_MFS = (String)e_MFS.nextElement();
				list_term.add((key_MFS));
			}
			Collections.sort(list_term);
		}

		boolean setimentFlag = isContainSentiment(ht_TextTerms, positiveList);

		// 1. if the sentence does not have sentiment word, update n_f_ny
		if(!setimentFlag){
			for(int ti = 0; ti < list_term.size(); ti++ ){
				String feature = list_term.get(ti);
				if(containFeature(ht_TextTerms, feature)){
					termWeight tmp = TWeight.get(feature);
					double n_f_ny = tmp.getN_f_ny() + 1;
					tmp.setN_f_ny(n_f_ny);
					TWeight.put(feature, tmp);
				}
			}
		}

		else{//2. have sentiment word, update nfy, n_nf_y, nfy_value
			for(int ti = 0; ti < list_term.size(); ti++ ){
				String feature_org = list_term.get(ti);
				if(containFeature(ht_TextTerms, feature_org)){
					double dis = 0;
					double num = 0;
					String feature = getFeature(feature_org, TWeight);
					if(feature.length() < 1) continue;
					ArrayList<TermInfo> infor = (ArrayList<TermInfo>)ht_TextTerms.get(feature);
					
					for (int j = ConstantValue.TERM_POSITION_LINE; j < infor.size() ; j++){
						int p_f = infor.get(j).getInfor();
						for(int sen = 0; sen < positiveList.size(); sen++){
							String opinion = positiveList.get(sen);
							if(ht_TextTerms.containsKey(opinion)){
								ArrayList<TermInfo> infor_sen = (ArrayList<TermInfo>)ht_TextTerms.get(opinion);
								for(int sen_pos = ConstantValue.TERM_POSITION_LINE; sen_pos < infor_sen.size() ; sen_pos++){
									int p_s = infor_sen.get(sen_pos).getInfor();
									if(p_s - p_f == 0){
										dis = -1; break; //the feature is a sentiment word
									}else
										dis = dis + 1.0/Math.abs( p_s - p_f );
								}
							}
						}
					}
					if(dis > 0)
						num = 1;
					termWeight tmp = TWeight.get(feature_org);
					double nfy = 0; 
					double nfy_value = 0;
					if(tmp.getNfy() >= 0 && dis > 0){
						nfy = tmp.getNfy() + num;
						nfy_value = tmp.getNfy_value() + dis;

					}else{
						if(dis < 0){
							nfy = -1; 
							nfy_value = -1;
						}
					}
					tmp.setNfy(nfy);
					tmp.setNfy_value(nfy_value);
					TWeight.put(feature_org, tmp);
				}
				else{// n_nf_y
					termWeight tmp = TWeight.get(feature_org);
					double n_nf_y = tmp.getN_nf_y() + 1;
					tmp.setN_nf_y(n_nf_y);
					TWeight.put(feature_org, tmp);
				}
			}
		}

	}

	private static boolean isContainSentiment(Hashtable <String, ArrayList<TermInfo>> ht_TextTerms, 
			ArrayList<String> positiveList){
		boolean flag = false;
		for(int sen = 0; sen < positiveList.size(); sen++){
			String opinion = positiveList.get(sen);
			if(ht_TextTerms.containsKey(opinion)){
				flag = true; 
				break;
			}
		}
		return flag;
	}

	private static boolean containFeature(Hashtable <String, ArrayList<TermInfo>> ht_TextTerms, String feature){
		boolean flag = false;
		int num = 0;
		ArrayList<String> terms = getTerms(feature);
		for(int i = 0; i < terms.size(); i++ ){
			String item = terms.get(i);
			if(ht_TextTerms.containsKey(item)){
				num++;
			}
		}
		if(num == terms.size()){
			flag = true;
		}
		return flag; 
	}

	private static String getFeature(String feature, Hashtable<String, termWeight> TWeight){
		String f = "";
		try{
			ArrayList<String> terms = getTerms(feature);
			if(terms.size() == 1){
				f = feature;
			}
			else{
				String max_f = terms.get(0).toString();
				double max_freq = TWeight.get(max_f).getTermFreq();
				for(int i = 1; i < terms.size(); i++ ){
					String tmp_f = terms.get(i).toString();
					Scanner scanner = new Scanner("C:/Users/Arpit.Singh/workspace/artifactextraction/Input/opinion-feature.txt");
					int flag=0;
					while (scanner.hasNextLine()) {
					   final String lineFromFile = scanner.nextLine();
					   if(lineFromFile.contains(tmp_f)) { 
					       // a match!
					       flag=1;
					       break;
					   }
					}
					Scanner scanner2 = new Scanner("C:/Users/Arpit.Singh/workspace/artifactextraction/Input/opinionFeatureStemed.txt");
					while (scanner2.hasNextLine()) {
					   final String lineFromFile = scanner2.nextLine();
					   if(lineFromFile.contains(tmp_f)) { 
					       // a match!
					       flag=1;
					       break;
					   }
					}
					if (flag==1) continue;
					double tmp_freq = TWeight.get(tmp_f).getTermFreq();
					if(tmp_freq > max_freq){
						max_f = tmp_f;
						max_freq = tmp_freq;
					}
				}
				f = max_f;
			}
		}
		catch(Exception e){
			
		}
		
		return f; 
	}

	private static ArrayList<String> getTerms(String str) {
		//get term from the sentence. concern what kind of operations are needed in text preprocessing. stop word....
		StringTokenizer st = new StringTokenizer(str);
		ArrayList<String> al = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			al.add(st.nextToken().trim());
		}
		return al;
	}

	private static boolean invertedIndex(String term, int linex, 
			int pos, Hashtable <String, ArrayList<TermInfo>> ht_TextTerms){
		try {
			if (ht_TextTerms.containsKey(term)) { // The term already exists in the Hashtable ht
				ArrayList<TermInfo> infor = (ArrayList<TermInfo>)ht_TextTerms.get(term);

				int oldDocFreq = infor.get(ConstantValue.TERM_DOCFREQ).getInfor();
				infor.get(ConstantValue.TERM_DOCFREQ).SetInfor(oldDocFreq + 1);
				infor.get(ConstantValue.TERM_REMAIN_FREQ).SetInfor(oldDocFreq + 1);
				infor.add(new TermInfo(linex,pos));
				ht_TextTerms.put(term, infor);
				return true;

			} else { // The term does not exist in the Hashtable ht_TextTerms
				// Create a list of positions for the document
				//ArrayList<Integer> new_term_infor = new ArrayList<Integer>();
				ArrayList<TermInfo> new_term_infor = new ArrayList<TermInfo>();

				//set space for store termfreq
				for (int i = 0; i<=ConstantValue.TERM_POSITION_LINE;i++)
				{
					TermInfo t = new TermInfo(0,0);
					new_term_infor.add(t);
				}		
				//term is first counted. store term freq and its position
				//TermInfo t1 = new TermInfo(ConstantValue.TERM_DOCFREQ,1);
				new_term_infor.get(ConstantValue.TERM_DOCFREQ).SetIndex(ConstantValue.TERM_DOCFREQ);
				new_term_infor.get(ConstantValue.TERM_DOCFREQ).SetInfor(1);

				//TermInfo t2 = new TermInfo(ConstantValue.TERM_REMAIN_FREQ,1);
				new_term_infor.get(ConstantValue.TERM_REMAIN_FREQ).SetIndex(ConstantValue.TERM_REMAIN_FREQ);
				new_term_infor.get(ConstantValue.TERM_REMAIN_FREQ).SetInfor(1);

				//TermInfo t3 = new TermInfo(ConstantValue.TERM_POSITION_LINE,linex);
				new_term_infor.get(ConstantValue.TERM_POSITION_LINE).SetIndex(linex);
				new_term_infor.get(ConstantValue.TERM_POSITION_LINE).SetInfor(pos);

				// add to the Hashtable 
				ht_TextTerms.put(term, new_term_infor);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}




}
