package com.project.mobile;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

public class FragmentTransition {
    public static void to(Fragment newFragment, FragmentActivity activity, boolean addToBackstack, int layoutViewID)
    {

        System.out.println("pogodjena metoda");
        FragmentManager fm = activity.getSupportFragmentManager();

        Fragment specificFragment = fm.findFragmentByTag("DELETE");
        if (specificFragment != null) {
            fm.beginTransaction()
                    .remove(specificFragment)
                    .commitNow();
        }
        FragmentTransaction transaction = activity
                .getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        transaction.replace(layoutViewID, newFragment);

        if (addToBackstack) transaction.addToBackStack(null);

        transaction.commit();
    }
    public static void to(Fragment newFragment, FragmentActivity activity, boolean addToBackstack, int layoutViewID, String tag)
    {
        System.out.println("pogodjena metoda");
        FragmentManager fm = activity.getSupportFragmentManager();

        Fragment specificFragment = fm.findFragmentByTag("DELETE");
        if (specificFragment != null) {
            fm.beginTransaction()
                    .remove(specificFragment)
                    .commitNow();
        }
        FragmentTransaction transaction = activity
                .getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(layoutViewID, newFragment, tag);

        if (addToBackstack) transaction.addToBackStack(null);

        transaction.commit();
    }

}
