package com.cube9.gmarket.Category.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.cube9.gmarket.Category.Activity.SubCategoryDetailsActivity;
import com.cube9.gmarket.Category.Adapter.SubCategoryExpandableAdapter;
import com.cube9.gmarket.Category.ModelClass.ParentSubCatPojo;
import com.cube9.gmarket.Category.ModelClass.SubCatPojo;
import com.cube9.gmarket.HelperClass.SharedPrefManager;
import com.cube9.gmarket.Home.ModelClass.ParentDrawerPojo;
import com.cube9.gmarket.Products.Activity.ProductListActivity;
import com.cube9.gmarket.R;
import com.cube9.gmarket.WebUrls.WebUrls;
import com.cube9.gmarket.network.Api;
import com.cube9.gmarket.network.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.mime.TypedByteArray;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubCategoryFragment extends Fragment {
View v;
ExpandableListView ex_subcat;
    SubCategoryExpandableAdapter expandableListAdapter;
    String cat_id,cat_name;
    private ArrayList<ParentSubCatPojo> parentSubCatPojos = new ArrayList<ParentSubCatPojo>();

    public SubCategoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_sub_category, container, false);
        initView(v);

        getSubCat();

        ex_subcat.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(
                        getApplicationContext(),
                        parentSubCatPojos.get(groupPosition).getSubCatList().get(childPosition).getCat_id()
                             , Toast.LENGTH_SHORT

                ).show();

                startActivity(new Intent(getActivity(),ProductListActivity.class).putExtra("cat_id",parentSubCatPojos.get(groupPosition).getSubCatList().get(childPosition).getCat_id()).putExtra("cat_name",parentSubCatPojos.get(groupPosition).getSubCatList().get(childPosition).getCat_name()));
                return false;
            }
        });

        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.i("bssss", "keyCode: " + keyCode);
                if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    Log.i("bssss", "onKey Back listener is working!!!");
                    getFragmentManager().popBackStack(null, android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    return true;
                }
                return false;
            }
        });
        return v;

    }

    public void initView(View v)
    {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
         cat_name = getArguments().getString("cat_name");
         cat_id = getArguments().getString("cat_id");

        ex_subcat=v.findViewById(R.id.ex_subcat);
        ex_subcat.setIndicatorBounds(width - GetPixelFromDips(50), width - GetPixelFromDips(10));


    }//initViewClose;

    public int GetPixelFromDips(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }
    public void getSubCat() {

        // List<AllProductsModel> movies = AllProductsModel.createMovies(adapter.getItemCount());
        // progressBar.setVisibility(View.GONE);
        RetrofitClient retrofitClient = new RetrofitClient();
        Api service = retrofitClient.getAPIClient(WebUrls.DOMAIN_URL);
        service.getSubCategory(cat_id,  new SharedPrefManager(getActivity()).getLanguage(),
                new retrofit.Callback<retrofit.client.Response>() {
                    @Override
                    public void success(retrofit.client.Response result, retrofit.client.Response response) {
                        try {

                            String bodyString = new String(((TypedByteArray) response.getBody()).getBytes());
                            JSONObject jsonObject = new JSONObject(bodyString);
                            Log.i("JSONNNNNNNN", "" + jsonObject.toString());

                            String status=jsonObject.getString("status");
                            if (status.equals("1"))
                            {
                                JSONObject category=jsonObject.getJSONObject("category");
                                JSONArray category_array=category.getJSONArray("category");
                                for (int k=0;k<category_array.length();k++)
                                {
                                    ParentSubCatPojo parentSubCatPojo=new ParentSubCatPojo();
                                    JSONObject j1=category_array.getJSONObject(k);
                                    String cat_id=j1.getString("cat_id");
                                    String cat_name=j1.getString("cat_name");
                                    String image=j1.getString("image");
                                    JSONArray Sub_cats=j1.getJSONArray("Sub_cats");
                                    parentSubCatPojo.setCat_id(cat_id);
                                    parentSubCatPojo.setCat_name(cat_name);


                                    for (int j=0;j<Sub_cats.length();j++)
                                    {

                                        ArrayList<SubCatPojo> subCatPojoList = parentSubCatPojo.getSubCatList();
                                        JSONObject j2=Sub_cats.getJSONObject(j);
                                        String sub_cat_id=j2.getString("cat_id");
                                        String sub_cat_name=j2.getString("cat_name");
                                        String sub_image=j2.getString("image");
                                        subCatPojoList.add(new SubCatPojo(sub_cat_id,sub_cat_name,sub_image));
                                        parentSubCatPojo.SetSubCatList(subCatPojoList);


                                    }
                                    parentSubCatPojos.add(parentSubCatPojo);


                                }
                                expandableListAdapter=new SubCategoryExpandableAdapter(getActivity(),parentSubCatPojos);
                                ex_subcat.setAdapter(expandableListAdapter);
                            }


                        } catch (JSONException je) {

                            Log.i("JSONNNNNNNN", "" + je.toString());
                            je.printStackTrace();

                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {

                        Log.i("JSONNNNNNNN", "" + error.getMessage());
                        error.printStackTrace();
                    }
                });


   /* if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
    else isLastPage = true;*/
    }

}
