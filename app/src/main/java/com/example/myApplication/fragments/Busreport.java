package com.example.myApplication.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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


public class Busreport extends Fragment {



    Spinner busspinner;
    Button submit_btn;
    EditText editText_busissue;

    FirebaseFirestore db;

    public Busreport() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v=inflater.inflate(R.layout.fragment_busreport, container, false);
        busspinner=v.findViewById(R.id.spinner_qr);
        submit_btn=v.findViewById(R.id.submit_qr);
        editText_busissue=v.findViewById(R.id.edittext_busissue);
        submit_btn.setEnabled(false);

        final List<String>buslist=new ArrayList();


        final ArrayAdapter<String>adapter =new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,buslist);
        busspinner.setAdapter(adapter);

        db=FirebaseFirestore.getInstance();

        db.collection("Route").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful())
                {
                    for(QueryDocumentSnapshot document:task.getResult())
                    {
                        buslist.add(document.getId().toString());

                    }

                    adapter.notifyDataSetChanged();

                }
            }
        });


        editText_busissue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(editText_busissue.getText().toString()!="")
                {
                    submit_btn.setEnabled(true);
                }
                else
                {
                    submit_btn.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HashMap<String,Object> map=new HashMap<>();
                String number= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString();
                map.put("phone",number);
                map.put("tag","bus");
                map.put("issue",editText_busissue.getText().toString());
                map.put("bus_no",busspinner.getSelectedItem().toString());
                db.collection("Report").document().set(map);

                Toast.makeText(getContext(),"Report Submitted",Toast.LENGTH_SHORT).show();

                editText_busissue.setText("");







            }
        });










        return v;


    }


}
