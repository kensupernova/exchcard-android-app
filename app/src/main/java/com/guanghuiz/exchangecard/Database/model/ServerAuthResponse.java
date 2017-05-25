package com.guanghuiz.exchangecard.Database.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Guanghui on 10/3/16.
 */
public class ServerAuthResponse {
    @SerializedName("username")
    public String username = " ";
    @SerializedName("isauth")
    public Boolean isauth = false;
    @SerializedName("accessToken")
    public String accessToken = " ";

    @SerializedName("profile_id")
    public int profile_id;

    @SerializedName("name")
    public String name="";
    @SerializedName("address")
    public String address="";
    @SerializedName("postcode")
    public String postcode="";

    public ServerAuthResponse(String username, Boolean isauth,
                              String accessToken, int profile_id,
                              String name, String address, String postcode){
        this.username = username;
        this.isauth = isauth;
        this.accessToken = accessToken;
        this.profile_id = profile_id;
        this.name = name;
        this.address = address;
        this.postcode = postcode;
    }

    public String toString(){
        return "username=" + this.username
                +", isauth="+this.isauth
                +", accessToken="+this.accessToken;
    }

    public MailAddress getMailAddress(){
        return new MailAddress(name, address, postcode );
    }
}
