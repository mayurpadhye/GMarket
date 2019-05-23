package com.cube9.gmarket.Orders.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cube9.gmarket.Orders.Activities.OrderDetailsActivity;
import com.cube9.gmarket.Orders.ModelClass.OrderListPojo;
import com.cube9.gmarket.R;

import java.util.List;

public class MyOrderListAdapter extends RecyclerView.Adapter<MyOrderListAdapter.MyViewHolder>  {
    private List<OrderListPojo> orderListPojosList;
    Context context;

    public MyOrderListAdapter(List<OrderListPojo> orderListPojosList, Context context) {
        this.orderListPojosList = orderListPojosList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyOrderListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_my_orders, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyOrderListAdapter.MyViewHolder holder, int position) {
      final OrderListPojo items=orderListPojosList.get(position);


      holder.tv_order_id.setText(Html.fromHtml("<font color="+context.getResources().getColor(R.color.black)+">"+context.getResources().getString(R.string.order_id)+"</font> <font color="+context.getResources().getColor(R.color.blue)+">"+items.getOrder_id()+"</font>"));
      holder.tv_order_date.setText(Html.fromHtml("<font color="+context.getResources().getColor(R.color.black)+">"+context.getResources().getString(R.string.date)+"</font> <font color="+context.getResources().getColor(R.color.blue)+">"+items.getOrder_date()+"</font>"));


      holder.tv_ship_to.setText(items.getShip_to());
        //"FCFA "+new DecimalFormat("##.##").format(Double.parseDouble(item.getProduct_special_price()))
      String order_total=items.getOrder_total();

      holder.tv_order_price.setText(Html.fromHtml("<font color="+context.getResources().getColor(R.color.black)+">"+context.getResources().getString(R.string.currency)+": "+"</font> <font color="+context.getResources().getColor(R.color.blue)+">"+" "+order_total+"</font>"));

      holder.tv_status.setText(Html.fromHtml("<font color="+context.getResources().getColor(R.color.black)+">"+context.getResources().getString(R.string.order_status)+": "+"</font> <font color="+context.getResources().getColor(R.color.blue)+">"+items.getOrder_status()+"</font>"));

      holder.btn_view_order.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent i=new Intent(context, OrderDetailsActivity.class);
              i.putExtra("order_id",items.getOrder_id());
              context.startActivity(i);
          }
      });


    }

    @Override
    public int getItemCount() {
        return orderListPojosList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_order_id,tv_order_date,tv_ship_to,tv_order_price,tv_status;
        Button btn_view_order;
        public MyViewHolder(View view) {
            super(view);

            tv_order_id = (TextView) view.findViewById(R.id.tv_order_id);
            tv_order_date = (TextView) view.findViewById(R.id.tv_order_date);
            tv_status = (TextView) view.findViewById(R.id.tv_status);
            tv_ship_to = (TextView) view.findViewById(R.id.tv_ship_to);
            tv_order_price = (TextView) view.findViewById(R.id.tv_order_price);
             btn_view_order = (Button) view.findViewById(R.id.btn_view_order);

        }

    }
}
