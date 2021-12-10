package com.example.consultion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class Register extends AppCompatActivity {
    Button regButton;
    ImageView image;
    private FirebaseAuth mAuth;
    EditText user,pass , name;
    boolean isDoctor ;
    FirebaseFirestore firestore;
    ProgressDialog waiting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        regButton = findViewById(R.id.loginBtn);
        user = findViewById(R.id.fullNameTxt);
        image = findViewById(R.id.userImg);
        name = findViewById(R.id.usernameTxt);
        pass = findViewById(R.id.passwordTxt);
        user.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0 || pass.getText().toString().length() == 0){
                    regButton.setEnabled(false);
                }else{
                    regButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0 || user.getText().toString().length() == 0){
                    regButton.setEnabled(false);
                }else{
                    regButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regButton.setEnabled(false);
                waiting = new ProgressDialog(Register.this);
                waiting.setMessage("Loading ...");
                waiting.setTitle("Logging");
                waiting.setIndeterminate(false);
                waiting.setCancelable(true);
                waiting.show();
                name.setEnabled(false);
                user.setEnabled(false);
                pass.setEnabled(false);
                mAuth.createUserWithEmailAndPassword(user.getText().toString(),pass.getText().toString())
                        .addOnSuccessListener(Register.this, new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                DocumentReference dr = firestore.collection("Users").document(user.getUid());
                                HashMap<String,Object> info = new HashMap<>();
                                info.put("Name" , name.getText().toString());
                                info.put("isDoctor" , isDoctor);
                                dr.set(info);
                                    startActivity(new Intent(Register.this, Requests.class));
                                finish();
                                waiting.dismiss();
                            }
                        }).addOnFailureListener(Register.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        regButton.setEnabled(true);
                        waiting.dismiss();
                        Toast.makeText(Register.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        name.setEnabled(true);
                        user.setEnabled(true);
                        pass.setEnabled(true);
                    }
                });
            }
        });
    }
}