package speedlab4.params;

/**
 * Created by IntelliJ IDEA.
 * User: avnermaiberg
 * Date: 1/25/12
 * Time: 7:16 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ParamNumber<N extends Number> extends Param<N> {

    public ParamNumber(String name, N value, N min, N max) {
        super(name, value);
        this.min = min;
        this.max = max;

        if (this.value.doubleValue() > max.doubleValue())
            this.value = max;
        else if (this.value.doubleValue() < min.doubleValue())
            this.value = min;
    }

    public ParamNumber(String name, N value,  N min, N max,String description) {
        this(name,value,min,max);
        this.description = description;
    }

    public ParamNumber(String name,N value,  N min, N max, String description,boolean reqRestart){
        this(name,value,min,max,description);
        this.reqRestart = reqRestart;
    }

    public N min, max;

    public void setParam(N val) {
        double tVal = val.doubleValue();
        double tMax = max.doubleValue();
        double tMin = min.doubleValue();
        if (tVal > tMax)
            value = max;
        else if (tVal < tMin)
            value = min;
        else value = val;
    }

}
