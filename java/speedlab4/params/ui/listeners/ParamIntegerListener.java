package speedlab4.params.ui.listeners;

import speedlab4.model.ModelController;
import speedlab4.params.ParamInteger;
import speedlab4.params.ui.ParamNumberView;

/**
 * Created with IntelliJ IDEA.
 * User: mog
 * Date: 6/8/12
 * Time: 3:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class ParamIntegerListener extends ParamBarListener<ParamInteger, Integer> {
    public ParamIntegerListener(ParamNumberView pv, ParamInteger paramInteger, ModelController mc) {
        super(paramInteger,pv, mc);
    }


    @Override
    protected Integer getProgress() {
        return (int) progress;  //To change body of implemented methods use File | Settings | File Templates.
    }


}
