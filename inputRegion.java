
public class inputRegion implements Comparable<inputRegion> {
	private String chromosome;
	private int startPosition;
	private int endPosition;
	
	inputRegion(String chromosome, int startPosition, int endPosition){
		this.chromosome = chromosome;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
	}
	
	public int compareTo(inputRegion o) {
		return this.startPosition - o.startPosition;
	}

	public int getStart(){
		return this.startPosition;
	}
	
	public int getEnd(){
		return this.endPosition;
	}
	
	public String getChr(){
		return this.chromosome;
	}
	
	public int getChrAsInt(){
		if(chromosome.equals("X")){
			return 23;
		} else {
			return Integer.parseInt(chromosome);
		}
	}
	
}
