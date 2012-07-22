package speedlab4.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/*
 * GridView that forces all the children to be displayed. Allows for
 * embedding a GridView within a ScrollView with other content,
 * without the problem of nested scrolling
 */
public class ExpandedGridView extends GridView {

	public ExpandedGridView(Context context) {
        super(context);
    }
	
	public ExpandedGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandedGridView(Context context, AttributeSet attrs, int defaultStyle) {
        super(context, attrs, defaultStyle);
    }
    
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    		// set large max value for at_most
            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                        MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
            
            // super has measured the children, so just set the measurement
            // to the layout params
            android.view.ViewGroup.LayoutParams params = getLayoutParams();
            params.height = getMeasuredHeight();
    }
}
