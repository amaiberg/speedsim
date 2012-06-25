package speedlab4.params;

/**
 * Created by IntelliJ IDEA.
 * User: avnermaiberg
 * Date: 2/3/12
 * Time: 11:35 AM
 * To change this template use File | Settings | File Templates.
 */

public interface Paramable<T, A> {
    public <AD extends ParamDouble> T doDouble(AD pd, A args);

    public <AI extends ParamInteger> T doInt(AI pi, A args);

    public <B extends ParamBoolean> T doBoolean(B pi, A args);
    
    public <LD extends ParamLinkedDouble> T doLinkedDouble(LD pd, A args);

    //  public <G extends ParamGroup> T doGroup(G pi, A args);

    public <G extends ParamGroupDouble> T doGroupDouble(G pi, A args);

}
