package com.example.myApplication.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.myApplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Qrreport extends Fragment {


    public Qrreport() {
        // Required empty public constructor
    }

    Spinner stopspinner;
    Button submit_btn;

    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v =inflater.inflate(R.layout.fragment_qrreport, container, false);
        stopspinner=v.findViewById(R.id.spinner_qr);
        submit_btn=v.findViewById(R.id.submit_qr);

        final List<String>stoplist=new ArrayList<>();
        final ArrayAdapter<String>adapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,stoplist);

        stopspinner.setAdapter(adapter);

        db=FirebaseFirestore.getInstance();
        db.collection("Stop").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for(QueryDocumentSnapshot document:task.getResult())
                    {
                        stoplist.add(document.getId().toString());
                    }

                    adapter.notifyDataSetChanged();
                }
            }
        });




        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HashMap<String,Object>map=new HashMap<>();
                map.put("tag","qr");
                map.put("phone", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString());
                map.put("stop",stopspinner.getSelectedItem().toString());

                db.collection("Report").document().set(map);



                Toast.makeText(getContext(),"Report added",Toast.LENGTH_SHORT).show();





            }
        });






        return v;
    }


}
