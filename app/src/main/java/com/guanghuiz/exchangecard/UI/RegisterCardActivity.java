package com.guanghuiz.exchangecard.UI;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.guanghuiz.exchangecard.Database.model.CardPhoto;
import com.guanghuiz.exchangecard.R;
import com.guanghuiz.exchangecard.SendReceiveApi.ServiceApi;
import com.guanghuiz.exchangecard.SendReceiveApi.ServiceGenerator;
import com.guanghuiz.exchangecard.Utils.SessionManager;
import com.guanghuiz.exchangecard.Utils.Utils;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterCardActivity extends AppCompatActivity {
    private Button btn_confirm_register_card ;
    private EditText edit_card_name;
    private static final int TAKE_PHOTO_REQUEST = 400;
    private static final String TAG = "RegisterCard";

    private File photo_file = null;

    private File dest_crop_photo_file = null;
    private Uri dest_crop_uri = null;

    private ImageView image_photo_of_card;

    // private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_card);

        btn_confirm_register_card = (Button) findViewById(R.id.btn_confirm_register_card);
        btn_confirm_register_card.setOnClickListener(ocl_confirm_register);
        edit_card_name = (EditText) findViewById(R.id.edittext_card_name);
        image_photo_of_card = (ImageView) findViewById(R.id.image_photo_of_card);

        ImageButton btn =(ImageButton) findViewById(R.id.imagebutton_register_card_backward);
        if(null != btn) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        //Button btn_take_photo_of_card = (Button) findViewById(R.id.btn_take_photo_of_card);
        image_photo_of_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePictureCamera();
            }
        });

    }

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

            try{
                photo_file = Utils.createExternalPublicImageFile("exchangecard");
            } catch(IOException e){
                Log.i("dispatch camera", e.toString());
            }
            // if create image file is successful, start the intent, put the uri of the file as the Uri for intent
            if(photo_file !=null){
                startCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photo_file));
                startActivityForResult(startCameraIntent, TAKE_PHOTO_REQUEST);
            }


        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        // picture is from camera
        if(requestCode==TAKE_PHOTO_REQUEST) {
            if (resultCode == RESULT_OK) {
                Log.i(TAG, "add to gallery ...  " + photo_file.getAbsolutePath());
                Uri imageUri =Utils.addPicToGallery(this, photo_file.getAbsolutePath(), photo_file.getName());
                if(null!= imageUri) {
                    Log.i(TAG, "add to gallery, uri: "+imageUri.toString());
                    //setPicToAvatarAndSend2Server(imageUri);

                    // startCrop(imageUri);

                } else{
                    Log.i(TAG, "add to gallery, uri is null");
                }

                // dest_crop_uri = startUCrop(imageUri);
                dest_crop_uri = startUCrop(Uri.parse(photo_file.getAbsolutePath()));

            } else {
                Log.i(TAG, "take camera photo fail!");

            }

        } else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            // Log.i(TAG, "add to gallery, Ucrop uri: "+ dest_crop_uri.toString());
            Log.i(TAG, "add to gallery, Ucrop uri: "+resultUri.toString());
            Uri uri =Utils.addPicToGallery(this, dest_crop_photo_file.getAbsolutePath(), dest_crop_photo_file.getName());
            setPhotoToView(resultUri);
        }

    }


    public Uri startUCrop(Uri source_uri){

        try {
            dest_crop_photo_file = Utils.createExternalPrivateImageFile(this, "cardphotos");

        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("RegisterCard", source_uri.toString());
        Log.i("RegisterCard", dest_crop_photo_file.toString());

        Uri crop_uri =Uri.fromFile(dest_crop_photo_file);
        UCrop.of(source_uri, crop_uri)
                .withAspectRatio(148,105)
                .start(this);

        return crop_uri;
    }

    private void setPhotoToView(Uri uri){
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds =false;
        bmOptions.inSampleSize = 1;
        Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath(), bmOptions);

        image_photo_of_card.setImageBitmap(bitmap);
    }

    /*
    * OnClickLister for confirm register the card
     */

    View.OnClickListener ocl_confirm_register = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // hide the keyboard
            View view = RegisterCardActivity.this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            // progressDialog = ProgressDialog.show(RegisterCardActivity.this, "", "Please wait ...", true );

            String card_name = String.valueOf(edit_card_name.getText());
            if(null != card_name && !card_name.isEmpty()){



                //final SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
                // String current_username = settings.getString(MainActivity.key_current_log_in_username, "none");
                //int current_profile_id = settings.getInt(MainActivity.key_current_log_in_profile_id, -1);

                SessionManager sessionManager = new SessionManager(RegisterCardActivity.this);
                String current_username = sessionManager.getUserName();
                int current_profile_id = sessionManager.getProfileId();

                Log.i("Register card", "card name, profile id:" +card_name +":"+current_profile_id);

                // close the activity b
                finish();

                // use retrofit to register the card
                ServiceApi api = ServiceGenerator.createService(ServiceApi.class,
                        current_username, Utils.getPW(getApplicationContext(), current_username));

                //                Call<Card> call = api.registerReceivedCard(card_name);
                //
                //                call.enqueue(new Callback<Card>() {
                //                    @Override
                //                    public void onResponse(Call<Card> call, Response<Card> response) {
                //
                //                        // progressDialog.cancel();
                //
                //                        int status_code = response.code();
                //                        Card response_body = response.body();
                //
                //                        if(status_code ==404 ||status_code == 403){
                //                            Toast.makeText(getApplicationContext(),
                //                                    "Register card - " + status_code +", "+ response_body,
                //                                    Toast.LENGTH_LONG).show();
                //                        }
                //
                //                        if(null != response_body){
                //                            Toast.makeText(getApplicationContext(),
                //                                    "Register card - " + status_code +", "+ response_body,
                //                                    Toast.LENGTH_LONG).show();
                //                        } else{
                //                            Log.i("Register card", "status code:" +status_code +
                //                                    ", response body:"+ response.body());
                //                            Toast.makeText(getApplicationContext(),
                //                                    "Register card - " + status_code +", "+ response_body,
                //                                    Toast.LENGTH_LONG).show();
                //                        }
                //
                //
                //                    }
                //
                //                    @Override
                //                    public void onFailure(Call<Card> call, Throwable t) {
                //                        // progressDialog.cancel();
                //                        // ("Register card", "registered failed");
                //                        Toast.makeText(getApplicationContext(), "Register card failed",
                //                                Toast.LENGTH_LONG).show();
                //
                //
                //
                //                    }
                //                });


                // create RequestBody instance from file
                RequestBody requestFile =
                        RequestBody.create(MediaType.parse("image/*"), dest_crop_photo_file);

                // MultipartBody.Part is used to send also the actual file name
                MultipartBody.Part body =
                        MultipartBody.Part.createFormData("cardphoto", dest_crop_photo_file.getName(), requestFile);

                //add another part within the multipart request
                String titleString = dest_crop_photo_file.getName();
                RequestBody title = RequestBody.create(
                        MediaType.parse("multipart/form-data"), titleString);

                //                Call<CardPhoto> call_add_photo = api.addCardPhoto(body,title);
                //                call_add_photo.enqueue(new Callback<CardPhoto>() {
                //                    @Override
                //                    public void onResponse(Call<CardPhoto> call, Response<CardPhoto> response) {
                //                        int status_code = response.code();
                //                        CardPhoto response_body = response.body();
                //
                //                        if(status_code ==404 ||status_code == 403){
                //                            Toast.makeText(getApplicationContext(),
                //                                    "Photo card - " + status_code +", "+ response_body,
                //                                    Toast.LENGTH_LONG).show();
                //                        }
                //
                //                        if(null != response_body){
                //
                //                        } else{
                //                            Log.i("Register card", "status code:" +status_code +
                //                                    ", response body:"+ response.body());
                //                            Toast.makeText(getApplicationContext(),
                //                                    "Photo card - " + status_code +", "+ response_body,
                //                                    Toast.LENGTH_LONG).show();
                //                        }
                //                    }
                //
                //                    @Override
                //                    public void onFailure(Call<CardPhoto> call, Throwable t) {
                //
                //                    }
                //                });

                Call<CardPhoto> call_add_photo = api.registerReceivedCardWithPhoto(card_name, body, title);
                call_add_photo.enqueue(new Callback<CardPhoto>() {
                    @Override
                    public void onResponse(Call<CardPhoto> call, Response<CardPhoto> response) {
                        int status_code = response.code();
                        CardPhoto response_body = response.body();

                        if(status_code ==404 ||status_code == 403){
                            Toast.makeText(getApplicationContext(),
                                    "Photo card - " + status_code +", "+ response_body,
                                    Toast.LENGTH_LONG).show();
                        }

                        if(null != response_body){

                        } else{
                            Log.i("Register card", "status code:" +status_code +
                                    ", response body:"+ response.body());
                            Toast.makeText(getApplicationContext(),
                                    "Photo card - " + status_code +", "+ response_body,
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CardPhoto> call, Throwable t) {

                    }
                });
            }

        }
    };
}
