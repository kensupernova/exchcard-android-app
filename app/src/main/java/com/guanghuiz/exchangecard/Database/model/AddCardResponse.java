package com.guanghuiz.exchangecard.Database.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Guanghui on 12/3/16.
 */
public class AddCardResponse {

    @SerializedName("card_name")
    public String card_name;

    // profile id of recipient
    @SerializedName("torecipient_id")
    public int torecipient_id;


    // profile id of sender
    @SerializedName("fromsender_id")
    public int fromsender_id;

    @SerializedName("sent_time")
    public long sentTime;

    //    @SerializedName("arrived_time")
    //    public long arrivedTime;


    public AddCardResponse(){

    }


    public String toString(){
        return this.card_name + ", \nfrom: "
                + this.fromsender_id + ", \nto:"
                + this.torecipient_id + ", \nat:"
                + this.sentTime;
    }
}
