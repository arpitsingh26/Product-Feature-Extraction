package ly.ux.discovery;

public class termWeight {

	String term = "";
	private double termFreq = 0;
	private double nfy = 0; // n(f, Y) number of cooccurrance of the feature and sentiment
	private double nfy_value = 0; // calculate n(f,Y) based on term distance
	private double n_nf_y = 0;
	private double n_f_ny = 0;
	private double final_wd = 0;
	private double final_w = 0;
	
	public termWeight() {
		super();
		this.term = "";
		this.termFreq = 0;
		this.nfy = 0;
		this.nfy_value = 0;
		this.n_nf_y = 0;
		this.n_f_ny = 0;
		this.final_w = 0;
	}

	public termWeight(String term, double termFreq) {
		super();
		this.term = term;
		this.termFreq = termFreq;
		this.nfy = 0;
		this.nfy_value = 0;
		this.n_nf_y = 0;
		this.n_f_ny = 0;
		this.final_w = 0;
	}

	
	
	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public double getTermFreq() {
		return termFreq;
	}

	public void setTermFreq(double termFreq) {
		this.termFreq = termFreq;
	}

	public double getNfy() {
		return nfy;
	}

	public void setNfy(double nfy) {
		this.nfy = nfy;
	}

	public double getNfy_value() {
		return nfy_value;
	}

	public void setNfy_value(double nfy_value) {
		this.nfy_value = nfy_value;
	}

	public double getN_nf_y() {
		return n_nf_y;
	}

	public void setN_nf_y(double n_nf_y) {
		this.n_nf_y = n_nf_y;
	}

	public double getN_f_ny() {
		return n_f_ny;
	}

	public void setN_f_ny(double n_f_ny) {
		this.n_f_ny = n_f_ny;
	}

	public double getFinal_w() {
		return final_w;
	}
	public double getFinal_wd() {
		return final_wd;
	}
	public void setFinal_w(double final_w) {
		this.final_w = final_w;
	}
	
	public void setFinal_wd(double final_w) {
		this.final_wd = final_w;
	}

}
