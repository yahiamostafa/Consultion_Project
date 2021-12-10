package com.example.consultion.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.consultion.R;
import com.example.consultion.Request;
import com.example.consultion.RequestAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

    RecyclerView recyclerView;
    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    RequestAdapter adapter;
    ArrayList<Request> requests;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications,container , false);
        recyclerView = root.findViewById(R.id.requestList);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext() ));
        requests = new ArrayList<>();
        adapter = new RequestAdapter(getContext() , requests , null , false);
        recyclerView.setAdapter(adapter);
        firestore.collection(mAuth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()){
                            String  question = document.getString("Question");
                            String  answer = document.getString("Answer");
                            boolean  isAnswered = document.getBoolean("isAnswered");
                            //TODO put the correct parameteres
                            requests.add(new Request(question,answer,mAuth.getCurrentUser().getUid().toString(),"", isAnswered));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
        return root;
    }
}