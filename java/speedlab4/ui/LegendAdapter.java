package speedlab4.ui;

import speedlab4.model.State;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.speedlab4.R;

public class LegendAdapter extends BaseAdapter {
	
	private State[] modelStates;
	private LayoutInflater inflator;
	
	public LegendAdapter(Context context, State[] modelStates){
		this.modelStates = modelStates;
		inflator = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		View cell = convertView;
		ViewHolder holder = null;

		if(cell == null){
			// Inflate View from XML layout
			cell = inflator.inflate(R.layout.legend_item, null);
			holder = new ViewHolder();
			holder.title = (TextView) cell.findViewById(R.id.legendKey);
			holder.colorSwatch = (TextView) cell.findViewById(R.id.legendColor);
			cell.setTag(holder); // associates data in holder with cell View
		}
		else{		
			holder = (ViewHolder) cell.getTag();
		}
		
		// Populate the view
		State thisState = (State) getItem(position);
		String name = thisState.stateName;
		Integer color = thisState.stateColor;
		holder.title.setText(name);
		holder.colorSwatch.setTextColor(color);
		if (color == Color.BLACK) // because background is black, need border
			holder.colorSwatch.setBackgroundResource(R.drawable.blacksquare);
		else
			holder.colorSwatch.setBackgroundColor(color);
		
		return cell;
	}
	
	// Inner class that describes the view for one cell
	private class ViewHolder {
		TextView title = null;
		TextView colorSwatch = null;
	}

}
