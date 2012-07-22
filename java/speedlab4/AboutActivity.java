package speedlab4;

import com.speedlab4.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class AboutActivity extends Activity {
	
	public static final String ABOUT_FROM_BROWSE = "browse called";
	public static final String ABOUT_FROM_SIMMODEL = "speedsim called";
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_speedsim);
	}
	
	public void closeAbout(View v){
		finish();
	}

}
