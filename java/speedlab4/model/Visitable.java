package speedlab4.model;

import speedlab4.params.Paramable;

/**
 * Created by IntelliJ IDEA.
 * User: avnermaiberg
 * Date: 2/3/12
 * Time: 11:35 AM
 * To change this template use File | Settings | File Templates.
 */
public interface Visitable {
    public <N, E, T extends Paramable<E, N>> E visit(T param, N arg);


}