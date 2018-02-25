package ly.ux.discovery;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class SituationFeature {

	// get the situation groups. 
	public static void main(String[] args) {
//		//		///////inputs/////////////////////////////////////////////////
//		String stopWordPath = "E:/experiments/Experiment-Crawler/ReviewFeature/Output/StopWords_0.txt";
//		String inputFolder = "E:/experiments/Experiment-Crawler/ReviewFeature/Input/review_sent/review_sent.txt"; // the review text
//		//		String positive_List = "E:/experiments/Experiment-Crawler/ReviewFeature/Input/opinion-words.txt"; // the given list
//		String situation_list = "E:/experiments/Experiment-Crawler/ReviewFeature/Output/01_situation_mult_list.txt";
//		String csvPath = "E:/experiments/Experiment-Crawler/ReviewFeature/Output/situationPair_v1.txt"; // the saved csv path
//		//////////////////////////////////////////////////////////////////////
		
//		///////inputs/////////////////////////////////////////////////
		String stopWordPath = args[0];
		String inputFolder = args[1]; // the review text
		//		String positive_List = "E:/experiments/Experiment-Crawler/ReviewFeature/Input/opinion-words.txt"; // the given list
		String situation_list = args[2];
		String csvPath = args[3]; // the saved csv path
		//////////////////////////////////////////////////////////////////////

		int stemFlag = 1;
		double alfa = 0.4;

		Hashtable<String, String> h_pTerms = new Hashtable<String, String>();
		getSituationterms(situation_list, h_pTerms, stemFlag, stopWordPath);
		getTextSit(inputFolder, h_pTerms, stemFlag, csvPath, alfa, stopWordPath);
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

	private static void getSituationterms(String fileName, Hashtable<String, String> h_pTerms, int stemFlag, String docStopword){

		FileIO fileIO = new FileIO();
		fileIO.InitialStopwordList(docStopword, stemFlag);
		Set<String> hashSet = new HashSet<String>();
		try {
			String stopw = fileIO.readContent(fileName);
			String[] term = stopw.split("\r\n");
			for (int kk = 0; kk < term.length; kk++){
				String str = term[kk].toString().trim().toLowerCase();
				if(str.indexOf(",") > 0){
					str = str.substring(0, str.indexOf(",")).trim();
				}
				String txt = fileIO.stemSentence(str, stemFlag).trim();
				if(txt.length() > 0){
					h_pTerms.put(txt, "");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void getTextSit(String fileName, Hashtable<String, String> h_pTerms, int stemFlag, 
			String savePath, double alfa, String docStopword){
		FileIO fileIO = new FileIO();
		fileIO.InitialStopwordList(docStopword, stemFlag);
		try{
			String reivew_sent = fileIO.readContent(fileName);
			String [] sentences = reivew_sent.split("\r\n");
			for(int i = 0; i < sentences.length; i++ ){
				String sent_i = fileIO.stemSentence( sentences[i].toString().trim().toLowerCase(), stemFlag);
				updatePhraseContext(h_pTerms, sent_i);
				System.out.println(i);
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		String txt = calSim(h_pTerms, savePath, alfa);
		fileIO.WriteContent(txt, savePath);
	}

	/**
	 * if sent contains a phrase x, then add the sent to the h_pTerms. form the global context of x
	 * @param h_pTerms
	 * @param sent: already stemming
	 */
	private static void updatePhraseContext(Hashtable<String, String> h_pTerms, String sent){

		List<String> list_term = new ArrayList<String>();
		if(h_pTerms.size()>0)	{
			Enumeration e_MFS = h_pTerms.keys();
			while(e_MFS.hasMoreElements()) {
				String key_MFS = (String)e_MFS.nextElement();
				list_term.add((key_MFS));
			}
			Collections.sort(list_term);
		}

		for(int i = 0; i < list_term.size(); i++ ){
			String txt = list_term.get(i).trim();
			if(sent.indexOf(txt) >= 0){
				String tmp = h_pTerms.get(txt);
				tmp = tmp + sent + "\r\n";
				h_pTerms.put(txt, tmp);
			}
		}
	}

	/**
	 * calculate the situation term similarity
	 * @param h_pTerms
	 * @param savePath
	 */
	private static String calSim(Hashtable<String, String> h_pTerms, String savePath, double alfa){
		String txt = "";
		List<String> list_term = new ArrayList<String>();
		if(h_pTerms.size()>0)	{
			Enumeration e_MFS = h_pTerms.keys();
			while(e_MFS.hasMoreElements()) {
				String key_MFS = (String)e_MFS.nextElement();
				list_term.add((key_MFS));
			}
			Collections.sort(list_term);
		}

		for(int i = 0; i < list_term.size(); i++ ){
			String string1 = list_term.get(i).trim();
			String context1 = h_pTerms.get(string1);
			System.out.println("-" + i);
			for(int j = i + 1; j < list_term.size(); j++ ){
				String string2 = list_term.get(j).trim();
				String context2 = h_pTerms.get(string2);

				StringSimilarity s_sim = new StringSimilarity();
				double local = s_sim.jaccardSimilarity(string1, string2);
				double global = s_sim.cosineSimilarity(context1, context2);
				double value = local * alfa + (1 - alfa) * global;
				txt = txt + string1 + ", " + string2 + ", " + Double.toString(value) + "\r\n";
			}
		}
		return txt;
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


	private static void sortWeight(Hashtable<String, termWeight> TWeight, String savePath){
		String txt = "";
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
					xx = Math.log( 1+ tmp.getNfy()* tmp.getNfy()/(tmp.getN_nf_y()*tmp.getN_f_ny()));
				}
				if(tmp.getN_nf_y()*tmp.getN_f_ny() > 0){
					xxd = Math.log( 1+ tmp.getNfy_value()* tmp.getNfy_value()/(tmp.getN_nf_y()*tmp.getN_f_ny()));
				}
				tmp.setFinal_w(tmp.getTermFreq() * xx);
				tmp.setFinal_wd(tmp.getTermFreq() * xxd);
			}
			//			TWeight.put(feature, tmp);

			txt = txt + tmp.getTerm() + ", " + tmp.getTermFreq() + ", " + tmp.getFinal_w() + ", " + tmp.getFinal_wd() + "\r\n";

		}
		FileIO fileIO = new FileIO();
		fileIO.WriteContent(txt, savePath);
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
		ArrayList<String> terms = getTerms(feature);
		if(terms.size() == 1){
			f = feature;
		}
		else{
			String max_f = terms.get(0).toString();
			double max_freq = TWeight.get(max_f).getTermFreq();
			for(int i = 1; i < terms.size(); i++ ){
				String tmp_f = terms.get(i).toString();
				double tmp_freq = TWeight.get(tmp_f).getTermFreq();
				if(tmp_freq > max_freq){
					max_f = tmp_f;
					max_freq = tmp_freq;
				}
			}
			f = max_f;
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
