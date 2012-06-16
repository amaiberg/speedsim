package speedlab4.params.ui;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import speedlab4.ui.SwitchFactory;
import speedlab4.ui.Switchable;
import speedlab4.params.ParamBoolean;

/**
 * Created with IntelliJ IDEA.
 * User: mog
 * Date: 5/24/12
 * Time: 11:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class ParamBooleanView extends ParamView<ParamBoolean> {
    private LinearLayout.LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

    public ParamBooleanView(Context context, ParamBoolean paramBoolean, ViewGroup.LayoutParams parent) {
        super(context, paramBoolean, parent);

        Switchable toggleButton = SwitchFactory.createSwitch(context);
        toggleButton.setChecked(param.value);

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                param.setParam(b);
            }
        });

        this.setOrientation(LinearLayout.VERTICAL);
        lp.gravity = Gravity.LEFT;
        CompoundButton swtchBtn = toggleButton.getSwitchBtn();
        swtchBtn.setLayoutParams(lp);
        this.addView(swtchBtn);

    }


}
