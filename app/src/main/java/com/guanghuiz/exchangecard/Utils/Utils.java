package com.guanghuiz.exchangecard.Utils;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.guanghuiz.exchangecard.R;
import com.guanghuiz.exchangecard.SendReceiveApi.ServiceApi;
import com.guanghuiz.exchangecard.SendReceiveApi.ServiceGenerator;
import com.guanghuiz.exchangecard.Services.AddPhotoToGalleryService;
import com.guanghuiz.exchangecard.UI.MainActivity;
import com.guanghuiz.exchangecard.Database.LogInSQLiteHelper;
import com.guanghuiz.exchangecard.Database.UserSQLiteHelper;
import com.guanghuiz.exchangecard.Database.model.AvatarPhotoResponse;
import com.guanghuiz.exchangecard.Database.model.MailAddress;
import com.guanghuiz.exchangecard.Database.model.User;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.zip.GZIPInputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.provider.MediaStore.Images;

/**
 * Created by Guanghui on 27/2/16.
 */
public class Utils {

    private static final String TAG = "Utils";
    // set expire to be 2 hours after now
    public static final long offset_seconds_for_expire = 2 * 60 * 60;

    public static String make_to_md5(String astring) {

        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(astring.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    // turn string into md5 hash code
    public static String getMD5(String val) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(val.getBytes());
        byte[] m = md5.digest();//加密
        return getString(m);
    }

    // Convert byte into string
    public static String getString(byte[] b) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            sb.append(b[i]);
        }
        return sb.toString();
    }

    public static String getPhoneInfo(Context context) {
        TelephonyManager phoneMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        // String linenumber = phoneMgr.getLine1Number(); //txtPhoneNumber是一个EditText 用于显示手机号
        String deviceId = phoneMgr.getDeviceId(); // device id
        String phoneModel = Build.MODEL;
        String sdkVersion = String.valueOf(Build.VERSION.SDK_INT);
        String osRelease = Build.VERSION.RELEASE;
        return deviceId + "+" + phoneModel + "+" + sdkVersion + "+" + osRelease;
    }

    public static String createSalt() {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer buf = new StringBuffer();
        // choose five letters from str randomly
        for (int i = 0; i < 5; i++) {
            int num = random.nextInt(62);
            buf.append(str.charAt(num));
        }
        return buf.toString();
    }

    /**
     * MD5字符串加密
     *
     * @param resource 源字符串
     * @return <tt>String</tt> 加密后的MD5字符串
     */
    public static String md5Encryption(String resource) {
        if (resource == null) {
            resource = "null";
        }
        String str = null;
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(resource.getBytes("UTF-8"));
            byte b[] = md.digest();
            int i;

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) i += 256;
                if (i < 16) buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            // 32位的加密
            //str = buf.toString();

            // 16位的加密
            str = buf.toString().substring(8, 24);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * MD5字符串加密
     *
     * @param resource 源字符串
     * @return <tt>String</tt> 加密后的MD5字符串
     */
    public static String md5Encryption(String[] resource) {
        if (null == resource || resource.length < 1) {
            return null;
        }

        String allMD5 = "";
        for (String tempStr : resource) {
            allMD5 = allMD5 + md5Encryption(tempStr);
        }
        return allMD5;
    }


    public static String converZipToString(byte[] zippedData) {
        ByteArrayInputStream byteInput = null;
        GZIPInputStream gzin = null;
        ByteArrayOutputStream byteOutput = null;
        String data = null;
        byte[] byteData = null;
        try {
            byte[] buf = new byte[1024];
            byteInput = new ByteArrayInputStream(zippedData);
            gzin = new GZIPInputStream(byteInput);
            byteOutput = new ByteArrayOutputStream();
            int num = -1;
            while ((num = gzin.read(buf, 0, buf.length)) != -1) {
                byteOutput.write(buf, 0, num);
            }
            byteData = byteOutput.toByteArray();
            if (byteOutput != null) {
                byteOutput.flush();
                byteOutput.close();
            }
            if (byteInput != null) {
                byteInput.close();
            }
            if (gzin != null) {
                gzin.close();
            }
            data = new String(byteData, "UTF-8");
        } catch (IOException e) {
            return null;
        }
        return data;
    }

    public int getCurrentTimeInSeconds() {
        // set Log In TO App Token
        Calendar c = Calendar.getInstance();
        int seconds = c.get(Calendar.SECOND);
        return seconds;
    }

    public static void login_database(Context context, String username) {
        // set Log In TO App Token
        Calendar c = Calendar.getInstance();
        long now_in_seconds = c.getTimeInMillis() / 1000;

        long expire_time = now_in_seconds + offset_seconds_for_expire;
        // set salt
        String salt = Utils.createSalt();


        AESCodeHelper helper = new AESCodeHelper();
        // turned login information into aes coded
        String encrypted_login = helper.encrypt(salt, username + "+" + salt + "+" + expire_time);

        // store the token into database
        LogInSQLiteHelper loginhelper = new LogInSQLiteHelper(context);
        loginhelper.insert(username, salt, encrypted_login, now_in_seconds, expire_time);

    }

    /*
    ** the method to log out, clear the preference, set log in token expire
     */
    public static boolean logout_database(Context context) {
        // current profile information are stored in preference
        //SharedPreferences settings = context.getSharedPreferences(MainActivity.PREFS_NAME, 0);
        //String current_username = settings.getString(MainActivity.key_current_log_in_username, "none");

        SessionManager sessionManager = new SessionManager(context);
        String current_username = sessionManager.getUserName();


        Calendar c = Calendar.getInstance();
        long now_in_seconds = c.getTimeInMillis() / 1000;
        // set expire_time to be 30 days later;30*24*60*60
        long expire_time = now_in_seconds;
        // set salt
        String salt = Utils.createSalt();


        AESCodeHelper helper = new AESCodeHelper();
        // turned login information into aes coded
        String encrypted_login = helper.encrypt(salt, current_username + "+" + salt + "+" + expire_time);

        // store the token into database
        LogInSQLiteHelper loginhelper = new LogInSQLiteHelper(context);
        loginhelper.insert(current_username, salt, encrypted_login, now_in_seconds, expire_time);

        // clean out data in preferences
        //SharedPreferences.Editor editor = settings.edit();
        //editor.remove(MainActivity.key_current_log_in_username);
        //editor.remove(MainActivity.key_current_log_in_profile_id);
        //editor.remove(MainActivity.key_current_is_log_in);

        //editor.commit();

        return true;
    }

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public static User getLoggedInUser(Context context, String username) {
        UserSQLiteHelper helper = new UserSQLiteHelper(context);

        return helper.getUser(username);
    }

    public static MailAddress getMailAddressByUser(Context context, String username) {
        UserSQLiteHelper helper = new UserSQLiteHelper(context);
        return helper.getMailAddress(username);
    }

    public static int getProfileIdByUser(Context context, String username) {
        UserSQLiteHelper helper = new UserSQLiteHelper(context);
        int profile_id = helper.getProfileId(username);
        if (profile_id == -1) {
            // if id is negative, try to get from server
            Log.i(MainActivity.TAG, "Utils" + " get profile id from server");
            Log.i(MainActivity.TAG, "Utils" + " or by sign in again");
            // or log in again


        }

        return profile_id;
    }

    public static String getCardId() {

        String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();

        StringBuffer buf = new StringBuffer();
        // choose five letters from str randomly
        for (int i = 0; i < 2; i++) {
            // 26 +26 +10
            int num = random.nextInt(26);
            buf.append(str.charAt(num));
        }

        StringBuilder sb = new StringBuilder();
        sb.append(buf.toString());
        sb.append("-");
        sb.append(random.nextInt(10));
        sb.append(random.nextInt(10));
        sb.append(random.nextInt(10));

        return sb.toString();
    }

    public static String getPW(Context context, String username) {
        // get the password by username
        UserSQLiteHelper helper = new UserSQLiteHelper(context);
        String pw = helper.getHashedPassword(username);
        return pw;
    }

    public static boolean setUserMailAddress(Context context, int profile_id, String username,
                                             String password,
                                             MailAddress mailAddress) {

        Log.i("Utils", "set user mail address - " + username + ":" + mailAddress.toString());
        UserSQLiteHelper helper = new UserSQLiteHelper(context);
        helper.updateorinsertMailAddress(profile_id, username, password, mailAddress);
        return true;
    }

    /*
    * update address on local and server
     */
    public static Boolean updateAddress(Context context, int profile_id, String username,
                                        MailAddress mailAddress) {
        setUserMailAddress(context, profile_id, username, getPW(context, username),
                mailAddress);


        // update the address on server
        // Service api
        ServiceApi api = ServiceGenerator.createService(ServiceApi.class,
                username, getPW(context, username));

        Call<MailAddress> call = api.updateAddress(mailAddress, profile_id);

        // asyncally do do the job
        call.enqueue(new Callback<MailAddress>() {
            @Override
            public void onResponse(Call<MailAddress> call, Response<MailAddress> response) {
                if (response.isSuccessful()) {
                    int status_code = response.code();
                    if (status_code == 200) {
                        MailAddress responseBody = response.body();
                        Log.i("Utils", "update address: " + responseBody.toString());

                    } else {
                        Log.i(MainActivity.TAG, "update address failed from server");

                    }

                } else {
                    Log.i(MainActivity.TAG, "update address failed from server");

                }


            }

            @Override
            public void onFailure(Call<MailAddress> call, Throwable t) {

            }
        });


        return true;
    }

    public static void showAlertRedirectLogIn(final SessionManager sessionManager, Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.alert);
        builder.setMessage(R.string.alert_not_logged_in);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                sessionManager.redirectToLoginActivity();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog

            }
        });

        builder.create().show();

    }

    // method for jump to mainactivity
    public static void jump_to_mainactivity(Context context, int THIS_ACTIVITY_INDEX,
                                            int item_index_when_back,
                                            boolean log_in_or_not,
                                            String current_username,
                                            int curent_id) {
        //创建Intent对象，传入源Activity和目的Activity的类对象
        Intent intent = new Intent(context, MainActivity.class);

        //If you want to close existing activity stack regardless of
        // what's in there and create new root, correct set of flags is the following:
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // put activity index into bundle
        Bundle bundle = new Bundle();
        bundle.putInt("JUMP_FROM_ACTIVITY", THIS_ACTIVITY_INDEX);
        bundle.putInt("ITEM_INDEX_WHEN_BACK", item_index_when_back);
        bundle.putBoolean("LOG_IN_OR_NOT", log_in_or_not);
        bundle.putString("CURRENT_USER_NAME", current_username);
        bundle.putInt("CURRENT_PROFILE_ID", curent_id);
        intent.putExtras(bundle);

        //启动Activity
        context.startActivity(intent);
    }

    public static byte[] getBytesFromFile(String filepath) {
        FileInputStream fileInputStream = null;

        File file = new File(filepath);

        byte[] bFile = new byte[(int) file.length()];

        try {
            //convert file into array of bytes
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bFile);
            fileInputStream.close();

            for (int i = 0; i < bFile.length; i++) {
                System.out.print((char) bFile[i]);
            }

            System.out.println("Done");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bFile;
    }

    public static void send_by_retrofit(Context context, String scaledAvatarPath) {
        // File file = new File(scaledAvatarPath);
        final String TAG = "Utils";
        SessionManager sessionManager = new SessionManager(context);

        File file = new File(scaledAvatarPath);
        ServiceApi service =
                ServiceGenerator.createServiceForPostMultiPart(ServiceApi.class,
                        sessionManager.getUserName(),
                        Utils.getPW(context, sessionManager.getUserName()));

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("image/*"), file);

        // MultipartBody.Part is used to send also the actual file name
        Log.i(TAG, "file.getName-" + file.getName());
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("avatar", file.getName(), requestFile);

        //add another part within the multipart request
        //        String titleString = file.getName();
        //        RequestBody title = RequestBody.create(
        //                               MediaType.parse("multipart/form-data"), titleString);


        Call<AvatarPhotoResponse> call = service.updateAvatarPhoto2(body,
                sessionManager.getProfileId());

        call.enqueue(new Callback<AvatarPhotoResponse>() {
            @Override
            public void onResponse(Call<AvatarPhotoResponse> call,
                                   Response<AvatarPhotoResponse> response) {
                Log.i(TAG, "response raw: " + response.raw());

                AvatarPhotoResponse responseBody = response.body();
                if (null != responseBody) {
                    Log.i(TAG, "ResponseBody: " + responseBody.toString());
                } else {
                    Log.i(TAG, "Response body is null");
                }


            }

            @Override
            public void onFailure(Call<AvatarPhotoResponse> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    public static void resetPassword(Context context, String newPassword) {

    }

    // internal file storage
    public static File createInternalFile(Context context, String dir) {
        File internalFileDir = new File(context.getFilesDir(), dir);
        if(!internalFileDir.isDirectory()){
            internalFileDir.mkdirs();
        }

        if(internalFileDir.isDirectory()){
            // delete all files in
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File internalfile = new File(internalFileDir, new SessionManager(context).getUserNameStamp()+
                "_"+timeStamp+"_scaled.png");

        return internalfile;
    }

    public static File createInternalCachedFile(Context context, String dir) {
        File internalFileDir = new File(context.getCacheDir(), dir);
        if(!internalFileDir.isDirectory()){
            internalFileDir.mkdirs();
        }

        if(internalFileDir.isDirectory()){
            // delete all files in
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File internalfile = new File(internalFileDir, new SessionManager(context).getUserNameStamp()+
                "_"+timeStamp+"_scaled.png");


        return internalfile;
    }



    // external private store
    public static File createExternalPrivateImageFile(Context context, String dir) throws IOException {

        File storageDir = new File(context.getApplicationContext().getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), dir);

        if (!storageDir.mkdirs()) {

        }

        if(storageDir.isDirectory()){
            // delete all files in
            File[] Files = storageDir.listFiles();
            if(Files != null) {
                int j;
                for(j = 0; j < Files.length; j++) {
                    System.out.println(Files[j].getAbsolutePath());
                    System.out.println(Files[j].delete());
                }
            }

        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File image = new File(storageDir, imageFileName);

        String mCurrentPhotoPath = image.getAbsolutePath();
        Log.i(TAG, mCurrentPhotoPath);

        return image;
    }

    // create file in external public
    public static File createExternalPublicImageFile(String dir) throws IOException {
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), dir) ;


        if (!storageDir.mkdirs()) {

        }

        if(storageDir.isDirectory()){
            // delete all files in
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File image = new File(storageDir, imageFileName);

        String mCurrentPhotoPath = image.getAbsolutePath();
        Log.i(TAG, mCurrentPhotoPath);

        return image;
    }

    // add photo to gallery
    public static Uri addPicToGallery(Context context, String photo_file_path, String imageFileName) {
        Log.i("Utils", "add to gallery, filepath: " + photo_file_path);

        //        try{
        //            String thumbnail  = MediaStore.Images.Media.insertImage(context.getContentResolver(),
        //                    photo_file_path, imageFileName, imageFileName);
        //
        //            Log.i("Utils", "add to gallery, thumbnail:" + thumbnail );
        //            Uri imageUri= Uri.parse(thumbnail);
        //            return imageUri;
        //
        //        } catch (FileNotFoundException e){
        //            return null;
        //        }


        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(photo_file_path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);

        return contentUri;
    }

    private static Uri RESULT_URI =null;

    private static class MyBroadcastReceiver extends BroadcastReceiver {
        public static final String PROCESS_RESPONSE = "ADD_PHOTO_TO_GALLERY_URI";

        @Override
        public void onReceive(Context context, Intent intent) {
            String responseMessage = intent.getStringExtra(AddPhotoToGalleryService.RESPONSE_MESSAGE);
            RESULT_URI = Uri.parse(responseMessage);
        }

    }

    public static Uri addPicToGallery2(Context context, String path, String imageFileName) {
        MyBroadcastReceiver receiver;

        IntentFilter filter = new IntentFilter(MyBroadcastReceiver.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new MyBroadcastReceiver();
        context.registerReceiver(receiver, filter);


        AddPhotoToGalleryService service = new AddPhotoToGalleryService();
        service.startActionSendPhoto(context, path, imageFileName);


        context.unregisterReceiver(receiver);

        return null;

    }

}
