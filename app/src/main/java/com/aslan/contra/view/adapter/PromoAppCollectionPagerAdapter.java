package com.aslan.contra.view.adapter;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.aslan.contra.view.fragment.PromoAppObjectFragment;

/**
 * Created by Vishnuvathsasarma on 26-Jan-16.
 */
public class PromoAppCollectionPagerAdapter extends FragmentStatePagerAdapter {
    final int apps;

    public PromoAppCollectionPagerAdapter(FragmentManager fm, final int apps) {
        super(fm);
        this.apps = apps;
    }

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {
        //do nothing here! no call to super.restoreState(arg0, arg1);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = new PromoAppObjectFragment();
        Bundle args = new Bundle();
        args.putInt(PromoAppObjectFragment.ARG_OBJECT, i); // Our object is just an integer :-P
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        // For this contrived example, we have a 100-object collection.
        return apps;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "OBJECT " + position;
    }
}
