package speedlab4.params;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: avnermaiberg
 * Date: 1/25/12
 * Time: 5:40 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ParamGroup<P extends Param> extends Param<Set<P>> {


    public ParamGroup(String name, Set<P> params) {
        super(name, params);
    }

    public ParamGroup(String name, P... params) {
        this(name, newSetFromArgs(params));
    }


    @Override
    public void setParam(Set<P> val) {
        this.value = val;
    }

    public Collection<P> getParams() {
        return this.value;
    }

    /*
    @Override
    public abstract <N, E, T extends Paramable<E, N>> E visit(T param, N arg) {
        return param.doGroup(this, arg);
    }
    */

    //public abstract <G extends ParamGroup> G  getThis(String name, Set<P> params);


    public static <T> Set<T> newSetFromArgs(T... params) {
        Set<T> parameters = new HashSet<T>();
        Collections.addAll(parameters, params);
        return parameters;
    }
}
