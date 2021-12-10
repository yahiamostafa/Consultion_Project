package com.example.consultion;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class HospitalFragment extends Fragment {
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    HospitalAdapter hospitalAdapter;
    RecyclerView recyclerView;
    ArrayList<Hospital> hospitals ;
    FloatingActionButton addHospital;
    String nameStr , latitudeStr , longitudeStr;
    boolean isDoctor ;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.hospital_layout,container , false);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        addHospital = root.findViewById(R.id.addHospital);
        Bundle bundle = getArguments();
        if (bundle != null){
            isDoctor = bundle.getBoolean("isDoctor");
        }
        if (!isDoctor)
            addHospital.setVisibility(View.GONE);
        if (isDoctor)
            addHospital.setVisibility(View.VISIBLE);
        recyclerView = root.findViewById(R.id.hospitalList);
        hospitals = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext() ));
        hospitalAdapter = new HospitalAdapter(getContext() , hospitals);
        recyclerView.setAdapter(hospitalAdapter);
        addHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addHospital();
            }
        });
        firestore.collection("Hospitals").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()){
                            String  name = document.getString("name");
                            String  longitude = document.getString("long");
                            String  latitude = document.getString("lat");
                            //TODO put the correct parameters
                            hospitals.add(new Hospital(longitude,latitude,name));
                        }
                        hospitalAdapter.notifyDataSetChanged();
                    }
                });
        return root;
    }
    private void addLongitude(){
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getContext());
        EditText name = new EditText(getContext());
        dialogBuilder.setView(name);
        name.setGravity(Gravity.CENTER);
        name.setInputType(InputType.TYPE_CLASS_NUMBER);
        if (longitudeStr ==null ||longitudeStr.isEmpty())
            name.setHint("Longitude");
        else
            name.setText(longitudeStr);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialogBuilder.setBackground(getContext().getDrawable(R.drawable.rounded_edit_text));
        }
        dialogBuilder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                addHospital();
                dialogInterface.dismiss();
            }
        });
        dialogBuilder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                longitudeStr = name.getText().toString();
                if (nameStr.isEmpty())
                    Toast.makeText(getContext(), "Add Longitude", Toast.LENGTH_SHORT).show();
                else{
                    addLatitude();
                    dialogInterface.dismiss();
                }
            }
        });
        dialogBuilder.show();
    }

    private void addLatitude() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getContext());
        EditText name = new EditText(getContext());
        dialogBuilder.setView(name);
        name.setGravity(Gravity.CENTER);
        name.setInputType(InputType.TYPE_CLASS_NUMBER);
        if (latitudeStr ==null ||latitudeStr.isEmpty())
            name.setHint("Latitude");
        else
            name.setText(latitudeStr);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialogBuilder.setBackground(getContext().getDrawable(R.drawable.rounded_edit_text));
        }
        dialogBuilder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                addLongitude();
                dialogInterface.dismiss();
            }
        });
        dialogBuilder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                latitudeStr = name.getText().toString();
                if (nameStr.isEmpty())
                    Toast.makeText(getContext(), "Add Latitude", Toast.LENGTH_SHORT).show();
                else{
                    HashMap<String,Object> hm = new HashMap<>();
                    hm.put("name",nameStr);
                    hm.put("long",longitudeStr);
                    hm.put("lat",latitudeStr);
                    firestore.collection("Hospitals").document().set(hm);
                    hospitals.add(new Hospital(longitudeStr,latitudeStr,nameStr));
                    hospitalAdapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Added Successfully", Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            }
        });
        dialogBuilder.show();
    }

    private void addHospital() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getContext());
        EditText name = new EditText(getContext());
        dialogBuilder.setView(name);
        name.setGravity(Gravity.CENTER);
        if (nameStr ==null ||nameStr.isEmpty())
            name.setHint("Hospital Name");
        else
            name.setText(nameStr);
        name.setInputType(InputType.TYPE_CLASS_TEXT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialogBuilder.setBackground(getContext().getDrawable(R.drawable.rounded_edit_text));
        }
        dialogBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialogBuilder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                    nameStr = name.getText().toString();
                    if (nameStr.isEmpty())
                        Toast.makeText(getContext(), "Add Hospital Name", Toast.LENGTH_SHORT).show();
                    else{
                        addLongitude();
                        dialogInterface.dismiss();
                    }
            }
        });
        dialogBuilder.show();
    }
}
