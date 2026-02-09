package com.project.mobile;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import com.google.android.material.navigation.NavigationView;
import com.project.mobile.databinding.ActivityAdminBinding;
import com.project.mobile.fragments.DriverHistoryFragment;
import com.project.mobile.fragments.ProfileFragment;

public class AdminActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActivityAdminBinding binding;
    private ActionBarDrawerToggle drawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
            binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        setupNavigationDrawer();
        setupBottomNavBarRegistered();
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

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
            else if(itemId == R.id.nav_panic_button)
            {
                FragmentTransition.to(new LiveChat(), this, false, containerId);
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
    private void setupNavigationDrawer(){
        drawerToggle = new ActionBarDrawerToggle(
                this,
                binding.drawerLayout,
                binding.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        binding.drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        binding.navView.setNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int containerId = binding.fragmentContainerViewTag.getId();
        if(menuItem.getItemId() == R.id.registe_driver)
        {
            FragmentTransition.to(new LiveChat(), this, false, containerId);
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;

        } else if (menuItem.getItemId() == R.id.driver_profile_change_approval) {
            FragmentTransition.to(new LiveChat(), this, false, containerId);
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else if (menuItem.getItemId() == R.id.blocking) {
            FragmentTransition.to(new LiveChat(), this, false, containerId);
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else if (menuItem.getItemId() == R.id.nav_history) {
            FragmentTransition.to(new DriverHistoryFragment(), this, false, containerId);
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else if (menuItem.getItemId() == R.id.price_management) {
            FragmentTransition.to(new LiveChat(), this, false, binding.fragmentContainerViewTag.getId());
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }

}