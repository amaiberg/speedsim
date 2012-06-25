package speedlab4.params.ui.listeners;

import speedlab4.model.ModelController;
import speedlab4.params.ParamDouble;
import speedlab4.params.ParamLinkedDouble;
import speedlab4.params.ui.ParamNumberView;
import android.widget.SeekBar;

public class ParamLinkedDoubleListener extends ParamBarListener<ParamLinkedDouble, Double> {
	
    public ParamLinkedDoubleListener(ParamLinkedDouble param, ParamNumberView<ParamLinkedDouble> paramView, ModelController mc) {
        super(param, paramView, mc);
    }
    
    @Override
    protected Double getProgress() {
        return progress;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        param.setParam(seekBar.getProgress() / 100d);
        if (param.linked){ 
        	updateOtherParam();
        }
        paramView.onValueChanged(param.value);
        modelController.setParams(param);
        
    }
   
    private void updateOtherParam(){
    	boolean valsOk = param.checker.checkParams(param, param.pair);
		if (!valsOk){
			// if dependent, change this val
			if (param.pushover){
				param.value = param.checker.changeParamTo(param);
				paramView.onValueChanged(param.value);
			}
			// else change pair's val
			else{
				param.pair.value = param.checker.changeParamTo(param.pair);
				paramView.pairView.onValueChanged(param.pair.value);
			}						
		}
    }

}
