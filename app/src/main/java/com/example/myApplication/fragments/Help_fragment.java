package com.example.myApplication.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.myApplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class Help_fragment extends Fragment {

    FirebaseFirestore db;

    public Help_fragment()
    {}

    private ListView buses;
    private Button find_buses_button;
    private Spinner s,d;
    String source_text,dest_text;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         final View v=inflater.inflate(R.layout.fragment_help_fragment,container,false);
         buses=v.findViewById(R.id.bus_listview);
         find_buses_button=v.findViewById(R.id.findbus_button);
         find_buses_button.setEnabled(false);


         final List<String> sources=new ArrayList<>();
         db = FirebaseFirestore.getInstance();

         db.collection("Stop").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
             @Override
             public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for(QueryDocumentSnapshot document:task.getResult())
                    {
                        sources.add(document.getId());

                    }
                }
                else
                {

                }

             }
         });



        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),R.layout.spinner_item,sources);
        adapter.notifyDataSetChanged();

        s=v.findViewById(R.id.souce_spinner);
        d=v.findViewById(R.id.dest_spinner);


        s.setAdapter(adapter);
        d.setAdapter(adapter);

        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0)
                {
                    Toast.makeText(getActivity(),"Fail",Toast.LENGTH_SHORT).show();

                    return;
                }
                source_text=s.getSelectedItem().toString();
               TextView t1= v.findViewById(R.id.textView5);
               t1.setText(source_text);
                Toast.makeText(getActivity(),source_text,Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(getActivity(),"Fail",Toast.LENGTH_SHORT).show();

                return;

            }
        });

        d.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0)
                {
                    Toast.makeText(getActivity(),"Fail",Toast.LENGTH_SHORT).show();

                    return;
                }
                dest_text=s.getSelectedItem().toString();
                Toast.makeText(getActivity(),dest_text,Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(getActivity(),"Fail",Toast.LENGTH_SHORT).show();

                return;

            }
        });

        return v;

    }






}
