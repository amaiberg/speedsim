/**
 * Copyright (C) 2009, 2010 SC 4ViewSoft SRL
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package speedlab4.model;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import speedlab4.params.Param;
import speedlab4.ui.LatticeView;
import speedlab4.ui.chart.ChartView;
import speedlab4.ui.listeners.DrawingListener;

import java.io.Serializable;
import java.util.ArrayList;

import org.achartengine.model.XYMultipleSeriesDataset;

/**
 * Scatter demo chart.
 */
public class ModelController implements Serializable {

    transient public LatticeView latticeView;
    public AbstractSimModel simModel;
    transient private ChartView chartView;
    private AbstractAnalyzer analyzer;
    transient private SimThread runningSim;
    private Param[] paramsToUpdate;
    public boolean pause = true;
    volatile public boolean run = true;
    transient private TextView descriptionView;
    transient private BrushController drawController;
//    private double rate;

    public ModelController(LatticeView view, ChartView chartView, TextView descriptionView, LinearLayout brushView) {
        this.latticeView = view;
        this.chartView = chartView;
        this.descriptionView = descriptionView;
        this.drawController = new BrushController(latticeView, brushView);
    }

    public void setSimModel(AbstractSimModel simModel, boolean saved) {
        this.simModel = simModel;
        latticeView.setSimModel(simModel);
        if (saved)
        	latticeView.resume(1, false); //draw current, not next step
        else
        	next(1); //repaint next step in lattice 

        analyzer = simModel.getAnalyzer();
        if (!saved) // otherwise already done in resetController
        	chartView.setChart(analyzer.getChartData());
        
        descriptionView.setText(simModel.getDescriptionID());
        drawController.setSimModel(simModel);
    }

    public void setChartView(ChartView chartView) {
        this.chartView = chartView;
    }


    private void setLatticeView(LatticeView latticeView) {
        this.latticeView = latticeView;
        latticeView.setSimModel(simModel);
    }
    
    public void setDescriptionView(TextView descriptionView){
    	this.descriptionView = descriptionView;
    }

    /*
     * Called by the Activity in onCreate when the modelController
     * is being pulled from a savedInstanceState
     */
    public void resetController(AbstractSimModel simModel, ChartView chartView, LatticeView latticeView,
    		TextView descriptionView, LinearLayout brushView, double[][] currentMat, State drawState,
    		XYMultipleSeriesDataset savedChartData) {
        setChartView(chartView);
        chartView.setSavedChart(simModel.getAnalyzer().getChartData(),savedChartData);
        setDescriptionView(descriptionView);
        setLatticeView(latticeView);
        latticeView.setCurrentMatrix(currentMat);
        this.drawController = new BrushController(latticeView, brushView, drawState);
    }
    
    public BrushController getBrushController(){
    	return this.drawController;
    }
    
    public double[][] getCurrentMatrix(){
    	return latticeView.getCurrentMatrix();
    }
    
    public XYMultipleSeriesDataset getChartData(){
    	return chartView.getDataSet();
    }

    public void execute() {
        computeLatticeAndUpdate();
        // if (pause) latticeView.drawOne()
    }

    /*
     * Called by Activity's onStop method
     */
    public void stop() {
    	if (pause == false) pause();
        latticeView.stop();
        run = false;
        if (runningSim != null) runningSim.interrupt();
    }

    /*
     * Called when the Restart button is clicked
     */
    public void restart() {
        simModel.restart();
        endSimThread();
        latticeView.flush();
        computeLatticeAndUpdate();
        chartView.restart(analyzer.getChartData());
        next(1);
    }

    public void pause() {
        latticeView.pause();
        pause = true;
    }

    public void resume() {
        latticeView.resume();
        pause = false;
        if (runningSim == null) computeLatticeAndUpdate();
    }

    /*
     * Called by Activity's onDestroy method
     */
    public void destroyModel() {
        if (runningSim != null) endSimThread();
        chartView.destroyChart();
    }


    public void setParams(boolean restart, Param... params) {
        paramsToUpdate = params;
        boolean ran = !pause;
        if (ran) {
            pause();
            endSimThread();
        }
        // simModel.setParams(params);

        if (restart) {
        	if (runningSim != null) endSimThread();
            latticeView.flush();
            drawController.notifyRestart();
            simModel.restart();
            chartView.restart(analyzer.getChartData());
        }
        if (ran) {
            resume();
        } else if (restart){ 
        	if (!ran) computeLatticeAndUpdate();//make new sim thread
        	next(1);
        }
    }

    public void setParams(Param... params) {
        setParams(false, params);
    }


    public void next(int maxFrames) {
        if(pause)
            latticeView.resume(maxFrames, true);
    }

    public void computeLatticeAndUpdate() {
    	run = true;
    	runningSim = new SimThread();
        runningSim.start();
    }
    
    class SimThread extends Thread{
    	public static final int SET_CELL = 2;
        Handler handler;
        
        public SimThread(){
        	super("Running Sim Thread");
        }
        
        public Handler getHandler(){
        	return handler;
        }
        
    	public void run() {
    		if (handler == null){
    			Looper.prepare();
    			handler = new Handler(){
    				public void handleMessage(Message m){
    					if (m.what == SET_CELL)
    						simModel.setCell(m.arg1, m.arg2);
    					else if (run){
    						if (paramsToUpdate != null){
    							simModel.setParams(paramsToUpdate);
    							paramsToUpdate =null;}
    						ModelInstance mi = new ModelInstance(simModel.next(1), analyzer.getXPoint(), analyzer.getYPoint());
    						update(mi);
    					}
    					else Looper.myLooper().quit();
    				}
    			};
    			latticeView.setHandler(handler);
    			drawController.setHandler(handler);
				ModelInstance mi = new ModelInstance(simModel.first(), analyzer.getXPoint(), analyzer.getYPoint());
				update(mi);
    			Looper.loop();
    		}
        }
    }

    public void setRate(double rate) {
        double speed = 0.001d + (100d - rate) / 100d;
        latticeView.setRate(speed);
        chartView.setSpeed(speed);
    }

    private void update(ModelInstance mi) {

        latticeView.addMatrix(mi.lattice);

        chartView.addPoint(mi.x, mi.y);
    }
    
    private void endSimThread(){
    	run = false;
    	if (runningSim != null){
    		try{
    			runningSim.getHandler().sendEmptyMessage(0); // kill pill
    		} catch (NullPointerException e){
    			// looper was never initialized, so ignore
    		} 
    		runningSim.interrupt();
    		boolean retry = true;
    		while(retry){  // wait for runningSim to terminate
    			try{
    				runningSim.join();
    				retry = false;
    			}
    			catch (InterruptedException e){}
    		}
    		runningSim = null; // throw away reference to dead thread
    	}
    }

    private static String printlattice(double[][] lat){
        String val="";
        for(int i=0;i<lat.length;i++)
            for(int j=0;j<lat.length;j++)
                   val += lat[i][j];
        return val;

    }


}
