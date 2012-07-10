package speedlab4.model;

import java.io.Serializable;

/*
 * Represents one state in a model
 */
public class State implements Serializable{
	
	public String stateName;
	public int stateColor;
	public int constant;
	
	public State(String name, int color, int constant){
		stateName = name;
		stateColor = color;
		this.constant = constant;
	}
}