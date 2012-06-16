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
package speedlab4.model.java;

import android.graphics.Color;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.renderer.BasicStroke;
import la4j.matrix.Matrix;
import la4j.matrix.Matrix.ProbDeathFunction;
import la4j.matrix.MatrixFactory;
import la4j.matrix.MatrixUtils;
import la4j.vector.Vector;
import la4j.vector.VectorFactory;
import speedlab4.model.AbstractAnalyzer;
import speedlab4.ui.chart.ChartData;
import speedlab4.params.Param;
import speedlab4.params.ParamDouble;

import java.util.HashMap;
import java.util.Map;

/**
 * Scatter demo chart.
 */
public class Random extends JAbstractSimModel {

    private Vector pvec, ppvec, tvec;
    private Matrix occ;
    private double time = 0;
    private Param test;


    public static enum State {TEST1, TEST2, INTRO}

    private final ParamDouble lambda = DP("lambda", 0.2, 0, 1), mu = DP("mu", 0.6, 0d, 1d), p0 = DP("p0", 0.75, 0, 1);

    private static final Map<State, Map<Integer, Integer>> colorMap = new HashMap<State, Map<Integer, Integer>>() {
        {
            put(State.TEST1, new HashMap<Integer, Integer>() {
                {
                    put(0, Color.GRAY);
                    put(1, Color.BLUE);
                }
            });

            put(State.TEST2, new HashMap<Integer, Integer>() {
                {
                    put(0, Color.BLACK);
                    put(1, Color.GREEN);
                }
            });

            put(State.INTRO, new HashMap<Integer, Integer>() {
                {
                    put(0, Color.parseColor("#323232"));
                    put(1, Color.parseColor("#ff0099cc"));
                }
            });
        }
    };

    private State state;

    public Random(int size, State state, Param... pd) {
        super(size, pd);
        test = pd[0];
        init(state);
        name = "Random";
    }

    public Random(State state, Param... pd) {
        super(100, pd);
        test = pd[0];
        init(state);
        name = "Random";
    }

    private void init(State state) {
        pvec = new Vector(maxsteps + 1); // vector to hold pop
        ppvec = new Vector(maxsteps + 1); // hold predicted pop
        tvec = new Vector(maxsteps + 1); // vector of time values
        this.state = state;

        analyzer = new RandomAnalyzer();

        //occ = MatrixFactory.createSquareUnifMatrix(L, p0);
        L = super.getSize();
        if (state == State.TEST2) {
            occ = MatrixFactory.createDenseMatrix(L, L);
            name = "TEST2";
        } else if (state == State.TEST1) occ = MatrixFactory.createSquareUnifMatrix(L, p0.value);
        else {
            occ = MatrixFactory.createSquareUnifMatrix(L, p0.value);
            name = "TEST1";
        }
    }


    private int L = 100, maxsteps = 100;

    /**
     * Returns the chart description.
     *
     * @return the chart description
     */
    public String getDesc() {
        return "Randomly generated values for the scatter chart";
    }

    public int getColor(int cellState) {
        return colorMap.get(state).get(cellState);
    }

    public double[][] first() {
        return occ.toArray();
    }


    public double[][] next(double time) {
        L = getSize();
        switch (state) {
            case TEST1:
                return random(time);
            case TEST2:
                return cellRandom(time);
            case INTRO:
                return intro(time);
            default:
                return intro(time);
        }
    }

    @Override
    protected void init() {
        init(state);
    }


    public double[][] intro(double time) {
        return random(time);
    }

    public double[][] cellRandom(double time) {
        this.time += time;
        for (int i = 0; i < time; i++) {
            int x = (int) (Math.random() * L);
            int y = (int) (Math.random() * L);
            occ.set(x, y, (occ.get(x, y) == 1) ? 0 : 1);
        }


        return occ.toArray();
    }


    private double[][] random(double time) {

        /*
        * xlab='time',ylab='pop density') # put the current pop. density into
        * first spot of pvec vector
        */
        this.time += time;
        double totalN = MatrixUtils.sum(occ), k = Math.pow(L, 2);
        double pt = totalN / k;
        pvec.set(0, pt);
        ppvec.set(0, pt);
        // publishProgress(occ);
        // main loop 7.4. SPATIAL MODEL WITH
        // LONG-DISTANCE DISPERSAL: MATHEMATICAL METHOD97

        for (int i = 0; i < time; i++) {
// birth: let a given site in occ be 1 if it
// already was 1, or if the site gets colonized (each site gets
// colonized independently with probability pcol)
            Vector x = VectorFactory.createRandomVector((int) (lambda.value * totalN), L);
            Vector y = VectorFactory.createRandomVector((int) (lambda.value * totalN), L);
            for (int j = 0; j < lambda.value * totalN - 1; j++)
                occ.set((int) x.get(j), (int) y.get(j), 1);
            occ = MatrixFactory.createSquareUnifMatrix(L, p0.value);
// death: proportion
// mu of the sites die, i.e. a given site in
// occ will then be 1 if it
// was already 1 and it doesn't die
            occ.transform(new ProbDeathFunction(mu.value));
// # compute new total pop size, for
// plotting # and for the next iteration
            totalN = MatrixUtils.sum(occ);
// record measured population density


        }

        android.util.Log.i("Matrix", " " + test.value);
        return occ.toArray();
    }

    public class RandomAnalyzer extends AbstractAnalyzer {

        @Override
        public ChartData getChartData() {
            int[] colors = new int[]{getColor(0), getColor(1)};
            PointStyle[] styles = new PointStyle[]{PointStyle.X, PointStyle.CIRCLE};
            BasicStroke[] strokes = new BasicStroke[]{BasicStroke.SOLID, BasicStroke.SOLID};
            String[] types = new String[]{LineChart.TYPE, LineChart.TYPE};
            String[] titles = new String[]{"CellType 1", "CellType 2"};
            ChartData chartData = new ChartData("Random", "time", "% of cells", titles, colors, styles, strokes, types, 2);
            return chartData;
        }

        @Override
        public double getXPoint() {

            return time;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public double[] getYPoint() {
            double total = occ.rows() * occ.columns();
            double sum = occ.sum();
            double first = sum / total * 100;
            double other = (total - sum) / total * 100;
            return new double[]{other, first};  //To change body of implemented methods use File | Settings | File Templates.
        }


    }


}
