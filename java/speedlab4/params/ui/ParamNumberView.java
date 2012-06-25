package speedlab4.params.ui;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.*;
import speedlab4.params.ParamDouble;
import speedlab4.params.ParamNumber;
import speedlab4.params.ui.listeners.ParamBarListener;

/**
 * Created by IntelliJ IDEA.
 * User: avnermaiberg
 * Date: 1/15/12
 * Time: 9:58 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ParamNumberView<P extends ParamNumber> extends ParamView<P> {
    protected TextView tMin, tMax, tCur;
    public SeekBar paramBar;
    private LinearLayout ll;
    private RelativeLayout rl;
    private int width, height;
    private Context mContext;
    private RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT),
            rp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT),
            cp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    private LinearLayout.LayoutParams llp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

    public ParamNumberView<P> pairView; // used by ParamDoubleView<ParamLinkedDouble>
    // pull out into ParamLinkedDoubleView, but first need to parameterize ParamBarListener by V extends ParamView

    protected abstract String getMaxStr();

    protected abstract Number getMax();

    protected abstract Number getCur();

    protected abstract Number getDisplayVal();


    public ParamNumberView(Context context, P p, ViewGroup.LayoutParams parent) {
        super(context, p, parent);
        mContext = context;
        Number min = p.min.intValue(), max = getMax(), cur = getCur();
        String sTitle = p.name, desc = p.description, sMin = "" + min, sMax = getMaxStr(), sCur = "" + getDisplayVal();
        /*
          title = new TextView(context);
          title.setText(sTitle);
          this.addView(title);
        */
        ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);


        rl = new RelativeLayout(context);


        tMin = new TextView(context);
        tMax = new TextView(context);
        tCur = new TextView(context);

        tMin.setText(sMin);
        tMax.setText(sMax);
        tCur.setText(sCur);

        paramBar = new SeekBar(context);
        paramBar.setMax(max.intValue());
        paramBar.setProgress(getCur().intValue());
        //  paramBar.setOnSeekBarChangeListener(getParamBarListener());

        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        rp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        cp.addRule(RelativeLayout.CENTER_IN_PARENT);

        ll.addView(paramBar);
        ll.addView(rl);
        rl.addView(tMin);
        rl.addView(tMax);
        rl.addView(tCur);


        this.addView(ll);
    }


    @Override
    public void onMeasure(int width, int height) {

        ll.setLayoutParams(llp);
        paramBar.setLayoutParams(llp);
        rl.setLayoutParams(llp);
        tMin.setLayoutParams(lp);
        tMax.setLayoutParams(rp);
        tCur.setLayoutParams(cp);
        super.onMeasure(width, height);
    }

    /*
     * Called when the value of the seekbar changes. Updates the string representation
     * of the value.
     * Also sets the progress of the seek bar to 'progress', in case this value
     * does not match what the seek bar shows
     */
    public void onValueChanged(Number progress) {
        //   Toast.makeText(mContext, param.name + " = " + progress + "X" + progress, Toast.LENGTH_SHORT).show();
    	String sProgress = progress.toString();
        int min = param.min.intValue();
        if (progress.doubleValue() > min)
            tCur.setText("" + ((sProgress.length() > 5) ? sProgress.substring(0, 5) : sProgress));
        else tCur.setText("" + min);
        
        // update the bar to show the current value, because
        // we may have changed it if it was illegal
        paramBar.setProgress((int)((progress.doubleValue()-param.min.doubleValue())*paramBar.getMax() / (param.max.doubleValue() - param.min.doubleValue())));

    }


    public void setOnSeekListener(ParamBarListener<P, ? extends Number> sListener) {
        paramBar.setOnSeekBarChangeListener(sListener);
    }
    
    // right now only used by ParamDoubleView<ParamLinkedDouble>
    public void setPairView(ParamNumberView<P> pairView){
    	this.pairView = pairView;
    }


}
