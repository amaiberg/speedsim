package speedlab4.params;

/**
 * Created with IntelliJ IDEA.
 * User: mog
 * Date: 5/24/12
 * Time: 10:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class ParamBoolean extends Param<Boolean> {


    public ParamBoolean(String name, Boolean value, String description) {
        super(name, value, description);
    }

    @Override
    public void setParam(Boolean val) {
        this.value = val;
    }


    public ParamBoolean(String name, Boolean value) {
        super(name, value);
    }



    public <N, E, T extends Paramable<E, N>> E visit(T param, N arg) {
        return param.doBoolean(this, arg);  //To change body of implemented methods use File | Settings | File Templates.
    }
}
