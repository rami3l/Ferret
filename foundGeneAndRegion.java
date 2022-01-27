
public class foundGeneAndRegion {
	private String geneNamesFound;
	private inputRegion[] locationOfFoundGenes;
	private Boolean allFound;
	
	foundGeneAndRegion(String geneNamesFound, inputRegion[] locationOfFoundGenes, Boolean allFound){
		this.geneNamesFound = geneNamesFound; 
		this.locationOfFoundGenes = locationOfFoundGenes;
		this.allFound = allFound;
	}
	
	public String getFoundGenes(){
		return this.geneNamesFound;
	}
	
	public inputRegion[] getInputRegionArray(){
		return this.locationOfFoundGenes;
	}
	
	public Boolean getFoundAllGenes(){
		return this.allFound;
	}
	
}
