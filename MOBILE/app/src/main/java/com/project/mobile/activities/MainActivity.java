package com.project.mobile.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationView;
import com.project.mobile.FragmentTransition;
import com.project.mobile.HomeRegistered;
import com.project.mobile.R;
import com.project.mobile.databinding.ActivityMainBinding;
import com.project.mobile.fragments.HistoryFragment;
import com.project.mobile.fragments.Registered.Rides.BookedRidesFragment;
import com.project.mobile.fragments.chat.ChatFragment;
import com.project.mobile.fragments.profile.ProfilePageFragment;
import com.project.mobile.viewModels.AuthModel;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActivityMainBinding binding;
    private ActionBarDrawerToggle drawerToggle;
    private AuthModel authModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
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
                FragmentTransition.to(new HomeRegistered(), this, false, containerId);
                return true;
            } else if(itemId == R.id.nav_profile)
            {
                FragmentTransition.to(new ProfilePageFragment(), this, false, containerId);
                return true;
            }
            else if(itemId == R.id.nav_active_drive)
            {
                FragmentTransition.to(new BookedRidesFragment(), this, false, containerId);
                return true;
            }
            else if(itemId == R.id.nav_history)
            {
                FragmentTransition.to(new HistoryFragment(), this, false, containerId);
                return true;
            }
            else if(itemId == R.id.nav_live_chat)
            {
                FragmentTransition.to(ChatFragment.newInstanceForUser(), this, false, containerId);
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
        return false;
    }
}