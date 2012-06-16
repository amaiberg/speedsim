package speedlab4.params.ui.listeners;

import android.widget.SeekBar;
import speedlab4.model.ModelController;
import speedlab4.params.ParamNumber;
import speedlab4.params.ui.ParamNumberView;

/**
 * Created with IntelliJ IDEA.
 * User: mog
 * Date: 6/8/12
 * Time: 3:27 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ParamBarListener<P extends ParamNumber, N extends Number> extends ParamListener<P,ParamNumberView<P>> implements SeekBar.OnSeekBarChangeListener {
    protected double progress;

    public ParamBarListener(P param, ParamNumberView<P> paramView, ModelController mc) {
        super(param, paramView, mc);
    }

    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        progress = (param.min.doubleValue() + ((double) i / (double) seekBar.getMax() * (param.max.doubleValue() - param.min.doubleValue())));
        paramView.onValueChanged(getProgress());
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    protected abstract N getProgress();

    public void onStopTrackingTouch(SeekBar seekBar) {
        param.setParam(getProgress());
        paramView.onValueChanged(getProgress());
        modelController.setParams(reqRestart,param);
    }
}