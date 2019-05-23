package com.cube9.gmarket.Account.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cube9.gmarket.Account.Activity.AllAddressActivity;
import com.cube9.gmarket.Account.Activity.UpdateAddressActivity;
import com.cube9.gmarket.Account.ModelClass.AllAddressPojo;
import com.cube9.gmarket.HelperClass.CustomUtils;
import com.cube9.gmarket.HelperClass.GMarketApplication;
import com.cube9.gmarket.HelperClass.SharedPrefManager;
import com.cube9.gmarket.R;
import com.cube9.gmarket.WebUrls.WebUrls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class AllAddressesAdapter extends RecyclerView.Adapter<AllAddressesAdapter.MyViewHolder> {
    Context context;
    List<AllAddressPojo> allAddressPojoList;
    ProgressBar p_bar;
    TextView tv_no_address_found;
    Button btn_deliver_to_address;
AllAddressActivity allAddressActivity;

    private int SELECTED_POSITION = 0;
    public AllAddressesAdapter(Context context, List<AllAddressPojo> allAddressPojoList, ProgressBar p_bar, TextView tv_no_address_found, Button btn_deliver_to_address, AllAddressActivity allAddressActivity) {
        this.context = context;
        this.allAddressPojoList = allAddressPojoList;
        this.p_bar = p_bar;
        this.tv_no_address_found = tv_no_address_found;
        this.btn_deliver_to_address=btn_deliver_to_address;
        this.allAddressActivity=allAddressActivity;
        SELECTED_POSITION=0;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_all_address, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final AllAddressPojo item=allAddressPojoList.get(position);
        holder.tv_name.setText(item.getFname()+" "+item.getLname());
        holder.tv_street_name.setText(item.getStreet_name());
        holder.tv_city.setText(" "+item.getCity());
        holder.tv_zip.setText(" "+item.getZip());
        holder.tv_state_country.setText(" "+item.getState()+", "+item.getCountry());
        if (item.getChecked().equals("1"))
        {
            holder.rb_address.setChecked(true);
        }
        else
        {
            holder.rb_address.setChecked(false);
        }

        if (item.getIntent_from().equals("my_account_activity"))
        {
            holder.rb_address.setVisibility(View.GONE);
        }
        else
        {
            holder.rb_address.setVisibility(View.VISIBLE);
        }


             holder.iv_edit.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Intent i=new Intent(context, UpdateAddressActivity.class);
                     i.putExtra("address_id",item.getAddress_id());
                     context.startActivity(i);
                 }
             });
             holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     DeletAddress(item.getAddress_id(),position);
                 }
             });
            holder.rb_address.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     holder.rb_address.setChecked(false);
                     for (int i=0;i<allAddressPojoList.size();i++)
                     {
                         allAddressPojoList.get(i).setChecked("0");
                     }
                     allAddressPojoList.get(position).setChecked("1");
                     notifyDataSetChanged();
                 }
             });

       /* holder.cv_update_address_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.rb_address.setChecked(false);
                for (int i=0;i<allAddressPojoList.size();i++)
                {
                    allAddressPojoList.get(i).setChecked("0");
                }
                allAddressPojoList.get(position).setChecked("1");
                notifyDataSetChanged();
            }
        });*/
       /* btn_deliver_to_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.getIntent_from().equals("my_account_activity"))
                {
                    context.startActivity(new Intent(context,AddNewAddressActivity.class));
                }
                else
                {
                    UpdateAddress(allAddressPojoList.get(position).getFname(),item.getLname(),item.getTelephone(),item.getStreet1(),item.getStreet2(),item.getCity(),item.getZip(),item.getState_id(),item.getCountry_code(),item.getAddress_id());
                }
            }
        });*/

        holder.iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, holder.iv_menu);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.poupup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item1) {

                        if (item1.getTitle().equals(context.getResources().getString(R.string.edit)))
                        {
                            Intent i=new Intent(context, UpdateAddressActivity.class);
                            i.putExtra("address_id",item.getAddress_id());
                            context.startActivity(i);
                        }
                        else if (item1.getTitle().equals(context.getResources().getString(R.string.delete)))
                        {
                            DeletAddress(item.getAddress_id(),position);
                        }

                        return true;
                    }
                });

                popup.show();//showing popup menu
            }

        });

    }

    @Override
    public int getItemCount() {
        return allAddressPojoList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        public TextView tv_name, tv_street_name,tv_city,tv_zip,tv_state_country;
        ImageView iv_edit,iv_delete,iv_menu;
        CardView cv_update_address_details;
        RadioButton rb_address;


        public MyViewHolder(View view) {
            super(view);

            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_street_name = (TextView) view.findViewById(R.id.tv_street_name);
            tv_city = (TextView) view.findViewById(R.id.tv_city);
            tv_zip = (TextView) view.findViewById(R.id.tv_zip);
            tv_state_country = (TextView) view.findViewById(R.id.tv_state_country);
            iv_edit = (ImageView) view.findViewById(R.id.iv_edit);
            iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
            iv_menu = (ImageView) view.findViewById(R.id.iv_menu);
            cv_update_address_details = (CardView) view.findViewById(R.id.cv_update_address_details);
            rb_address=(RadioButton)view.findViewById(R.id.rb_address);
            cv_update_address_details.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cv_update_address_details:
                   // holder.rb_address.setChecked(false);
                    for (int i=0;i<allAddressPojoList.size();i++)
                    {
                        allAddressPojoList.get(i).setChecked("0");
                    }
                    allAddressPojoList.get(this.getAdapterPosition()).setChecked("1");

                    if(allAddressPojoList.get(this.getAdapterPosition()).getIntent_from().equals("my_account_activity"))
                    {
                      //  context.startActivity(new Intent(context,AddNewAddressActivity.class));
                    }
                    else {


                        setSelectedMenuTypePosition(this.getAdapterPosition());
                        allAddressPojoList.get(this.getAdapterPosition()).setChecked("1");
                    }
                    notifyDataSetChanged();

                    break;
            }
        }
    }
    private void setSelectedMenuTypePosition(int position) {
        SELECTED_POSITION = position;
    }


    public int getSelectedMenuPosition() {
        return SELECTED_POSITION;
    }

    public void DeletAddress(final String address_id, final int position)
    {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.DELETE_ADDRESS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {

                            JSONObject Jsonobj = new JSONObject(response);
                            String status=Jsonobj.getString("status");

                            if (status.equals("1"))
                            {
                                String message=Jsonobj.getString("message");
                                CustomUtils.showToast(message,context);
                                allAddressPojoList.remove(position);
                                notifyDataSetChanged();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        p_bar.setVisibility(View.GONE);
                      //  Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("address_id",address_id);


                return params;
            }
        };

        GMarketApplication.getInstance(context).addToRequestQueue(stringRequest);
    }



    public void UpdateAddress(final String fname, final String lname, final String mobile, final String street1, final String street2, final String city, final String zip, final String state, final String country_id, final String address_id)
    {
        p_bar.setVisibility(View.VISIBLE);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.UPDATE_ADDRESS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {

                            JSONObject Jsonobj = new JSONObject(response);
                            String status = Jsonobj.getString("status");

                            if (status.equals("1")) {
                                String message=Jsonobj.getString("message");
                                //openAlertDialog();
                                Intent intent = new Intent();
                                intent.putExtra("intent_from", "delivery_detail" );
                                allAddressActivity.setResult(RESULT_OK, intent);
                                allAddressActivity. finish();

                            } else {

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        p_bar.setVisibility(View.GONE);
                        //Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id",new SharedPrefManager(context).GetCustomerId());
                params.put("fname",fname);
                params.put("lname",lname);
                params.put("contact",mobile);
                params.put("street1",street1);
                params.put("street2",street2);
                params.put("city",city);
                params.put("zip",zip);
                params.put("state",state);
                params.put("country",country_id);
                params.put("address_id",address_id);
                params.put("default_billing","1");
                params.put("default_shipping","1");

                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(context).addToRequestQueue(stringRequest);
    }
}
