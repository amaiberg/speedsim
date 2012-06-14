package speedlab4.params.ui.listeners;

import android.widget.SeekBar;
import speedlab4.model.ModelController;
import speedlab4.params.ParamDouble;
import speedlab4.params.ui.ParamDoubleView;
import speedlab4.params.ui.ParamGroupDoubleView;
import speedlab4.params.ui.ParamNumberView;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: mog
 * Date: 6/8/12
 * Time: 3:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class ParamGroupDoubleListener extends ParamDoubleListener {

    private SortedSet<ParamDoubleView> paramGroupDouble;
    List<ParamDoubleView> sortedList;
    Comparator<ParamDoubleView> viewComparator;

    public ParamGroupDoubleListener(ParamNumberView pv, ParamDouble p, ParamGroupDoubleView pgd, ModelController mc) {

        super( p,pv ,mc);
        this.paramGroupDouble = pgd.getViews();
        this.sortedList = new LinkedList<ParamDoubleView>(paramGroupDouble);
        this.viewComparator = new ParamDoubleViewComparator();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //    super.onStopTrackingTouch(seekBar);
        updateOtherParams(seekBar);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        super.onProgressChanged(seekBar, i, b);
        //           updateOtherParams(seekBar);

    }

    private void updateOtherParams(SeekBar seekBar) {
        double prevVal, curVal, delta, distDelta;
        prevVal = param.value;
        super.onStopTrackingTouch(seekBar);

        curVal = param.value;
        delta = prevVal - curVal;
        distDelta = delta / (paramGroupDouble.size() - 1);

        Collections.sort(sortedList, viewComparator);

        if (distDelta >= 0)
            Collections.reverse(sortedList);

        double offset = 0d, epsilon = Math.pow(10, -6);

        for (ParamDoubleView pi : sortedList)
            if (!param.equals(pi.param)) {

                double ival = pi.param.value;

                double newival = pi.param.value + distDelta + offset;

                double displayVal = (newival - epsilon < 0) ? 0 : newival;
                pi.param.setParam(displayVal);

                double excess = 0d, applied = 0d;

                if (newival > 1) {
                    excess = newival - param.max;
                    applied = (ival + distDelta > 1) ? 0 : 1 - (ival + distDelta);
                } else if (newival < 0) {
                    excess = newival;
                    applied = (ival + distDelta < 0) ? 0 : -(ival + distDelta);
                }

                offset += excess + applied;
                pi.paramBar.setProgress((int) (pi.param.value * 100));
                pi.onValueChanged(pi.param.value);
            }
    }

    private class ParamDoubleViewComparator implements Comparator<ParamDoubleView> {

        @Override
        public int compare(ParamDoubleView p1, ParamDoubleView p2) {
            Double p1Val = p1.getCur().doubleValue(), p2Val = p2.getCur().doubleValue();
            return p1Val.compareTo(p2Val);
        }
    }
}

