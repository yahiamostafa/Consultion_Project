    package com.example.consultion;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.consultion.ui.dashboard.DashboardFragment;
import com.example.consultion.ui.home.HomeFragment;
import com.example.consultion.ui.notifications.NotificationsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationBarView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class Requests extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,new HomeFragment()).commit();
        BottomNavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selected = null;
                switch (item.getItemId()){
                    case R.id.navigation_home:
                        selected = new HomeFragment();
                        break;
                    case R.id.navigation_notifications:
                        selected = new NotificationsFragment();
                        break;
                    case R.id.hospitals_fragment:
                        Bundle b = new Bundle();
                        b.putBoolean("isDoctor",false);
                        selected = new HospitalFragment();
                        selected.setArguments(b);
                        break;
                    case R.id.navigation_dashboard:
                        Bundle c = new Bundle();
                        c.putBoolean("isDoctor",false);
                        selected = new DashboardFragment();
                        selected.setArguments(c);
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,selected).commit();
                return true;
            }
        });

    }

    @Override
    public void onBackPressed() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(Requests.this);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.drawable.exit);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setBackground(getResources().getDrawable(R.drawable.rounded_edit_text,null));
        }
        builder.setMessage("Do you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}