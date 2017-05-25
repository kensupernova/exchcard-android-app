package com.guanghuiz.exchangecard.Database.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Guanghui on 17/2/16.
 */
public class User {

    @SerializedName("username")
    public String username = " ";
    @SerializedName("password")
    public String password = " ";
    @SerializedName("email")
    public String email = " ";

    public User(String username){
        this.username = username;
        this.email = username;
    }

    public User(String username, String password){
        this.username = username;
        this.password = password;
        this.email = username;
    }


    public String toString(){
        return "username=" + this.username
                +", pw="+this.password
                +", email="+this.email;
    }
}
