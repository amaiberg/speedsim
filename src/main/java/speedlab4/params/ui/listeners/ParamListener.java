package speedlab4.params.ui.listeners;

import speedlab4.model.ModelController;
import speedlab4.params.Param;
import speedlab4.params.ui.ParamView;

/**
 * Created with IntelliJ IDEA.
 * User: mog
 * Date: 6/10/12
 * Time: 6:13 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ParamListener<P extends Param, V extends ParamView> {
    protected P param;
    protected V paramView;
    protected ModelController modelController;
    protected boolean reqRestart = false;
    public ParamListener(P param, V paramView, ModelController mc){
          this.param = param;
          this.paramView = paramView;
          this.modelController =mc;
          this.reqRestart = param.reqRestart;
    }



}
