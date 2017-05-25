package com.guanghuiz.exchangecard.Database.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Guanghui on 16/3/16.
 */
public class CardArrivedTravelling {
    @SerializedName("total")
    public int total;

    @SerializedName("arrived")
    public int arrived;

    @SerializedName("travelling")
    public int travelling;

    public CardArrivedTravelling(int total, int arrived, int travelling){
        this.total = total;
        this.arrived = arrived;
        this.travelling = travelling;
    }

    public String toString(){
        return "{ total: " + this.total +
                ", arrived: " + this.arrived +
                ", travelling: " + this.travelling
                +" }";
    }

}
