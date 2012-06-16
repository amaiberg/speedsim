package speedlab4.params;

public class ParamDouble extends ParamNumber<Double> {


    public ParamDouble(String name, Double value, double min, double max,String description, boolean reqRestart) {
        super(name, value, min, max,description,reqRestart);
    }

    public ParamDouble(String name, Double value, double min, double max) {
        super(name, value, min, max);
    }

    public ParamDouble(String name, Double value, double min) {
        this(name, value, min, 1d);
    }

    public ParamDouble(String name, Double value) {
        this(name, value, 0d, 1d);
    }



    public <N, E, T extends Paramable<E, N>> E visit(T param, N arg) {
        return param.doDouble(this, arg);
    }

}
