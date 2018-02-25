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
import java.util.HashSet;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Scanner;
import rita.*;

public class ssl{
	public static void main(String[] args) throws IOException {  
		//input////////////////////////////////////////////////////////////////
		
		RiWordNet rw = new RiWordNet("C:/Users/Arpit.Singh/workspace/WordNet-3.1");
		rw.randomizeResults(false);      
		  
		  
		
		File text = new File("C:/Users/Arpit.Singh/workspace/artifactextraction/Output/Features.csv");
        Scanner scnr = new Scanner(text);

		List<String> unlabeledlist = new ArrayList<String>();
		List<String> labeledlist = new ArrayList<String>();
		HashMap<String, List<String>> labeledexample = new HashMap<String, List<String>>();
		HashMap<String, List<String>> unlabeledexample = new HashMap<String, List<String>>();
		HashMap<String, List<String>> softlabeledexample = new HashMap<String, List<String>>();
        //Reading each line of file using Scanner class
        int lineNumber = 0;
        while(scnr.hasNextLine()){
            String line = scnr.nextLine();
            String s=line.split(",")[0].trim();
			if(lineNumber<=5) {
				labeledlist.add(s);
			}
			else unlabeledlist.add(s);
			lineNumber++;
        }   
		scnr.close();
		int windowsize=5;
		
		String inputFolder = "C:/Users/Arpit.Singh/workspace/OriginalReviews/";
		String stopWordPath = "C:/Users/Arpit.Singh/workspace/artifactextraction/Input/StopWords.txt";
		
		int stemFlag = 1;
		File folder = new File(inputFolder);
		String[] docs = folder.list();
		FileIO fileIO = new FileIO();

		fileIO.InitialStopwordList(stopWordPath, stemFlag);
		

		if (docs == null) {
		} else {
			// Read each document from the document collection
			for (int i = 0; i < 1; i++) {//replace 1 by docs.length
				String sourceFilePath = inputFolder + docs[i];
				String contents = fileIO.readContent(sourceFilePath).trim();
				contents = contents.replaceAll("[-+.^:,]","");
				contents = contents.replaceAll("[\\\r\\\n]+","");
				contents=contents.replaceAll("\\s++", " ");
				contents = fileIO.stemSentence(contents, stemFlag); 
				//System.out.println(contents);
				
			
				for(int j=1;j<labeledlist.size();j++){
					String labeleds="";
					List<String> labeledlist2 = new ArrayList<String>();
					String s=labeledlist.get(j).trim();
					int index = contents.indexOf(s);
					while(index >= 0) {
					   String[] words = new String[2*windowsize+2];
					   for (int i3=0;i3<2*windowsize+2;i3++){
						   words[i3]="";
					   }
					   int i1=0, i2=index-2;
					   int templength=0;
					   while(true){
						   if(i1>=(windowsize+1)) break;
						   if(i2<0) break;
						   while(true) {
							   if(i2<0) break;
							   if((contents.charAt(i2)+"").equals(" "))
							   		{i2--;break;}
							   words[i1]+=(contents.charAt(i2)+"").toLowerCase();
							   i2--;
						   }
						words[i1] = new StringBuffer(words[i1]).reverse().toString();
						templength=i1;
						i1++;
					   }
					   
					   String tmp=words[templength];
					   words[templength]=words[0];
					   words[0]=tmp;
					   
					   int i4=i1;
					   i1=0;
					   i2=index;
					   while(true){
						   if(i1>=(windowsize+1)) break;
						   if(i2>=contents.length()) break;
						   while(true) {
							   if(i2>=contents.length()) break;
							   if((contents.charAt(i2)+"").equals(" ")) {i2++;break;}
							   words[i4+i1]+=(contents.charAt(i2)+"").toLowerCase();
							   i2++;
						   }
						   i1++;
					   }
					   for(int i3=0;i3<words.length;i3++){
					     labeleds+=words[i3]+" ";
					   }
						index = contents.indexOf(s, index+1);
					}
				    
					
					labeleds=labeleds.trim();
					for(int i3=0;i3<labeledlist.size();i3++){
						if((labeleds.toLowerCase().contains(" "+labeledlist.get(i3).toLowerCase()+" "))
								&&labeledlist.get(i3)!=s)
							labeledlist2.add(labeledlist.get(i3));
					}   
					for(int i3=0;i3<unlabeledlist.size();i3++){
						if((labeleds.toLowerCase().contains(" "+unlabeledlist.get(i3).toLowerCase()+" ")))
							labeledlist2.add(unlabeledlist.get(i3));
					}
					labeledexample.put(s,labeledlist2);
					
				}
				
				for(int j=0;j<unlabeledlist.size();j++){
					List<String> unlabeledlist2 = new ArrayList<String>();
					String unlabeleds="";
					String s=unlabeledlist.get(j).trim();
					int index = contents.indexOf(s);
					while(index >= 0) {
					   String[] words = new String[2*windowsize+2];
					   for (int i3=0;i3<2*windowsize+2;i3++){
						   words[i3]="";
					   }
					   int i1=0, i2=index-2;
					   int templength=0;
					   while(true){
						   if(i1>=(windowsize+1)) break;
						   if(i2<0) break;
						   while(true) {
							   if(i2<0) break;
							   if((contents.charAt(i2)+"").equals(" "))
							   		{i2--;break;}
							   words[i1]+=(contents.charAt(i2)+"").toLowerCase();
							   i2--;
						   }
						words[i1] = new StringBuffer(words[i1]).reverse().toString();
						templength=i1;
						i1++;
					   }
					   
					   String tmp=words[templength];
					   words[templength]=words[0];
					   words[0]=tmp;
					   
					   int i4=i1;
					   i1=0;
					   i2=index;
					   while(true){
						   if(i1>=(windowsize+1)) break;
						   if(i2>=contents.length()) break;
						   while(true) {
							   if(i2>=contents.length()) break;
							   if((contents.charAt(i2)+"").equals(" ")) {i2++;break;}
							   words[i4+i1]+=(contents.charAt(i2)+"").toLowerCase();
							   i2++;
						   }
						   i1++;
					   }
					   for(int i3=0;i3<words.length;i3++){
					     unlabeleds+=words[i3]+" ";
					   }
						index = contents.indexOf(s, index+1);
					}
				    
					
					unlabeleds=unlabeleds.trim();
					for(int i3=0;i3<labeledlist.size();i3++){
						if(unlabeleds.toLowerCase().contains(" "+labeledlist.get(i3).toLowerCase()+" "))
							unlabeledlist2.add(labeledlist.get(i3));
					}   
					for(int i3=0;i3<unlabeledlist.size();i3++){
						if((unlabeleds.toLowerCase().contains(" "+unlabeledlist.get(i3).toLowerCase()+" "))
								&&unlabeledlist.get(i3)!=s)
							unlabeledlist2.add(unlabeledlist.get(i3));
					}
					unlabeledexample.put(s,unlabeledlist2);
					
				}
			   
			    

			}
		}
		
		 for (String key: labeledexample.keySet()) {
		        softlabeledexample.put(key, new ArrayList<String>());
		    }
		 HashMap<String, Integer> score = new HashMap<String, Integer>();
		 for (String key: softlabeledexample.keySet()) {
		        score.put(key,0);
		 }
		 
		for(int i=0;i<unlabeledlist.size();i++){
			for (String key : labeledexample.keySet()) {
				List<String> labeledlist2 = new ArrayList<String>();
				labeledlist2=labeledexample.get(key);
				for(int i1=0;i1<labeledlist2.size();i1++){
					if(unlabeledlist.get(i).contains(" ")==false){
						if(labeledlist2.get(i1).contains(" ")==false){
							//check for synonyms
							String[] str1 = rw.getAllSynonyms(unlabeledlist.get(i), "n");
							String[] str2 = rw.getAllSynonyms(labeledlist2.get(i1), "n");
							int flag=0;
							outer:
							for(int i2=0;i2<str1.length;i2++){
								for(int i3=0;i3<str2.length;i3++){
									if(str1[i2].equals(str2[i3])) {
										flag=1;
										break outer;
									}
								}
							}
							if(flag==1) score.put(key, score.get(key) + 1);
						}
						else{
							if(labeledlist2.get(i1).contains(unlabeledlist.get(i))) 
								score.put(key, score.get(key) + 1);
						}
					} 
					else{
						if(labeledlist2.get(i1).contains(" ")==false){
							if(unlabeledlist.get(i).contains(labeledlist2.get(i1)))
								score.put(key, score.get(key) + 1);
						}
						else{
							String[] parts = unlabeledlist.get(i1).trim().split(" ");
							String[] parts2 = labeledlist2.get(i).trim().split(" ");
							for(int i2=0;i2<parts.length;i2++){
								for(int i3=0;i3<parts2.length;i3++){
									if(parts[i2].equals(parts2[i3])) 
										score.put(key, score.get(key) + 1);
								}
							}
						}
					}
							
				}
				
		    }
			
			String maxKey=null;
			int maxValue = -1; 
			for(HashMap.Entry<String,Integer> entry : score.entrySet()) {
			     if(entry.getValue() > maxValue) {
			         maxValue = entry.getValue();
			         maxKey = entry.getKey();
			     }
			}
			List<String> labeledlist2 = new ArrayList<String>();
			labeledlist2=softlabeledexample.get(maxKey);
			labeledlist2.add(unlabeledlist.get(i));
			softlabeledexample.put(maxKey,labeledlist2);
		}
		
		
		
		
	}
}