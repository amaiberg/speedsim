package speedlab4.params;

/**
 * Created by IntelliJ IDEA.
 * User: avnermaiberg
 * Date: 1/25/12
 * Time: 7:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class ParamInteger extends ParamNumber<Integer> {

    public ParamInteger(String name, Integer value, Integer min, Integer max) {
        super(name, value, min, max);
    }

    public ParamInteger(String name, Integer value, Integer min, Integer max, String description, boolean reqRestart) {
        super(name, value, min, max,description,reqRestart);
    }

    public <N, E, T extends Paramable<E, N>> E visit(T param, N arg) {
        return param.doInt(this, arg);
    }
}

