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

import android.util.Log;
import speedlab4.params.Param;
import speedlab4.ui.LatticeView;
import speedlab4.ui.chart.ChartView;

import java.io.Serializable;

/**
 * Scatter demo chart.
 */
public class ModelController implements Serializable {


    transient public LatticeView latticeView;
    public AbstractSimModel simModel;
    transient private ChartView chartView;
    private AbstractAnalyzer analyzer;
    transient private Thread runningSim; 
    private Param[] paramsToUpdate;
    public boolean pause = true;
    volatile public boolean run = true;
//    private double rate;

    public ModelController(LatticeView view, ChartView chartView) {
        this.latticeView = view;
        this.chartView = chartView;
    }

    public void setSimModel(AbstractSimModel simModel) {
        this.simModel = simModel;
        latticeView.setSimModel(simModel);

        analyzer = simModel.getAnalyzer();
        chartView.setChart(analyzer.getChartData());
    }

    public void setChartView(ChartView chartView) {
        this.chartView = chartView;
    }


    public void setLatticeView(LatticeView latticeView) {
        this.latticeView = latticeView;
        latticeView.setSimModel(simModel);
    }

    /*
     * Called by the Activity in onCreate when the modelController
     * is being pulled from a savedInstanceState
     */
    public void resetController(AbstractSimModel abstractSimModel, ChartView chartView, LatticeView latticeView) {
        setChartView(chartView);
        setLatticeView(latticeView);
        setSimModel(abstractSimModel);
    }


    public void execute() {
        computeLatticeAndUpdate();
        // if (pause) latticeView.drawOne()
    }

    /*
     * Called by Activity's onStop method
     */
    public void stop() {
    	pause();
        latticeView.stop();
        run = false;
        runningSim.interrupt();
    }

    public void restart() {
        simModel.restart();
        next(1);
    }

    public void pause() {
        latticeView.pause();
        pause = true;
    }

    public void resume() {
        latticeView.resume();
        pause = false;
    }

    /*
     * Called by Activity's onDestroy method
     */
    public void destroyModel() {
        if (runningSim != null){
        	run = false; //
            runningSim.interrupt();
        }
    }


    public void setParams(boolean restart, Param... params) {
        paramsToUpdate = params;
        boolean ran = !pause;
        if (ran) {
            pause();
            run = false; 
            runningSim.interrupt();
        }
        // simModel.setParams(params);

        if (restart) {
            latticeView.flush();
            simModel.restart();
        }
        if (ran) {
            resume();
            computeLatticeAndUpdate(); 
        } else if (restart) next(1);
    }

    public void setParams(Param... params) {
        setParams(false, params);
    }


    public void next(int maxFrames) {
        if(pause)
            latticeView.resume(maxFrames);
    }

    public void computeLatticeAndUpdate() {
    	run = true;
        runningSim = new Thread("Running Sim Thread") {
            public void run() {
                while (run) { 
                    if (paramsToUpdate != null){
                        simModel.setParams(paramsToUpdate);
                        paramsToUpdate =null;}
                    ModelInstance mi = new ModelInstance(simModel.next(1), analyzer.getXPoint(), analyzer.getYPoint());
                    update(mi);
                	}
            }
        };
        runningSim.start();

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

    private static String printlattice(double[][] lat){
        String val="";
        for(int i=0;i<lat.length;i++)
            for(int j=0;j<lat.length;j++)
                   val += lat[i][j];
        return val;

    }


}
