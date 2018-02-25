package ly.ux.discovery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

public class TermInfo {


	public int index;//line that this term appears in 
	public int infor;//position that this term appears in the line

	public TermInfo(int indexx, int infort) {
		index = indexx;
		infor = infort;
	}
	public TermInfo(){};


	public void SetIndex(int i) {
		index = i;
	}

	public void SetInfor(int i) {
		infor = i;
	}

	public int getIndex() {
		return index;
	}

	public int getInfor() {
		return infor;
	}

	/*	public void SetInforValue(int index, int value)	
	{// set infor in the index th space of the infor arraylist		
		infor.add(index, value);
	}

	public void SetInforValue(int value)	
	{//add the value at the end of the infor arraylist
		infor.add(value);
	}*/


	public double[] comparisionTandG(String TrueList, String GetList)
	{
		Hashtable<String, String> ht_t = new Hashtable<String, String>();
		ArrayList<String> ar_g = new ArrayList<String>();

		double[] results = new double[5];
		for(int j = 0; j < 5; j++)
			results[j] = 0;

		String[] line_t = TrueList.split("\r\n");
		for(int i = 0; i< line_t.length; i++ )
		{
			String key = line_t[i].toString();
			if(!ht_t.containsKey(key))
			{
				ht_t.put(key, "");
				results[0] = results[0] + 1;
			}
		}

		String[] line_g = TrueList.split("\r\n");
		for(int i = 0; i< line_g.length; i++ )
		{
			String key = line_g[i].toString();
			ar_g.add(key);
			results[1] = results[1] + 1;
		}


		Enumeration <String> e = ht_t.keys();
		List<String> akey = new ArrayList<String>();

		while(e.hasMoreElements()) {
			String key = (String)e.nextElement();
			akey.add((key));
		}
		Collections.sort(akey);
		for (int i = 0; i < akey.size(); i++) { 
			String t = akey.get(i).toString();

			for(int k = 0; k <ar_g.size(); k++)
			{
				String g = ar_g.get(k).toString();
				if(t.equals(g))
					results[2] = results[2] + 1;
			}
		}
		return results;
	}
	@Override
	public String toString() {
		return  index + "-" + infor + ", ";
	}


}
