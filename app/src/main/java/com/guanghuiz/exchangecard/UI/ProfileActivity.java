package com.guanghuiz.exchangecard.UI;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.guanghuiz.exchangecard.R;
import com.guanghuiz.exchangecard.Utils.SessionManager;
import com.guanghuiz.exchangecard.Utils.Utils;
import com.guanghuiz.exchangecard.Database.UserSQLiteHelper;
import com.guanghuiz.exchangecard.Database.model.MailAddress;
import com.guanghuiz.exchangecard.Services.SendPhotoIntentService;
import com.soundcloud.android.crop.Crop;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private static final int TAKE_PHOTO_REQUEST = 10;
    private static final int SELECT_GALLERY_REQUEST = 11;

    private static ImageView prof_btn_avatar;
    private SessionManager sessionManager;
    public static final int THIS_ACTIVITY_INDEX = 4;
    public TextView txt_profile_text_username;

    private TextView text_name ;
    private TextView text_address;
    private TextView text_postcode ;

    private SharedPreferences settings;

    // whether it is taking new photo or crop photo, the filename and path to the file
    private static String mCurrentPhotoPath;
    private static String mCurrentPhotoFileName;

    // store the photo file and uri of the cropped photo
    private File dest_crop_photo_file = null;
    private Uri dest_crop_uri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle(getResources().getString(R.string.my_personal_profile));



        prof_btn_avatar = (ImageView) findViewById(R.id.profile_btn_avatar);
        // profile text username
        txt_profile_text_username = (TextView) findViewById(R.id.profile_text_usernamee);

        text_name = (TextView) findViewById(R.id.text_name);
        text_address = (TextView) findViewById(R.id.text_address);
        text_postcode = (TextView) findViewById(R.id.text_postcode);

        if(null!= prof_btn_avatar) {
            prof_btn_avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSelectAlert();
                    // showSelectAlertFragment();
                }
            });
        }

        if(null !=sessionManager.getUserPrefsName() && sessionManager.isLoggedIn()) {

            settings = this.getSharedPreferences(
                    sessionManager.getUserPrefsName(), 0);
            // the internal avatar file path
            String avatar_path = settings.getString("key_avatar_path", "");

            setAvatarPhoto(avatar_path);

            txt_profile_text_username.setText(sessionManager.getUserName());

            final String current_username = sessionManager.getUserName();

            if (current_username !="" && null!=current_username){
                UserSQLiteHelper helper = new UserSQLiteHelper(getApplicationContext());
                MailAddress address = helper.getMailAddress(current_username);

                if(null != address) {
                    text_name.setText(address.getName());
                    text_address.setText(address.getAddress());
                    text_postcode.setText(address.getPostcode());
                } else{
                    Log.i(TAG, "profile, get address is null");
                }

            }
        }



    }

    /**
     * when back button is clicked
     */

    @Override
    public void onBackPressed() {
        returnBackToTab();
        finish();
        //super.onBackPressed();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                //finish();
                returnBackToTab();
                break;
            default:
                break;


        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        // picture is from camera
        if(requestCode==TAKE_PHOTO_REQUEST) {
            if (resultCode == RESULT_OK) {

                Uri imageUri =Utils.addPicToGallery(this, mCurrentPhotoPath, mCurrentPhotoFileName);
                if(null!= imageUri) {
                    Log.i(TAG, "add to gallery, uri: "+imageUri.toString());
                    // startCrop(imageUri);
                    startUCrop(imageUri);
                }

            } else {
                Log.i(TAG, "take camera photo fail!");

            }

        } else if(requestCode == SELECT_GALLERY_REQUEST){
            // picture is from gallery
            Uri imageUri = data.getData();
            Log.i(TAG, "select picture from gallery, uri: " + imageUri.toString());
            // crop image
            // startCrop(imageUri);
            startUCrop(imageUri);

        //// after croping, set to avator photo and send to server
        } else if (resultCode == RESULT_OK &&requestCode == Crop.REQUEST_CROP ){
            if(null!= dest_crop_uri) {
                setPicToAvatarAndSend2Server(dest_crop_uri, mCurrentPhotoPath);

            }
        } else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            Uri resultUri = UCrop.getOutput(data);
            setPicToAvatarAndSend2Server(resultUri, mCurrentPhotoPath);

        }

    }

    private void returnBackToTab(){
        Utils.jump_to_mainactivity(this,
                this.THIS_ACTIVITY_INDEX,
                2,
                sessionManager.isLoggedIn(),
                sessionManager.getUserName(),
                sessionManager.getProfileId());

        setResult(Activity.RESULT_OK);
        finish();
    }

    private void setAvatarPhoto(String avatar_path){
        Log.i(TAG, "SETTING AVATAR PHOTO FROM INTERNAL FILE: " + avatar_path);

        if(""!=avatar_path && !avatar_path.isEmpty()) {

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds =false;
            bmOptions.inSampleSize = 1;
            Bitmap bitmap = BitmapFactory.decodeFile(avatar_path, bmOptions);

            prof_btn_avatar.setImageBitmap(bitmap);
        }
    }

    private void showSelectAlert(){
        // Builder(context) must be the current activity context
        // otherwise you need to use dialogframent
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // builder.setView(inflater.inflate(R.layout.dialog_signin, null))
        builder.setItems(R.array.select_pic_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item);
                        switch (which){
                            case 0:
                                Log.i(TAG, "clicked camera button");
                                takePictureCamera();
                                break;
                            case 1:
                                Log.i(TAG, "clicked pictures button");
                                selectFromGallery();
                                break;

                            case 2:
                                String original_avatar_uri_2 = settings.getString("key_original_image_uri", "");
                                startUCrop(Uri.parse(original_avatar_uri_2));
                                break;
                        }

                    }
                });

        builder.create().show();
    }


    /**
     * USING CAMERA TO TAKE PICTURES AND SEND BACK TO THIS ACTIVITY
     */
    public void takePictureCamera(){
        PackageManager pm = getPackageManager();
        if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            dispatchTakePictureIntent();
        }

    }

    /**
     * start camera activity
     */

    public void dispatchTakePictureIntent(){
        // create camera intent
        Intent startCameraIntent = new Intent();
        startCameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        // startCameraIntent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);

        // check if camera feature is available start the camera intent
        if(startCameraIntent.resolveActivity(getPackageManager())!=null){
            //    		create a file
            File taken_photo_temp_file = null;
            try{
                taken_photo_temp_file = Utils.createExternalPublicImageFile("exchangecard");
                Log.i(TAG, taken_photo_temp_file.getAbsolutePath());

                mCurrentPhotoPath = taken_photo_temp_file.getAbsolutePath();
            } catch(IOException e){
                Log.i("dispatch camera", e.toString());
            }
            // if create image file is successful, start the intent, put the uri of the file as the Uri for intent
            if(taken_photo_temp_file!=null){
                startCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(taken_photo_temp_file));
                startActivityForResult(startCameraIntent, TAKE_PHOTO_REQUEST);
            }


        }
    }



    //get image from uri, scale the image, save the scaled image
    private boolean setPicToAvatarAndSend2Server(Uri uri, String filepath){
        Log.i(TAG, "add cropped image to avatar");

        // get the dimension of the view
        int targetW = prof_btn_avatar.getWidth();
        int targetH = prof_btn_avatar.getHeight();

        Bitmap originBitmap = null;
        try {
            originBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

            //            BitmapFactory.Options options = new BitmapFactory.Options();
            //            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            //            originBitmap = BitmapFactory.decodeFile(filepath, options);
        } catch (Exception e){
            e.printStackTrace();
        }

        if(null == originBitmap){
            Log.i(TAG, "original bitmapt is null");
            return false;
        }


        int photoW = originBitmap.getWidth();
        int photoH = originBitmap.getHeight();

        // determine the scale factor
        int scaleFactor = Math.max(photoW/targetW, photoH/targetH);

        // decode the image file into a bitmap sized to fill the view
        //BitmapFactory.Options bmOptions2 = new BitmapFactory.Options();
        //bmOptions2.inJustDecodeBounds =false;
        //bmOptions2.inSampleSize = scaleFactor;
        //bmOptions2.in
        //Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions2);

        if(scaleFactor==0){
            return false;
        }

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(originBitmap, photoW/scaleFactor, photoH/scaleFactor, true);
        //originBitmap.recycle();

        prof_btn_avatar.setImageBitmap(scaledBitmap);


        File internalfile = null;
        internalfile = Utils.createInternalFile(this, "avatars");

        OutputStream internal_ops = null;
        try{
            internal_ops =new FileOutputStream(internalfile);
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }


        if(null != internal_ops) {
            // compress the image
            // scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, ops);
            scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, internal_ops);

            //String scaledAvatarPath = out_file.getAbsolutePath();
            String scaledAvatarPath = internalfile.getAbsolutePath();
            Log.i(TAG, "save: "+scaledAvatarPath);

            // set the avatar photo path to pref
            String prefs_of_user = new SessionManager(this).getUserPrefsName();
            SharedPreferences settings = this.getSharedPreferences(prefs_of_user, 0);

            SharedPreferences.Editor editor = settings.edit();
            editor.putString("key_avatar_path", scaledAvatarPath);
            editor.putString("key_original_image_uri", uri.toString());

            editor.commit();

            Log.i(TAG, "original avatar image uri in perference: " + uri.toString());
            Log.i(TAG, "scaled avatar image saving to prefs, scaled avatar: "+scaledAvatarPath );

            // send to server
            // sendAvatarToServer(scaledAvatarPath);


            return true;
        }


        return false;
    }

    /**
     * send avatar photo to server
     * first, scale the picture to smaller size, less than 1m
     */
    private void sendAvatarToServer(String scaledAvatarPath){
        Log.i(TAG, "sending to server:"+scaledAvatarPath);
        //// METHOD 1: USING SERVICE
        SendPhotoIntentService.startActionSendPhoto(this, scaledAvatarPath);
        // METHOD 2: USING RETROFIT
        // uploadFile(scaledAvatarPath);
    }

    /**
     * Select picture from gallery then cut for avatar photo
     */
    public void selectFromGallery(){
        Intent selectIntent = new Intent(Intent.ACTION_GET_CONTENT,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        selectIntent.setType("image/*");
        startActivityForResult(selectIntent, SELECT_GALLERY_REQUEST);
    }


    public void startImageEditActivity(){
        String original_avatar_uri = settings.getString("key_original_image_uri", "");

        Log.i(TAG, "START CUT ACTIVITY:" +original_avatar_uri);

        Intent editIntent = new Intent(this, ImageEditActivity.class);
        editIntent.putExtra("key_uri", original_avatar_uri);

        startActivity(editIntent);

    }

    public void startCrop(Uri uri){

        try {
            dest_crop_photo_file = Utils.createExternalPrivateImageFile(this, "avatar");
            mCurrentPhotoPath = dest_crop_photo_file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dest_crop_uri =Uri.fromFile(dest_crop_photo_file);
        //Crop.pickImage(this);

        Crop.of(uri, dest_crop_uri).asSquare()
                .start(ProfileActivity.this);
    }

    // second library to crop image
    // ucrop library
    public void startUCrop(Uri source_uri){

        try {
            dest_crop_photo_file = Utils.createExternalPrivateImageFile(this, "avatar");
            mCurrentPhotoPath = dest_crop_photo_file.getAbsolutePath();

        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("ProfileActivity", source_uri.toString());
        dest_crop_uri =Uri.fromFile(dest_crop_photo_file);
        UCrop.of(source_uri, dest_crop_uri)
                .withAspectRatio(1,1)
                .start(this);
    }

    //UPDATED!
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if(cursor!=null)
        {
            //HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            //THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else return null;
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
