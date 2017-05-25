package com.guanghuiz.exchangecard.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.guanghuiz.exchangecard.R;
import com.guanghuiz.exchangecard.Utils.SessionManager;
import com.guanghuiz.exchangecard.Tasks.RegisterEncryptedInfoToServerTask;
import com.guanghuiz.exchangecard.Utils.RSACodeHelper;
import com.guanghuiz.exchangecard.Utils.Utils;
import com.guanghuiz.exchangecard.Database.UserSQLiteHelper;
import com.guanghuiz.exchangecard.Database.model.RegisterUserAddressRequestData;
import com.guanghuiz.exchangecard.Database.model.RegisterUserAddressResponseData;
import com.guanghuiz.exchangecard.Tasks.RegisterUserAddressToServerTask;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A login screen that offers login via email/password and via Google+ sign in.
 * <p/>
 * ************ IMPORTANT SETUP NOTES: ************
 * In order for Google+ sign in to work with your app, you must first go to:
 * https://developers.google.com/+/mobile/android/getting-started#step_1_enable_the_google_api
 * and follow the steps in "Step 1" to create an OAuth 2.0 client for your package.
 */
public class SignUpActivity extends AppCompatActivity {
    public static int THIS_ACTIVITY_INDEX = 2;

    //
    private LinearLayout container;

    //  edittext username, password, register a new user
    private EditText editText_email;
    private EditText editText_password;

    // MailAddress
    private EditText editText_name;
    private EditText editText_address;
    private EditText editText_postcode;


    private Button btn_confirm_address_signup;
    private Intent intent;

    private int originalchildrenCount;
    private View error;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        sessionManager = new SessionManager(SignUpActivity.this);

        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_signup);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        // get the backward button on the toolbar
        ImageButton imageButton = (ImageButton) toolbar.findViewById(R.id.imagebutton_login_backward);
        imageButton.setOnClickListener(backward_click);

        // sign up button
        btn_confirm_address_signup = (Button) findViewById(R.id.btn_confirm_address_signup);
        btn_confirm_address_signup.setOnClickListener(ocl_confirm_signup);


        // set the edittext password and username, name, address, postcode
        editText_password = (EditText) findViewById(R.id.edittext_user_password);

        editText_password.setTransformationMethod(PasswordTransformationMethod.getInstance());

        editText_email = (EditText) findViewById(R.id.edittext_user_email);

        editText_name = (EditText) findViewById(R.id.edittext_write_name);
        editText_address = (EditText) findViewById(R.id.edittext_write_address);
        editText_postcode = (EditText) findViewById(R.id.edittext_write_postcode);

        // get the container
        container = (LinearLayout) findViewById(R.id.activity_signup_container);

        // get the orignal view count for error display
        originalchildrenCount = container.getChildCount();
        error = getLayoutInflater().inflate(R.layout.layout_error, null);

    }

    // onclick listener for image button, click and backwards to parent activity
    OnClickListener backward_click = new OnClickListener() {
        @Override
        public void onClick(View v) {
            //创建Intent对象，传入源Activity和目的Activity的类对象
            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);

            // put activity index into bundle
            Bundle bundle = new Bundle();
            bundle.putInt("JUMP_FROM_ACTIVITY", 2);
            bundle.putInt("ITEM_INDEX_WHEN_BACK", 2);
            bundle.putBoolean("LOG_IN_OR_NOT", false);
            intent.putExtras(bundle);

            //启动Activity
            startActivity(intent);
        }
    };


    // click listener for confirm signup
    OnClickListener ocl_confirm_signup= new OnClickListener() {
        @Override
        public void onClick(View v) {

            List<String> wrongInputs = check_signup_input();

            // validate inputs first
            if(wrongInputs.isEmpty()){
                // if inputs are valid

                // remove error view
                if(container.indexOfChild(error) != -1){
                    // remove view
                    container.removeView(error);
                }

                // send the register information to server
                try {
                    // check internet available
                    if(!Utils.isInternetAvailable(getApplicationContext())){
                        Toast.makeText(getApplicationContext(), R.string.internet_is_not_connected,
                                Toast.LENGTH_SHORT).show();
                    } else {

                        sendRegistrationToSeverUnencoded();
                    }


                } catch (Exception e){
                    e.printStackTrace();
                };


            } else {
                // if inputs are invalid, show error

                // make error view and add to container
                TextView textView_error =(TextView) error.findViewById(R.id.textview_error);
                textView_error.setText("fields "+wrongInputs.toString() + getResources().getString(R.string.are_wrong));
                textView_error.setTextColor(getResources().getColor(R.color.red));
                if (container.indexOfChild(error) == -1 ){
                    container.addView(error, originalchildrenCount);
                }


            }



        }
    };


    public String register_new_profile_to_local(RegisterUserAddressResponseData responseData){
        List<String> inputList = getAllInputs();
        // get database

        UserSQLiteHelper helper = new UserSQLiteHelper(getApplicationContext());

        // profile id, then username, pasword, email, name, address, postcode
        Log.i(MainActivity.TAG, "new user registered locally - " +
                helper.insert(responseData.profile_id,
                        inputList.get(0), inputList.get(1),inputList.get(2),
                inputList.get(3), inputList.get(4),inputList.get(5)));

        return inputList.toString();
    }

    // send username, password, phone informatio, encrypted information to server
    private boolean sendRegistrationToSeverEncoded(){

        String phone_information = Utils.getPhoneInfo(getApplicationContext());
        Log.i(MainActivity.TAG, "phone information = " + phone_information);

        String unencrypt_str = getAllInputs()+"+" +phone_information;

        // use RSA to encrypt the string
        RSACodeHelper rsaCodeHelper = new RSACodeHelper();
        rsaCodeHelper.initKey();
        String encryptedCode = rsaCodeHelper.encrypt(unencrypt_str);
        Log.i(MainActivity.TAG, "encrypt - " + encryptedCode);
        Log.i(MainActivity.TAG, "decrypt - " + rsaCodeHelper.decrypt(encryptedCode));

        RegisterEncryptedInfoToServerTask task = new RegisterEncryptedInfoToServerTask(getApplicationContext());
        task.execute(encryptedCode+"+rsa private+"+
                rsaCodeHelper.base64Enc(rsaCodeHelper.mPrivateKey.getEncoded()));

        return true;
    }

    private boolean sendRegistrationToSeverUnencoded(){
        // get the information
        final List<String> inputList = getAllInputs();
        RegisterUserAddressToServerTask task = new RegisterUserAddressToServerTask(getApplicationContext(), this);
        task.execute(new RegisterUserAddressRequestData(
                inputList.get(0), inputList.get(1),inputList.get(2),
                inputList.get(3), inputList.get(4),inputList.get(5)
        ));




        return true;
    }

    public List<String> getAllInputs(){
        List<String> inputList = new ArrayList<>();
        String username = String.valueOf(editText_email.getText());

        String email = username;

        String name = String.valueOf(editText_name.getText());
        String address = String.valueOf(editText_address.getText());
        String postcode = String.valueOf(editText_postcode.getText());

        String password = "";
        try{
            // hash the password with MD5
            password = Utils.getMD5(String.valueOf(editText_password.getText()));
            Log.i("Signup", "password: " +password);
        } catch (Exception e){
            e.printStackTrace();
            // when hash password fail, return null
            return null;
        }

        inputList.add(username);
        inputList.add(password);
        inputList.add(email);

        inputList.add(name);
        inputList.add(address);
        inputList.add(postcode);

        return inputList;
    }



    private List<String> check_signup_input(){

        List<String> wrongInputs = new ArrayList<>();

        // create regex for username and user password
        final Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

        final Pattern VALID_PASSWORD_REGEX =
                Pattern.compile("^[a-zA-Z0-9!()-._~@]+$");

        final Pattern VALID_NAME = Pattern.compile("^[a-zA-Z0-9\u4e00-\u9fa5 ]+$");
        final Pattern VALID_ADDRESS = Pattern.compile("^[^()<>;/?_]+$");
        final Pattern VALID_POSTCODE = Pattern.compile("^[^()<>;/?%#\u4e00-\u9fa5]+$");

        String username = String.valueOf(editText_email.getText());
        String userpassword = String.valueOf(editText_password.getText());

        String name = String.valueOf(editText_name.getText());
        String address = String.valueOf(editText_address.getText());
        String postcode = String.valueOf(editText_postcode.getText());

        // if invalid, return false
        if(!VALID_EMAIL_ADDRESS_REGEX.matcher(username).find()){
            wrongInputs.add("email");
        }
        if (!VALID_PASSWORD_REGEX.matcher(userpassword).find()){
            wrongInputs.add("password");;
        }

        if(null == name ||name.isEmpty()
                || name.matches("^[ ]+$") ||
                !VALID_NAME.matcher(name).find() ){
            Log.i(MainActivity.TAG, "name is wrong");
            wrongInputs.add("name");
        }

        if(null == address ||address.isEmpty()
                || address.matches("^[ ]+$") ||
                !VALID_ADDRESS.matcher(address).find()){
            Log.i(MainActivity.TAG, "address is wrong");
            wrongInputs.add("address");
        }
        if(null == postcode ||postcode.isEmpty()
                || postcode.matches("^[ ]+$") ||
                !VALID_POSTCODE.matcher(postcode).find()){
            Log.i(MainActivity.TAG, "postcode is wrong");
            wrongInputs.add("postcode");
        }

        return wrongInputs;
    }

}

