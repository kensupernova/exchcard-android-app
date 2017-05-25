package com.guanghuiz.exchangecard.Services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.guanghuiz.exchangecard.Utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;


/**
 * Created by Guanghui on 1/6/16.
 */
public class AddPhotoToGalleryService extends IntentService {
    private static final String ACTION_ADD_PHOTO = "com.guanghuiz.exchangecard.services.action.ADD_PHOTO_TO_GALLERY";
    private static final String EXTRA_PARAM_PATH = "com.guanghuiz.exchangecard.services.extra.PHOTO_FILE_PATH";
    private static final String EXTRA_PARAM_FILENAME = "com.guanghuiz.exchangecard.services.extra.PHOTO_FILE_NAME";
    public static final String RESPONSE_MESSAGE = "ADD_PHOTO_TO_GALLERY_URI";
    private static Context _context;

    public AddPhotoToGalleryService(){
        super("AddPhotoToGalleryService");
    }
    public static void startActionSendPhoto(Context context, String path, String imageFileName) {
        _context = context;
        Intent intent = new Intent(context, AddPhotoToGalleryService.class);
        intent.setAction(ACTION_ADD_PHOTO);

        intent.putExtra(EXTRA_PARAM_PATH, path);
        intent.putExtra(EXTRA_PARAM_FILENAME, imageFileName);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            final String photo_file_path = intent.getStringExtra(EXTRA_PARAM_PATH);
            final String imageFileName = intent.getStringExtra(EXTRA_PARAM_PATH);

            Uri imageUri = null;

            try {
                File file = new File(photo_file_path);
                Log.i("Utils", "add to gallery, " +file.getAbsolutePath());

                String thumbnail  = MediaStore.Images.Media.insertImage(_context.getContentResolver(),
                        photo_file_path, imageFileName, imageFileName);
                Log.i("Utils", "add to gallery, thumbnail:" + thumbnail );
                imageUri= Uri.parse(thumbnail);

                //                ContentValues image = new ContentValues();
                //
                //                image.put(MediaStore.Images.Media.TITLE, imageFileName);
                //                image.put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName);
                //                image.put(MediaStore.Images.Media.DESCRIPTION, imageFileName);
                //                image.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                //                image.put(MediaStore.Images.Media.ORIENTATION, 0);
                //
                //                File parent = file.getParentFile();
                //                String parent_path = parent.toString().toLowerCase();
                //                String name = parent.getName().toLowerCase();
                //                image.put(MediaStore.Images.ImageColumns.BUCKET_ID, parent_path.hashCode());
                //                image.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, name);
                //                image.put(MediaStore.Images.Media.SIZE, file.length());
                //                image.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                //
                //                Uri result = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, image);
                //
                //                return result;
            } catch (FileNotFoundException e){
                Log.i("Utils", "add to gallery, errors" );
            }

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("ADD_PHOTO_TO_GALLERY_URI");
            broadcastIntent.putExtra(RESPONSE_MESSAGE, imageUri);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);

            _context.sendBroadcast(broadcastIntent);


        }
    }
}
