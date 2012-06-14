package speedlab4.params.ui;

import android.content.Context;
import android.view.ViewGroup;
import speedlab4.params.ParamDouble;
import speedlab4.params.ParamGroup;
import speedlab4.params.ui.listeners.ParamBarListener;

/**
 * Created with IntelliJ IDEA.
 * User: mog
 * Date: 6/1/12
 * Time: 12:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class ParamGroupDoubleView extends ParamGroupView<Double, ParamDouble, ParamDoubleView> {


    public ParamGroupDoubleView(Context context, ParamGroup<ParamDouble> paramDoubleParamGroup, ViewGroup.LayoutParams parent) {
        super(context, paramDoubleParamGroup, parent);

    }

    public void setOnSeekListeners(ParamBarListener<ParamDouble, Double> listener) {
        for (ParamDoubleView pdv : paramViews) {
            pdv.setOnSeekListener(listener);
        }
    }


}
