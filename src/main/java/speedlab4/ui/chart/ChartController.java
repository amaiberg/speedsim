package speedlab4.ui.chart;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import org.achartengine.GraphicalView;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.BasicStroke;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import speedlab4.model.XYPoint;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;


public class ChartController extends AbstractDemoChart {

    private int numPlots;
    private GraphicalView view;
    double xValMax = 0, yValMax = 0;
    XYMultipleSeriesRenderer renderer;
    private List<Queue<XYPoint>> lq;
    private double rate = .5d;
    private static final Map<Character, PointStyle> styleMap = new HashMap<Character, PointStyle>() {
        {
            put('.', PointStyle.POINT);
            put('x', PointStyle.X);
            put('X', PointStyle.X);
            put('o', PointStyle.CIRCLE);
            put('O', PointStyle.CIRCLE);
            put('o', PointStyle.DIAMOND);

        }
    };


    private XYMultipleSeriesDataset xymult;


    public void addPoints(double x, double... y) {
        for (int i = 0; i < numPlots; i++) {
            lq.get(i).offer(new XYPoint(x, y[i]));
        }
    }


    public void draw(int k) {
        new Thread(new Runnable() {
            //@Override
            public void run() {
                while (true) {
                    //  synchronized(xymult){
                    for (int i = 0; i < lq.size(); i++)
                        if (!lq.get(i).isEmpty()) {
                            XYPoint point = lq.get(i).poll();
                            double newYVal = point.y;
                            double newXVal = point.x;
                            repaint(newXVal, newYVal, i);

                        }
                    try {
                        Thread.sleep((int)(rate * 600d));
                    } catch (InterruptedException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }
        }
        ).start();
    }


    public void repaint(double xMax, double yMax, int i) {
        xValMax = (xMax > xValMax) ? xMax : xValMax;
        yValMax = (yMax > yValMax) ? yMax : yValMax;
        renderer.setXAxisMax(xValMax);
        renderer.setYAxisMax(yValMax);
        xymult.getSeriesAt(i).add(xMax, yMax);
        view.repaint();
    }

    //Generalize
    public View createChart(Context context, ChartData cData) {
        double[][] xVals = cData.xValues;
        double[][] yVals = cData.yValues;
        numPlots = cData.numPlots;
        List<double[]> x = new ArrayList<double[]>(), y = new ArrayList<double[]>();
        lq = new ArrayList<Queue<XYPoint>>();
        for (int i = 0; i < numPlots; i++) {
            lq.add(new ArrayBlockingQueue<XYPoint>(100));
            x.add(xVals[i]);
            y.add(yVals[i]);
        }

        String xTitle = cData.xTitle, yTitle = cData.yTitle, plotTitle = cData.title;
        ;
        String[] titles = cData.plotTitles;
        PointStyle[] styles = cData.styles;
        BasicStroke[] strokes = cData.strokes;
        int[] colors = cData.colors;
        String[] types = cData.types;

        renderer = buildRenderer(colors, styles);
        setChartSettings(renderer, plotTitle, xTitle, yTitle, 0, 100, 0,
                1, Color.BLACK, Color.BLACK);
        renderer.setBackgroundColor(Color.WHITE);
        renderer.setApplyBackgroundColor(true);
        renderer.setMarginsColor(Color.WHITE);
        renderer.setZoomButtonsVisible(false);
        renderer.setZoomEnabled(true);

        renderer.setXLabels(10);
        renderer.setYLabels(10);
        // renderer.getSeriesRendererAt(1).setStroke(BasicStroke.DASHED);
        int length = renderer.getSeriesRendererCount();
        for (int i = 0; i < length; i++) {
            ((XYSeriesRenderer) renderer.getSeriesRendererAt(i))
                    .setFillPoints(true);
            ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setStroke(strokes[i]);
        }
        xymult = buildDataset(titles, x, y, cData.numPlots);
        Log.d("xymult", xymult + "");

        Log.d("renderer", types + "");

        view = org.achartengine.ChartFactory.getCombinedXYChartView(context, xymult, renderer, types);
        draw(1);
        return view;
    }


    public View lab3spaceChart(Context context, double[]... vecs) {
        double[] pvec = vecs[0], ppvec = vecs[1];
        int count = Math.min(pvec.length, ppvec.length);
        this.numPlots = vecs.length;

        /*
           * for (s in 1:maxsteps) { # use totalN, which has already been computed
           * ######################### FILL IN NEXT LINE pcol = #
           * ######################### FILL IN NEXT LINE occ = #
           * ######################### FILL IN NEXT LINE occ = totalN = sum(occ)
           * pvec[s+1] = totalN/L^2 # ######################### FILL IN NEXT LINE
           * ppvec[s+1] = tvec[s+1] = tvec[s] + 1 # plot population image in first
           * figure dev.set(dev.list()[1]); image(occ,col=mycolors,add=TRUE) # add
           * line segment showing just the new population size (a line # segment
           * connecting last population size to current size)
           * dev.set(dev.list()[2]); lines(tvec[c(s,s+1)], pvec[c(s,s+1)])
           * lines(tvec[c(s,s+1)], ppvec[c(s,s+1)],lty=2,col='red') } # one final
           * image plot without "add=TRUE", to clear the history
           * dev.set(dev.list()[1]); image(occ,col=mycolors)
           */

        double xvals[] = new double[numPlots];
        for (int i = 1; i < numPlots + 1; i++)
            xvals[i - 1] = i;
        double[][] y = {pvec, ppvec};
        List<double[]> x = new ArrayList<double[]>(), values = new ArrayList<double[]>();
        lq = new ArrayList<Queue<XYPoint>>();
        for (int i = 0; i < numPlots; i++) {
            lq.add(new ArrayBlockingQueue<XYPoint>(100));
            x.add(xvals);
            values.add(y[i]);
        }

        String[] titles = {"simulated", "predicted"};
        int length = titles.length;
        PointStyle[] styles = {PointStyle.X, PointStyle.CIRCLE};
        int[] colors = {Color.BLUE, Color.RED};
        String[] types = {LineChart.TYPE, LineChart.TYPE};

        XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
        setChartSettings(renderer, "Pop. Density", "t", "Density", 0, 100, 0,
                1, Color.BLACK, Color.BLACK);
        renderer.setBackgroundColor(Color.WHITE);
        renderer.setApplyBackgroundColor(true);
        renderer.setMarginsColor(Color.WHITE);
        renderer.setZoomButtonsVisible(false);
        renderer.setZoomEnabled(true);
        renderer.setXLabels(10);
        renderer.setYLabels(10);
        renderer.getSeriesRendererAt(1).setStroke(BasicStroke.DASHED);


        length = renderer.getSeriesRendererCount();
        for (int i = 0; i < length; i++) {
            ((XYSeriesRenderer) renderer.getSeriesRendererAt(i))
                    .setFillPoints(true);
        }
        xymult = buildDataset(titles, x, values, length);

        view = org.achartengine.ChartFactory.getCombinedXYChartView(context, xymult, renderer,
                types);
        return view;
    }

    public void setRate(double rate){
        this.rate =rate;
    }

    public String getName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getDesc() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Intent executeEx42(Context context) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
