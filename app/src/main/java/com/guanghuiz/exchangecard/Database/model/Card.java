package com.guanghuiz.exchangecard.Database.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Guanghui on 10/3/16.
 */
public class Card {


    @SerializedName("card_name")
    public String card_name;

    // profile id of recipient
    @SerializedName("torecipient_id")
    public int torecipient_id;

    // profile id of sender
    @SerializedName("fromsender_id")
    public int fromsender_id;


    // sent time in seconds
    @SerializedName("sent_time")
    public long sentTime;

    // arrived time in seconds
    @SerializedName("arrived_time")
    public long arrivedTime;

    @SerializedName("has_arrived")
    public boolean has_arrived;

    public String cachedPhotoPath;

    public String serverPhotoUrl;

    public Card(){
    }

    public Card(String card_name){
        this.card_name = card_name;
    }

    public String toString(){
        return "\n"+this.card_name + ", \nfrom: "
                + this.fromsender_id + ", \nto: "
                + this.torecipient_id + ", \nat: "
                + this.sentTime +", \nhas arrived: "
                + this.has_arrived +", \narrive: "
                + this.arrivedTime;
    }

}
