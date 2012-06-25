package speedlab4.params.ui;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import speedlab4.model.ModelController;
import speedlab4.params.*;
import speedlab4.params.ui.listeners.ParamDoubleListener;
import speedlab4.params.ui.listeners.ParamGroupDoubleListener;
import speedlab4.params.ui.listeners.ParamIntegerListener;
import speedlab4.params.ui.listeners.ParamLinkedDoubleListener;

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
    private SortedMap<String, ParamNumberView<ParamLinkedDouble>> orphanPairViewList; //for linked params to find their pair

    public ParamViewFactory(ModelController modelController, ViewGroup.LayoutParams parent) {//, SpeedSimActivity activity) {
        //  this.activity =activity;
        this.modelController = modelController;
        this.parent = parent;
        orphanPairViewList = new TreeMap<String, ParamNumberView<ParamLinkedDouble>>();
    }


    /*
     * Sets up a view with a given Param,
     * then sets up a listener with the view and its Param.
     */
    public ParamDoubleView<ParamDouble> doDouble(ParamDouble pd, Context c) {
        ParamDoubleView<ParamDouble> pv = new ParamDoubleView<ParamDouble>(c, pd, parent);
        pv.setOnSeekListener(new ParamDoubleListener(pd, pv, modelController));
        return pv;
    }

    public ParamDoubleView<ParamDouble> doDouble(ParamDouble pd, Context c, ViewGroup.LayoutParams parent) {
        ParamDoubleView<ParamDouble> pv = new ParamDoubleView<ParamDouble>(c, pd, parent);
        pv.setOnSeekListener(new ParamDoubleListener(pd, pv, modelController));
        return pv;
    }
    
    public ParamDoubleView<ParamLinkedDouble> doLinkedDouble(ParamLinkedDouble pd, Context c) {
        ParamDoubleView<ParamLinkedDouble> pv = new ParamDoubleView<ParamLinkedDouble>(c, pd, parent);
        //try to match up with pair
        if (pd.linked){
        	// look up pair's name in orphanPairViewList
        	if (orphanPairViewList.containsKey(pd.pair.name)){
        		ParamNumberView<ParamLinkedDouble> pairView = orphanPairViewList.get(pd.pair.name);
        		pv.setPairView(pairView);
        		pairView.setPairView(pv);
        		orphanPairViewList.remove(pd.pair.name);
        	}
        	else{
        		// otherwise put self on orphan list
        		orphanPairViewList.put(pd.name, pv);
        	}
        }
        else throw new IllegalStateException("ParamLinkedDouble not linked");
        pv.setOnSeekListener(new ParamLinkedDoubleListener(pd, pv, modelController));
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

    //@Override
    public <G extends ParamGroupDouble> ParamGroupDoubleView doGroupDouble(G doubleGroup, Context args) {
        SortedSet<ParamDoubleView<ParamDouble>> paramDoubleViews = new TreeSet<ParamDoubleView<ParamDouble>>(new Comparator<ParamDoubleView<ParamDouble>>() {
            //@Override
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

        for (ParamDoubleView<ParamDouble> v : paramGroupDoubleView.getViews()) {
            v.setOnSeekListener(new ParamGroupDoubleListener(v, v.param, paramGroupDoubleView, modelController));
        }

        return paramGroupDoubleView;
    }


}
