package common;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.bosictsolution.waiterone.MultiPrinterSettingActivity;
import com.bosictsolution.waiterone.print.GPrinterCommand;
import com.bosictsolution.waiterone.print.PrintPic;
import com.bosictsolution.waiterone.print.PrintQueue;
import com.bosictsolution.waiterone.print.PrintUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by yefeng on 6/2/15.
 * github:yefengfreedom
 * <p/>
 * printNetworkPrinter ticket service
 */
public class BtService extends IntentService {

    public BtService() {
        super("BtService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public BtService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }
        if (intent.getAction().equals(PrintUtil.ACTION_PRINT_TEST)) {
            printTest();
        } else if (intent.getAction().equals(PrintUtil.ACTION_PRINT)) {
            print(intent.getByteArrayExtra(PrintUtil.PRINT_EXTRA));
        } else if (intent.getAction().equals(PrintUtil.ACTION_PRINT_TICKET)) {
        } else if (intent.getAction().equals(PrintUtil.ACTION_PRINT_BITMAP)) {
            if(SystemSetting.isTestPrint) printBitmapTest(MultiPrinterSettingActivity.WidthSize);
            else printBitmapOrder(intent.getStringExtra("STypeName"));
        } else if (intent.getAction().equals(PrintUtil.ACTION_PRINT_PAINTING)) {
            printPainting();
        }
    }

    private void printTest() {
        try {
            ArrayList<byte[]> bytes = new ArrayList<byte[]>();
            String message = "蓝牙打印测试\n蓝牙打印测试\n蓝牙打印测试\n\n";
            bytes.add(com.bosictsolution.waiterone.print.GPrinterCommand.reset);
            bytes.add(message.getBytes("gbk"));
            bytes.add(com.bosictsolution.waiterone.print.GPrinterCommand.print);
            bytes.add(com.bosictsolution.waiterone.print.GPrinterCommand.print);
            bytes.add(com.bosictsolution.waiterone.print.GPrinterCommand.print);
            PrintQueue.getQueue(getApplicationContext()).add(bytes);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void print(byte[] byteArrayExtra) {
        if (null == byteArrayExtra || byteArrayExtra.length <= 0) {
            return;
        }
        PrintQueue.getQueue(getApplicationContext()).add(byteArrayExtra);
    }

    private void printBitmapTest(int size) {
        BufferedInputStream bis=null;
        try {
            if(size==58) {
                bis = new BufferedInputStream(getAssets().open("print58.png"));
            }
            else if(size==80) {
                bis = new BufferedInputStream(getAssets().open("print80.png"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Bitmap bitmap = BitmapFactory.decodeStream(bis);
        PrintPic printPic = PrintPic.getInstance();
        printPic.init(bitmap);
        if (null != bitmap) {
            if (bitmap.isRecycled()) {
                bitmap = null;
            } else {
                bitmap.recycle();
                bitmap = null;
            }
        }
        byte[] bytes = printPic.printDraw();
        ArrayList<byte[]> printBytes = new ArrayList<byte[]>();
        printBytes.add(com.bosictsolution.waiterone.print.GPrinterCommand.reset);
        printBytes.add(com.bosictsolution.waiterone.print.GPrinterCommand.print);
        printBytes.add(bytes);
        Log.e("BtService", "image bytes size is :" + bytes.length);
        printBytes.add(com.bosictsolution.waiterone.print.GPrinterCommand.print);
        printBytes.add(GPrinterCommand.cut);
        PrintQueue.getQueue(getApplicationContext()).add(printBytes);
    }

    private void printBitmapOrder(String sTypeName) {
        Bitmap bitmap=null;
        File directory = new File(Environment.getExternalStorageDirectory().getPath(), "/WaiterOneDB");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File filePath=new File(directory,sTypeName+"_order.png");
        if(filePath.exists()) bitmap = BitmapFactory.decodeFile(filePath.getAbsolutePath());
        PrintPic printPic = PrintPic.getInstance();
        printPic.init(bitmap);
        if (null != bitmap) {
            if (bitmap.isRecycled()) {
                bitmap = null;
            } else {
                bitmap.recycle();
                bitmap = null;
            }
        }
        byte[] bytes = printPic.printDraw();
        ArrayList<byte[]> printBytes = new ArrayList<byte[]>();
        printBytes.add(com.bosictsolution.waiterone.print.GPrinterCommand.reset);
        printBytes.add(com.bosictsolution.waiterone.print.GPrinterCommand.print);
        printBytes.add(bytes);
        Log.e("BtService", "image bytes size is :" + bytes.length);
        printBytes.add(com.bosictsolution.waiterone.print.GPrinterCommand.print);
        PrintQueue.getQueue(getApplicationContext()).add(bytes);
    }

    private void printPainting() {
        byte[] bytes = PrintPic.getInstance().printDraw();
        ArrayList<byte[]> printBytes = new ArrayList<byte[]>();
        printBytes.add(com.bosictsolution.waiterone.print.GPrinterCommand.reset);
        printBytes.add(com.bosictsolution.waiterone.print.GPrinterCommand.print);
        printBytes.add(bytes);
        Log.e("BtService", "image bytes size is :" + bytes.length);
        printBytes.add(com.bosictsolution.waiterone.print.GPrinterCommand.print);
        PrintQueue.getQueue(getApplicationContext()).add(bytes);
    }
}