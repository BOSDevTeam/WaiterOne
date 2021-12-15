package listener;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by NweYiAung on 14-02-2017.
 */
public interface OrderButtonClickListener {

    void onPlusButtonClickListener(int position,EditText editText);
    void onMinusButtonClickListener(int position, EditText editText);
    void onTasteButtonClickListener(int position,TextView textView);
    void onCancelButtonClickListener(int position,View view);
    void onCalculatorButtonClickListener(int position,EditText editText);
    void onPriceButtonClickListener(int position,Button btnPrice);
}
