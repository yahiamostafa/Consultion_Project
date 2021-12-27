package com.example.consultion.ui.dashboard;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.consultion.Call;
import com.example.consultion.MainActivity;
import com.example.consultion.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class DashboardFragment extends Fragment {
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    TextView email ,makeCall;
    EditText  name , age , number;
    ImageView image;
    public static boolean isDoctor = false ;
    FloatingActionButton signOutBtn , saveBtn;
    StorageReference storageReference;
    FirebaseStorage storage;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_dashboard,container , false);
    email  = root.findViewById(R.id.userName);
    mAuth = FirebaseAuth.getInstance();
    signOutBtn = root.findViewById(R.id.signBtn);
    name = root.findViewById(R.id.nameTxt);
    makeCall = root.findViewById(R.id.makeVoiceCall);
    makeCall.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(getActivity() , Call.class));
        }
    });
    age  = root.findViewById(R.id.ageTxt);
    number = root.findViewById(R.id.numberTxt);
    Bundle bundle = getArguments();
    if (bundle!=null){
        isDoctor = bundle.getBoolean("isDoctor");
    }
    image = root.findViewById(R.id.patientIMG);
    saveBtn = root.findViewById(R.id.saveBtn);
    storage = FirebaseStorage.getInstance();
    storageReference = storage.getReference();
    downloadImage();
    image.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.CAMERA)) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA}, 1);
                } else {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA}, 1);
                }
            }
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                // display error state to the user
            }
        }
    });
    signOutBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mAuth.signOut();
            startActivity(new Intent(getActivity(), MainActivity.class));
        }
    });
    saveBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String nameStr = name.getText().toString();
            String numberStr = number.getText().toString();
            String ageStr = age.getText().toString();
            if (nameStr.isEmpty() || numberStr.isEmpty() || ageStr.isEmpty())
                Toast.makeText(getActivity(), "Invalid input", Toast.LENGTH_SHORT).show();
            else{
                HashMap<String,Object> map = new HashMap<>();
                map.put("Name",nameStr);
                map.put("Age",ageStr);
                map.put("Number", numberStr);
                map.put("isDoctor",isDoctor);
                DocumentReference documentReference = firestore.collection("Users").document(mAuth.getCurrentUser().getUid());
                documentReference.set(map, SetOptions.merge());
                Toast.makeText(getActivity(), "Saved successfully", Toast.LENGTH_SHORT).show();
            }
        }
    });
    firestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firestore.collection("Users").document(mAuth.getCurrentUser().getUid());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String nameStr = documentSnapshot.getString("Name");
                email.setText(mAuth.getCurrentUser().getEmail().toString());
                String ageStr = documentSnapshot.getString("Age");
                String numberStr = documentSnapshot.getString("Number");
                if (ageStr ==null){
                    age.setHint("Your age");
                }else{
                    age.setText(ageStr);
                }
                if (nameStr ==null){
                    name.setHint("Your name");
                }else{
                    name.setText(nameStr);
                }
                if (numberStr ==null){
                    number.setHint("Your number");
                }else{
                    number.setText(numberStr);
                }
            }
        });
        return root;
    }

    private void downloadImage() {
        StorageReference profilePhoto = storageReference.child("profiles/"+mAuth.getCurrentUser().getUid()+".jpeg");
        Long bytes = 1024*1024l;
        profilePhoto.getBytes(bytes).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                image.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    //    ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.GetContent(),
//            new ActivityResultCallback<Uri>() {
//                @Override
//                public void onActivityResult(Uri result) {
//                    Toast.makeText(getActivity(), result.toString(), Toast.LENGTH_SHORT).show();
//                    if (result != null)
//                        image.setImageURI(result);
//                }
//            });
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            image.setImageBitmap(imageBitmap);
            uploadPhoto(imageBitmap);
        }
    }

    private void uploadPhoto(Bitmap imageBitmap) {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        String upload_name = mAuth.getCurrentUser().getUid();
        StorageReference ref = storageReference.child("profiles");
        ByteArrayOutputStream outputStream =new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG,90,outputStream);
        ref.child(upload_name+".jpeg").putBytes(outputStream.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Added Successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}