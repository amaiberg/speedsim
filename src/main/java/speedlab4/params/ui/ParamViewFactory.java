package speedlab4.params.ui;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import speedlab4.model.ModelController;
import speedlab4.params.*;
import speedlab4.params.ui.listeners.ParamDoubleListener;
import speedlab4.params.ui.listeners.ParamGroupDoubleListener;
import speedlab4.params.ui.listeners.ParamIntegerListener;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: avnermaiberg
 * Date: 2/3/12
 * Time: 12:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class ParamViewFactory implements Paramable<ParamView, Context> {
    private ModelController modelController;
    //private SpeedSimActivity activity;
    private ViewGroup.LayoutParams parent;

    public ParamViewFactory(ModelController modelController, ViewGroup.LayoutParams parent) {//, SpeedSimActivity activity) {
        //  this.activity =activity;
        this.modelController = modelController;
        this.parent = parent;
    }



    public ParamDoubleView doDouble(ParamDouble pd, Context c) {
        ParamDoubleView pv = new ParamDoubleView(c, pd, parent);
        pv.setOnSeekListener(new ParamDoubleListener(pd, pv, modelController));
        return pv;
    }

    public ParamDoubleView doDouble(ParamDouble pd, Context c, ViewGroup.LayoutParams parent) {
        ParamDoubleView pv = new ParamDoubleView(c, pd, parent);
        pv.setOnSeekListener(new ParamDoubleListener(pd, pv, modelController));
        return pv;
    }

    public ParamIntegerView doInt(ParamInteger pi, Context c) {
        ParamIntegerView pv = new ParamIntegerView(c, pi, parent);
        pv.setOnSeekListener(new ParamIntegerListener(pv, pi, modelController));

        return pv;//To change body of implemented methods use File | Settings | File Templates.
    }

    public ParamBooleanView doBoolean(ParamBoolean pi, Context c) {
        return new ParamBooleanView(c, pi, parent);  //To change body of implemented methods use File | Settings | File Templates.
    }


    public <G extends ParamGroup> ParamGroupView doGroup(G pi, Context args) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <G extends ParamGroupDouble> ParamGroupDoubleView doGroupDouble(G doubleGroup, Context args) {
        SortedSet<ParamDoubleView> paramDoubleViews = new TreeSet<ParamDoubleView>(new Comparator<ParamDoubleView>() {
            @Override
            public int compare(ParamDoubleView p1, ParamDoubleView p2) {
                Double p1Val = p1.getCur().doubleValue(), p2Val = p2.getCur().doubleValue();
                return p1Val.compareTo(p2Val);
            }
        });

        ParamGroupDoubleView paramGroupDoubleView = new ParamGroupDoubleView(args, doubleGroup, parent);
        for (ParamDouble paramDouble_i : doubleGroup.getParams()) {
            ParamDoubleView paramDoubleView = doDouble(paramDouble_i, args, new LinearLayout.LayoutParams(0, 0));
            paramDoubleViews.add(paramDoubleView);
        }
        paramGroupDoubleView.addViews(paramDoubleViews);

        for (ParamDoubleView v : paramGroupDoubleView.getViews()) {
            v.setOnSeekListener(new ParamGroupDoubleListener(v, v.param, paramGroupDoubleView, modelController));
        }

        return paramGroupDoubleView;
    }


}
