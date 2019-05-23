package com.cube9.gmarket.Orders.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cube9.gmarket.Home.Activities.HomeActivity;
import com.cube9.gmarket.R;

public class OrderPaymentFailedActivity extends AppCompatActivity {
    Button btn_continue_shopping;
    TextView tv_view_order;
    Toolbar toolbar;
    TextView tv_title;
    ImageView iv_back,iv_search,iv_cart;
    TextView tv_cart_count;

    @Override
    protected void onResume() {
        super.onResume();
        iv_search.setVisibility(View.GONE);
        iv_cart.setVisibility(View.GONE);
        tv_cart_count.setVisibility(View.GONE);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_payment_failed);
        initView();
        btn_continue_shopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(OrderPaymentFailedActivity.this, HomeActivity.class);
                startActivity(i);
                finishAffinity();
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(OrderPaymentFailedActivity.this, HomeActivity.class);
                startActivity(i);
                finishAffinity();
            }
        });
    }

    public void initView()
    {
        btn_continue_shopping=(Button)findViewById(R.id.btn_continue_shopping);
        tv_view_order=(TextView) findViewById(R.id.tv_view_order);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        iv_back=(ImageView)toolbar.findViewById(R.id.iv_back);
        iv_search=(ImageView)toolbar.findViewById(R.id.iv_search);
        iv_cart=(ImageView)toolbar.findViewById(R.id.iv_cart);
        tv_title=(TextView) toolbar.findViewById(R.id.tv_title);
        tv_cart_count=(TextView) toolbar.findViewById(R.id.tv_cart_count);

    }//initViewClos

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(OrderPaymentFailedActivity.this,HomeActivity.class);
        startActivity(i);
        finishAffinity();
    }
}
