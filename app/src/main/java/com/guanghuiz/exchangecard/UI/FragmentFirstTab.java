package com.guanghuiz.exchangecard.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.guanghuiz.exchangecard.Database.model.Card;
import com.guanghuiz.exchangecard.R;
import com.guanghuiz.exchangecard.Utils.MyCardListAdapter;
import com.guanghuiz.exchangecard.Utils.SessionManager;
import com.guanghuiz.exchangecard.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guanghui on 16/2/16.
 */
public class FragmentFirstTab extends Fragment {
    public static final String ARG_OBJECT = "object";

    public static final String SEND_CARD_INTENT = "SEND_CARD";
    public static final int register_received_card_request_code = 10;
    public static final int send_to_card_request_code = 11;
    private SessionManager sessionManager;

    public FragmentFirstTab() {
    }

    public static FragmentFirstTab newInstance(String msg) {
        FragmentFirstTab fragment = new FragmentFirstTab();
        Bundle args = new Bundle();
        args.putString(ARG_OBJECT, msg);
        // add bundle data to fragment
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        Bundle arg = getArguments();
        String msg = arg.getString(ARG_OBJECT);
        sessionManager = new SessionManager(getActivity());

        // then add the data to the fragment view
        View rootView = inflater.inflate(R.layout.fragment_page_first_tab, container, false);

        //
        ListView listView = (ListView) rootView.findViewById(R.id.cardPhotosList);
        List<Card> cardList = new ArrayList<>();
        cardList.add(new Card("Card 1"));
        cardList.add(new Card("Card 2"));
        cardList.add(new Card("Card 3"));

        cardList.add(new Card("Card 4"));
        cardList.add(new Card("Card 5"));
        MyCardListAdapter adapter = new MyCardListAdapter(this.getContext(), cardList);
        listView.setAdapter(adapter);

        // send card
        // 寄送给别人明信片

        Button btn_send_card = (Button) rootView.findViewById(R.id.btn_send_card);
        btn_send_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if not loged in, log in first

                if (!sessionManager.isLoggedIn()) {
                    Utils.showAlertRedirectLogIn(sessionManager, getActivity());
                } else {

                    Log.i("exchangecard", "send card");
                    if (!Utils.isInternetAvailable(FragmentFirstTab.this.getContext())) {
                        // if internet is not available , jump back
                        Toast.makeText(FragmentFirstTab.this.getContext(),
                                "No Internet, Check Connectivity!",
                                Toast.LENGTH_LONG).show();
                    } else {
                        // jump to get_send_card_task_fragment
                        Intent intent = new Intent(getContext(), SendToCardActivity.class);
                        intent.putExtra(SEND_CARD_INTENT, "I want to send card");
                        startActivityForResult(intent, send_to_card_request_code);
                    }


                }
            }
        });


        // 登录收到的明信片
        Button register_received_card = (Button) rootView.findViewById(R.id.btn_register_received_card);
        register_received_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if not loged in, log in first
                if (!sessionManager.isLoggedIn()) {
                    Utils.showAlertRedirectLogIn(sessionManager, getActivity());
                } else {

                    Log.i("exchangecard", "register received card card");

                    if (!Utils.isInternetAvailable(FragmentFirstTab.this.getContext())) {
                        // if internet is not available , jump back
                        Toast.makeText(FragmentFirstTab.this.getContext(),
                                "No Internet, Check Connectivity!",
                                Toast.LENGTH_LONG).show();
                    } else {
                        // jump to get_send_card_task_fragment
                        Intent intent = new Intent(getContext(), RegisterCardActivity.class);
                        intent.putExtra(SEND_CARD_INTENT, "receive_card");
                        startActivityForResult(intent, register_received_card_request_code);
                    }

                }
            }
        });

        return rootView;


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == register_received_card_request_code) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                // Do something with the contact here (bigger example below)
            }
        } else if(requestCode == send_to_card_request_code){
            if (resultCode == Activity.RESULT_OK) {
                // Do something with the contact here (bigger example below)
            }
        }
    }


}