package common;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.andprn.request.android.RequestHandler;

import java.io.IOException;

public class NTPrinterConnection {

    private static final String TAG = "WiFiConnectMenu";
    private PrinterWiFiPort wifiPort= PrinterWiFiPort.getInstance();
    private Thread hThread;
    String ioException;

    private Context context;
    public static boolean isPrinterReady;

    public NTPrinterConnection(Context context){
        this.context=context;
        try {
            wifiDisConn();
        }catch (InterruptedException e){
            Log.e(TAG,e.getMessage(),e);
        }
        catch (IOException e){
            Log.e(TAG,e.getMessage(),e);
        }
    }

    public boolean connectPrinter(String printerIPAddress){
        wifiPort = PrinterWiFiPort.getInstance();
        try{
            wifiConn(printerIPAddress);
        }
        catch (IOException e)
        {
            Log.e(TAG,e.getMessage(),e);
        }
        return isPrinterReady;
    }

    private void wifiConn(String ipAddr) throws IOException
    {
        new connTask().execute(ipAddr);
    }

    private void wifiDisConn() throws IOException, InterruptedException
    {
        wifiPort.disconnect();
        if(hThread!=null)hThread.interrupt();
    }

    private class connTask extends AsyncTask<String, Void, Integer> {
        private final ProgressDialog dialog = new ProgressDialog(context);

        @Override
        protected void onPreExecute()
        {
            dialog.setTitle("Printer Connect");
            dialog.setMessage("Connecting");
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... params)
        {
            Integer retVal = null;
            try
            {
                wifiPort.connect(params[0]);
                retVal = new Integer(0);
            }
            catch (IOException e)
            {
                Log.e(TAG,e.getMessage(),e);
                retVal = new Integer(-1);
                ioException=e.getMessage();
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(Integer result)
        {
            if(result.intValue() == 0)
            {
                RequestHandler rh = new RequestHandler();
                hThread = new Thread(rh);
                hThread.start();
                if(dialog.isShowing()) {
                    dialog.dismiss();
                    isPrinterReady=true;
                }
            }
            else
            {
                if(dialog.isShowing())
                    dialog.dismiss();
                AlertView.showAlert("Failed", "Check Devices!"+ioException, context);
            }
            super.onPostExecute(result);
        }
    }
}
