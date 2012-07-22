package speedlab4.model;

import java.util.ArrayList;

import com.speedlab4.R;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import speedlab4.model.ModelController.SimThread;
import speedlab4.ui.LatticeView;
import speedlab4.ui.listeners.DrawingListener;

public class BrushController implements SurfaceHolder.Callback{
	
	LatticeView latticeView;
	LinearLayout brushView;
	AbstractSimModel simModel;
	TextView brushSizeText, brushTip;
	int brushSize;
	Point tempPoint;
	State currentDrawState;
	SeekBar brushSizeBar;
	SeekBar.OnSeekBarChangeListener brushListener;
	Handler handler;
	boolean restoredDrawState = false;
	
	public BrushController(LatticeView latticeView, LinearLayout brushView){
		this.latticeView = latticeView;
		latticeView.setOnTouchListener(new DrawingListener(this, latticeView)); // add touch listener
		latticeView.getHolder().addCallback(this);
		
		this.brushView = brushView;
		this.brushSizeText = (TextView) brushView.findViewById(R.id.brushSize);
		this.brushTip = (TextView) brushView.findViewById(R.id.brushTip);
				
		this.brushSizeBar = (SeekBar) brushView.findViewById(R.id.brushSizeBar);
		brushListener = new BrushSizeListener();
		brushSizeBar.setOnSeekBarChangeListener(brushListener);
		
		brushSize = 1;
		tempPoint = new Point();
	}
	
	public BrushController(LatticeView latticeView, LinearLayout brushView, State initDrawState){
		this(latticeView, brushView);
		if (initDrawState != null){
			restoredDrawState = true;
			currentDrawState = initDrawState;
		}
	}
	
	public void setSimModel(AbstractSimModel model){
		this.simModel = model;
		if (restoredDrawState){
			setDrawState(currentDrawState);
			restoredDrawState = false;
		}
		else
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
    	// based on brush size, add points to points list
    	if (brushSize > 1){
    		int pointsInitSize = points.size();
    		for (int i=0; i<pointsInitSize; i++){
    			for (int dx=0; dx < brushSize; dx++){
    				for (int dy=0; dy < brushSize; dy++){
    					tempPoint.x = (points.get(i).x+dx) % getLatticeSize();
    					tempPoint.y = (points.get(i).y+dy) % getLatticeSize();
    					points.add(new Point(tempPoint.x, tempPoint.y));
    				}
    			}
    		}
    	}
    	// check if other points need to be added to the list
    	if (latticeView.isPaused())
    		points = simModel.preprocessSetCell(points, state);
    	
    	// update the lattice and model
    	for (int i=0; i<points.size(); i++){
    		if (latticeView.isPaused()){
    			latticeView.setCurrentMatrixCell(points.get(i).x, points.get(i).y, state.constant);
    			simModel.setCell(points.get(i).x, points.get(i).y, state); 
    		}
    		// if model is running we don't want to modify the lattice from this (UI) thread since
    		// the simThread is also modifying it, so we tell the simThread to handle it
    		else handler.sendMessage(Message.obtain(handler, SimThread.SET_CELL, points.get(i).x, points.get(i).y));
    	}
    	
    	// repaint if paused
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
    	simModel.setDrawingState(state);
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
     * Sets the handler that this thread (UI thread) sends messages
     * to when the lattice needs to be updated while the model is
     * running
     */
    public void setHandler(Handler h){
    	this.handler = h;
    }
    
    /*
     * Callback from modelController when model is restarted
     */
    public void notifyRestart(){
    	updateBrushTipView();
    }
    
    /*
     * Resizes the brush tip based on the current lattice size
     */
    public void updateBrushTipView(){
    	int newWidth = (int) ((float)latticeView.getWidth()) / getLatticeSize();
		int newHeight = (int) ((float)latticeView.getHeight()) / getLatticeSize();
		brushTip.setWidth(newWidth*brushSize);
		brushTip.setHeight(newHeight*brushSize);
    }
    
    /*
     * Part of SurfaceHolder.Callback interface
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    	try{
    		updateBrushTipView();
    	}
    	catch (NullPointerException e){}
    }


    /*
     * Part of SurfaceHolder.Callback interface
     * Called by system when surface is first created
     */
    public void surfaceCreated(SurfaceHolder holder) {
    	try{
    	updateBrushTipView();
    	}
    	catch (NullPointerException e){}
    }
    
    /*
     * Part of SurfaceHolder.Callback interface
     * Called by system when surface is about to be destroyed
     */
    public void surfaceDestroyed(SurfaceHolder holder) {

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
