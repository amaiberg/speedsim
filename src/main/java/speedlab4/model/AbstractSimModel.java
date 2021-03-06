package speedlab4.model;

import android.util.Log;
import speedlab4.params.*;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: avnermaiberg
 * Date: 12/23/11
 * Time: 2:57 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractSimModel<I extends ParamInteger, D extends ParamDouble> implements Serializable, Paramable<Object, Object> {


    protected SortedMap<String, Param> params = new TreeMap<String, Param>();
    protected SortedMap<String, D> dParams = new TreeMap<String, D>();
    protected SortedMap<String, I> iParams = new TreeMap<String, I>();
    protected SortedMap<String, ParamBoolean> bParams = new TreeMap<String, ParamBoolean>();
    //    protected SortedMap<String,Param> defaults;
    protected Map<Integer, Integer> colorMap;
    protected AbstractAnalyzer analyzer;
    protected String name;
    protected I latticeSize;
      //test
    public AbstractSimModel(int latticeSize) {

        this.latticeSize = getParamInteger(latticeSize, "Lattice Size", 50, 100, "Size N of the NxN simulation grid", true);
        System.out.println(this.latticeSize.value + "");
        Log.i("lvalue??", "=" + this.latticeSize.value);
        iParams.put(this.latticeSize.name, this.latticeSize);
        params.put(this.latticeSize.name, this.latticeSize);
    }


    //initialize lattice
    protected abstract void init();

    //return next model instance
    public abstract double[][] next(double time);

    //return color of cell given state
    public abstract int getColor(int state);

    //return first lattice instance
    public abstract double[][] first();

    public abstract I getParamInteger(int value, String name, int min, int max, String description, boolean reqRestart);
    // public abstract P getParamDouble(double value, String name, double min, double max);

    public abstract D getParamDouble(String name, double value, double min, double max, String description, boolean reqRestart);


    public String getName() {
        return name;
    }

    protected double displayEvery = 1;

    public void restart() {
        init();
    }

    public void setParams(Param... params) {
        for (Param param : params) {
            this.params.put(param.name, param);
            param.visit(this, null);
        }
    }

    public void setParam(Param param) {
        this.params.put(param.name, param);
        param.visit(this, null);
    }

    public Set<String> getParamNames() {
        return params.keySet();
    }



    public Param[] getParams() {
        return params.values().toArray(new Param[]{});
    }


    public AbstractAnalyzer getAnalyzer() {
        return analyzer;
    }


    protected int getSize() {
        return this.latticeSize.value;
    }

    // public abstract P getParam(double value, String name);

    //public abstract P getParam(double value, String name, double min, double max);

    // public abstract I getParamInteger(int value, String name, int min, int max);


    public AbstractSimModel(int latticeSize, Param[] params) {
        this(latticeSize);
        for (Param p : params) {
            this.params.put(p.name, p);
            p.visit(this, null);
        }
    }

    public D DP(String name, double value, double min, double max, String description, boolean reqRestart) {
        D dp = getParamDouble(name, value, min, max, description, reqRestart);
        setParam(dp);
        return dp;
    }

    public D DP(String name, double value, double min, double max) {
        return DP(name, value, min, max, "", false);

    }

    public I IP(String name, int value, int min, int max, String description, boolean reqRestart) {
        I ip = getParamInteger(value, name, min, max, description, reqRestart);
        setParam(ip);
        return ip;
    }

    public I IP(String name, int value, int min, int max) {
        return IP(name, value, min, max, "", false);
    }


    public <AD extends ParamDouble> Object doDouble(AD pd, Object v) {
        dParams.put(pd.name, (D) pd);
        return null;
    }

    public <AI extends ParamInteger> Object doInt(AI pd, Object v) {

        iParams.put(pd.name, (I) pd);
        return null;
    }

    public <L extends ParamBoolean> Object doBoolean(L pd, Object v) {
        bParams.put(pd.name, pd);
        return null;
    }

    public <G extends ParamGroupDouble> Object doGroupDouble(G groupDouble, Object o) {
        for (ParamDouble pd : groupDouble.getParams())
            dParams.put(pd.name, (D) pd);
        return null;
    }


}



