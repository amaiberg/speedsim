package speedlab4.browsemodels;

public class LatticeThumb {
	int imageResID, modelID, descID, fullDescrpID;
	String description; // make this a string resource
	//int index;
	
	public LatticeThumb(int imageResID, String desc, int modelResID, int fullDescrpID){
		this.imageResID = imageResID;
		this.description = desc;
		this.modelID = modelResID;
		this.fullDescrpID = fullDescrpID;
	}	
}
