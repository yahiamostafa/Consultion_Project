package com.example.consultion;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.consultion.ui.dashboard.DashboardFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationBarView;

public class Doctor_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_);
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,new RequestsFragment()).commit();
        BottomNavigationView navigationView = findViewById(R.id.doctor_nav);
        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selected = null;
                switch (item.getItemId()){
                    case R.id.home_frag:
                        selected = new RequestsFragment();
                        break;
                    case R.id.map_frag:
                        Bundle c = new Bundle();
                        c.putBoolean("isDoctor",true);
                        selected = new HospitalFragment();
                        selected.setArguments(c);
                        break;
                    case R.id.profile_frag:
                        Bundle b = new Bundle();
                        b.putBoolean("isDoctor",true);
                        selected = new DashboardFragment();
                        selected.setArguments(b);
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,selected).commit();
                return true;
            }
        });
    }
    @Override
    public void onBackPressed() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(Doctor_Activity.this);
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