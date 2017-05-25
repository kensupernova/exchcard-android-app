package com.guanghuiz.exchangecard.Database.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Guanghui on 27/3/16.
 */
public class AvatarPhotoResponse {

    @SerializedName("name")
    public String name;

    // profile id of recipient
    @SerializedName("url")
    public String url;

    @SerializedName("owner")
    public String owner;

    @SerializedName("owner_id")
    public String owner_id;

    public AvatarPhotoResponse(){

    }
    public String toString(){
        return "avatar on s3: name:"+this.name+",url:"+this.url
                +",owner:" +this.owner
                +",owner_id:"+ this.owner_id;
    }
}
