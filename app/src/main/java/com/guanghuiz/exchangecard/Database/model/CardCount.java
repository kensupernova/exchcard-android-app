package com.guanghuiz.exchangecard.Database.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Guanghui on 13/3/16.
 */
public class CardCount {
    @SerializedName("sent_cards")
    public CardArrivedTravelling sent_cards;

    @SerializedName("received_cards")
    public CardArrivedTravelling received_cards;

    public CardCount(CardArrivedTravelling sent_cards, CardArrivedTravelling received_cards){
        this.received_cards = received_cards;
        this.sent_cards = sent_cards;

    }

    public CardArrivedTravelling getSentCards(){
        return this.sent_cards;
    }

    public CardArrivedTravelling getReceivedCards(){
        return this.received_cards;
    }

    public String toString(){
        return "sent_cards:" + sent_cards
                +", " + "received_cards: "+ received_cards;
     }
}
