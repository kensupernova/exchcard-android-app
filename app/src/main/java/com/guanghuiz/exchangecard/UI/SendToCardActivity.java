package com.guanghuiz.exchangecard.UI;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.guanghuiz.exchangecard.R;
import com.guanghuiz.exchangecard.SendReceiveApi.ServiceApi;
import com.guanghuiz.exchangecard.SendReceiveApi.ServiceGenerator;
import com.guanghuiz.exchangecard.Utils.SessionManager;
import com.guanghuiz.exchangecard.Utils.Utils;
import com.guanghuiz.exchangecard.Database.model.Card;
import com.guanghuiz.exchangecard.Database.model.RecipientForCard;
import com.guanghuiz.exchangecard.Database.model.AddCardResponse;
import com.guanghuiz.exchangecard.Tasks.ReceiveAddressFromServerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Guanghui on 17/2/16.
 */
public class SendToCardActivity extends AppCompatActivity {

    private String msg = " ";
    private TextView textview_address;
    private ProgressBar progressBar;
    private Button btn_confirm_send_card_to_address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_to_card);

        // set the toolbar to act as action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_send_card);
        setSupportActionBar(toolbar);

        // back to parent acitivity button, remove title first
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);


        progressBar =(ProgressBar) findViewById(R.id.progressBar);

        textview_address = (TextView)findViewById(R.id.textview_send_card_address);

        // get the msg from the intent which start this activity
        msg = getIntent().getStringExtra(FragmentFirstTab.SEND_CARD_INTENT);

        // set the progress bar visible
        progressBar.setVisibility(View.VISIBLE);
        // check internet available
        if(!Utils.isInternetAvailable(this)){
            // if internet is not available , jump back
            Toast.makeText(getApplicationContext(), R.string.internet_is_not_connected,
                    Toast.LENGTH_SHORT).show();

            finish();
        } else {

            ReceiveAddressFromServerTask task = new ReceiveAddressFromServerTask(progressBar, this);
            task.execute(msg);
        }

        // backwards button
        ImageButton btn_go_back = (ImageButton) findViewById(R.id.imagebutton_send_to_backward);
        if(null != btn_go_back){
            btn_go_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(Activity.RESULT_CANCELED);
                    finish();

                }
            });
        }


        // the button confirm address
        btn_confirm_send_card_to_address =
                (Button) findViewById(R.id.btn_confirm_send_card_to_address);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home)
        {
            finish();       //kill subActivity so as to get same screen which was before going to subActivity
        }

        return super.onOptionsItemSelected(item);
    }


    public void updateTextView(final RecipientForCard recipient){


        if(null == recipient ){
            textview_address.setText("Address is null!");
            // make confirm address button invisible
            btn_confirm_send_card_to_address.setVisibility(View.GONE);
        } else {

            textview_address.setText(recipient.getMailAddress().toString());

            btn_confirm_send_card_to_address.setVisibility(View.VISIBLE);

            btn_confirm_send_card_to_address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // create the card
                    long current_in_millseconds = System.currentTimeMillis();
                    // set the current time as the time card is created
                    final Card sentCard = new Card();

                    sentCard.sentTime = current_in_millseconds;
                    // set card id
                    sentCard.card_name = Utils.getCardId();

                    /*
                    SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
                    String current_user = settings.getString(MainActivity.key_current_log_in_username,
                            "user");
                    */
                    SessionManager sessionManager = new SessionManager(SendToCardActivity.this);
                    String current_user = sessionManager.getUserName();

                    sentCard.fromsender_id = recipient.sender_profile_id;
                    // Utils.getProfileIdByUser(getApplicationContext(), current_user);
                    // sentCard.fromsender = current_user;
                    // sentCard.fromAddress = Utils.getMailAddressByUser(getApplicationContext(), current_user);
                    // if from sender id == -1, not assigned correctly, need to fix later

                    // recipient name and address are from the server
                    sentCard.torecipient_id = recipient.recipient_profile_id;
                    // sentCard.torecipient = recipient.recipient_user_username;
                    // sentCard.toAddress = recipient.getMailAddress();

                    Log.i(MainActivity.TAG, "card: "+ sentCard.toString());

                    // add card

                    String pw =  Utils.getPW(SendToCardActivity.this, current_user);
                    Log.i(MainActivity.TAG, "ADD CARD:"+current_user +":" + pw);
                    ServiceApi api = ServiceGenerator.createService(ServiceApi.class, current_user,
                           pw);

                    // ServiceApi api = ServiceGenerator.createService(ServiceApi.class, current_user,
                     //       "-2292-8-477940-10912761233779-1949-91116");


                    Call<AddCardResponse> call = api.addCard(sentCard);

                    // async resgister card to server
                    call.enqueue(new Callback<AddCardResponse>() {
                        @Override
                        public void onResponse(Call<AddCardResponse> call, Response<AddCardResponse> response) {
                            int result = response.code();
                            Log.i(MainActivity.TAG, "Add card " +"status code: " + result);
                            AddCardResponse cardResponse = response.body();
                            Log.i(MainActivity.TAG, ""+cardResponse);
                            if(cardResponse != null){
                                Log.i(MainActivity.TAG, sentCard.card_name +" Add succesfully!");
                                Toast.makeText(getApplicationContext(), "Add card succeed!"+result, Toast.LENGTH_SHORT).show();

                            } else{
                                Log.i(MainActivity.TAG, sentCard.card_name +" Add sent card fail!" + response.errorBody());
                                Log.i(MainActivity.TAG, sentCard.card_name +" Add sent card fail!" + response.message());
                                Toast.makeText(getApplicationContext(), "Add card fail!" +result, Toast.LENGTH_SHORT).show();
                                Toast.makeText(getApplicationContext(), "Add card fail!" +response.message(), Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<AddCardResponse> call, Throwable t) {
                            Log.i(MainActivity.TAG, "Add card " + call.toString());
                            Log.i(MainActivity.TAG, "Add card " + t.toString());
                            Toast.makeText(getApplicationContext(), "Add card fail!"+ call.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    Log.i(MainActivity.TAG, sentCard.toString());

                    setResult(RESULT_OK);
                    finish();
                }
            });
        }

    }
}
