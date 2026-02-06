package com.project.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import com.google.android.material.navigation.NavigationView;
import com.project.mobile.activities.LoginActivity;
import com.project.mobile.databinding.ActivityUnregisterBinding;
import com.project.mobile.fragments.HomeUnregistered;

public class UnregisterActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityUnregisterBinding binding;
    private ActionBarDrawerToggle drawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_unregister);
        binding = ActivityUnregisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        setupNavigationDrawer();

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
        FragmentTransition.to(new HomeUnregistered(), this, false, binding.fragmentContainerViewTag.getId());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.home) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            FragmentTransition.to(new HomeUnregistered(), this, false, binding.fragmentContainerViewTag.getId());
            return true;
        } else if (id == R.id.login ) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
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
}