package com.cube9.gmarket.SearchProducts;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cube9.gmarket.HelperClass.GMarketApplication;
import com.cube9.gmarket.HelperClass.SharedPrefManager;
import com.cube9.gmarket.R;
import com.cube9.gmarket.SearchProducts.Adapters.SearchProductListAdapter;
import com.cube9.gmarket.SearchProducts.ModelClass.SearchProductModel;
import com.cube9.gmarket.WebUrls.WebUrls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchProductsActivity extends AppCompatActivity {
RecyclerView rv_search;
EditText et_search;
ProgressBar p_bar;
String search_value="";
ImageView iv_search,iv_close;
    EditText auto_search;
List<SearchProductModel> searchProductModelList,tempItems,suggestions;
SearchProductListAdapter adapter;
Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_products);
        initView();
        auto_search.setEnabled(false);
       // SearchAllProducts();

        auto_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString().trim().length()>0)
                {
                    iv_search.setImageDrawable(getResources().getDrawable(R.drawable.back));
                    iv_close.setVisibility(View.VISIBLE);
                }
                else
                {
                    iv_search.setImageDrawable(getResources().getDrawable(R.drawable.search_gray));
                    iv_close.setVisibility(View.GONE);
                }
                //after the change calling the method and passing the search input
                filter(editable.toString());
            }
        });

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auto_search.setText("");
                rv_search.setAdapter(null);
            }
        });
        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    public void initView()
    {
        context=this;
        searchProductModelList=new ArrayList<SearchProductModel>();
        auto_search=(EditText)findViewById(R.id.auto_search);
        iv_search=(ImageView) findViewById(R.id.iv_search);
        iv_close=(ImageView) findViewById(R.id.iv_close);
        rv_search=(RecyclerView)findViewById(R.id.rv_search);
        p_bar=(ProgressBar)findViewById(R.id.p_bar);
        rv_search.setHasFixedSize(true);
        rv_search.setLayoutManager(new LinearLayoutManager(this));

        suggestions = new ArrayList<SearchProductModel>();
    }//initViewClose

    private void filter(String text) {
        //new array list that will hold the filtered data
        ArrayList<SearchProductModel> filterdNames = new ArrayList<>();

        //looping through existing elements
        if (tempItems!=null)
        {
            for (SearchProductModel people : tempItems) {
                //if the existing elements contains the search input
                if (people.getName().toLowerCase().contains(text.toLowerCase())) {
                    //adding the element to filtered list
                    filterdNames.add(new SearchProductModel(people.getEntity_id(),people.getType_id(),people.getSku(),people.getCreated_at(),people.getUpdated_at(),people.getPrice(),people.getName(),people.getCategory_id()));
                }

            }
        }

        if (filterdNames.size()>0)
        {
            adapter = new SearchProductListAdapter(filterdNames,context);
            rv_search.setAdapter(adapter);
            //calling a method of the adapter class and passing the filtered list
            adapter.filterList(filterdNames);

        }
        else {
            rv_search.setAdapter(null);
        }
        if (text.trim().length()==0)
        {
            filterdNames.clear();
            rv_search.setAdapter(null);
        }

    }

    public void SearchAllProducts()
    {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.URL_SEARCH_PRODUCTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {
                            auto_search.setEnabled(true);
                            JSONObject Jsonobj = new JSONObject(response);
                            Log.i("SearchResult",""+Jsonobj.toString());
                            String status=Jsonobj.getString("status");

                            if (status.equals("1"))
                            {
                                JSONArray productinfo=Jsonobj.getJSONArray("productinfo");
                                for (int i=0;i<productinfo.length();i++)
                                {
                                    JSONObject j1=productinfo.getJSONObject(i);
                                    String entity_id=j1.getString("entity_id");


                                    String type_id=j1.getString("type_id");
                                    String sku=j1.getString("sku");

                                    String created_at=j1.getString("created_at");
                                    String updated_at=j1.getString("updated_at");
                                    String price=j1.getString("price");
                                    String name=j1.getString("name");
                                    String category_id=j1.getString("category_id");



                                    searchProductModelList.add(new SearchProductModel(entity_id,type_id,sku,created_at,updated_at,price,name,category_id));
                                }
                                tempItems = new ArrayList<SearchProductModel>(searchProductModelList);


                            }
                            else
                            {
                                auto_search.setEnabled(true);
                            }


                        } catch (JSONException e) {
                            auto_search.setEnabled(true);
                            e.printStackTrace();
                            Log.i("JSONNNNNNNNNN",""+e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        p_bar.setVisibility(View.GONE);
                        //  Toast.makeText(SearchProductsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language",new SharedPrefManager(SearchProductsActivity.this).getLanguage());




                return params;
            }
        };

        GMarketApplication.getInstance(SearchProductsActivity.this).addToRequestQueue(stringRequest);

    }//
}
