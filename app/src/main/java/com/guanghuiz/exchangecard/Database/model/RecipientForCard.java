package com.guanghuiz.exchangecard.Database.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Guanghui on 12/3/16.
 */
public class RecipientForCard {
    @SerializedName("sender_profile_id")
    public int sender_profile_id;

    @SerializedName("profile_id")
    public int recipient_profile_id;

    //    @SerializedName("profile_user_id")
    //    public int recipient_user_id;
    //
    //    @SerializedName("profile_address_id")
    //    public int recipient_address_id;


    @SerializedName("profile_user_username")
    public String recipient_user_username;

    @SerializedName("name")
    public String name = " ";
    @SerializedName("address")
    public String address = " ";
    @SerializedName("postcode")
    public String postcode = " ";

    public RecipientForCard (String name, String address, String postcode,
                             int recipient_profile_id, int recipient_user_id, int recipient_address_id,
                             String recipient_username){
        this.name = name;
        this.address = address;
        this.postcode = postcode;
        this.recipient_user_username = recipient_username;
        this.recipient_profile_id = recipient_profile_id;
        ///this.recipient_user_id = recipient_user_id;
        //this.recipient_address_id = recipient_address_id;

    }

    public MailAddress getMailAddress(){
        return new MailAddress(this.name,
                this.address, this.postcode);
    }

    public String toString(){
        return "recipient_profile_id="+ this.recipient_profile_id +"\n" +
            "name="+ this.name +"\n" +
            "addr="+  this.address +"\n" +
            "postcode="+ this.postcode +"\n";
    }
}
