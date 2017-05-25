package com.guanghuiz.exchangecard.Database.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Guanghui on 19/3/16.
 */
public class RegisterCard {

    @SerializedName("card_name")
    public String card_name;

    // profile id of recipient
    @SerializedName("profile_id")
    public int profile_id;

    public RegisterCard(String card_name, int profile_id){
        this.card_name = card_name;
        this.profile_id = profile_id;

    }

}
