package com.bosictsolution.waiterone;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class XCreateImageTestPrintActivity extends AppCompatActivity {

    Button btnCreate;
    LinearLayout createdLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xcreate_image_test_print);

        btnCreate=(Button)findViewById(R.id.btnCreate);
        createdLayout=(LinearLayout)findViewById(R.id.createdLayout);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) createdLayout.getLayoutParams();
        params.width = 380;

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePrintLayoutToBitmap();
            }
        });
    }

    private void changePrintLayoutToBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(createdLayout.getWidth(), createdLayout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        createdLayout.draw(canvas);
        savePrintLayoutToWaiterOneDB(bitmap);
    }

    private String savePrintLayoutToWaiterOneDB(Bitmap bitmapImage){
        File directory = new File(Environment.getExternalStorageDirectory().getPath(), "/WaiterOneDB");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File logoPath=new File(directory,"print58.png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(logoPath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG,100,fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }
}
