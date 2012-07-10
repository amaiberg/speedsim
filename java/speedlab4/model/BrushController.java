package speedlab4.model;

import java.util.ArrayList;

import com.speedlab4.R;

import android.graphics.Color;
import android.graphics.Point;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import speedlab4.ui.LatticeView;
import speedlab4.ui.listeners.DrawingListener;

public class BrushController {
	
	LatticeView latticeView;
	LinearLayout brushView;
	AbstractSimModel simModel;
	TextView brushSizeText, brushTip;
	int brushSize;
	Point tempPoint;
	State currentDrawState;
	SeekBar brushSizeBar;
	SeekBar.OnSeekBarChangeListener brushListener;
	
	public BrushController(LatticeView latticeView, LinearLayout brushView){
		this.latticeView = latticeView;
		latticeView.setOnTouchListener(new DrawingListener(this, latticeView)); // add touch listener
		
		this.brushView = brushView;
		this.brushSizeText = (TextView) brushView.findViewById(R.id.brushSize);
		this.brushTip = (TextView) brushView.findViewById(R.id.brushTip);
				
		this.brushSizeBar = (SeekBar) brushView.findViewById(R.id.brushSizeBar);
		brushListener = new BrushSizeListener();
		brushSizeBar.setOnSeekBarChangeListener(brushListener);
		
		brushSize = 1;
		tempPoint = new Point();
	}
	
	public void setSimModel(AbstractSimModel model){
		this.simModel = model;
		setDrawState(simModel.getStates()[0]);
		TextView drawingMode = (TextView) brushView.findViewById(R.id.drawingMode);
		drawingMode.setText("Drawing Mode: "+currentDrawState.stateName);
		updateBrushTipView();
	}
	
	/*
     * Called by DrawingListener. Commands the simModel to update
     * its matrix with the user's drawn points, then redraws the
     * lattice.
     * @param points the points touched by the user
     */
    public void setLatticeCell(ArrayList<Point> points, State state){
    	//System.out.println("setting lattice cell, lattice paused = "+latticeView.isPaused());
    	try{
    		if (brushSize == 1){
    			for (int i=0; i<points.size(); i++){
    				simModel.setCell(points.get(i), state);
    				if (latticeView.isPaused()) latticeView.setCurrentMatrixCell(points.get(i).x, points.get(i).y, state.constant);
    			}
    		}
    		else{
    			for (int i=0; i<points.size(); i++){
    				for (int dx=0; dx < brushSize; dx++){
    					for (int dy=0; dy < brushSize; dy++){
    						tempPoint.x = (points.get(i).x+dx) % getLatticeSize();
    						tempPoint.y = (points.get(i).y+dy) % getLatticeSize();
    						simModel.setCell(tempPoint, state);
    						if (latticeView.isPaused()) latticeView.setCurrentMatrixCell(tempPoint.x, tempPoint.y, state.constant);
    					}
    				}
    			}
    		}
    	} catch(ArrayIndexOutOfBoundsException e){} //ignore
        if(latticeView.isPaused())
            latticeView.resume(1, false);
    }
    
    /*
     * Returns the size of the lattice, in number of cells
     */
    public int getLatticeSize(){
    	return simModel.getSize();
    }
    
    /*
     * Sets the current drawing state of the model to the given State
     * and updates the color of the brush tip to match the new state
     */
    public void setDrawState(State state){
    	currentDrawState = state;
    	if (state.stateColor != Color.BLACK)
    		brushTip.setBackgroundColor(state.stateColor);
    	else brushTip.setBackgroundResource(R.drawable.blackborder);
    }
    
    /*
     * Returns the current drawing state of the model
     */
    public State getDrawState(){
    	return currentDrawState;
    }
    
    /*
     * Returns the current brush size
     */
    public int getBrushSize(){
    	return this.brushSize;
    }
    
    /*
     * Resizes the brush tip based on the current lattice size
     */
    public void updateBrushTipView(){
    	int newWidth = (int) ((float)latticeView.getWidth()) / getLatticeSize();
		int newHeight = (int) ((float)latticeView.getHeight()) / getLatticeSize();
		System.out.println("width = "+newWidth+" ,height = "+newHeight);
		brushTip.setWidth(newWidth*brushSize);
		brushTip.setHeight(newHeight*brushSize);
    }
    
	class BrushSizeListener implements SeekBar.OnSeekBarChangeListener{

		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			brushSize = progress+1;
			brushSizeText.setText("Brush Size: "+brushSize);
			updateBrushTipView();
		}

		public void onStartTrackingTouch(SeekBar seekBar) {
			
		}

		public void onStopTrackingTouch(SeekBar seekBar) {
	
		}
		
	}

}
