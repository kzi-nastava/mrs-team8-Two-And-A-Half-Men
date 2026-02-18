package com.project.mobile.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class TabsPagerAdapter extends FragmentStateAdapter {
    
    private final List<Fragment> fragments = new ArrayList<>();

    public TabsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public TabsPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public void addFragment(Fragment fragment) {
        fragments.add(fragment);
    }

    public void clearFragments() {
        fragments.clear();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }
}
