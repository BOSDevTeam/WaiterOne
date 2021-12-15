package customfont;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by NweYiAung on 11-02-2017.
 */
public class ArialEditText extends EditText {

    public ArialEditText(Context context, AttributeSet attrs){
        super(context,attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/ARIALN.TTF"));
    }
}
