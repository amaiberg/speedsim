package speedlab4;

import com.speedlab4.R;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import speedlab4.model.AbstractSimModel;
import speedlab4.model.ModelController;
import speedlab4.model.c.CTestAbstractSimModel;
import speedlab4.model.java.CommunityVaccinated;
import speedlab4.model.java.Random;
//import speedlab4.model.scala.SIRS;
import speedlab4.params.*;
import speedlab4.ui.LatticeView;
import speedlab4.ui.chart.ChartView;

//TODO: Add models(2 or 3)
//TODO: intro screen
//TODO: painting
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
    public int curSimID;
    private final Param[] testParamList = {new ParamBoolean("ToggleTest", true), new ParamDouble("Test", 0.5d, 0d, 1d)
            , new ParamGroupDoubleUnity("TestGroup", new ParamDouble("Test1", 0.4d, 0d), new ParamDouble("Test2", 0.1d, 0d), new ParamDouble("Test3", 0.5d, 0d))};

    private final AbstractSimModel<ParamInteger, ParamDouble> testSim = new Random(Random.State.INTRO, testParamList);

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_layout);
        LatticeView latticeView = (LatticeView) findViewById(R.id.popview);

        ChartView chartView = (ChartView) findViewById(R.id.chartView);
        flipper = (ViewFlipper) findViewById(R.id.view_flipper);
        paramGrid = (GridView) findViewById(R.id.param_grid);

//        Toast.makeText(getApplicationContext(), stringFromJNI(), Toast.LENGTH_LONG).show();
        restartBtn = (Button) findViewById(R.id.restartBtn);
        fltBtn = (Button) findViewById(R.id.floating_button);
        this.thisContext = this;

        this.saved = (savedInstanceState != null);

        if (saved) {
            prevSim = curSim = (AbstractSimModel) savedInstanceState.get("curSim");
            modelController = (ModelController) savedInstanceState.get("modelController");
            modelController.resetController(curSim, chartView, latticeView);
            //      modelController.setSimModel(curSim);

        } else {

            modelController = new ModelController(latticeView, chartView);
            curSimID = R.id.vac; //default simModel
        }
    }

    @Override
    public void onStart() {

        super.onStart();
        //  int orientation = this.getResources().getConfiguration().orientation;
        //if (orientation == Configuration.ORIENTATION_PORTRAIT) {
        initUI();

        if (!saved)
            //       initSim(new CTestAbstractSimModel(256, this));
            runSim(curSimID);
        else {
            initSim(curSim);
        }
    }


    public void flipView(View view) {
        flipper.showNext();
    }

    public void initUI() {
        Toast.makeText(this, "Portrait Mode", Toast.LENGTH_LONG).show();
        strtBtn = (Button) findViewById(R.id.strtBtn);
        if(!modelController.pause)
            strtBtn.setText(getString(R.string.stopBtnTxt));
        Button nxtButton = (Button) findViewById(R.id.nxtBtn);
        SeekBar speedBar = (SeekBar) findViewById(R.id.speedbar);
        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                setModelSpeed(seekBar.getProgress());
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });

        fltBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                flipView(view);
            }
        });

        nxtButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                next(view);
            }
        });

    }


    @Override
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putSerializable("modelController", modelController);
        bundle.putSerializable("curSim", curSim);
        super.onSaveInstanceState(bundle);


    }

    @Override
    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);

    }

    /*
     * Currently not being called because configChanges not specified in Manifest
     * 
     * (non-Javadoc)
     * @see android.app.Activity#onConfigurationChanged(android.content.res.Configuration)
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT)
            Toast.makeText(getApplicationContext(), "PORTRAIT", Toast.LENGTH_LONG).show();
        else {
            Toast.makeText(getApplicationContext(), "LANDSCAPE", Toast.LENGTH_LONG).show();
        }


    }

    public void next(View view) {
        modelController.next(1);

    }

    public void restart(View view){

    }

    public void reset(View view){
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

    public void restartModel(View view) {
        curSim.restart();
        modelController.execute();
    }


    public void setModelSpeed(int rate) {
        modelController.setRate(rate);
    }


    public void onResume() {
        super.onResume();
    }

    public void onPause() {
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean runSim(int simID, Param... params) {
        modelController.destroyModel();
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
            case R.id.ctest:
                initSim(new CTestAbstractSimModel(256, this));
                return true;
            case R.id.frog:
                //    initSim(new FrogAbstractSim(100,params));
                return true;
            case R.id.plot:
                //showHelp();
                return true;
            case R.id.parameters:
                //showHelp();
                return true;
            // case R.id.model:
            //showHelp();
            //      return true;
            case R.id.more:
                //showHelp();
                return true;
            case R.id.random:
                initSim(new Random(Random.State.TEST1, new ParamDouble("Test", 0.5d, 0d, 1d)));
                curSim.setParams(params);
                return true;
            case R.id.random2:
                initSim(new Random(Random.State.TEST2, new ParamDouble("Test", 0.5d, 0d, 1d)));
                curSim.setParams(params);
                return true;
            default:
                initSim(new Random(Random.State.INTRO, new ParamDouble("Test", 0.5d, 0d, 1d)));
                return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return runSim(item.getItemId());

    }

    public void initSim(AbstractSimModel abstractSimModel) {

        if (curSim != prevSim) {
            if (!modelController.pause)
                pauseModel(strtBtn);
            modelController.destroyModel();
        }


        prevSim = curSim;
        //   modelController = new ModelController(latticeView, chartView);
        curSim = abstractSimModel;
        modelController.setSimModel(curSim);
        modelController.execute();

        ParamAdapter pAdapter = new ParamAdapter(thisContext, curSim.getParams(), modelController, new AbsListView.LayoutParams(0, 0));
        paramGrid.setAdapter(pAdapter);

    }

}
