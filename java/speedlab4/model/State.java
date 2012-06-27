package speedlab4.model;

import java.io.Serializable;

/*
 * Represents one state in a model
 */
public class State implements Serializable{
	
	public String stateName;
	public int stateColor;
	
	public State(String name, int color){
		stateName = name;
		stateColor = color;
	}
}