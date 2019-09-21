



package com.example.myApplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.model.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.grpc.Server;


public class Home_fragment extends Fragment {


    private static final String TAG = "MyActivity";

EditText source,destination,busNo, adult,children;
Button btn;
TextView t1;

FirebaseAuth firebaseAuth;
FirebaseFirestore db;

    Home_fragment()
    {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View v=inflater.inflate(R.layout.fragment_home_fragment,container,false);

                source=(EditText)v.findViewById(R.id.source);
                destination=(EditText)v.findViewById(R.id.destination);
                busNo=(EditText)v.findViewById(R.id.busNo);
                adult=(EditText)v.findViewById(R.id.adults);
                children=(EditText)v.findViewById(R.id.children);
                btn = v.findViewById(R.id.button);
                firebaseAuth = FirebaseAuth.getInstance();
                db = FirebaseFirestore.getInstance();

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        //Yes button clicked
                                        addToDB();
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        dialog.dismiss();
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();




                    }
                });



        return v;
    }

    private void addToDB(){

        final int total;
        final Map<String,Object> map = new HashMap<>();
        map.put("source", source.getText().toString());
        map.put("destination", destination.getText().toString());
        map.put("busNo", busNo.getText().toString());

        ArrayList<Integer> list = new ArrayList<>();
        list.add(Integer.parseInt(adult.getText().toString()));
        list.add(Integer.parseInt(children.getText().toString()));
        map.put("people", list);

        map.put("tripNo", "5");
        map.put("ticketNo", "534");
        map.put("depot", "Manapa");

        total = Integer.parseInt(adult.getText().toString()) * 15 + Integer.parseInt(children.getText().toString()) * 10;
        map.put("total", total);
        map.put("timestamp", FieldValue.serverTimestamp());

        db.collection("User")
                .document(firebaseAuth.getCurrentUser().getPhoneNumber()).collection("Ticket")
                .document().set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "Ticket kadhla!!",Toast.LENGTH_SHORT);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Error" + e.getMessage(),Toast.LENGTH_SHORT);
            }
        });

        map.clear();
        //map.put("tokens",);

        db.collection("User").document(firebaseAuth.getCurrentUser().getPhoneNumber()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                DocumentSnapshot document = task.getResult();
                if(document!=null) {
                    long t = (long) document.get("tokens");
                    t = t - total;

                    map.put("tokens", t);
                    db.collection("User").document(firebaseAuth.getCurrentUser().getPhoneNumber()).update(map);
                }
            }
        });



    }



}
