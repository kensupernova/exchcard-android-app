package com.guanghuiz.exchangecard.Database.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Guanghui on 11/4/16.
 */
public class CardPhoto {

    @SerializedName("owner_id")
    String owner_id;

    @SerializedName("card_host_id")
    String card_host_id;

    @SerializedName("name_on_server")
    String name_on_server;

    @SerializedName("url_on_server")
    String url_on_server="";

    String path_local="";

    public CardPhoto(String name, String url, String path){
        this.name_on_server = name;
        this.url_on_server = url;
        this.path_local = path;
    }

    public String toString(){
        return "owner id: "+this.owner_id +
                "card host id: "+this.card_host_id +
                "name on server:  "+this.name_on_server +
                "url on server: "+this.url_on_server;
    }
}
