package speedlab4.model.java;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.renderer.BasicStroke;

import com.speedlab4.R;
import android.graphics.Color;
import android.graphics.Point;
import speedlab4.model.AbstractAnalyzer;
import speedlab4.model.State;
import speedlab4.model.java.CommunityVaccinated.CommunityVaccAnalyzer;
import speedlab4.params.Param;
import speedlab4.params.ParamDouble;
import speedlab4.params.ParamInteger;
import speedlab4.ui.chart.ChartData;

public class DynamicLandscape extends JAbstractSimModel {
	
	private int numOcc, numSuitable, numUnsuitable, numUpdatesPerStep,
		newx, newy, timestep;
	private volatile double cells[][];
    private int occGrid[][], occX[], occY[];
    private int unsuitableGrid[][], unsuitableX[], unsuitableY[];
    private Random random;
    
    private final ParamInteger initDensity = IP("Initial population density", 25, 0, 100, "Initial population density", true);
    private final ParamDouble birthRate = DP("birth rate", 2, 0, 20);
    private final ParamDouble deathRate = DP("death rate", 1, 0, 20);
    private final ParamDouble regrowthRate = DP("regrowth rate", 0, 0, 20);
    private final ParamDouble disturbanceRate = DP("disturbance rate", 0, 0, 20);
    private final ParamInteger blockXSize = IP("block x-size", 1, 1, 20);
    private final ParamInteger blockYSize = IP("block y-size", 1, 1, 20);
    private final ParamDouble alpha = DP("long distance dispersal", 0, 0, 1);
   // private final ParamInteger dispersalScale = IP("dispersal radius", -1, -1, 20);
    
	private static final int EMPTY = -1, UNOCC_GOODSITE = 0, BADSITE = 1, OCC_GOODSITE = 2;
	
	private static final State[] states = { new State("Unoccupied suitable site", Color.BLACK, 0),
									new State("Unsuitable site", Color.GRAY, 1),
									new State("Occupied suitable site", Color.GREEN, 2) };
	
	public DynamicLandscape(Param... pd){
		super(100, R.string.DynamicLandModel, pd);
		name = "Dynamic Landscape";
		int size = super.getSize();
		occX = new int[size*size];
		occY = new int[size*size];
		unsuitableX = new int[size*size];
		unsuitableY = new int[size*size];
		random = new Random();
		init();
	}

	@Override
	protected void init() {
		analyzer = new DynamicLandscapeAnalyzer();
		int size = super.getSize();
		cells = new double[size][size];
		occGrid = new int[size][size];
		unsuitableGrid = new int[size][size];
		numUpdatesPerStep = size*size/8;
		initLandscape();
		initPop();
	}

	@Override
	public double[][] next(double time) {
		step();
		System.out.println("Num unsuitable: "+numUnsuitable+" ,Num suitable: "+numSuitable);
		return cells;
	}

	@Override
	public int getColor(int state) {
		return states[state].stateColor;
	}
	
	@Override
	public State[] getStates(){
		return states;
	}
	
	@Override
    public void setCell(Point point, State state){
		if (state.constant == UNOCC_GOODSITE){
			if (cells[point.x][point.y] == OCC_GOODSITE){ // kill pop
				int index = occGrid[point.x][point.y];
				int lastX = occX[numOcc-1];
				int lastY = occY[numOcc-1];
				occX[index] = lastX;
				occY[index] = lastY;
				occGrid[lastX][lastY] = index;
				numOcc--;
				cells[point.x][point.y] = UNOCC_GOODSITE;
			}
			else if (cells[point.x][point.y] == BADSITE){ // turn bad site good
				int index = unsuitableGrid[point.x][point.y];
				int lastX = unsuitableX[numUnsuitable-1];
				int lastY = unsuitableY[numUnsuitable-1];
				unsuitableX[index] = lastX;
        		unsuitableY[index] = lastY;
        		unsuitableGrid[lastX][lastY] = index;
        		unsuitableGrid[point.x][point.y] = -1;
        		numSuitable++;
        		numUnsuitable--;
        		cells[point.x][point.y] = UNOCC_GOODSITE;
			}
		}
		else if (state.constant == BADSITE){
			if (cells[point.x][point.y] == OCC_GOODSITE){ // kill pop
				int index = occGrid[point.x][point.y];
				int lastX = occX[numOcc-1];
				int lastY = occY[numOcc-1];
				occX[index] = lastX;
				occY[index] = lastY;
				occGrid[lastX][lastY] = index;
				numOcc--;
			}
			if (cells[point.x][point.y] != BADSITE){ // turn site bad
				unsuitableX[numUnsuitable] = point.x;
				unsuitableY[numUnsuitable] = point.y;
				unsuitableGrid[point.x][point.y] = numUnsuitable;
				numSuitable--;
				numUnsuitable++;
				cells[point.x][point.y] = BADSITE;
			}
		}
		else if (state.constant == OCC_GOODSITE){
			if (cells[point.x][point.y] == BADSITE){ // turn bad site good
				int index = unsuitableGrid[point.x][point.y];
				int lastX = unsuitableX[numUnsuitable-1];
				int lastY = unsuitableY[numUnsuitable-1];
				unsuitableX[index] = lastX;
        		unsuitableY[index] = lastY;
        		unsuitableGrid[lastX][lastY] = index;
        		unsuitableGrid[point.x][point.y] = -1;
        		numSuitable++;
        		numUnsuitable--;
			}
			if (cells[point.x][point.y] != OCC_GOODSITE){ // place pop
				occX[numOcc] = point.x;				
				occY[numOcc] = point.y;
				occGrid[point.x][point.y] = numOcc;
				numOcc++;
				cells[point.x][point.y] = OCC_GOODSITE;
			}
		}
    }

	@Override
	public double[][] first() {
		return cells;
	}
	
	public class DynamicLandscapeAnalyzer extends AbstractAnalyzer implements Serializable {

        @Override
        public ChartData getChartData() {
            int[] colors = new int[]{getColor(0), getColor(1), getColor(2)};
            PointStyle[] styles = new PointStyle[]{PointStyle.X, PointStyle.CIRCLE, PointStyle.CIRCLE};
            BasicStroke[] strokes = new BasicStroke[]{BasicStroke.SOLID, BasicStroke.SOLID, BasicStroke.SOLID};
            String[] types = new String[]{LineChart.TYPE, LineChart.TYPE, LineChart.TYPE};
            String[] titles = new String[]{"Suitable Empty Sites", "Unsuitable Sites", "Occupied Sites"};
            ChartData chartData = new ChartData("Dynamic Landscape", "time", "% of sites", titles, colors, styles, strokes, types, 3);
            return chartData;
        }

        @Override
        public double getXPoint() {
            return timestep;  
        }

        @Override
        public double[] getYPoint() {
            return new double[]{(numSuitable - numOcc)/10000d, numUnsuitable/10000d, numOcc/10000d};
        }
    }
	
	/*
	 * 
	 * Private helper methods
	 * 
	 */
	
	/*
	 * Initializes the landscape to all suitable sites
	 */
	private void initLandscape(){
		int size = super.getSize();
		// make all sites suitable
		numSuitable=size*size;
		numUnsuitable=0;
		for(int x=0;x<size;x++) {
			for(int y=0;y<size;y++) {
				// empty unsuitable grid
				unsuitableGrid[x][y] = -1;
				// fix cells grid so all unoccupied sites are suitable 
				if(cells[x][y] != OCC_GOODSITE)
					cells[x][y] = UNOCC_GOODSITE;
			}
		}
	}
	
	private void initPop(){
		int x, y;
		int n = initDensity.value;
		int size = super.getSize();
		numOcc = n*numSuitable/100; // if called after initLand, numSuitable is all sites
		for (y=0; y < size; y++)
			for (x=0; x < size; x++) {
				occGrid[x][y] = EMPTY;
				// bad sites stay bad, occupied sites turn to unoccupied good sites
				cells[x][y] = (int)cells[x][y] & ~OCC_GOODSITE;
			}
		for (int i=0; i < numOcc; i++) {
			// find an empty good site
			do {
				x = random.nextInt(size);
				y = random.nextInt(size);
			} while (cells[x][y] != UNOCC_GOODSITE);
			// place pop
			occX[i] = x;
			occY[i] = y;
			occGrid[x][y] = i;
			cells[x][y] = OCC_GOODSITE;
		}
	}
	
	private void step(){
		timestep++;
		int size = super.getSize();
		int index, x, y;

		// loop for numUpdatesPerStep, doing events
		for (int i=0; i < numUpdatesPerStep; i++){

			// choose event type
			double probArray[] = {numOcc*birthRate.value, numOcc*deathRate.value, numUnsuitable*regrowthRate.value,
					size*size*disturbanceRate.value/(blockXSize.value*blockYSize.value)};
			int eventType = randvalRescale(4, probArray);
			
			if (eventType == 0){ // birth				
				if (random.nextDouble() < alpha.value) { // global dispersal
					newx = random.nextInt(size);
					newy = random.nextInt(size);
				}
				else{ // von neumann dispersal
					index = random.nextInt(numOcc); // choose parent site
					x = occX[index];
					y = occY[index];
					switch (random.nextInt(4)) {
					case 0:
						newx = x; newy = y-1; break;
					case 1:
						newx = x; newy = y+1; break;
					case 2:
						newx = x-1; newy = y; break;
					case 3:
						newx = x+1; newy = y; break;
					}
					newx = (newx+size) % size; // wraparound
					newy = (newy+size) % size;
				}				
				if (cells[newx][newy] == UNOCC_GOODSITE){ // if site is suitable
					occX[numOcc] = newx;				// birth is successful
					occY[numOcc] = newy;
					occGrid[newx][newy] = numOcc;
					cells[newx][newy] = OCC_GOODSITE;
					numOcc++;
				}
			}			
			else if (eventType == 1){ // individual death
				index = random.nextInt(numOcc);
				x = occX[index];
				y = occY[index];
				int lastX = occX[numOcc-1];
				int lastY = occY[numOcc-1];
				occX[index] = lastX;
				occY[index] = lastY;
				occGrid[lastX][lastY] = index;
				cells[x][y] = UNOCC_GOODSITE;
				numOcc--;
			}			
			else if (eventType == 2){ // landscape regrowth
                index = random.nextInt(numUnsuitable);
                x = unsuitableX[index];
                y = unsuitableY[index];
                unsuitableGrid[x][y] = -1;
        		unsuitableX[index] = unsuitableX[numUnsuitable-1]; // move last elt in array into new open spot
        		unsuitableY[index] = unsuitableY[numUnsuitable-1];
        		unsuitableGrid[unsuitableX[numUnsuitable-1]][unsuitableY[numUnsuitable-1]] = index;
        		cells[x][y] = UNOCC_GOODSITE;
        		numSuitable++;
        		numUnsuitable --;
			}
			
			else if (eventType == 3){ // block extinction
				x = random.nextInt(size);
				y = random.nextInt(size);
				int bx, by, x2, y2;
				if (random.nextInt(2) == 0) {
				    bx = blockXSize.value;
				    by = blockYSize.value;
				}
				else {
				    bx = blockYSize.value;
				    by = blockXSize.value;
				}
				for (int dx=0; dx < bx; dx++) {
				    x2 = (x+dx) % size;
				    for (int dy=0; dy < by; dy++) {
						y2 = (y+dy) % size;
						if (cells[x2][y2] == OCC_GOODSITE){ // kill pop
							index = occGrid[x2][y2];
							int lastX = occX[numOcc-1];
							int lastY = occY[numOcc-1];
							occX[index] = lastX;
							occY[index] = lastY;
							occGrid[lastX][lastY] = index;
							numOcc--;
						}
						if (cells[x2][y2] != BADSITE){ // turn site bad
							unsuitableX[numUnsuitable] = x2;
							unsuitableY[numUnsuitable] = y2;
							unsuitableGrid[x2][y2] = numUnsuitable;
							cells[x2][y2] = BADSITE;
							numSuitable--;
							numUnsuitable++;
						}

				    }
				}
			}
		}
	}
	
	/* 
	 * Given a list of n probabilities, re-scales them to add
	 * up to 1, then returns an int between 0 and n-1 inclusive,
	 * where the probability of returning i is p_i 
	 */
	private int randvalRescale(int n, double localProbs[]){		
		double newProbs[] = new double[n];
		double d, dSum;
		double pSum = 0.0;
		for (int i=0; i<n; i++){ //sum original probs
			pSum += localProbs[i];}
		for (int j=0; j<n; j++){ // divide by sum to rescale
			newProbs[j] = localProbs[j]/pSum;
		}		
		d = random.nextDouble(); // dice, between 0 and 1		
		dSum = 0.0;
		for(int k=0; k<n; k++){ 
			dSum += newProbs[k];
			if (dSum > d) {
				return k; 
			}
		}
		int retVal = -1;
		if (retVal == -1)
			throw new ArrayIndexOutOfBoundsException();
		//System.out.println("Something went wrong. d="+d+" dSum="+dSum);
		return retVal; //cannot reach this
	}

}
