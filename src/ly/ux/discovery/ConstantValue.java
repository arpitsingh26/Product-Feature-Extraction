package ly.ux.discovery;

public class ConstantValue
{
	public final static String TITLE="";
	
	public final static String INDEX_FILEID="fileId";
	public final static String INDEX_TITLE="title";
	public final static String INDEX_CONTENT="content";
	public final static String INDEX_CLASS="class";
	
	public final static String INDEX_DIR="h:/Program/project/MCV1/Index/rawdata";
	public final static String INDEX_DIR_BAK="h:/Program/project/MCV1/Index/rawdata1";
	
	public final static String FILE_CLASSFICATION="h:/Program/project/MCV1/Classification.txt";
	
	public final static int FILE_SIZE=1434;
	public final static int CONNECT_TERM_SIZE=15;
	
	public final static int SELECTED_CONNECT_TERM_NUM=3;
	
	public static int NUM_OF_WORD = 0;
	
	public final static int MFS_G=2;
	public final static int MFS_Support=2;
	
	public final static int TERM_DOCFREQ = 0;//arraylist index stars from 0: store term freq
	public final static int TERM_REMAIN_FREQ = 1;//index 1:store the (term freq - term related MFS freq) 
	public final static int TERM_POSITION_LINE = 3;// the line position of the term are stored in the 3 and after space of the arraylist
	
	
	public final static String DownloadFolder="I://Experiment//HP inkjet printhead//";
	public final static String URL_root="http://www.freepatentsonline.com/";
	public final static String Path_itemKeyword="I://Experiment//Data//itemKeywordList.txt";
	public final static String Path_excel="I://Experiment//patentList.xls";
	
	// items of patents (21)
	public final static String Title = "Title:";
	public final static String PNum = "United States Patent";
	public final static String Abstract = "Abstract:";
	public final static String Inventors = "Inventors:";
	public final static String Application_Number = "Application Number:";
	public final static String Publication_Date = "Publication Date:";
	public final static String Filing_Date = "Filing Date:";
	public final static String Assignee = "Assignee:";
	public final static String Primary_Class = "Primary Class:";
	public final static String International_Classes = "International Classes:";
	public final static String Field_of_Search = "Field of Search:";
	public final static String US_Patent_References = "US Patent References:";
	public final static String Foreign_References = "Foreign References:";
	public final static String Other_References = "Other References:";
	public final static String Primary_Examiner = "Primary Examiner:";
	public final static String Assistant_Examiner = "Assistant Examiner:";
	public final static String Claims = "Claims:";
	public final static String Description = "Description:";
	public final static String BACKGROUND = "BACKGROUND";
	public final static String SUMMARY_OF_INVENTION = "SUMMARY OF THE INVENTION";
	public final static String BRIEF_DESCRIPTION_DRAWINGS = "BRIEF DESCRIPTION OF THE DRAWINGSTHE DRAWINGS";
	
	public final static String PatentItemStream = "Title:" +
			"United States Patent:" +
			"Abstract:" +
			"Inventors:" +
			"Application Number:" +
			"Publication Date:" +
			"Filing Date:" +
			"Assignee:Primary Class:" +
			"International Classes:" +
			"Field of Search:" +
			"US Patent References:" +
			"Foreign References:" +
			"Other References:" +
			"Primary Examiner:Assistant Examiner:" +
			"Claims:" +
			"Description:" +
			"BACKGROUND:" +
			"SUMMARY OF THE INVENTIONBRIEF:" +
			"DISCLOSURE:" +
			"BRIEF DESCRIPTION:" +
			"TECHNICAL FIELD:" + "DETAILED:" + 
			"View Patent Images:" +
			"Other Classes:" +
			"Export Citation:";
}