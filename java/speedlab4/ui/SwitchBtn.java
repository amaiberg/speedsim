package speedlab4.ui;

import android.content.Context;
import android.widget.CompoundButton;
import android.widget.Switch;

/**
 * Created with IntelliJ IDEA.
 * User: mog
 * Date: 5/30/12
 * Time: 12:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class SwitchBtn extends Switchable<Switch> {

    public SwitchBtn(Context c) {
        super(c);
        switchBtn = new Switch(c);
    }


    @Override
    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener oc) {
        switchBtn.setOnCheckedChangeListener(oc);
    }

    @Override
    public void setChecked(boolean b) {
        switchBtn.setChecked(b);
    }


}
