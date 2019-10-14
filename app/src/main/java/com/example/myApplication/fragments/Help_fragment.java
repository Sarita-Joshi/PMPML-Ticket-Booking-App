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
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Help_fragment extends Fragment {

    FirebaseFirestore db;

    public Help_fragment()
    {}

    private ListView buslist;
    private Button find_buses_button;
    private Spinner s,d;
    String source_text="",dest_text="";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         final View v=inflater.inflate(R.layout.fragment_help_fragment,container,false);
         find_buses_button=v.findViewById(R.id.findbus_button);
         find_buses_button.setEnabled(false);


        s=v.findViewById(R.id.souce_spinner);
        d=v.findViewById(R.id.dest_spinner);
        buslist=v.findViewById(R.id.bus_list);
         final List<String> sources=new ArrayList<>();
       final ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),R.layout.spinner_item,sources);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        d.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();


       final List<String>l1=new ArrayList<>();
       final ArrayAdapter<String> busadapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,l1);
        buslist.setAdapter(busadapter);

        busadapter.notifyDataSetChanged();

        db.collection("Stop").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
             @Override
             public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for(QueryDocumentSnapshot document:task.getResult())
                    {

                        sources.add(document.getId().toString());

                    }
                    adapter.notifyDataSetChanged();
                }


             }
         });

List<String> example=new ArrayList<>();
    example.add("A");
    example.add("B");
    example.add("C");
    example.add("D");
    example.add("E");
        ArrayAdapter<String> adapter1=new ArrayAdapter<String>(getActivity(),R.layout.spinner_item,example);



//        s.setAdapter(adapter1);





        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                source_text=s.getSelectedItem().toString();
               // Toast.makeText(getActivity(),source_text,Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Toast.makeText(getActivity(),"Fail",Toast.LENGTH_SHORT).show();

                find_buses_button.setEnabled(false);

                return;

            }
        });

        d.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                dest_text=d.getSelectedItem().toString();
                if(source_text!=""&&dest_text!="")
                {
                    find_buses_button.setEnabled(true);

                }

                //Toast.makeText(getActivity(),dest_text,Toast.LENGTH_SHORT).show();

            }



            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
              //  Toast.makeText(getActivity(),"Fail",Toast.LENGTH_SHORT).show();
                find_buses_button.setEnabled(false);

                return;

            }
        });

find_buses_button.setOnClickListener(new View.OnClickListener() {
    Set<String> h1=new HashSet<>();
    Set<String>h2=new HashSet<>();

    @Override
    public void onClick(View view) {

        //Toast.makeText(getActivity(),dest_text,Toast.LENGTH_SHORT).show();



        db.collection("Route").whereArrayContains("stopsequence",source_text).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {



                if(task.isSuccessful())
                {
                    for(QueryDocumentSnapshot document:task.getResult())
                    {
                        String var=document.getId().toString();

                        h1.add(var);
                    }
                    db.collection("Route").whereArrayContains("stopsequence",dest_text).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {



                            if(task.isSuccessful())
                            {
                                for(QueryDocumentSnapshot document:task.getResult())
                                {
                                    String var=document.getId().toString();

                                    h2.add(var);

                                }
                                //Toast.makeText(getActivity(),Integer.toString(i),Toast.LENGTH_SHORT).show();
                                HashSet<String>h3=new HashSet<String>(h1);
                                h3.retainAll(h2);

                                if(h3.isEmpty())
                                {

                                    Toast.makeText(getActivity(),"No Buses available",Toast.LENGTH_SHORT).show();

                                }
                                else
                                {
                                    l1.clear();


                                    for (String bus:h3) {

                                        l1.add(bus);
                                        busadapter.notifyDataSetChanged();

                                    }


                                }

                            }
                            else
                            {
                                Toast.makeText(getActivity(),"Fetch failed",Toast.LENGTH_SHORT).show();

                            }

                        }
                    });



                }
                else
                {
                    Toast.makeText(getActivity(),"Fetch failed",Toast.LENGTH_SHORT).show();

                }

            }
        });




    }
});




        return v;

    }






}
