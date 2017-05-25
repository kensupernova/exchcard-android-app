package com.guanghuiz.exchangecard.Database.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Guanghui on 28/2/16.
 */
public class RegisterUserAddressResponseData {

    @SerializedName("username")
    public String username;

    @SerializedName("id")
    public int profile_id;
    @SerializedName("profileuser_id")
    public String profileuser_id;
    @SerializedName("profileaddress_id")
    public String profileaddress_id;


    @SerializedName("name")
    public String name;
    @SerializedName("address")
    public String address;
    @SerializedName("postcode")
    public String postcode;

    public RegisterUserAddressResponseData(String username,
                                           int id,
                                           String profileaddress_id, String profileuser_id,
                                           String name, String address, String postcode){
        this.profile_id = id;
        this.profileaddress_id = profileaddress_id;
        this.profileuser_id = profileuser_id;

        this.username = username;

        this.name = name;
        this.address = address;
        this.postcode = postcode;
    }


    public String toString(){
        return "username=" + this.username
                +", name="+this.name
                +", address="+this.address
                +", postcode="+this.postcode
                +", id="+this.profile_id
                +", profileaddress_id="+this.profileaddress_id
                +", profileuser_id="+this.profileuser_id;
    }
}
