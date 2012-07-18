package speedlab4;


import speedlab4.browsemodels.LatticeThumb;
import speedlab4.browsemodels.LatticeThumbAdapter;

import com.speedlab4.R;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.support.v4.view.ViewPager;

public class BrowseModelsActivity extends Activity {
	
	private LatticeThumbAdapter adapter;
	private boolean speedSimStarted;
	private static LatticeThumb[] allModels = {
		new LatticeThumb(R.drawable.vants, "Vants", R.id.vants, R.string.VantsModel),
		new LatticeThumb(R.drawable.communities, "Vaccinated Communities", R.id.vac, R.string.VaccCommModel),
		new LatticeThumb(R.drawable.dynamic, "Dynamic Landscape", R.id.dynamic, R.string.DynamicLandModel)
	};
	public static final String SELECT_ANOTHER_MODEL = "another model";
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_models);
        
        if ((getIntent().getAction()).equals(SELECT_ANOTHER_MODEL))
        	speedSimStarted = true;
        else speedSimStarted = false;
        
        // for finding screen size
    	DisplayMetrics metrics = new DisplayMetrics();
    	getWindowManager().getDefaultDisplay().getMetrics(metrics);
        
        ViewPager modelFlipper = (ViewPager) findViewById(R.id.pageflipper);
           
        // find out screen size category and orientation
        boolean landscape = false;
        boolean large = false;
        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)
        		== Configuration.SCREENLAYOUT_SIZE_LARGE ||
        		(getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)
        		== 4) 
        	large = true;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        	landscape = true;
        
        // set up pager adapter
        adapter = new LatticeThumbAdapter(this, allModels, getResources(),
    			Math.min(metrics.widthPixels, metrics.heightPixels), landscape, large);
        modelFlipper.setAdapter(adapter);
        Toast.makeText(getApplicationContext(), "Swipe to browse. Tap to select model.", Toast.LENGTH_LONG).show();
        
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.browse_models_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.selectModel:
    		return true;
    	case R.id.aboutThisApp:
    		Intent i = new Intent(this, AboutActivity.class);
    		startActivity(i);
    		return true;
    	default: // then item must be a model
    		launchSpeedSim(item.getItemId());
    		return true;
    	}
    }
	
	/*
	 * Called when the "Start" button on a model is clicked
	 */
	public void launchSpeedSim(View view){
		launchSpeedSim(adapter.getCurrentModel());
	}
	
	public void launchSpeedSim(int modelID){
		Bundle b = new Bundle();
		b.putInt("modelID", modelID);
		Intent i = new Intent(SpeedSimActivity.NEW_MODEL_SELECTED, null, this, SpeedSimActivity.class);
		i.putExtras(b);
		startActivity(i);
		finish();
	}
	
	/*
	 * Called when the "Back" button on a model is clicked
	 */
	public void closeDetail(View view){
		adapter.closeLastDetailView();
	}

}
