package com.example.consultion;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;

import android.os.Build;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {
    Context context; 
    ArrayList<Request> requests ;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    ArrayList<String> ids;
    boolean isDoctor;
    public RequestAdapter(Context context, ArrayList<Request> requests , ArrayList<String> ids , boolean isDoctor) {
        this.context = context;
        this.requests = requests;
        this.ids = ids;
        this.isDoctor = isDoctor;
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.request,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Request request = requests.get(position);
        holder.question.setText(request.getQuestion());
        holder.answer.setText(request.getAnswer());
        holder.status.setText(request.isAnswered()?"CLOSED":"OPEN");
        holder.status.setTextColor(request.isAnswered()? Color.RED:Color.GREEN);
        if (isDoctor) {
            holder.image.setVisibility(View.GONE);
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog(position);
                }
            });
        }else{
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, MapsActivity.class));
                }
            });
        }
    }
    private void showDialog(int position){
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(context);
        final EditText editText = new EditText(context);
        dialog.setTitle("Reply");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.setBackground(context.getDrawable(R.drawable.rounded_edit_text));
        }
//        Dialog dialog = new Dialog(context);
//        dialog.setContentView(R.layout.dialog);
        String [] options = new String[]{
          "Take an Aspirin",
          "You're good",
          "Visit the nearest Hospital", "Others..."
        };
        dialog.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        final int[] picked = {0};
        dialog.setSingleChoiceItems(options, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i==3)
                            editText.setEnabled(true);
                        else
                            editText.setEnabled(false);
                        picked[0] = i;
                    }
                });
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        dialog.setView(editText);
        editText.setHint("Others...");
        editText.setEnabled(false);
        editText.setGravity(Gravity.CENTER);
        dialog.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String msg = "";
                if (picked[0] ==3){
                    if (editText.getText().toString().isEmpty())
                        Toast.makeText(context, "Empty Field", Toast.LENGTH_SHORT).show();
                    else{
                        msg = editText.getText().toString();
                    }
                }else
                    msg = options[picked[0]];
                    String id = ids.get(position);
                    DocumentReference documentReference = firestore.collection(requests.get(position).getUid())
                            .document(requests.get(position).getId());
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("Answer", msg);
                    map.put("Question", requests.get(position).getQuestion());
                    map.put("isAnswered", true);
                    map.put("uid", requests.get(position).getUid());
                    map.put("id", requests.get(position).getId());
                    documentReference.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            firestore.collection("Requests")
                                    .document(id).delete();
                            requests.remove(position);
                            ids.remove(position);
                            notifyDataSetChanged();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context,"SomeThing went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
                dialogInterface.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public static  class ViewHolder extends  RecyclerView.ViewHolder{
         TextView answer , question ,status;
         CardView cardView;
         ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            status = itemView.findViewById(R.id.status);
            answer = itemView.findViewById(R.id.answerTxt);
            question = itemView.findViewById(R.id.questionTxt);
            cardView = itemView.findViewById(R.id.cardView);
            image = itemView.findViewById(R.id.hospitalImage);
        }
    }
}
