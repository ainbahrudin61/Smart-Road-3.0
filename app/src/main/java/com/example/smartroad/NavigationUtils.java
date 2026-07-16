package com.example.smartroad;

import android.app.Activity;
import android.content.Intent;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationUtils {

    public static void setupBottomNavigation(Activity activity, BottomNavigationView bottomNavigationView, int selectedItemId) {
        bottomNavigationView.setSelectedItemId(selectedItemId);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            
            if (id == selectedItemId) {
                return true;
            }

            Intent intent = null;
            if (id == R.id.nav_home) {
                intent = new Intent(activity, HomeActivity.class);
            } else if (id == R.id.nav_report) {
                intent = new Intent(activity, ReportActivity.class);
            } else if (id == R.id.nav_map) {
                intent = new Intent(activity, MapActivity.class);
            } else if (id == R.id.nav_profile) {
                intent = new Intent(activity, ProfileActivity.class);
            } else if (id == R.id.nav_about) {
                intent = new Intent(activity, AboutActivity.class);
            }

            if (intent != null) {
                activity.startActivity(intent);
                // activity.finish(); // Optional: depend on navigation behavior preference
                return true;
            }
            return false;
        });
    }
}