package speedlab4.params;

/**
 * Created with IntelliJ IDEA.
 * User: mog
 * Date: 6/2/12
 * Time: 9:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class ParamGroupDouble extends ParamGroup<ParamDouble> {


    public ParamGroupDouble(String name, ParamDouble... params) {
        super(name, params);
    }


    public <N, E, T extends Paramable<E, N>> E visit(T param, N arg) {
        return param.doGroupDouble(this, arg);
    }
}
