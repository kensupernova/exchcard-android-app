package com.guanghuiz.exchangecard.Database.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Guanghui on 31/5/16.
 */
public class ReceiveCardWithPhotoResponse {
    @SerializedName("card_name")
    public String card_name;

    @SerializedName("arrived_time")
    public long arrivedTime;

    @SerializedName("has_arrived")
    public boolean has_arrived;

    @SerializedName("name_on_server")
    String name_on_server;

    @SerializedName("url_on_server")
    String url_on_server="";

    @SerializedName("owner_id")
    String owner_id;

    @SerializedName("card_host_id")
    String card_host_id;



    public ReceiveCardWithPhotoResponse(){

    }
}
