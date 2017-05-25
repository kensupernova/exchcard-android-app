package com.guanghuiz.exchangecard.UI;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.guanghuiz.exchangecard.R;
import com.guanghuiz.exchangecard.Utils.Utils;

public class ResetPasswordActivity extends AppCompatActivity {
    private Button confirm_reset_password;
    private EditText text_old_password;
    private EditText text_new_password;
    private EditText text_new_password_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //        fab.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View view) {
        //                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //                        .setAction("Action", null).show();
        //            }
        //        });

        confirm_reset_password = (Button) findViewById(R.id.confirm_reset_password);
        if(null != confirm_reset_password) {
            confirm_reset_password.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Utils.resetPassword(ResetPasswordActivity.this, "");
                        }
                    }
            );
        }

        text_old_password = (EditText) findViewById(R.id.edittext_old_password);
        text_new_password = (EditText) findViewById(R.id.edittext_new_password);
        text_new_password_2 = (EditText) findViewById(R.id.edittext_new_password_2);

    }

}
