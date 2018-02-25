package ly.ux.discovery;

public class PMIforDoc {

	
	
	public String docName;//line
	public double pmi;//position
	
	public PMIforDoc(String i, double j) {
		docName = i;
		pmi = j;
	}
	public PMIforDoc(){};
	
	
	public void SetDocName(String i) {
		docName = i;
	}
	
	public void SetPmi(double j) {
		pmi = j;
	}
	
	public String getDocName() {
		return docName;
	}
	
	public double getPmi() {
		return pmi;
	}
	

	
	

}
