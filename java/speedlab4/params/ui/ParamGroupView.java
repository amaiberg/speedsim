package speedlab4.params.ui;

import android.content.Context;
import android.view.ViewGroup;
import speedlab4.params.Param;
import speedlab4.params.ParamGroup;

import java.util.SortedSet;

/**
 * Created by IntelliJ IDEA.
 * User: avnermaiberg
 * Date: 1/25/12
 * Time: 1:35 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ParamGroupView<T, E extends Param<T>, V extends ParamView<E>> extends ParamView<ParamGroup<E>> {
    protected SortedSet<V> paramViews;

    public ParamGroupView(Context context, ParamGroup<E> paramGroup, ViewGroup.LayoutParams parent) {
        super(context, paramGroup, parent);
//        this.paramViews =paramViews;
        //      this.setBackgroundResource(R.drawable.border);

    }

    public void addViews(SortedSet<V> paramViews) {

        this.paramViews = paramViews;
        for (V v : paramViews)
            this.addView(v);
    }

    public SortedSet<V> getViews() {
        return paramViews;
    }


}
