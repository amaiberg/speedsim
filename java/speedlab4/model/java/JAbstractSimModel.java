package speedlab4.model.java;

import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.renderer.BasicStroke;
import speedlab4.model.AbstractAnalyzer;
import speedlab4.ui.chart.ChartData;
import speedlab4.model.AbstractSimModel;
import speedlab4.params.Param;
import speedlab4.params.ParamDouble;
import speedlab4.params.ParamGroupDouble;
import speedlab4.params.ParamInteger;
import speedlab4.params.ParamLinkedDouble;

public abstract class JAbstractSimModel extends AbstractSimModel<ParamInteger, ParamDouble> {

    public JAbstractSimModel(int size, Param... params) {
        super(size, params);
        analyzer = new StandInAnalyzer();
    }

    public JAbstractSimModel(int size) {
        super(size);
        analyzer = new StandInAnalyzer();
    }


    /*
     * Returns a new ParamInteger object
     */
    public ParamInteger getParamInteger(int value, String name, int min, int max) {
        return new ParamInteger(name, value, min, max);
    }

    /*
     * Returns a new ParamInteger object
     */
    @Override
    public ParamInteger getParamInteger(int value, String name, int min, int max, String description, boolean reqRestart) {
        return new ParamInteger(name, value, min, max, description, reqRestart);
    }

    /*
     * Returns a new ParamDouble object
     */
    @Override
    public ParamDouble getParamDouble(String name, double value, double min, double max, String description, boolean reqRestart) {
        return new ParamDouble(name, value, min, max, description, reqRestart);
    }
    
    /*
     * Returns a new ParamLinkedDouble object
     */
    public ParamLinkedDouble getParamLinkedDouble(String name, double value, double min, double max, String description, boolean reqRestart) {
        return new ParamLinkedDouble(name, value, min, max, description, reqRestart);
    }

    /*
     * Returns a new ParamGroupDouble object
     */
    public ParamGroupDouble getParamGroupDouble(String name, ParamDouble... params){
    	return new ParamGroupDouble(name, params);
    }

    public class StandInAnalyzer extends AbstractAnalyzer {

        @Override
        public ChartData getChartData() {
            int[] colors = new int[]{getColor(0), getColor(1)};
            PointStyle[] styles = new PointStyle[]{PointStyle.X, PointStyle.CIRCLE};
            BasicStroke[] strokes = new BasicStroke[]{BasicStroke.SOLID, BasicStroke.SOLID};
            String[] types = new String[]{LineChart.TYPE, LineChart.TYPE};
            String[] titles = new String[]{"CellType 1", "CellType 2"};
            ChartData chartData = new ChartData("Temp", "time", "% of cells", titles, colors, styles, strokes, types, 2);
            return chartData;
        }

        @Override
        public double getXPoint() {

            return 0.0d;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public double[] getYPoint() {
            return new double[]{0.0d, 0.0d};
        }
    }
}
