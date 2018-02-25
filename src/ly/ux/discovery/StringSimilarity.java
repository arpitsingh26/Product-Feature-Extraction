package ly.ux.discovery;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class StringSimilarity {

    /**
     * gets the similarity of the two strings using CosineSimilarity.
     *
     * @param string1
     * @param string2
     * @return a value between 0-1 of the similarity
     */
    public float cosineSimilarity(String string1, String string2) {
        final ArrayList<String> str1Tokens = getTerms(string1);
        final ArrayList<String> str2Tokens = getTerms(string2);

        final Set<String> allTokens = new HashSet<String>();
        allTokens.addAll(str1Tokens);
        final int termsInString1 = allTokens.size();
        final Set<String> secondStringTokens = new HashSet<String>();
        secondStringTokens.addAll(str2Tokens);
        final int termsInString2 = secondStringTokens.size();

        //now combine the sets
        allTokens.addAll(secondStringTokens);
        final int commonTerms = (termsInString1 + termsInString2) - allTokens.size();

        //return CosineSimilarity
        return (float) (commonTerms) / (float) (Math.pow((float) termsInString1, 0.5f) * Math.pow((float) termsInString2, 0.5f));
    }
    
	private ArrayList<String> getTerms(String str) {
		//get term from the sentence. concern what kind of operations are needed in text preprocessing. stop word....
		StringTokenizer st = new StringTokenizer(str);
		ArrayList<String> al = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			al.add(st.nextToken().trim());
		}
		return al;
	}

	public float jaccardSimilarity(String string1, String string2){

        final ArrayList<String> str1Tokens = getTerms(string1);
        final ArrayList<String> str2Tokens = getTerms(string2);

        final Set<String> allTokens = new HashSet<String>();
        allTokens.addAll(str1Tokens);
        final int termsInString1 = allTokens.size();
        final Set<String> secondStringTokens = new HashSet<String>();
        secondStringTokens.addAll(str2Tokens);
        final int termsInString2 = secondStringTokens.size();

        //now combine the sets
        allTokens.addAll(secondStringTokens);
        final int commonTerms = (termsInString1 + termsInString2) - allTokens.size();

        //return CosineSimilarity
        return (float) (commonTerms) / (float) ( termsInString1 + termsInString2 );
	}
	
}
