package com.guanghuiz.exchangecard.UI;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guanghuiz.exchangecard.Utils.SessionManager;
import com.guanghuiz.exchangecard.Utils.Utils;
import com.guanghuiz.exchangecard.Database.UserSQLiteHelper;
import com.guanghuiz.exchangecard.Database.model.MailAddress;
import com.guanghuiz.exchangecard.R;

public class EditAddressActivity extends AppCompatActivity {

    private EditText edittext_write_name;
    private EditText edittext_write_address;
    private EditText edittext_write_postcode;

    private Button btn_confirm_address;
    private Button btn_edit_address;
    private boolean has_clicked_edit;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_edit_address);
        setSupportActionBar(toolbar);

        // back to parent acitivity button, remove title first
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        final LinearLayout container = (LinearLayout) findViewById(R.id.toolbar_edit_address_container);


        // edittext input, name, address, postcode
        edittext_write_name = (EditText) findViewById(R.id.edittext_write_name);
        edittext_write_address = (EditText) findViewById(R.id.edittext_write_address);
        edittext_write_postcode = (EditText) findViewById(R.id.edittext_write_postcode);

        // get the mailaddress of current loged in user
        // SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        // final String current_username = settings.getString(MainActivity.key_current_log_in_username, "");
        // final int current_profile_id = settings.getInt(MainActivity.key_current_log_in_profile_id, -1);
        sessionManager = new SessionManager(EditAddressActivity.this);

        final String current_username = sessionManager.getUserName();
        final int current_profile_id = sessionManager.getProfileId();

        Log.i("EXCHANGECARD", "activity beg for card, current user: "+current_username+","+current_profile_id);

        if (current_username !="" && null!=current_username){
            UserSQLiteHelper helper = new UserSQLiteHelper(getApplicationContext());
            MailAddress address = helper.getMailAddress(current_username);

            if(null != address) {
                edittext_write_name.setText(address.getName());
                edittext_write_address.setText(address.getAddress());
                edittext_write_postcode.setText(address.getPostcode());
            } else{
                Log.i("EXCHANGECARD", "activity beg for card, address in null");
            }

        }

        // set edit text views un editable
        // method 1
        edittext_write_name.setFocusableInTouchMode(false);
        edittext_write_name.setClickable(false);
        edittext_write_name.setFocusable(false);
        edittext_write_name.setTextColor(getResources().getColor(R.color.text_clor_hint));

        edittext_write_address.setFocusableInTouchMode(false);
        edittext_write_address.setClickable(false);
        edittext_write_address.setFocusable(false);
        edittext_write_address.setTextColor(getResources().getColor(R.color.text_clor_hint));

        edittext_write_postcode.setFocusableInTouchMode(false);
        edittext_write_postcode.setClickable(false);
        edittext_write_postcode.setFocusable(false);
        edittext_write_postcode.setTextColor(getResources().getColor(R.color.text_clor_hint));

        // method 2
        //        edittext_write_name.setFilters(new InputFilter[] {
        //                new InputFilter() {
        //                    public CharSequence filter(CharSequence src, int start,
        //                                               int end, Spanned dst, int dstart, int dend) {
        //                        return src.length() < 1 ? dst.subSequence(dstart, dend) : "";
        //                    }
        //                }
        //        });



        btn_confirm_address = (Button) findViewById(R.id.btn_confirm_address);

        btn_edit_address = (Button) findViewById(R.id.btn_edit_address);

        final int originalchildrenCount = container.getChildCount();

        final View error = getLayoutInflater().inflate(R.layout.layout_address_error, null);

        // collect all the address
        btn_confirm_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = String.valueOf(edittext_write_name.getText());
                String address = String.valueOf(edittext_write_address.getText());
                String postcode = String.valueOf(edittext_write_postcode.getText());


                // if the address input are right, start popup dialog
                if(checkAddressInput(name, address, postcode)) {
                    // remove the text error if exist
                    Log.i("exchangecard", "error view index " + container.indexOfChild(error));
                    if(container.indexOfChild(error) != -1){
                        // remove view
                        container.removeView(error);
                    }
                    startPopupDialog(current_profile_id, current_username,
                            new MailAddress(name, address, postcode));
                } else{
                    // if the address are wrong, add feedback below

                    TextView textView_error =(TextView) error.findViewById(R.id.textview_error);
                    textView_error.setText("Mail address is wrong!");
                    textView_error.setTextColor(getResources().getColor(R.color.red));
                    if (container.indexOfChild(error) == -1 ){
                        container.addView(error, originalchildrenCount);
                    }
                }


            }
        });

        // set onclicklistener to edit button
        // update address in local sqlite and server database
        btn_edit_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                edittext_write_name.setFocusableInTouchMode(true);
                edittext_write_name.setClickable(true);
                edittext_write_name.setFocusable(true);
                edittext_write_name.setTextColor(getResources().getColor(R.color.black));

                edittext_write_address.setFocusableInTouchMode(true);
                edittext_write_address.setClickable(true);
                edittext_write_address.setFocusable(true);
                edittext_write_address.setTextColor(getResources().getColor(R.color.black));

                edittext_write_postcode.setFocusableInTouchMode(true);
                edittext_write_postcode.setClickable(true);
                edittext_write_postcode.setFocusable(true);
                edittext_write_postcode.setTextColor(getResources().getColor(R.color.black));

                has_clicked_edit = true;


            }
        });


        // backwards button
        ImageButton btn_go_back = (ImageButton) findViewById(R.id.imagebutton_beg_for_card_backward);

        if(null!=btn_go_back){
            btn_go_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(Activity.RESULT_CANCELED);
                    finish();

                }
            });

        }

    }

    public boolean checkAddressInput(String name, String address, String postcode){
        if (name.isEmpty()){
            return false;
        } else if (address.isEmpty()){
            return false;
        } else if (postcode.isEmpty()){
            return false;
        }

        return true;
    }

    public void startPopupDialog(final int current_profile_id,
                                 final String current_username,
                                 final MailAddress address){
        Context context = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.is_address_right);
        builder.setMessage(address.toString());
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // create and start task
                // send the address to server

                // if clicked edit button, the address has been modified,
                // update local and server
                if(has_clicked_edit) {
                    String name = String.valueOf(edittext_write_name.getText());
                    String address = String.valueOf(edittext_write_address.getText());
                    String postcode = String.valueOf(edittext_write_postcode.getText());
                    Utils.updateAddress(getApplicationContext(),
                            current_profile_id,
                            current_username,
                            new MailAddress(name, address, postcode));
                }

                // go back to main activity
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        builder.create().show();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_beg_for_card, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent homeIntent = new Intent(this, MainActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
        }
        return (super.onOptionsItemSelected(item));
    }
}
