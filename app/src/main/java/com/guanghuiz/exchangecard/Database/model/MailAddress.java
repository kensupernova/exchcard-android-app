package com.guanghuiz.exchangecard.Database.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Guanghui on 17/2/16.
 */
public class MailAddress {

    @SerializedName("name")
    private String name = " ";
    @SerializedName("address")
    private String address = " ";
    @SerializedName("postcode")
    private String postcode = " ";
    public MailAddress(String name, String address, String postcode){
        this.name = name;
        this.address = address;
        this.postcode = postcode;

    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public String getAddress(){
        return this.address;
    }

    public void setPostcode(String postcode){
        this.postcode = postcode;
    }

    public String getPostcode(){
        return this.postcode;
    }

    public String toString(){
        return this.name +",\n" +
                this.address +",\n" +
                this.postcode;
    }




}
