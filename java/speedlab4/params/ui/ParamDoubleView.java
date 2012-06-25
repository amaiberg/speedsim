package speedlab4.params.ui;

import android.content.Context;
import android.view.ViewGroup;
import speedlab4.params.ParamDouble;

/**
 * Created by IntelliJ IDEA.
 * User: avnermaiberg
 * Date: 2/3/12
 * Time: 6:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class ParamDoubleView<D extends ParamDouble> extends ParamNumberView<D> {
	
    public ParamDoubleView(Context context, D paramDouble, ViewGroup.LayoutParams parent) {
        super(context, paramDouble, parent);
    }

    @Override
    protected String getMaxStr() {
        return param.max.toString();
    }

    @Override
    protected Number getMax() {
        return param.max * 100;
    }

    @Override
    public Number getCur() {
        return param.value * 100;
    }

    @Override
    protected Double getDisplayVal() {
        return param.value;
    }
}
