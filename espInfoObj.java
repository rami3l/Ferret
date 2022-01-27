public class espInfoObj{
	private String chromosome, snpName, refAllele, altAllele;
	private int position;
	private double EAFreq, AAFreq;
	
	espInfoObj(String chromosome, int position, String snpName, String refAllele, String altAllele, double EAFreq, double AAFreq){
		this.chromosome = chromosome;
		this.snpName = snpName;
		this.position = position;
		this.EAFreq = EAFreq;
		this.AAFreq = AAFreq;
		this.refAllele = refAllele;
		this.altAllele = altAllele;
	}
	
	espInfoObj(String chromosome, String position, String snpName, String refAllele, String altAllele, String refEA, String altEA, String refAA, String altAA){
		this.chromosome = chromosome;
		this.snpName = snpName;
		this.position = Integer.parseInt(position);
		double totalEA = Integer.parseInt(refEA) + Integer.parseInt(altEA), totalAA = Integer.parseInt(refAA) + Integer.parseInt(altAA);
		// Note that totalEA and totalAA are double so no need to worry about auto casting/floor to int
		this.EAFreq = (Integer.parseInt(refEA)/totalEA);
		this.AAFreq = (Integer.parseInt(refAA)/totalAA);
		this.refAllele = refAllele;
		this.altAllele = altAllele;
	}

	public String getChr(){
		return this.chromosome;
	}
	
	public int getChrAsInt(){
		return Integer.parseInt(chromosome);
	}
	
	public String getSNP(){
		return this.snpName;
	}
	
	public String getRefAllele(){
		return this.refAllele;
	}
	
	public String getAltAllele(){
		return this.altAllele;
	}
	
	public int getPos(){
		return this.position;
	}
	
	public double getEAFreq(){
		return this.EAFreq;
	}

	public double getAAFreq(){
		return this.AAFreq;
	}
	
	public double getEAFreqAlt(){
		return (1.0 - this.EAFreq);
	}

	public double getAAFreqAlt(){
		return (1.0 - this.AAFreq);
	}
	
}