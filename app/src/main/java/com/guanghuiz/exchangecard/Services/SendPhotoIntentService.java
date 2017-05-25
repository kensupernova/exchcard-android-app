package com.guanghuiz.exchangecard.Services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.guanghuiz.exchangecard.SendReceiveApi.ServiceApi;
import com.guanghuiz.exchangecard.Utils.SessionManager;
import com.guanghuiz.exchangecard.Utils.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

//import okhttp3.MediaType;
//import okhttp3.MultipartBody;
//import okhttp3.RequestBody;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SendPhotoIntentService extends IntentService {

    // settings for volley post
    private final Context context = this;
    private final String twoHyphens = "--";
    private final String lineEnd = "\r\n";
    private final String boundary = "apiclient-" + System.currentTimeMillis();
    private final String mimeType = "multipart/form-data;boundary=" + boundary;
    private byte[] multipartBody;


    private static final String TAG = "SendPhotoIntentService";
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_SEND_PHOTO = "com.guanghuiz.exchangecard.services.action.SEND_PHOTO";
    private static final String ACTION_BAZ = "com.guanghuiz.exchangecard.services.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM_PATH = "com.guanghuiz.exchangecard.services.extra.FILE_PATH";
    private static final String EXTRA_PARAM2 = "com.guanghuiz.exchangecard.services.extra.PARAM2";

    private static SessionManager sessionManager;
    private static Context _context;

    public SendPhotoIntentService() {
        super("SendPhotoIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionSendPhoto(Context context, String param1) {
        sessionManager = new SessionManager(context);
        _context = context;
        Intent intent = new Intent(context, SendPhotoIntentService.class);
        intent.setAction(ACTION_SEND_PHOTO);
        intent.putExtra(EXTRA_PARAM_PATH, param1);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param2) {
        Intent intent = new Intent(context, SendPhotoIntentService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SEND_PHOTO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM_PATH);
                handleActionSendPhoto(param1);

            } else if (ACTION_BAZ.equals(action)) {
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSendPhoto(String param1) {
        // TODO: Handle action Foo
        // send_by_httpurlconnection(param1);
        Utils.send_by_retrofit(_context, param1);
        //send_by_volley(param1);
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * SEND PHOTO FILE
     * @param filepath
     */



    private void send_by_httpurlconnection(String filepath) {

        // get bitmap data first
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds =false;
        bmOptions.inSampleSize = 1;

        Bitmap bitmap = BitmapFactory.decodeFile(filepath, bmOptions);


        String attachmentName = "avatar";
        String attachmentFileName = filepath;
        String crlf = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";

        // Create a new HttpClient and Post Header
        HttpURLConnection httpUrlConnection;
        URL url;

        try {
            // set up url connection
            url = new URL(ServiceApi.ENDPOINT+"profiles/"+sessionManager.getProfileId()+"/avatar");
            httpUrlConnection = (HttpURLConnection) url.openConnection();

            //// set up request
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setDoOutput(true);

            httpUrlConnection.setRequestMethod("POST");
            httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
            httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
            httpUrlConnection.setRequestProperty(
                    "Content-Type", "multipart/form-data;boundary=" + boundary);

            // content wrapper
            DataOutputStream request = new DataOutputStream(
                    httpUrlConnection.getOutputStream());

            request.writeBytes(twoHyphens + boundary + crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"" +
                    attachmentName + "\";filename=\"" +
                    attachmentFileName + "\"" + crlf);
            request.writeBytes(crlf);


            // Convert Bitmap to ByteBuffer:
            //I want to send only 8 bit black & white bitmaps
            byte[] pixels = new byte[bitmap.getWidth() * bitmap.getHeight()];
            for (int i = 0; i < bitmap.getWidth(); ++i) {
                for (int j = 0; j < bitmap.getHeight(); ++j) {
                    //we're interested only in the MSB of the first byte,
                    //since the other 3 bytes are identical for B&W images
                    pixels[i + j] = (byte) ((bitmap.getPixel(i, j) & 0x80) >> 7);
                }
            }

            request.write(pixels);

            request.writeBytes(crlf);
            request.writeBytes(twoHyphens + boundary +
                    twoHyphens + crlf);


            request.flush();
            request.close();

            // get response
            InputStream responseStream = new
                    BufferedInputStream(httpUrlConnection.getInputStream());

            BufferedReader responseStreamReader =
                    new BufferedReader(new InputStreamReader(responseStream));

            String line = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = responseStreamReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            responseStreamReader.close();

            String response = stringBuilder.toString();

            Log.i(TAG, "send by urlconnection: response - "+response);

            //Close response stream:

            responseStream.close();
            //Close the connection:

            httpUrlConnection.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }


}
