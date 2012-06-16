package speedlab4.ui;

import android.content.Context;
import android.widget.CompoundButton;

/**
 * Created with IntelliJ IDEA.
 * User: mog
 * Date: 5/30/12
 * Time: 11:43 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Switchable<C extends CompoundButton> {
    protected C switchBtn;

    public Switchable(Context c) {

    }

    public abstract void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener oc);

    public abstract void setChecked(boolean b);

    public C getSwitchBtn() {
        return switchBtn;
    }


}
