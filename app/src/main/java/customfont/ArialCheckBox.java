package customfont;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Created by NweYiAung on 11-02-2017.
 */
public class ArialCheckBox extends CheckBox {

    public ArialCheckBox(Context context, AttributeSet attrs){
        super(context,attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/ARIALN.TTF"));
    }
}
