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
import speedlab4.params.Param;
import speedlab4.params.ParamDouble;
import speedlab4.params.ParamGroupDouble;
import speedlab4.params.ParamInteger;
import speedlab4.params.ParamLinkedDouble;
import speedlab4.ui.chart.ChartData;

public class CommunityVaccinated extends JAbstractSimModel {

	private double suArray[], svArray[], iuArray[], ivArray[], ruArray[], rvArray[];
	private double iArray[], uArray[], vArray[], rArray[], sArray[];
	private int numPerCommunity, numCommunities;
	//private int totalNumU, totalNumV, totalNumI, totalNumR, totalNumS;
	private double cells[][];
	private double EU2, measuredQuu, measuredEU, EI, ES, ER;
	private int numSteps, maxNumSteps, numUpdatesPerStep, EX2denom, EXdenom, totalNumI, totalNumR,
		timestep;
	private Random random;
	private boolean ready;
	
	private final ParamDouble phi = DP("contact rate", 2, 0, 20);
	private final ParamDouble mu = DP("recovery rate", 1, 0, 20);
	private final ParamDouble rho = DP("vaccine effectiveness", 0.8, 0, 1);
	private final ParamDouble alpha = DP("alpha", 0.1, 0, 1);
	private final ParamDouble gamma = DP("loss of resistance rate", 0.5, 0, 20);
	private final ParamLinkedDouble Quu = LDP("Quu", 0.35, 0.0, 1.0, "Clustering", true);
	private final ParamLinkedDouble EU = LDP("EU", 0.35, 0.0, 1.0, "Expected unvaccinated", true);
	private final ParamInteger initInfectedComms = IP("Initial percentage communities infected", 30, 0, 100, "Init inf comms", true);
	private final ParamInteger initInfectedPerComm = IP("Initial infected per community", 20, 0, 100, "Init inf per comm", true);
	
	private static final int SUS_UNVACC = 0, SUS_VACC = 1, INFECTED_UNVACC = 2, INFECTED_VACC = 3,
			RESISTANT_UNVACC = 4, RESISTANT_VACC = 5;

	private static final State[] states= {new State("Susceptible Unvaccinated",Color.BLACK, 0),
		new State("Susceptible Vaccinated",Color.GREEN, 1),
		new State("Infected Unvaccinated",Color.RED, 2),
		new State("Infected Vaccinated",Color.MAGENTA, 3),
		new State("Resistant Unvaccinated",Color.BLUE, 4),
		new State("Resistant Vaccinated",Color.CYAN, 5),
	};
	
	
	public CommunityVaccinated(Param... pd){
		super(100, R.string.VaccCommModel, pd);
		ParamLinkedDouble.linkGreaterOrEqual(EU, Quu);
		name = "Vaccinated Communities";
		int size = super.getSize();
		numPerCommunity = size; 
		numCommunities = size;
		suArray = new double[size];
		svArray = new double[size];
		iuArray = new double[size];
		ivArray = new double[size];
		ruArray = new double[size];
		rvArray = new double[size];
		iArray = new double[size];
		uArray = new double[size];
		vArray = new double[size];
		rArray = new double[size];
		sArray = new double[size];
		numSteps = 0;
		maxNumSteps = 10000;
		totalNumI = 0;
		totalNumR = 0;
		random = new Random();
		analyzer = new CommunityVaccAnalyzer();
		init();
	}
	
	@Override
	protected void init() {
		ready = false;
		timestep = -1;
		int size = super.getSize();
		numPerCommunity = size; 
		numCommunities = size;
		cells = new double[size][size];
		numUpdatesPerStep = size;
		EX2denom = numCommunities*numPerCommunity*numPerCommunity;
		EXdenom = numCommunities*numPerCommunity;
		// Clear pop from previous run
		clearPop();
		// Fill pop arrays
		for(int i=0; i<numCommunities; i++){
			addSU(i, (int)(EU.value*numPerCommunity));
			addSV(i, numPerCommunity - (int)(EU.value*numPerCommunity));
		}
		// Calculate initial clustering
		if (EU.value == 0)
			measuredQuu = 0;
		else
			measuredQuu = EU2/measuredEU; //EU2/EU.value;
		
		// Adjust clustering
		adjustQuu();
		
		// Add infected pop
		int numInfComms = (int)(initInfectedComms.value/100.0*numCommunities);
		int numInfPerComm = (int)(initInfectedPerComm.value/100.0*numPerCommunity);
		for (int i=0; i < numInfComms; i++){
			int houseToInfect = random.nextInt(numCommunities);
			while (iArray[houseToInfect] != 0){ // choose a house that has no infection yet
				houseToInfect = random.nextInt(numCommunities);
			}
			for (int j=0; j < numInfPerComm; j++){
				// choose whether a vaccinated or unvaccinated will get infected
				if (random.nextDouble() < suArray[houseToInfect]/(suArray[houseToInfect]+svArray[houseToInfect])){
					// infect an unvaccinated
					removeSU(houseToInfect, 1);
					addIU(houseToInfect, 1);
				}
				else{ // infect a vaccinated
					removeSV(houseToInfect, 1);
					addIV(houseToInfect, 1);
				}
			}
		}		
		// Update cells grid
		updateCellsGrid();
		ready = true;
	}
	
	@Override
	public double[][] next(double time) {
//		if (timestep == -1){
//			timestep++;
//			return cells;
//		}
		if (ready){
//			if (Math.abs(Quu.value - measuredQuu) > .001){
//				adjustQuuStep();
//			} else System.out.println("measured Quu: "+measuredQuu);
//			if (Math.abs(EU.value - measuredEU) > .01){
//				adjustEUstep();
//			}
			step();
		}
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
    public void setCell(int x, int y, State state){
		if (cells[x][y] != state.constant){
			// find lower bound and upper bound of state s in house x
			// there is some weirdness in this function in usage of x-y because
			// axes are rotated from input point, cells grid, and pop arrays
			int lowBound = -1;
			int upBound = numPerCommunity;
			boolean lowBoundFound = false;
			boolean upBoundFound = false;
			// first try upper bound
			while (upBound > 0 && !upBoundFound){
				upBound--;
				if (cells[upBound][y] <= state.constant) 
					upBoundFound = true;
			}	
			// if y > upperBound, create pop in all cells between upperBound and y
			if (x > upBound){
				for (int i=upBound+1; i<=x; i++){
					int oldS = (int) cells[i][y]; 
					createPop(numCommunities-1-y, state.constant, oldS); 
				}
			}
			else{ // otherwise try lower bound
				while (lowBound < (numPerCommunity-1) && !lowBoundFound){
					lowBound++;
					if (cells[lowBound][y] >= state.constant) 
						lowBoundFound = true;
				}
				// if y < lowerBound, create pop in all cells between y and lowerBound
				if (x < lowBound){
					for (int i=lowBound-1; i>=x; i--){
						int oldS = (int) cells[i][y];
						createPop(numCommunities-1-y, state.constant, oldS);
					}
				}
			}
			updateCellsGrid();
		}
    }
	
	@Override
	public ArrayList<Point> preprocessSetCell(ArrayList<Point> points, State state){
		Point p;
		int totalPoints = points.size();
		for (int k=0; k<totalPoints; k++){
			p = points.get(k);
			if (cells[p.x][p.y] != state.constant){
				int lowBound = -1;
				int upBound = numPerCommunity;
				boolean lowBoundFound = false;
				boolean upBoundFound = false;
				while (upBound > 0 && !upBoundFound){
					upBound--;
					if (cells[upBound][p.y] <= state.constant) 
						upBoundFound = true;
				}	
				if (p.x > upBound)
					for (int i=upBound; i<=p.x; i++)
						points.add(new Point(i, p.y));
				else{ 
					while (lowBound < (numPerCommunity-1) && !lowBoundFound){
						lowBound++;
						if (cells[lowBound][p.y] >= state.constant) 
							lowBoundFound = true;
					}
					if (p.x < lowBound)
						for (int i=lowBound; i>=p.x; i--)
							points.add(new Point(i, p.y));
				}
			}
		}
		return points;
	}

	@Override
	public double[][] first() {
		return cells;
	}
	
	public class CommunityVaccAnalyzer extends AbstractAnalyzer implements Serializable {

        @Override
        public ChartData getChartData() {
            int[] colors = new int[]{getColor(1), getColor(2), getColor(4)};
            PointStyle[] styles = new PointStyle[]{PointStyle.X, PointStyle.CIRCLE, PointStyle.CIRCLE};
            BasicStroke[] strokes = new BasicStroke[]{BasicStroke.SOLID, BasicStroke.SOLID, BasicStroke.SOLID};
            String[] types = new String[]{LineChart.TYPE, LineChart.TYPE, LineChart.TYPE};
            String[] titles = new String[]{"Susceptible", "Infected", "Resistant"};
            ChartData chartData = new ChartData("Vaccinated Communities", "time", "% of population", titles, colors, styles, strokes, types, 3);
            return chartData;
        }

        @Override
        public double getXPoint() {
            return timestep;  
        }

        @Override
        public double[] getYPoint() {
            return new double[]{ES, EI, ER};
        }
    }
	
	
	/* 
	 * 
	 * Private helper methods
	 * 
	 */
	
	/*
	 * Attempts to bring the clustering as close to the desired
	 * level of clusterings as possible
	 */
	private void adjustQuu(){
		if (Quu.value < EU.value) // bring Quu up to minimum legal value
			Quu.value = EU.value;
		
		while((Math.abs(measuredQuu - Quu.value) > 0.001) && (numSteps < maxNumSteps)){
			 	adjustQuuStep();		
		} 
		numSteps = 0;
		updateCellsGrid();
	}
	
	/*
	 * Helper method to adjust Quu
	 */
	private void adjustQuuStep(){
		int commJ, commK, lessU, moreU;
		double quuVal = Quu.value;
		for (int i=0; i<numUpdatesPerStep; i++){
			if (Math.abs(measuredQuu - quuVal) > 0.001){
				// choose two communities at random
				commJ = random.nextInt(numCommunities);
				commK = random.nextInt(numCommunities);

				// roll weighted dice to decide if SU, IU, or RU will be swapped
				double totalUArray = uArray[commJ] + uArray[commK];
				if (totalUArray > 0){ // only continue if there are U's available in the houses
					double probSU = (suArray[commJ] + suArray[commK])/totalUArray;
					double probIU = (iuArray[commJ] + iuArray[commK])/totalUArray;
					double probRU = (ruArray[commJ] + ruArray[commK])/totalUArray;
					double probArray[] = {probSU, probIU, probRU};
					int whoToSwap = randvalRescale(3, probArray);
					
					if (uArray[commJ] <= uArray[commK]){ // find which community has more unvacc
						lessU = commJ; moreU = commK;}
					else{
						lessU = commK; moreU = commJ;}
						
					if(measuredQuu < quuVal){
						// need more clustering, so decrease the one with less, increase the one with more						
						// check categories. community to decrease must have >= 1, and community to increase must be < total of the category
						if (((whoToSwap == 0) && (suArray[lessU] >= 1.0) && (suArray[moreU] < sArray[moreU]))
								|| ((whoToSwap == 1) && (iuArray[lessU] >= 1.0) && (iuArray[moreU] < iArray[moreU]))
								|| ((whoToSwap == 2) && (ruArray[lessU] >= 1.0) && (ruArray[moreU] < rArray[moreU]))){							
							swap(moreU, lessU, whoToSwap); //allow swap
						}
					}
					else if(measuredQuu > quuVal){
						//need less clustering, so increase the one with less, decrease the one with more						
						if (((whoToSwap == 0) && (suArray[moreU] >= 1.0) && (suArray[lessU] < sArray[lessU]))
								|| ((whoToSwap == 1) && (iuArray[moreU] >= 1.0) && (iuArray[lessU] < iArray[lessU]))
								|| ((whoToSwap == 2) && (ruArray[moreU] >= 1.0) && (ruArray[lessU] < rArray[lessU]))){
							swap(lessU, moreU, whoToSwap); //allow swap
						}
					}
					// recompute Quu 
					if (EU.value == 0) measuredQuu = 0;
					else measuredQuu = EU2/measuredEU; //EU2/EU.value
				}
				numSteps++;
			}
		}
	}
	
	/*
	 * Private helper method to swap two individuals in different communities
	 */
	private void swap(int houseToIncrease, int houseToDecrease, int whoToSwap){
		if (whoToSwap == 0){ // swap susceptibles
			removeSU(houseToDecrease, 1);
			addSV(houseToDecrease, 1);
			removeSV(houseToIncrease, 1);
			addSU(houseToIncrease, 1);
		}
		else if (whoToSwap == 1){ // swap infected
			removeIU(houseToDecrease, 1);
			addIV(houseToDecrease, 1);
			removeIV(houseToIncrease, 1);
			addIU(houseToIncrease, 1);
		}
		else if (whoToSwap == 2){ // swap resistants
			removeRU(houseToDecrease, 1);
			addRV(houseToDecrease, 1);
			removeRV(houseToIncrease, 1);
			addRU(houseToIncrease, 1);
		}
	}
	
	/*
	 * Adjusts EU to match the desired value
	 */
	private void adjustEUstep(){
		
		int changeVaccHouse, whoToChange;
		
		for (int i=0; i<numUpdatesPerStep; i++){
			if (Math.abs(EU.value - measuredEU) > 0.001){
				if (measuredEU > EU.value){ // decrease EU, aka vaccinate people
					// choose a house with unvaccinated individuals using weighted probs (weighted by uArray)
					changeVaccHouse = randvalRescale(numCommunities, uArray);
					
					// choose S, I, or R to vaccinate within house
					double probArray[] = {suArray[changeVaccHouse], iuArray[changeVaccHouse], ruArray[changeVaccHouse]};
					whoToChange = randvalRescale(3, probArray);
					
					vaccinate(whoToChange, changeVaccHouse);
				}
				else if (measuredEU < EU.value){ // increase EU, aka unvaccinate people
					// choose a house with vaccinated individuals using weighted probs (weighted by vArray)
					changeVaccHouse = randvalRescale(numCommunities, vArray);
					
					// choose S, I, or R to unvaccinate within house
					double probArray[] = {svArray[changeVaccHouse], ivArray[changeVaccHouse], rvArray[changeVaccHouse]};
					whoToChange = randvalRescale(3, probArray);
					
					unvaccinate(whoToChange, changeVaccHouse);
				}
			}
			numSteps++;
		} 					
		// update display
		updateCellsGrid();
		// recompute Quu
		if (EU.value == 0) measuredQuu = 0;
		else measuredQuu = EU2/measuredEU;
	}
	
	/*
	 * Vaccinates one individual in the given house.
	 * @param whoToVacc category of who to vaccinate. Must be one of:
	 * 	0 : unvaccinated susceptible
	 * 	1 : unvaccinated infected
	 * 	2 : unvaccinated resistant
	 * @param house which house vaccination should occur in
	 */
	private void vaccinate(int whoToVacc, int house){
		if (whoToVacc == 0){ // vaccinate an unvacc susceptible
			removeSU(house, 1);
			addSV(house, 1);
		}
		else if (whoToVacc == 1){ // vaccinate an unvacc infected
			removeIU(house, 1);
			addIV(house, 1);
		}
		else if (whoToVacc == 2){ // vaccinate an unvacc resistant
			removeRU(house, 1);
			addRV(house, 1);
		}
	}
	
	/*
	 * Unvaccinates one individual in the given house.
	 * @param whoToUnvacc category of who to unvaccinate. Must be one of:
	 * 	0 : vaccinated susceptible
	 * 	1 : vaccinated infected
	 * 	2 : vaccinated resistant
	 * @param house which house unvaccination should occur in
	 */
	private void unvaccinate(int whoToUnvacc, int house){
		if (whoToUnvacc == 0){ // unvaccinate a vacc susceptible
			removeSV(house, 1);
			addSU(house, 1);
		}
		else if (whoToUnvacc == 1){ // unvaccinate a vacc infected
			removeIV(house, 1);
			addIU(house, 1);
		}
		else if (whoToUnvacc == 2){ // unvaccinate a vacc resistant
			removeRV(house, 1);
			addRU(house, 1);
		}
	}
	
	/*
	 * Creates an individual in the given house in the given
	 * state. Removes individual who is currently there.
	 * Does not update the grid--caller must do this
	 * @param oldState current state of the individual in that spot
	 */
	private void createPop(int house, int state, int oldState){
		// only update if new pop state is different from old pop state
		if (state != oldState){
			// first remove individual who is there
			removePop(house, oldState);	
			if (state == SUS_UNVACC)
				addSU(house, 1); //(x, 1)
			else if (state == SUS_VACC)
				addSV(house, 1);
			else if (state == INFECTED_UNVACC)
				addIU(house, 1);
			else if (state == INFECTED_VACC)
				addIV(house, 1);
			else if (state == RESISTANT_UNVACC)
				addRU(house, 1);
			else if (state == RESISTANT_VACC)
				addRV(house, 1);
			//cells[y][numCommunities-1-x] = state;
			//cells[x][y] = state;
		}
	}
	
	/*
	 * Removes the individual at the given x-y coords from the population.
	 * Does not replace with anything.
	 * Does not update grid -- caller must do this
	 * @param oldState the state of the individual being removed
	 */
	private void removePop(int house, int oldState){
		if (oldState == SUS_UNVACC)
			removeSU(house, 1);
		else if (oldState == SUS_VACC)
			removeSV(house, 1);
		else if (oldState == INFECTED_UNVACC)
			removeIU(house, 1);
		else if (oldState == INFECTED_VACC)
			removeIV(house, 1);
		else if (oldState == RESISTANT_UNVACC)
			removeRU(house, 1);
		else if (oldState == RESISTANT_VACC)
			removeRV(house, 1);
	}
	
	/*
	 * Private helper method to clear the population
	 */
	private void clearPop() {
		// clear arrays
		for (int i=0; i<numCommunities; i++){
			uArray[i] = 0; vArray[i] = 0;
			iArray[i] = 0; rArray[i] = 0; sArray[i] = 0;
			iuArray[i] = 0; ivArray[i] = 0;
			ruArray[i] = 0; rvArray[i] = 0;
			suArray[i] = 0; svArray[i] = 0;
		}
		// clear totals
		totalNumI = 0;
		totalNumR = 0;
		EU2 = 0; EI = 0; ES = 0; ER = 0;
		measuredQuu = 0; measuredEU = 0;
		// clear grid
		for (int x=0; x<numCommunities; x++){
			for (int y=0; y<numPerCommunity; y++){
				cells[x][y] = 0;
			}
		}
	}
	
	/*
	 * Private helper method to populate the cells grid
	 * based on values in the pop arrays
	 */
	private void updateCellsGrid(){
		int svOffset=0; int iuOffset=0; int ivOffset=0; int ruOffset=0; int rvOffset=0;
		for(int x=0; x<numCommunities; x++){
			for(int y=0; y < (int)(suArray[x]); y++){ // fill susceptible unvacc cells
				//cells[x][y] = SUS_UNVACC;
				cells[y][numCommunities-1 - x] = SUS_UNVACC;
			}
			
			svOffset = (int)(suArray[x]);
			for(int y=0; y < (int)(svArray[x]); y++){ // fill susceptible vacc cells
				//cells[x][y + svOffset] = SUS_VACC;
				cells[y + svOffset][numCommunities-1 - x] = SUS_VACC;
			}
			
			iuOffset = (int)(sArray[x]);
			for(int y=0; y < (int)(iuArray[x]); y++){
				//cells[x][y + iuOffset] = INFECTED_UNVACC;
				cells[y + iuOffset][numCommunities-1 - x] = INFECTED_UNVACC;
			}
			
			ivOffset = (int)(sArray[x]) + (int)(iuArray[x]);
			for(int y=0; y < (int)(ivArray[x]); y++){ // fill infected vacc
				//cells[x][y + ivOffset] = INFECTED_VACC;
				cells[y + ivOffset][numCommunities-1 - x] = INFECTED_VACC;
			}
			
			ruOffset = (int)(sArray[x]) + (int)(iArray[x]); //fill resistant unvacc
			for(int y=0; y < (int)(ruArray[x]); y++){
				//cells[x][y + ruOffset] = RESISTANT_UNVACC;
				cells[y + ruOffset][numCommunities-1 - x] = RESISTANT_UNVACC;
			}
			
			rvOffset = (int)(sArray[x]) + (int)(iArray[x]) + (int)(ruArray[x]); //fill resistant vacc
			for(int y=0; y < (int)(rvArray[x]); y++){
				//cells[x][y + rvOffset] = RESISTANT_VACC;
				cells[y + rvOffset][numCommunities-1 - x] = RESISTANT_VACC;
			}
		}
	}
	
	/*
	 * One step in the simulation, which includes numUpdatesPerStep events
	 */
	private void step(){
		timestep++;
		int eventHouse; int targetHouse;

		// loop for numUpdatesPerStep, doing events
	    for (int i=0; i < numUpdatesPerStep; i++){

	    	// choose event type
	    	double probArray[] = {phi.value, mu.value, gamma.value};
	    	int eventType = randvalRescale(3, probArray);

	    	// continue only if there are still event-generating individuals for the chosen event
	    	if ((totalNumI > 0 && (eventType == 0||eventType == 1)) || (totalNumR > 0 && eventType == 2)){

	    		if (eventType == 0){ // infection
	    			// choose house of event
		    		eventHouse = randvalRescale(numCommunities, iArray);

	    			// choose house that is target of infection
	    			if(random.nextDouble() < alpha.value){ // long distance
	    				targetHouse = random.nextInt(numCommunities);
	    			}
	    			else{ // short distance; same house
	    				targetHouse = eventHouse;
	    			}
	    			
	    			double targetState = random.nextDouble(); 	    		
	    			if (targetState < suArray[targetHouse]/numPerCommunity){ 
	    				// infect an susceptible unnvaccinated
	    				removeSU(targetHouse, 1);
	    				addIU(targetHouse, 1);
	    			}
	    			else if (targetState < sArray[targetHouse]/numPerCommunity){
	    				// infect a susceptible vaccinated
	    				if (random.nextDouble() < (1.0 - rho.value)){ //successful infection
	    					removeSV(targetHouse, 1);
	    					addIV(targetHouse, 1);
	    				}
	    			} // otherwise failed infection attempt
	    		}
	    		else if (eventType == 1){ // recovery
	    			// choose house of event
		    		eventHouse = randvalRescale(numCommunities, iArray);
		    		// choose if an infected vaccinated or infected unvaccinated is recovering
		    		if (random.nextDouble() < iuArray[eventHouse]/iArray[eventHouse]){ // unvaccinated recovery
		    			removeIU(eventHouse, 1);
//		    			if (SIS)
//		    				addSU(eventHouse, 1);
//		    			else
		    			addRU(eventHouse, 1);
		    		}
		    		else { // vaccinated recovery
		    			removeIV(eventHouse, 1);
//		    			if (SIS)
//		    				addSV(eventHouse, 1);
//		    			else
		    			addRV(eventHouse, 1);
		    		}
	    		}
	    		else{ // loss of resistance
	    			// choose house of event
		    		eventHouse = randvalRescale(numCommunities, rArray);
	    			if (random.nextDouble() < ruArray[eventHouse]/rArray[eventHouse]){ // unvaccinated loss of resistance
	    				removeRU(eventHouse, 1);
	    				addSU(eventHouse, 1);
	    			}
	    			else{ // vaccinated loss of resistance
	    				removeRV(eventHouse, 1);
	    				addSV(eventHouse, 1);
	    			}
	    		}
	    		
	    	} 
	    }
	    
	    // update display
	    updateCellsGrid();

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
//		if (retVal == -1)
//			throw new ArrayIndexOutOfBoundsException();
		//System.out.println("Something went wrong. d="+d+" dSum="+dSum);
		return retVal; //cannot reach this
	}
	
	// Helper methods
	private void removeSU(int house, double num){
		suArray[house] -= num;
		removeS(house, num);
		removeU(house, num);
	}
	
	private void removeSV(int house, double num){
		svArray[house] -= num;
		removeS(house, num);
		removeV(house, num);
	}
	
	private void removeIU(int house, double num){
		iuArray[house] -= num;
		removeI(house, num);
		removeU(house, num);
	}
	
	private void removeIV(int house, double num){
		ivArray[house] -= num;
		removeI(house, num);
		removeV(house, num);
	}
	
	private void removeRU(int house, double num){
		ruArray[house] -= num;
		removeR(house, num);
		removeU(house, num);
	}
	
	private void removeRV(int house, double num){
		rvArray[house] -= num;
		removeR(house, num);
		removeV(house, num);
	}
	
	private void addSU(int house, double num){
		suArray[house] += num;
		addS(house, num);
		addU(house, num);
	}
	
	private void addSV(int house, double num){
		svArray[house] += num;
		addS(house, num);
		addV(house, num);
	}
	
	private void addIU(int house, double num){
		iuArray[house] += num;
		addI(house, num);
		addU(house, num);
	}
	
	private void addIV(int house, double num){
		ivArray[house] += num;
		addI(house, num);
		addV(house, num);
	}
	
	private void addRU(int house, double num){
		ruArray[house] += num;
		addR(house, num);
		addU(house, num);
	}
	
	private void addRV(int house, double num){
		rvArray[house] += num;
		addR(house, num);
		addV(house, num);
	}
	
	// Helpers for the Helpers
	private void removeS(int house, double num){
		//ES2 -= sArray[house]*sArray[house]/EX2denom;
		sArray[house] -= num;
//		totalNumS -= num;
//		ES2 += sArray[house]*sArray[house]/EX2denom;
		ES -= num/EXdenom;
	}
	
	private void removeI(int house, double num){
		//EI2 -= iArray[house]*iArray[house]/EX2denom;
		iArray[house] -= num;
		totalNumI -= num;
		if (totalNumI < 0)
			throw new ArrayIndexOutOfBoundsException();
//		EI2 += iArray[house]*iArray[house]/EX2denom;
		EI -= num/EXdenom;
	}
	
	private void removeR(int house, double num){
		//ER2 -= rArray[house]*rArray[house]/EX2denom;
		rArray[house] -= num;
		totalNumR -= num;
		if (totalNumR < 0)
			throw new ArrayIndexOutOfBoundsException();
//		ER2 += rArray[house]*rArray[house]/EX2denom;
		ER -= num/EXdenom;
	}
	
	private void removeV(int house, double num){
		//EV2 -= vArray[house]*vArray[house]/EX2denom;
		vArray[house] -= num;
//		totalNumV -= num;
//		EV2 += vArray[house]*vArray[house]/EX2denom;
//		EV -= num/EXdenom;
	}
	
	private void removeU(int house, double num){
		EU2 -= uArray[house]*uArray[house]/EX2denom;
		uArray[house] -= num;
//		totalNumU -= num;
		EU2 += uArray[house]*uArray[house]/EX2denom;
		measuredEU -= num/EXdenom;
		if (EU.value == 0) measuredQuu = 0;
		else measuredQuu = EU2/EU.value;
	}
	
	private void addS(int house, double num){
		//ES2 -= sArray[house]*sArray[house]/EX2denom;
		sArray[house] += num;
//		totalNumS += num;
//		ES2 += sArray[house]*sArray[house]/EX2denom;
		ES += num/EXdenom;
	}
	
	private void addI(int house, double num){
		//EI2 -= iArray[house]*iArray[house]/EX2denom;
		iArray[house] += num;
		totalNumI += num;
//		EI2 += iArray[house]*iArray[house]/EX2denom;
		EI += num/EXdenom;
	}
	
	private void addR(int house, double num){
		//ER2 -= rArray[house]*rArray[house]/EX2denom;
		rArray[house] += num;
		totalNumR += num;
//		ER2 += rArray[house]*rArray[house]/EX2denom;
		ER += num/EXdenom;
	}
	
	private void addV(int house, double num){
		//EV2 -= vArray[house]*vArray[house]/EX2denom;
		vArray[house] += num;
//		totalNumV += num;
//		EV2 += vArray[house]*vArray[house]/EX2denom;
//		EV += num/EXdenom;
	}
	
	private void addU(int house, double num){
		EU2 -= uArray[house]*uArray[house]/EX2denom;
		uArray[house] += num;
//		totalNumU += num;
		EU2 += uArray[house]*uArray[house]/EX2denom;
		measuredEU += num/EXdenom;
		if (EU.value == 0) measuredQuu = 0;
		else measuredQuu = EU2/EU.value;
	}
	 

}
