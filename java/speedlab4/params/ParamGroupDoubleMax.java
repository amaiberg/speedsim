package speedlab4.params;

/**
 * Created with IntelliJ IDEA.
 * User: mog
 * Date: 6/6/12
 * Time: 10:02 AM
 * To change this template use File | Settings | File Templates.
 */
public class ParamGroupDoubleMax extends ParamGroupDouble {


    public ParamGroupDoubleMax(String name, double max, ParamDouble... params) {
        super(name, params);
        double sum = 0;

        for (ParamDouble p : params) {
            sum += p.value;
            p.max = max;
        }

        if (sum != max)
            throw new RuntimeException("sum of parameters is " + sum + ", but values must add up to " + max);

    }
}
