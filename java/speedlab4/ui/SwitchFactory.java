package speedlab4.ui;

import android.content.Context;
import android.os.Build;

/**
 * Created with IntelliJ IDEA.
 * User: mog
 * Date: 5/30/12
 * Time: 11:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class SwitchFactory {

    public static Switchable createSwitch(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return new SwitchBtn(c);
        } else return new ToggleBtn(c);
    }


}
