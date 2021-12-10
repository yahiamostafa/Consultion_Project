package com.example.consultion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_main);
    }
    public void changeActivity(View view){
        int id = view.getId();
        Intent intent = new Intent(this , Login.class);
        if (id == R.id.doctorIMG){
            intent.putExtra("user","doctor");
        }else{
            intent.putExtra("user","patient");
        }
        startActivity(intent);
    }
}