package com.project.mobile;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationView;
import com.project.mobile.databinding.ActivityDriverBinding;
import com.project.mobile.fragments.HistoryFragment;
import com.project.mobile.fragments.profile.ProfileFragment;

public class DriverActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityDriverBinding binding;
    private ActionBarDrawerToggle drawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityDriverBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        setupBottomNavBarRegistered();


    }
    private void setupBottomNavBarRegistered(){
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int containerId = binding.fragmentContainerViewTag.getId();
            int itemId = item.getItemId();
            if(itemId == R.id.nav_home)
            {
                FragmentTransition.to(new LiveChat(), this, false, containerId);
                return true;
            } else if(itemId == R.id.nav_profile)
            {
                FragmentTransition.to(new ProfileFragment(), this, false, containerId);

                return true;
            }
            else if(itemId == R.id.nav_active_drive)
            {
                FragmentTransition.to(new LiveChat(), this, false, containerId);

                return true;
            }
            else if(itemId == R.id.nav_history)
            {
                FragmentTransition.to(new HistoryFragment(), this, false, containerId);

                return true;
            }
            else if(itemId == R.id.nav_live_chat)
            {
                FragmentTransition.to(new LiveChat(), this, false, containerId);
                return true;
            }
            return false;
        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }
}