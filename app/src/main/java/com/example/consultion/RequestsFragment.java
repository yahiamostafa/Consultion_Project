
package com.example.consultion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class RequestsFragment extends Fragment {
    RecyclerView recyclerView;
    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    RequestAdapter adapter;
    ArrayList<Request> requests;
    ArrayList<String> documents;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_doctor,container , false);
        recyclerView = root.findViewById(R.id.requestList);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext() ));
        requests = new ArrayList<Request>();
        documents = new ArrayList<>();
        adapter = new RequestAdapter(getContext() , requests,documents,true);
        recyclerView.setAdapter(adapter);
        DocumentReference documentReference = firestore.collection("Users").document(mAuth.getCurrentUser().getUid());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                getList(documentSnapshot.getString("spec"));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                getList("Requests");
            }
        });
        return  root;
    }
    public void getList(String x){
        firestore.collection(x).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()){
                            documents.add(document.getId());
                            String  question = document.getString("Question");
                            String  answer = document.getString("Answer");
                            boolean  isAnswered = document.getBoolean("isAnswered");
                            String uid = document.getString("uid");
                            String id = document.getString("id");
                            //TODO put the correct parameteres
                            requests.add(new Request(question,answer,uid,id , isAnswered));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });

    }
}
