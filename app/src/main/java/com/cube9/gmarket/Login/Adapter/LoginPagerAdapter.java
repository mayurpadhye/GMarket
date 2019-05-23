package com.cube9.gmarket.Login.Adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class LoginPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mmFragmentTitleList= new ArrayList<>();

    private final List<Fragment>  FragmentList = new ArrayList<>();
    private final List<String> list= new ArrayList<>();

    public LoginPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return FragmentList.get(position);
    }

    @Override
    public int getCount() {
        return FragmentList.size();
    }

    public void addFrag(Fragment fragment, String title) {
        FragmentList.add(fragment);
        list.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return list.get(position);
    }
}
