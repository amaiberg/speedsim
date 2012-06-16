package speedlab4.params;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import speedlab4.model.ModelController;
import speedlab4.params.ui.ParamView;
import speedlab4.params.ui.ParamViewFactory;


/**
 * Created by IntelliJ IDEA.
 * User: avnermaiberg
 * Date: 1/15/12
 * Time: 5:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class ParamAdapter extends BaseAdapter {
    private Context mContext;
    private Param[] params;
    private ModelController modelController;
    private ParamViewFactory paramViewFactory;


    public ParamAdapter(Context context, Param[] params, ModelController modelController, ViewGroup.LayoutParams parent) {
        this.mContext = context;
        this.params = params;
        this.modelController = modelController;
        this.paramViewFactory = new ParamViewFactory(modelController, parent);

    }

    public int getCount() {
        return params.length;
    }

    public Object getItem(int i) {
        return params[i];
    }

    public long getItemId(int i) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        Param curParam = params[i];
        ParamView curPV = null;
        curPV = curParam.visit(paramViewFactory, mContext);
        //   curPV.setOnSeekListener(new ParamBarListener(curPV, curParam));


        return curPV;
    }


}
