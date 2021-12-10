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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.widget.Toast.LENGTH_SHORT;


public class Login extends AppCompatActivity {
    Button loginButton , regButton;
    ImageView image;
    private FirebaseAuth mAuth;
    EditText user,pass;
    ProgressDialog waiting;
    FirebaseFirestore firestore;
    static boolean isDoctor = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        loginButton = findViewById(R.id.loginBtn);
        regButton = findViewById(R.id.regBtn);
        image = findViewById(R.id.userImg);
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this , Register.class);
                startActivity(intent);
            }
        });
        user = findViewById(R.id.usernameTxt);
        pass = findViewById(R.id.passwordTxt);
        user.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0 || pass.getText().toString().length() == 0){
                    loginButton.setEnabled(false);
                }else{
                    loginButton.setEnabled(true);
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
                    loginButton.setEnabled(false);
                }else{
                    loginButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        Intent intent = getIntent();
        if (intent  != null){
            String type = intent.getStringExtra("user");
            if (type.equalsIgnoreCase("doctor")){
                isDoctor = true;
                image.setImageResource(R.drawable.doctor);
            }else{
                isDoctor = false;
                image.setImageResource(R.drawable.patient);
            }
        }
        if (isDoctor)
            regButton.setVisibility(View.INVISIBLE);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                waiting = new ProgressDialog(Login.this);
                waiting.setMessage("Loading ...");
                waiting.setTitle("Logging");
                waiting.setIndeterminate(false);
                waiting.setCancelable(true);
                waiting.show();
                loginButton.setEnabled(false);
                user.setEnabled(false);
                pass.setEnabled(false);
                mAuth.signInWithEmailAndPassword(user.getText().toString(), pass.getText().toString())
                        .addOnSuccessListener(Login.this, new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                userIsDoctor(authResult.getUser().getUid());
                            }
                        }).addOnFailureListener(Login.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this, e.getLocalizedMessage(), LENGTH_SHORT).show();
                        loginButton.setEnabled(true);
                        waiting.dismiss();
                        user.setEnabled(true);
                        pass.setEnabled(true);
                    }
                });
            }
        });
    }
    public void userIsDoctor(String uid)
    {
        DocumentReference document = firestore.collection("Users").document(uid);
        if (document == null) {
            Toast.makeText(Login.this, "InValid user or password", LENGTH_SHORT).show();
            return;
        }
        document.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (isDoctor == documentSnapshot.getBoolean("isDoctor")){
                    if (isDoctor)
                        startActivity(new Intent(getApplicationContext() , Doctor_Activity.class));
                    else
                        startActivity(new Intent(getApplicationContext(),Requests.class));
                    finish();
                }else{
                    Toast.makeText(Login.this, "Unauthorized", LENGTH_SHORT).show();
                    user.setEnabled(true);
                    pass.setEnabled(true);
                    loginButton.setEnabled(false);
                    mAuth.signOut();
                }
            }
        });
        waiting.dismiss();
    }
}