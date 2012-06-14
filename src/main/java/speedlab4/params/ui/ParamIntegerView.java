package speedlab4.params.ui;

import android.content.Context;
import android.view.ViewGroup;
import speedlab4.params.ParamInteger;

/**
 * Created by IntelliJ IDEA.
 * User: avnermaiberg
 * Date: 2/3/12
 * Time: 6:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class ParamIntegerView extends ParamNumberView<ParamInteger> {
    @Override
    protected String getMaxStr() {
        return param.max.toString();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected Number getMax() {
        return param.max;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected Number getCur() {
        return param.value;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected Number getDisplayVal() {
        return param.value;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ParamIntegerView(Context context, ParamInteger p, ViewGroup.LayoutParams parent) {
        super(context, p, parent);
    }
}
