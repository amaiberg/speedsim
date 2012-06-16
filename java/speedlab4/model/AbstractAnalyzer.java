package speedlab4.model;

import speedlab4.ui.chart.ChartData;

/**
 * Created by IntelliJ IDEA.
 * User: avnermaiberg
 * Date: 1/3/12
 * Time: 1:03 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractAnalyzer {


    public abstract ChartData getChartData();

    public abstract double getXPoint();

    public abstract double[] getYPoint();
}
