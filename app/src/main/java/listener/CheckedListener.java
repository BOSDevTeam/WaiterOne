package listener;

/**
 * Created by NweYiAung on 28-12-2016.
 */
public interface CheckedListener {
    void onWaiterCheckedListener(int position);
    void onWaiterUnCheckedListener(int position);
    void onTableCheckedListener(int position);
    void onTableUnCheckedListener(int position);
    void onSubMenuCheckedListener(int position);
    void onSubMenuUnCheckedListener(int position);
    void onItemCheckedListener(int position);
    void onItemUnCheckedListener(int position);
}
