package speedlab4.model.java;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.renderer.BasicStroke;

import android.graphics.Color;
import android.graphics.Point;

import com.speedlab4.R;

import speedlab4.model.AbstractAnalyzer;
import speedlab4.model.State;
import speedlab4.params.Param;
import speedlab4.params.ParamInteger;
import speedlab4.ui.chart.ChartData;

public class Vants extends JAbstractSimModel {
	
	private double cells[][];
	private int occGrid[][], underColors[][];
    private int vantX[],vantY[];
    private int vantHeading[];
    private int antiVantX[],antiVantY[];
    private int antiVantHeading[];
    private int numGrayTiles, timestep;
    private Random random;
    
    private final ParamInteger numVants = IP("number of vants", 1, 0, 100, "number of vants", true);
    private final ParamInteger numAntiVants = IP("number of anti-vants", 0, 0, 100, "number of vants", true);
    private final ParamInteger displayEvery = IP("display every", 1, 1, 20);
    
    private static final int xoffsets[] = {0, 1, 0, -1}; // north, east, south, west
    private static final int yoffsets[] = {1, 0, -1, 0};
	private static final int BLACK_TILE = 0, GRAY_TILE = 1, VANT = 2, ANTIVANT = 3;
	
	private static final State[] states = { new State("Black Tile", Color.BLACK, 0),
									new State("Gray Tile", Color.GRAY, 1),
									new State("Vant", Color.GREEN, 2),
									new State("Anti-vant", Color.RED, 3)};
    
    public Vants(Param... pd){
    	super(100, R.string.VantsModel, pd);
		name = "Vants";
		int size = super.getSize();
		vantX = new int[size*size];
		vantY = new int[size*size];
		antiVantX = new int[size*size];
		antiVantY = new int[size*size];
		vantHeading = new int[size*size];
		antiVantHeading = new int[size*size];
		random = new Random();
		init();
    }

	@Override
	protected void init() {
		timestep = -1;
		analyzer = new VantsAnalyzer();
		int size = super.getSize();
		cells = new double[size][size];
		underColors = new int[size][size];
		numGrayTiles = 0;
		// fill grid with black tiles
		for(int x=0; x<size; x++) {
	        for(int y=0; y<size; y++) {
	           underColors[x][y] = BLACK_TILE;
	           cells[x][y] = BLACK_TILE;
	        }
	    }
		// place vants
		int numberVants = numVants.value;
		int newVantX, newVantY;
		for (int i=0; i<numberVants; i++){
			// pick spot for vant
//			newVantX = size/2; // FOR DEBUGGING
//			newVantY = size/2; // FOR DEBUGGING
			newVantX = (size/2 + random.nextInt(numberVants*2) - numberVants)%size;
			newVantY = (size/2 + random.nextInt(numberVants*2) - numberVants)%size;
			while (newVantX < 0) newVantX += size;
			while (newVantY < 0) newVantY += size;
			vantX[i] = newVantX;
			vantY[i] = newVantY;
//			vantHeading[i] = 0; // FOR DEBUGGING
			vantHeading[i] = random.nextInt(4);
			cells[newVantX][newVantY] = VANT;
			underColors[newVantX][newVantY] = GRAY_TILE;
			numGrayTiles++;
		}
		// place anti-vants
		int numberAntiVants = numAntiVants.value;
		int newAntiVantX, newAntiVantY;
		for (int i=0; i<numberAntiVants; i++){
			// pick spot for anti-vant
			newAntiVantX = (size/2 + random.nextInt(numberAntiVants*2) - numberAntiVants)%size;
			newAntiVantY = (size/2 + random.nextInt(numberAntiVants*2) - numberAntiVants)%size;
			while (newAntiVantX < 0) newAntiVantX += size; 
			while (newAntiVantY < 0) newAntiVantY += size;
			antiVantX[i] = newAntiVantX;
			antiVantY[i] = newAntiVantY;
			antiVantHeading[i] = random.nextInt(4);
			cells[newAntiVantX][newAntiVantY] = ANTIVANT;
			underColors[newAntiVantX][newAntiVantY] = GRAY_TILE;
			numGrayTiles++;
		}
	}

	@Override
	public double[][] next(double time) {
//		if (timestep == -1){
//			timestep++;
//			return cells;
//		}
		for (int j=0; j<displayEvery.value; j++){
			for (int i=0; i<numVants.value; i++)
				stepVant(i);
			for (int i=0; i<numAntiVants.value; i++)
				stepAntiVant(i);
			timestep++;
		}
		return cells;
	}

	@Override
	public int getColor(int state) {
		return states[state].stateColor;
	}

	@Override
	public double[][] first() {
		return cells;
	}

	@Override
	public State[] getStates() {
		return states;
	}
	
	@Override
    public void setCell(Point point, State state){
    	// update state-specific data structures
    	if (state.constant == BLACK_TILE){
        		if (underColors[point.x][point.y] == GRAY_TILE) numGrayTiles--;
        		underColors[point.x][point.y] = BLACK_TILE;
        		// we don't draw over vants, only undercolor is changed
    	}
    	else if (state.constant == GRAY_TILE){
        		if (underColors[point.x][point.y] == BLACK_TILE) numGrayTiles++;
        		underColors[point.x][point.y] = GRAY_TILE;
        		// we don't draw over vants, only undercolor is changed
    	}
    	else if (state.constant == VANT && cells[point.x][point.y] != VANT && cells[point.x][point.y] != ANTIVANT){
        		vantX[numVants.value] = point.x;
        		vantY[numVants.value] = point.y;
        		vantHeading[numVants.value] = random.nextInt(4);
          		numVants.value++;
        		// flip tile
        		if (underColors[point.x][point.y] == BLACK_TILE)
        			underColors[point.x][point.y] = GRAY_TILE;
        		else underColors[point.x][point.y] = BLACK_TILE;
    	}
    	else if (state.constant == ANTIVANT && cells[point.x][point.y] != VANT && cells[point.x][point.y] != ANTIVANT){
        		antiVantX[numAntiVants.value] = point.x;
        		antiVantY[numAntiVants.value] = point.y;
        		antiVantHeading[numAntiVants.value] = random.nextInt(4);
        		numAntiVants.value++;
        		// flip tile
        		if (underColors[point.x][point.y] == BLACK_TILE)
        			underColors[point.x][point.y] = GRAY_TILE;
        		else underColors[point.x][point.y] = BLACK_TILE;
    	}
		// update the matrix
    		cells[point.x][point.y] = state.constant;
    }
	
	public class VantsAnalyzer extends AbstractAnalyzer implements Serializable {

        @Override
        public ChartData getChartData() {
            int[] colors = new int[]{getColor(0), getColor(1)};
            PointStyle[] styles = new PointStyle[]{PointStyle.X, PointStyle.CIRCLE,};
            BasicStroke[] strokes = new BasicStroke[]{BasicStroke.SOLID, BasicStroke.SOLID};
            String[] types = new String[]{LineChart.TYPE, LineChart.TYPE};
            String[] titles = new String[]{"Black Tiles", "Gray Tiles"};
            ChartData chartData = new ChartData("Vants", "time", "% of tiles", titles, colors, styles, strokes, types, 2);
            return chartData;
        }

        @Override
        public double getXPoint() {
            return timestep;  
        }

        @Override
        public double[] getYPoint() {
            return new double[]{(getSize()-numGrayTiles)/(double)getSize(), numGrayTiles/(double)getSize()};
        }
    }
	
	/*
	 * Steps the vant at the given index
	 */
	private void stepVant(int index){
		// put undercolor into cells grid
		int x = vantX[index];
		int y = vantY[index];
		cells[x][y] = underColors[x][y];
		// move vant based on heading
		int newX = (vantX[index] + xoffsets[vantHeading[index]] + super.getSize())%super.getSize();
		int newY = (vantY[index] + yoffsets[vantHeading[index]] + super.getSize())%super.getSize();
		vantX[index] = newX;
		vantY[index] = newY;
		if (underColors[newX][newY] == BLACK_TILE){ // turn left
			vantHeading[index] = (vantHeading[index] - 1 + 4)%4; //change to plus 3
			underColors[newX][newY] = GRAY_TILE; // flip tile	
			numGrayTiles++;
		}
		else{ // turn right
			vantHeading[index] = (vantHeading[index] + 1)%4;
			underColors[newX][newY] = BLACK_TILE; // flip tile
			numGrayTiles--;
		}
		cells[newX][newY] = VANT; // place vant
	}
	
	private void stepAntiVant(int index){
		// put undercolor into cells grid
		int x = antiVantX[index];
		int y = antiVantY[index];
		cells[x][y] = underColors[x][y];
		// move anti-vant based on heading
		int newX = (antiVantX[index] + xoffsets[antiVantHeading[index]] + super.getSize())%super.getSize();
		int newY = (antiVantY[index] + yoffsets[antiVantHeading[index]] + super.getSize())%super.getSize();
		antiVantX[index] = newX;
		antiVantY[index] = newY;
		if (underColors[newX][newY] == BLACK_TILE){ // turn right
			antiVantHeading[index] = (antiVantHeading[index] + 1)%4;
			underColors[newX][newY] = GRAY_TILE; // flip tile	
			numGrayTiles++;
		}
		else{ // turn left
			antiVantHeading[index] = (antiVantHeading[index] - 1 + 4)%4;
			underColors[newX][newY] = BLACK_TILE; // flip tile
			numGrayTiles--;
		}
		cells[newX][newY] = ANTIVANT; // place anti-vant
	}

}
