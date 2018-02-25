package ly.ux.discovery;

import java.util.ArrayList;
import java.util.List;

public class ContentSplit {

	private List<String> itemList = new ArrayList<String>();// some items that needed 
	private List<String> itemSet = new ArrayList<String>(); //the whole list of items

	public String contentSplitandSave(String content, String DocPath, String patentNum)
	{
		itemSetCreator();
		String[] linesOfContents = content.split("\r\n");
		int k = 0; // the line number of contents
		String saveContent = "";
		String itemContent = "";
		String exceContent = "";
		exceContent = exceContent + patentNum + ".\r\n";
		int flag = 0;
		for(int i = 0; i < itemList.size(); i++)
		{
			k = 0;
			itemContent = "";
			String item_k = itemList.get(i);

			while(k < linesOfContents.length)
			{
				String tmp = linesOfContents[k].toString();
				////System.out.println("tmp:------" + tmp);
				if( tmp.indexOf(item_k) >= 0 )	{
					// it is the item keywords in the itemList
					//itemContent = itemContent + tmp + ".\r\n";
					k++ ;
					while( k < linesOfContents.length )
					{
						tmp = linesOfContents[k].toString();
						////System.out.println("tmp:------" + tmp);
						if (itemFind(tmp))
						{
							////System.out.println(item_k + "------------item Content------------------");
							////System.out.println(itemContent);
							saveContent = saveContent + item_k + "------------item Content------------------\r\n" + itemContent+"\r\n";
							exceContent = exceContent + itemContent+ ".\r\n";						
							flag = 1;
							break;
						}
						else{
							itemContent = itemContent + tmp + ".\r\n";
							k++;
						}
						if(k == (linesOfContents.length )){
							saveContent = saveContent + item_k + "------------item Content------------------\r\n" + itemContent+"\r\n";
							exceContent = exceContent + itemContent+ ".\r\n";						
							flag = 1;
							break;
						}
					}					
				}
				else// does not contain any item keywords
					k++;
				if(flag > 0)
				{
					flag = 0;
					k--;
					break;
				}
			}
		}
		if (DocPath.length()>0)
		{
			FileIO sa = new FileIO();
			sa.WriteContent(saveContent, DocPath);
		}
		return exceContent;
	}

	public void isItemKeywork(String content, String DocPath)
	{
		String[] linesOfContents = content.split("\r\n");
		String saveKeyworkList = "";
		for(int i = 0; i<linesOfContents.length; i++)
		{
			String tmp = linesOfContents[i].toString();
			{
				if(tmp.length()<= 50){
					saveKeyworkList = saveKeyworkList + tmp + "\r\n";
				}
			}
		}

		FileIO sa = new FileIO();
		sa.WriteContent(saveKeyworkList, DocPath);
	}



	private boolean itemFind(String str) 
	{//find whether str contains any item keywords. find in the itemSet
		for(int i = 0; i < itemSet.size(); i++)
		{
			String item_i = itemSet.get(i);
			if ( str.indexOf(item_i) == 0 )
				return true;
		}
		return false; 
	}

	private boolean itemFindinList(String str, String itemKey) 
	{//find whether str contains the specified item keywords "itemKey". Find in the itemList
		if ( str.indexOf(itemKey) == 0 )
			return true;
		else
			return false; 
	}

	private void itemSetCreator() 
	{
		//		itemSet.add(ConstantValue.Title);
		//		itemSet.add(ConstantValue.PNum);
		//		itemSet.add(ConstantValue.Abstract);
		//		itemSet.add(ConstantValue.Inventors);
		//		itemSet.add(ConstantValue.Application_Number);
		//		itemSet.add(ConstantValue.Publication_Date);
		//		itemSet.add(ConstantValue.Filing_Date);
		//		itemSet.add(ConstantValue.Assignee);
		//		itemSet.add(ConstantValue.Primary_Class);
		//		itemSet.add(ConstantValue.International_Classes);
		//		itemSet.add(ConstantValue.Field_of_Search);
		//		itemSet.add(ConstantValue.US_Patent_References);
		//		itemSet.add(ConstantValue.Foreign_References);
		//		itemSet.add(ConstantValue.Other_References);
		//		itemSet.add(ConstantValue.Primary_Examiner);
		//		itemSet.add(ConstantValue.Assistant_Examiner);
		//		itemSet.add(ConstantValue.Claims);
		//		itemSet.add(ConstantValue.Description);
		//		itemSet.add(ConstantValue.BACKGROUND);
		//		itemSet.add(ConstantValue.SUMMARY_OF_INVENTION);
		//		itemSet.add(ConstantValue.BRIEF_DESCRIPTION_DRAWINGS);
		//		itemSet.add("View Patent Images:");
		//		itemSet.add("Other Classes:");
		//		itemSet.add("Export Citation:");

		String str = ConstantValue.PatentItemStream;
		String[] itemTmp = str.split(":");
		for(int i = 0; i<itemTmp.length; i++ )
		{
			itemSet.add(itemTmp[i].toString());
		}

	}

	public void itemListCreator(int intial) 
	{
		itemList.add(ConstantValue.Title);
		itemList.add(ConstantValue.PNum);
		itemList.add(ConstantValue.Abstract);
		//		itemList.add(ConstantValue.Inventors);
		//		itemList.add(ConstantValue.Application_Number);
		itemList.add(ConstantValue.Publication_Date);
		itemList.add(ConstantValue.Filing_Date);
		//		itemList.add(ConstantValue.Assignee);
		itemList.add(ConstantValue.Primary_Class);
		//		itemList.add(ConstantValue.International_Classes);
		itemList.add(ConstantValue.Field_of_Search);
		//		itemList.add(ConstantValue.US_Patent_References);
		//		itemList.add(ConstantValue.Foreign_References);
		//		itemList.add(ConstantValue.Other_References);
		//		itemList.add(ConstantValue.Primary_Examiner);
		//		itemList.add(ConstantValue.Assistant_Examiner);
		itemList.add(ConstantValue.Claims);
		//		itemList.add(ConstantValue.BACKGROUND);
		//		itemList.add(ConstantValue.SUMMARY_OF_INVENTION);
		//		itemList.add(ConstantValue.BRIEF_DESCRIPTION_DRAWINGS);
	}



	public void itemListCreator(String str) 
	{//the item of patent that you would like to extract, the split format "@"

		String[] itemTmp = str.split("@");
		for(int i = 0; i<itemTmp.length; i++ ){
			itemList.add(itemTmp[i].toString());
		}
	}

}
