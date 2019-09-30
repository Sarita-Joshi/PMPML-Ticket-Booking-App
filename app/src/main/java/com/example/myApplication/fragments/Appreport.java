package com.example.myApplication.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myApplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;


public class Appreport extends Fragment {

    EditText editText_appissue;
    Button sub_btn;
    FirebaseFirestore db;

    public Appreport() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_appreport, container, false);

        sub_btn=v.findViewById(R.id.submit_app);
        sub_btn.setEnabled(false);

        db=FirebaseFirestore.getInstance();

        editText_appissue=v.findViewById(R.id.editText_appissue);

        editText_appissue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(editText_appissue.getText().toString()!="")
                {
                    sub_btn.setEnabled(true);
                }
                else
                {
                    sub_btn.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {



            }
        });

        sub_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HashMap<String,Object> map=new HashMap<>();

                map.put("tag","app");
                map.put("phone", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString());
                map.put("issue",editText_appissue.getText().toString());

                db.collection("Report").document().set(map);
                editText_appissue.setText("");
                Toast.makeText(getContext(),"Report Added",Toast.LENGTH_SHORT).show();

            }
        });



        return v;






    }}

