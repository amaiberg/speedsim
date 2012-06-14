package speedlab4.params.ui.listeners;

import android.widget.SeekBar;
import speedlab4.model.ModelController;
import speedlab4.params.ParamDouble;
import speedlab4.params.ui.ParamNumberView;

/**
 * Created with IntelliJ IDEA.
 * User: mog
 * Date: 6/8/12
 * Time: 3:36 PM
 * To change this template use File | Settings | File Templates.
 */

public class ParamDoubleListener extends ParamBarListener<ParamDouble, Double> {


    public ParamDoubleListener(ParamDouble param, ParamNumberView<ParamDouble> paramView, ModelController mc) {
        super(param, paramView, mc);
    }

    @Override
    protected Double getProgress() {
        return progress;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        param.setParam(seekBar.getProgress() / 100d);
        paramView.onValueChanged(param.value);
        modelController.setParams(param);
    }
}
