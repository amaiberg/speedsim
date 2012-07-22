package speedlab4;

import org.achartengine.model.XYMultipleSeriesDataset;

import com.speedlab4.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import speedlab4.model.AbstractSimModel;
import speedlab4.model.ModelController;
import speedlab4.model.State;
import speedlab4.model.c.CTestAbstractSimModel;
import speedlab4.model.java.CommunityVaccinated;
import speedlab4.model.java.DynamicLandscape;
import speedlab4.model.java.Random;
import speedlab4.model.java.Vants;
//import speedlab4.model.scala.SIRS;
import speedlab4.params.*;
import speedlab4.ui.DrawStatesAdapter;
import speedlab4.ui.LatticeView;
import speedlab4.ui.LegendAdapter;
import speedlab4.ui.chart.ChartView;

//TODO: intro screen
//TODO: navigation between lattices - gallery, pre-animation
//TODO: Create enum class that uses R.id properties for each lattice
//TODO: Decide if graph engine should draw every step, or an entire interval
	// OR have a different interval for the analyzer

public class SpeedSimActivity extends Activity {
    private ModelController modelController;
    private LinearLayout ll;
    private AbstractSimModel prevSim, curSim;
    private Button strtBtn;
    private Button fltBtn, restartBtn;
    private ViewFlipper flipper;
    private GridView paramGrid;
    private Context thisContext;
    private boolean saved;
    public int curSimID, prevSimID;
    public static final String NEW_MODEL_SELECTED = "new model";
    private final Param[] testParamList = {new ParamBoolean("ToggleTest", true), new ParamDouble("Test", 0.5d, 0d, 1d)
            , new ParamGroupDoubleUnity("TestGroup", new ParamDouble("Test1", 0.4d, 0d), new ParamDouble("Test2", 0.1d, 0d), new ParamDouble("Test3", 0.5d, 0d))};

    private final AbstractSimModel<ParamInteger, ParamDouble> testSim = new Random(Random.State.INTRO, testParamList);

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_layout);
        LatticeView latticeView = (LatticeView) findViewById(R.id.popview);

        ChartView chartView = (ChartView) findViewById(R.id.chartView);
        LinearLayout brushView = (LinearLayout) findViewById(R.id.brushview);
        flipper = (ViewFlipper) findViewById(R.id.view_flipper);
        paramGrid = (GridView) findViewById(R.id.param_grid);
        TextView descriptionView = (TextView) findViewById(R.id.descriptionView);

//        Toast.makeText(getApplicationContext(), stringFromJNI(), Toast.LENGTH_LONG).show();
        restartBtn = (Button) findViewById(R.id.restartBtn);
        this.thisContext = this;

        this.saved = (savedInstanceState != null);
        String intentAction = getIntent().getAction();

        if (saved) {
            prevSim = curSim = (AbstractSimModel) savedInstanceState.get("curSim");
            modelController = (ModelController) savedInstanceState.get("modelController");
            modelController.resetController(curSim, chartView, latticeView, descriptionView, brushView,
            		(double[][])savedInstanceState.get("currentMatrix"), (State)savedInstanceState.get("drawState"),
            		(XYMultipleSeriesDataset)savedInstanceState.get("chartData"));
            curSimID = savedInstanceState.getInt("simID");
            flipper.setDisplayedChild(savedInstanceState.getInt("flipperIndex"));

        } else {
            modelController = new ModelController(latticeView, chartView, descriptionView, brushView);
            if (intentAction.equals(NEW_MODEL_SELECTED)){
            	Bundle whichModel = getIntent().getExtras();
                curSimID = whichModel.getInt("modelID");
            }
            else
            	curSimID = R.id.dynamic; //default simModel
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        initUI();
        if (!saved)
            runSim(curSimID);
        else
            initSim(curSim);
    }

    public void initUI() {
        Toast.makeText(this, "Portrait Mode", Toast.LENGTH_LONG).show();
        strtBtn = (Button) findViewById(R.id.strtBtn);
        if(!modelController.pause)
            strtBtn.setText(getString(R.string.stopBtnTxt));
        SeekBar speedBar = (SeekBar) findViewById(R.id.speedbar);
        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                setModelSpeed(seekBar.getProgress());
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putSerializable("modelController", modelController);
        bundle.putSerializable("curSim", curSim);
        bundle.putInt("simID", curSimID);
        bundle.putSerializable("currentMatrix", modelController.getCurrentMatrix());
        bundle.putSerializable("drawState", modelController.getBrushController().getDrawState());
        bundle.putSerializable("chartData", modelController.getChartData());
        bundle.putInt("flipperIndex", flipper.getDisplayedChild());
        super.onSaveInstanceState(bundle);
    }

    @Override
    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
    }
    
    public void onResume() {
        super.onResume();
    }

    public void onPause() {
    	modelController.pause();
        super.onPause();
    }

    public void onDestroy() {
        modelController.destroyModel();
        super.onDestroy();
    }

    public void onStop() {
        modelController.stop();
        super.onStop();
    }
    
    /*
     * Called when the right arrow button is clicked
     */
    public void flipView(View view) {
        flipper.showNext();
    }
    
    /*
     * Called when the left arrow button is clicked
     */
    public void flipBackView(View view) {
        flipper.showPrevious();
    }

    /*
     * Called when the Step button is clicked
     */
    public void next(View view) {
        modelController.next(1);
    }

    /*
     * Called when the Reset Defaults button is clicked
     */
    public void reset(View view){
    	if (!modelController.pause) pauseModel(strtBtn);
        runSim(curSimID);
    }

    /*
     * Called when Continue/Pause button is clicked
     */
    public void pauseModel(View view) {
        String text = "";
        if (!modelController.pause) {
            strtBtn.setText(getString(R.string.startBtnTxt));
            modelController.pause();
            text = "paused";
            Toast.makeText(getApplicationContext(), curSim.getName() + " " + text, Toast.LENGTH_SHORT).show();
        } else continueModel();
    }

    /*
     * Called when Continue button is clicked
     */
    public void continueModel() {
        String text;
        if (modelController.pause) {
            modelController.resume();
            text = "started";
            strtBtn.setText(getString(R.string.stopBtnTxt));
            Toast.makeText(getApplicationContext(), curSim.getName() + " " + text, Toast.LENGTH_SHORT).show();
        }

    }

    /*
     * Called when the Restart button is clicked
     */
    public void restartModel(View view) {
    	//if (!modelController.pause) pauseModel(strtBtn);
    	modelController.restart();
        //curSim.restart();
        //modelController.execute();
    }

    /*
     * Called when the model speed seekbar is changed
     */
    public void setModelSpeed(int rate) {
        modelController.setRate(rate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		if (!modelController.pause)
			pauseModel(strtBtn);
        return runSim(item.getItemId());
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 1){
        	Bundle whichModel = intent.getExtras();
        	curSimID = whichModel.getInt("modelID");
        }
        else { // resume same model
        	curSimID = prevSimID;
        	saved = true;
        }
    }

    public boolean runSim(int simID, Param... params) {
        modelController.destroyModel();
        prevSimID = curSimID;
        curSimID = simID;
        switch (simID) {
            case R.id.disp:
                return true;
            case R.id.sirs:
           //        initSim(new SIRS());
             //      curSim.setParams(params);
                return true;
            case R.id.vac:
            	initSim(new CommunityVaccinated());
                return true;
            case R.id.dynamic:
            	initSim(new DynamicLandscape());
                return true;
            case R.id.ctest:
                initSim(new CTestAbstractSimModel(256, this));
                return true;
            case R.id.frog:
                //    initSim(new FrogAbstractSim(100,params));
                return true;
//            case R.id.plot:
//                //showHelp();
//                return true;
//            case R.id.parameters:
//                //showHelp();
//                return true;
            // case R.id.model:
            //showHelp();
            //      return true;
            case R.id.browseModels:
            	Intent i = new Intent(BrowseModelsActivity.SELECT_ANOTHER_MODEL, null, this, BrowseModelsActivity.class);
                startActivityForResult(i, 1);//placeholder number, use named constant
                finish();
                return true;
            case R.id.aboutThisApp:
            	Intent k = new Intent(this, AboutActivity.class);
        		startActivityForResult(k, curSimID);
        		return true;
            case R.id.random:
                initSim(new Random(Random.State.TEST1, new ParamDouble("Test", 0.5d, 0d, 1d)));
                curSim.setParams(params);
                return true;
            case R.id.random2:
                initSim(new Random(Random.State.TEST2, new ParamDouble("Test", 0.5d, 0d, 1d)));
                curSim.setParams(params);
                return true;
            case R.id.vants:
                initSim(new Vants());
                curSim.setParams(params);
                return true;
            default:
            	return false;
                //initSim(new Random(Random.State.INTRO, new ParamDouble("Test", 0.5d, 0d, 1d)));
                //return true;
        }
    }

    public void initSim(AbstractSimModel abstractSimModel) {

    	if (curSim != prevSim) {
    		if (!modelController.pause)
    			pauseModel(strtBtn);
    		modelController.destroyModel();
    	}

    	prevSim = curSim;
    	curSim = abstractSimModel;
    	modelController.setSimModel(curSim, curSim == prevSim);
    	modelController.execute();

    	ParamAdapter pAdapter = new ParamAdapter(thisContext, curSim.getParams(), modelController, new AbsListView.LayoutParams(0, 0));
    	paramGrid.setAdapter(pAdapter);

    	// set up legend in description view
    	GridView legendGrid = (GridView)findViewById(R.id.legend_grid);
    	LegendAdapter lAdapter = new LegendAdapter(thisContext, curSim.getStates());
    	legendGrid.setAdapter(lAdapter);
    	
    	// set up drawing view
    	GridView drawStateGrid = (GridView)findViewById(R.id.drawState_grid);
    	DrawStatesAdapter dAdapter = new DrawStatesAdapter(thisContext, modelController.getBrushController(), curSim.getStates());
    	drawStateGrid.setAdapter(dAdapter);
    }
    

}
