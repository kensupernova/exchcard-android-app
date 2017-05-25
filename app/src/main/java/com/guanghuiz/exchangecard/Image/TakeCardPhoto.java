package com.guanghuiz.exchangecard.Image;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.guanghuiz.exchangecard.Utils.Utils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Guanghui on 13/4/16.
 */
public class TakeCardPhoto {
    private static final int TAKE_CARDPHOTO_REQUEST = 40;
    private Context context;
    public TakeCardPhoto(Context context){
        this.context = context;
    }

    private void takePictureCamera(){
        PackageManager pm = context.getPackageManager();
        if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            dispatchTakePictureIntent();
        }

    }

    public void dispatchTakePictureIntent(){
        // create camera intent
        Intent startCameraIntent = new Intent();
        startCameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        // startCameraIntent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);

        // check if camera feature is available start the camera intent
        if(startCameraIntent.resolveActivity(context.getPackageManager())!=null){
            //    		create a file
            File photoFile = null;
            try{
                photoFile = Utils.createExternalPublicImageFile("exchangecard");
            } catch(IOException e){
                Log.i("dispatch camera", e.toString());
            }
            // if create image file is successful, start the intent, put the uri of the file as the Uri for intent
            if(photoFile!=null){
                startCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                context.startActivity(startCameraIntent);
            }


        }
    }
}
