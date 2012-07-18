package speedlab4.ui.chart;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import org.achartengine.model.XYMultipleSeriesDataset;

/**
 * Created by IntelliJ IDEA.
 * User: avnermaiberg
 * Date: 1/2/12
 * Time: 3:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChartView extends FrameLayout{

    private View curChart;
    private ChartController chartController;

    public ChartView(Context context) {
        super(context);
        chartController = new ChartController();
        curChart = chartController.createChart(context, ChartData.dummyChartData(), null, false);
        this.addView(curChart);
    }

    public ChartView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        chartController = new ChartController();
        curChart = chartController.createChart(context, ChartData.dummyChartData(), null, false);
        this.addView(curChart);
        //     chartController.draw(1);
    }

    public void setChart(ChartData cData) {
        this.removeView(curChart);
        chartController = new ChartController();
        curChart = chartController.createChart(this.getContext(), cData, null, true);
        this.addView(curChart);
        //   chartController.draw(1);
    }
    
    public void setSavedChart(ChartData cData, XYMultipleSeriesDataset savedData){
    	 this.removeView(curChart);
         chartController = new ChartController();
         curChart = chartController.createChart(this.getContext(), cData, savedData, true);
         this.addView(curChart);
    }
    
    public XYMultipleSeriesDataset getDataSet(){
    	return chartController.getDataSet();
    }
    
    public void restart(ChartData c){
    	setChart(c);
    }
    
    public void destroyChart(){
    	chartController.endChartDrawThread();
    }

    public void addPoint(double x, double[] y) {
        chartController.addPoints(x, y);
        // chartController.draw(1);
    }

    public void setSpeed(double rate){
        chartController.setRate(rate);
    }


}
