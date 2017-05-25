package com.guanghuiz.exchangecard.Database.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Guanghui on 28/2/16.
 */
public class RegisterUserAddressRequestData {

    @SerializedName("username")
    public String username = " ";
    @SerializedName("password")
    public String password = " ";
    @SerializedName("email")
    public String email = " ";

    @SerializedName("name")
    public String name= " ";
    @SerializedName("address")
    public String address = " ";
    @SerializedName("postcode")
    public String postcode = " ";

    public RegisterUserAddressRequestData(String username, String password, String email,
                                          String name, String address, String postcode){
        // strings to create user
        this.username = username;
        this.email = email;
        this.password = password;
        // strings to create address
        this.name = name;
        this.address = address;
        this.postcode = postcode;
    }


    public String toString(){
        return "username=" + this.username
                +", password="+this.password
                +", email="+this.email
                +", name="+this.name
                +", address="+this.address
                +", postcode="+this.postcode;
    }
}
