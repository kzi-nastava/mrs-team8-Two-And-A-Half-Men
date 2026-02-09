package com.project.mobile.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.project.mobile.R;
import com.project.mobile.data.ProfileManager;
import com.project.mobile.models.PendingChange;

import java.util.List;


public class ProfileFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private View pendingReviewLayout;
    private LinearLayout pendingChangesList;
    private MaterialButton btnCancelRequest;
    private ProfileManager profileManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileManager = ProfileManager.getInstance(requireContext());

        initViews(view);
        setupViewPager();
        setupPendingReview();
    }

    private void initViews(View view) {
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        pendingReviewLayout = view.findViewById(R.id.pendingReviewLayout);
        pendingChangesList = pendingReviewLayout.findViewById(R.id.pendingChangesList);
        btnCancelRequest = pendingReviewLayout.findViewById(R.id.btnCancelRequest);
    }

    private void setupViewPager() {
        ProfilePagerAdapter adapter = new ProfilePagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Personal data");
                    tab.setIcon(R.drawable.ic_person);
                    break;
                case 1:
                    tab.setText("Vehicle data");
                    tab.setIcon(R.drawable.ic_car);
                    break;
            }
        }).attach();
    }

    private void setupPendingReview() {
        if (profileManager.hasPendingChanges()) {
            pendingReviewLayout.setVisibility(View.VISIBLE);
            displayPendingChanges();

            btnCancelRequest.setOnClickListener(v -> {
                profileManager.clearPendingChanges();
                pendingReviewLayout.setVisibility(View.GONE);
            });
        } else {
            pendingReviewLayout.setVisibility(View.GONE);
        }
    }

    private void displayPendingChanges() {
        pendingChangesList.removeAllViews();
        List<PendingChange> changes = profileManager.getPendingChanges();

        for (PendingChange change : changes) {
            View changeView = getLayoutInflater().inflate(
                    R.layout.item_pending_change, pendingChangesList, false);

            TextView tvField = changeView.findViewById(R.id.tvChangeField);
            TextView tvValue = changeView.findViewById(R.id.tvChangeValue);

            tvField.setText(change.getField() + ":");
            tvValue.setText(change.getOldValue() + " → " + change.getNewValue());

            pendingChangesList.addView(changeView);
        }
    }

    private static class ProfilePagerAdapter extends FragmentStateAdapter {

        public ProfilePagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new PersonalDataFragment();
                case 1:
                    return new VehicleDataFragment();
                default:
                    return new PersonalDataFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}
