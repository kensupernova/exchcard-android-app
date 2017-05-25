package com.guanghuiz.exchangecard.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.guanghuiz.exchangecard.Database.model.Card;
import com.guanghuiz.exchangecard.R;

import java.util.List;

/**
 * Created by Guanghui on 26/5/16.
 */
public class MyCardListAdapter extends BaseAdapter {

    private final Context context;
    private final List<Card> cards;
    private LayoutInflater mInflater;

    public MyCardListAdapter(Context context, List<Card> cards) {
        this.context = context;
        this.cards = cards;
        this.mInflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount(){
        return cards.size();
    }
    public int getItemCount(){
        return cards.size();
    }

    @Override
    public Object getItem(int position){
        return cards.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    public boolean addItem(Card card){
        cards.add(card);
        return true;
    }

    @Override
    public View getView(int position, View convertView ,ViewGroup parent){

        final ViewHolder holder;
        if(convertView ==null){
            convertView = mInflater.inflate(R.layout.card_list_item, parent, false);
            holder = new ViewHolder();
            holder.text_card_name = (TextView) convertView.findViewById(R.id.text_card_name);
            holder.heart_love_button = (ImageView) convertView.findViewById(R.id.heart_love_button);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }


        final Card card = cards.get(position);
        holder.text_card_name.setText(card.card_name);
        holder.heart_love_button.setImageResource(R.mipmap.heart_selected_1);
        holder.heart_love_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.heart_love_button.setImageResource(R.mipmap.heart_selected);
            }
        });


        return convertView;
    }



    private class ViewHolder {
        private TextView text_card_name;
        private ImageView heart_love_button;
    }
}
