package speedlab4.ui.chart;

import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.renderer.BasicStroke;

public class ChartData {

    public String title, xTitle, yTitle;
    public double xValues[][], yValues[][];
    public String plotTitles[], types[];
    public int colors[], numPlots;
    public PointStyle styles[];
    public BasicStroke strokes[];
    public double stepSize;

    // //
    public ChartData(String title, int numPlots) {
        this.title = title;
        this.numPlots = numPlots;
    }

    public ChartData(String title, String xTitle, String yTitle, int numPlots) {
        this(title, numPlots);
        this.xTitle = xTitle;
        this.yTitle = yTitle;
    }

    public ChartData(String title, String xTitle, String yTitle, String[] legendTitles, int[] colors, PointStyle[] styles, BasicStroke[] strokes, String[] types, int numPlots) {
        this(title, xTitle, yTitle, numPlots);
        this.plotTitles = legendTitles;
        this.colors = colors;
        this.styles = styles;
        this.strokes = strokes;
        this.types = types;
        this.xValues = new double[numPlots][0];
        this.yValues = new double[numPlots][0];
    }

    public void setVals(double[][] xValues, double[][] yValues) {
        if (xValues.length == numPlots && yValues.length == numPlots) {
            this.xValues = xValues;
            this.yValues = yValues;
        } else System.err.println("x and y datasets must be of the same length");
    }

    public void setTitles(String[] titles) {
        if (title.length() == numPlots)
            this.plotTitles = titles;
        else System.err.println("titles must be equal to the number of datasets");
    }

    public void setColors(int[] colors) {
        if (colors.length == numPlots)
            this.colors = colors;
        else System.err.println("colors must be equal to the number of datasets");
    }

    public void setStyles(PointStyle[] styles) {
        if (styles.length == numPlots)
            this.styles = styles;
        else System.err.println("colors must be equal to the number of datasets");
    }

    public void setTypes(String[] types) {
        if (types.length == numPlots)
            this.styles = styles;
        else System.err.println("colors must be equal to the number of datasets");
    }

    public void setStrokes(BasicStroke[] strokes) {
        if (strokes.length == numPlots)
            this.strokes = strokes;
        else System.err.println("colors must be equal to the number of datasets");
    }

    public static ChartData dummyChartData() {
        ChartData cdata = new ChartData("Default Title", "Default x", "Default y", new String[]{"Plot 1"}, new int[]{android.graphics.Color.BLACK}, new PointStyle[]{PointStyle.POINT}, new BasicStroke[]{BasicStroke.SOLID}, new String[]{LineChart.TYPE}, 1);
        cdata.setVals(new double[][]{{0}}, new double[][]{{0}});
        return cdata;
    }


}
