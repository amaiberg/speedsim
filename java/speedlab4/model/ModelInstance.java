package speedlab4.model;

/**
 * Created by IntelliJ IDEA.
 * User: avnermaiberg
 * Date: 1/5/12
 * Time: 10:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class ModelInstance {

    public double[][] lattice;
    public double x;
    public double[] y;

    public ModelInstance(double[][] lattice, double x, double[] y) {
        this.lattice = lattice;
        this.x = x;
        this.y = y;

    }


}
