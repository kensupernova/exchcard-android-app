package com.guanghuiz.exchangecard.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.guanghuiz.exchangecard.R;
import com.guanghuiz.exchangecard.Utils.SessionManager;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ImageEditActivity extends AppCompatActivity {

    private ImageView imageView;
    private static final int IMAGE_CUT = 13;
    private static final String TAG = "ImageEditActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit);

        Intent intent = getIntent();
        String uri_string = intent.getStringExtra("key_uri");
        //ViewGroup container =(ViewGroup) findViewById(R.id.container_edit_image);

        initComponent(uri_string);

    }

    private boolean initComponent(String uri_string) {

        Log.i(TAG, "edit image:" + uri_string);
        //selectImageBtn = (Button) findViewById(R.id.selectImageBtn);
        //cutImageBtn = (Button) findViewById(R.id.cutImageBtn);
        imageView = (ImageView) findViewById(R.id.imageview);

        Uri uri = Uri.parse(uri_string);

        int dw = getWindowManager().getDefaultDisplay().getWidth(); // 获得屏幕的宽度
        int dh = getWindowManager().getDefaultDisplay().getHeight(); // 2;
        try {


            Bitmap originBitmap= MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            // Bitmap originBitmap = getBitmapFromUri(uri);
            if(null == originBitmap){
                return false;
            }


            int photoW = originBitmap.getWidth();
            int photoH = originBitmap.getHeight();
            // rotate the image
            //Matrix matrix = new Matrix();

            // create scale factor accorinding to dimension of window and dimensions of image
            int scaleFactor = Math.min(photoW/dw, photoH/dh);

            // if scale factor ==0,then photoW must be < dw
            if(scaleFactor ==0) {
                scaleFactor = 1;
            }

            Bitmap bmp = Bitmap.createScaledBitmap(originBitmap, photoW / scaleFactor, photoH / scaleFactor, true);


            imageView.setImageBitmap(bmp);

        } catch (FileNotFoundException e) {

        } catch (IOException e){

        }

        return true;
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        // picture is from camera
        if(requestCode==IMAGE_CUT) {
            if (resultCode == RESULT_OK) {
                //表示裁剪图片
                //注意：getParcelableExtra("data")中"data"标签必须是要和getImageClipIntent()方法中的
                //intent.putExtra("return-data", true);的 "return-data"的后面"data"的标签是一样的
                Bitmap bitmap = data.getParcelableExtra("data");
                imageView.setImageBitmap(bitmap);

            } else {
                Log.i(TAG, "take camera photo fail!");

            }

        }

    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }
}
