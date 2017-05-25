package com.guanghuiz.exchangecard.Database.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Guanghui on 10/3/16.
 */
public class DetailedCard {

    @SerializedName("card_name")
    public String card_name;

    // profile id of recipient
    @SerializedName("torecipient_id")
    public int torecipient_id;

//    @SerializedName("torecipient")
    public String torecipient;

    @SerializedName("toAddress")
    public MailAddress toAddress;

    // profile id of sender
    @SerializedName("fromsender_id")
    public int fromsender_id;

    @SerializedName("fromsender")
    public String fromsender;
    //
    @SerializedName("fromAddress")
    public MailAddress fromAddress;

    @SerializedName("sentTime")
    public long sentTime;

    //    @SerializedName("sentDate")
    //    public Date sentDate;

    @SerializedName("arrivedTime")
    public long arrivedTime;

    //    @SerializedName("arrivedDate")
    //    public Date arrivedDate;

    public DetailedCard(long sentTimeMills){
        this.sentTime = sentTimeMills;

    }

    public DetailedCard(){

    }

    public String toString(){
        return this.card_name + ", \nfrom: "
                + this.fromsender_id + ", \nto: "
                + this.torecipient_id + ", \nat: "
                + this.sentTime;
    }

}
