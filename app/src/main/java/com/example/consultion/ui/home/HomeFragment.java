package com.example.consultion.ui.home;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.consultion.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class HomeFragment extends Fragment {

    EditText multi;
    FloatingActionButton btn;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    String [] options;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home,container , false);
        multi = root.findViewById(R.id.editTextTextMultiLine);
        btn = root.findViewById(R.id.Sendbutton);
        mAuth = FirebaseAuth.getInstance();
        options = new String[] {
          "Heart",
          "Orthopedic surgery",
          "Urology",
          "Ophthalmology",
          "Obstetrics and Gynecology",
          "Children",
          "Chest and Respiratory",
          "Ear, Nose and Throat",
          "Neurology",
          "Dermatology"
        };
        firestore = FirebaseFirestore.getInstance();
        multi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().isEmpty())
                    btn.setEnabled(false);
                else
                    btn.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        return root;
    }

    private void showDialog() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getContext());
        dialogBuilder.setTitle("Category");
        dialogBuilder.setSingleChoiceItems(options, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String s = options[i];
                DocumentReference documentReference = firestore.collection(mAuth.getCurrentUser().getUid()).document();
                HashMap<String , Object> map = new HashMap<>();
                map.put("Answer","No Answer yet");
                map.put("Question", multi.getText().toString());
                map.put("isAnswered",false);
                map.put("uid",mAuth.getCurrentUser().getUid());
                String id = documentReference.getId();
                map.put("id",id);
                documentReference.set(map);
                Toast.makeText(getActivity(), "Your Question is submitted successfully", Toast.LENGTH_SHORT).show();
                multi.setText("");
                documentReference = firestore.collection(s).document();
                documentReference.set(map);
                dialogInterface.dismiss();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialogBuilder.setBackground(getResources().getDrawable(R.drawable.rounded_edit_text,null));
        }
        dialogBuilder.show();
//        Dialog dialog = new Dialog(getContext());
//        dialog.setContentView(R.layout.formal_dialog);
//        dialog.findViewById(R.id.secSend).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                RadioGroup radioGroup = dialog.findViewById(R.id.radioGroup2);
//                int selected = radioGroup.getCheckedRadioButtonId();
//                RadioButton radioButton = dialog.findViewById(selected);
//                String s =  radioButton.getText().toString();
//                DocumentReference documentReference = firestore.collection(mAuth.getCurrentUser().getUid()).document();
//                HashMap<String , Object> map = new HashMap<>();
//                map.put("Answer","No Answer yet");
//                map.put("Question", multi.getText().toString());
//                map.put("isAnswered",false);
//                map.put("uid",mAuth.getCurrentUser().getUid());
//                String id = documentReference.getId();
//                map.put("id",id);
//                documentReference.set(map);
//                Toast.makeText(getActivity(), "Your Question is submitted successfully", Toast.LENGTH_SHORT).show();
//                multi.setText("");
//                documentReference = firestore.collection(s).document();
//                documentReference.set(map);
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
    }
}