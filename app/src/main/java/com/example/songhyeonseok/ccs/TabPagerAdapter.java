package com.example.songhyeonseok.ccs;//각 탭을 관리하고 이동하게 해주는 탭 어댑터.

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

public class TabPagerAdapter extends FragmentStatePagerAdapter {

    // Count number of tabs
    private int tabCount;
    private TabFragment1 tabFragment1;
    private TabFragment2 tabFragment2;
    private TabFragment3 tabFragment3;
    String st="";

    public TabPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {

        // Returning the current tabs
        switch (position) {
            case 0:
                tabFragment1 = new TabFragment1();
                return tabFragment1;
            case 1:
                tabFragment2 = new TabFragment2();
                return tabFragment2;
            case 2:
                tabFragment3 = new TabFragment3();
                return tabFragment3;
            default:
                return null;
        }
    }



    @Override
    public int getCount() {
        return tabCount;
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }
}


