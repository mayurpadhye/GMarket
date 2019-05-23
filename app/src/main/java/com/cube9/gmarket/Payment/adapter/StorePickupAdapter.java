package com.cube9.gmarket.Payment.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;

import com.cube9.gmarket.Delivery.Activity.BuyNowActivity;
import com.cube9.gmarket.Delivery.Activity.DeliveryDetailsActivity;
import com.cube9.gmarket.Payment.ModelClass.StorePickUpPojo;
import com.cube9.gmarket.R;

import java.util.List;

public class StorePickupAdapter extends BaseAdapter {
    Context context;
    List<StorePickUpPojo> storePickUpPojoList;
    String fromActivity;

    public StorePickupAdapter(Context context, List<StorePickUpPojo> storePickUpPojoList,String fromActivity) {
        this.context = context;
        this.storePickUpPojoList = storePickUpPojoList;
        this.fromActivity=fromActivity;
    }

    @Override
    public int getCount() {
        return storePickUpPojoList.size();
    }

    @Override
    public StorePickUpPojo getItem(int position) {
        return storePickUpPojoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("CheckResult")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        // If holder not exist then locate all view from UI file.
        if (convertView == null) {
            // inflate UI from XML file
            convertView = inflater.inflate(R.layout.row_store_pickup, parent, false);
            // get all UI view
            holder = new ViewHolder(convertView);
            // set tag for holder
            convertView.setTag(holder);
        } else {
            // if holder created, get tag from view
            holder = (ViewHolder) convertView.getTag();
        }
        final StorePickUpPojo items = storePickUpPojoList.get(position);

        holder.rb_pickup.setText(items.getTitle() + ", " + items.getAddress() + ", " + items.getPrice()+" FCFA");

        holder.rb_pickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromActivity.equals(""))

                {
                    if (context instanceof DeliveryDetailsActivity) {
                        ((DeliveryDetailsActivity) context).getStorePIckUpDetails(position);
                    }
                }
                else
                {
                    if (context instanceof BuyNowActivity) {
                        ((BuyNowActivity) context).getStorePIckUpDetails(position);
                    }
                }

            }
        });


        return convertView;
    }


    private class ViewHolder {


        RadioButton rb_pickup;

        public ViewHolder(View v) {
            rb_pickup = (RadioButton) v.findViewById(R.id.rb_pickup);

        }
    }
}
