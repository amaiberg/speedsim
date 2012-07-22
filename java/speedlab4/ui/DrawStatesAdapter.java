package speedlab4.ui;

import com.speedlab4.R;

import speedlab4.model.BrushController;
import speedlab4.model.ModelController;
import speedlab4.model.State;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class DrawStatesAdapter extends BaseAdapter {

	private State[] modelStates;
	private Context context;
	private BrushController controller;
	
	public DrawStatesAdapter(Context context, BrushController bc, State[] modelStates){
		this.modelStates = modelStates;
		controller = bc;
		this.context = context;
	}

	public int getCount() {
		return modelStates.length;
	}

	public Object getItem(int position) {
		return modelStates[position];
	}

	public long getItemId(int position) {
		return 0;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		View recycledButton = convertView; //recycled View
		DrawButton drawStateButton;
		State thisState = (State) getItem(position);

		if(recycledButton == null){
			drawStateButton = new DrawButton(context, thisState, position);
		}
		else{		
			drawStateButton = (DrawButton) recycledButton;
			drawStateButton.setState(thisState, position);
		}
		
		// Populate the view
		String name = thisState.stateName;
		Integer color = thisState.stateColor;
		drawStateButton.setText(name);
		if (color == Color.BLACK) // need border since view background may be black
			drawStateButton.setBackgroundResource(R.drawable.blackborder);
		else
			drawStateButton.setBackgroundColor(color);
		
		return drawStateButton;
	}
	
	private class DrawButton extends Button{
		State myState;
		public DrawButton(Context c, State s, int position){
			super(c);
			myState = s;
			setTextColor(Color.WHITE);
			setOnClickListener(new OnClickListener(){
				// change the textView to display this draw state
				public void onClick(View v){
					TextView drawingTitle = (TextView)getRootView().findViewById(R.id.drawingMode);
					drawingTitle.setText("Drawing Mode: "+myState.stateName);
					controller.setDrawState(myState);
				}
			});
		}
		public void setState(State s, int position){
			myState = s;
		}

	}
}
