package com.project.mobile.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.project.mobile.R;
import com.project.mobile.data.ProfileManager;
import com.project.mobile.fragments.PersonalDataFragment;
import com.project.mobile.fragments.VehicleDataFragment;
import com.project.mobile.models.PendingChange;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private View pendingReviewLayout;
    private LinearLayout pendingChangesList;
    private MaterialButton btnCancelRequest;
    private ProfileManager profileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileManager = ProfileManager.getInstance(this);

        initViews();
        setupToolbar();
        setupViewPager();
        setupPendingReview();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        pendingReviewLayout = findViewById(R.id.pendingReviewLayout);
        pendingChangesList = pendingReviewLayout.findViewById(R.id.pendingChangesList);
        btnCancelRequest = pendingReviewLayout.findViewById(R.id.btnCancelRequest);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("TAXI, TAXI");
        }
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

    private class ProfilePagerAdapter extends FragmentStateAdapter {

        public ProfilePagerAdapter(@NonNull AppCompatActivity activity) {
            super(activity);
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
