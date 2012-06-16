package speedlab4.params.ui;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.TextView;
import speedlab4.params.Param;

/**
 * Created with IntelliJ IDEA.
 * User: mog
 * Date: 5/24/12
 * Time: 11:25 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ParamView<P extends Param> extends LinearLayout {
    private TextView title;
    public P param;
    ViewGroup.LayoutParams parentLayoutParams;

    public ParamView(Context context, P param, ViewGroup.LayoutParams lp) {
        super(context);
        int width = LayoutParams.FILL_PARENT;
        int height = LayoutParams.WRAP_CONTENT;
        parentLayoutParams = lp;
        Log.e("parentParams", parentLayoutParams.toString() + "");
        parentLayoutParams.width = width;
        parentLayoutParams.height = height;
        this.setOrientation(LinearLayout.VERTICAL);
        this.setLayoutParams(parentLayoutParams);
        this.param = param;
        this.title = new TextView(context);
        title.setText(param.name);
        //if(param.reqRestart)
           // title.setTextColor(Color.RED);
        this.addView(title);
    }

    @Override
    public void onMeasure(int width, int height) {
        setMeasuredDimension(width, height);
        this.setLayoutParams(parentLayoutParams);

        super.onMeasure(width, height);
    }

}
