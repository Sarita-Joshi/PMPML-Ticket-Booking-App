



package com.example.myApplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;



public class Home_fragment extends Fragment {


    private static final String TAG = "MyActivity";

EditText fname,lname,age;
Button btn,btn2;
TextView t1;

    Home_fragment()
    {}




    public void adddatatodb(Map<String,Object> doc)
    {
        FirebaseFirestore db= FirebaseFirestore.getInstance();
        db.collection("Collection1").add(doc)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        //System.out.println("Success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        // System.out.println("Failure");

                    }
                });



    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        final View v=inflater.inflate(R.layout.fragment_home_fragment,container,false);
        btn=(Button)v.findViewById(R.id.button);
        btn2=(Button)v.findViewById(R.id.button2);
        t1=(TextView)v.findViewById(R.id.textView4);

        btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                fname=(EditText)v.findViewById(R.id.edit_fname);
                lname=(EditText)v.findViewById(R.id.edit_lname);
                age=(EditText)v.findViewById(R.id.edit_age);

                String fname1=fname.getText().toString();
                String lname1=lname.getText().toString();
                int age1= Integer.parseInt(age.getText().toString());

                Map<String, Object> doc = new HashMap<>();
                doc.put("first", fname1);
                doc.put("last",lname1);
                doc.put("born", age1);
                fname.getText().clear();
                lname.getText().clear();
                age.getText().clear();
                adddatatodb(doc);
            }

        });

        btn2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                FirebaseFirestore db= FirebaseFirestore.getInstance();
                DocumentReference docref=db.collection("cities").document("LA");
//                DocumentSnapshot documentSnapshot= docref.get().getResult();
//                t1.setText(documentSnapshot.getString("name"));
                docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful())
                        {
                            DocumentSnapshot documentSnapshot=task.getResult();
                            if(documentSnapshot!=null)
                            {
                                t1.setText(documentSnapshot.getString("name"));  //here documentsnapshot is a map

                            }
                        }
                        else
                        {
                            t1.setText("Error");
                        }

                    }
                });



            }

        });


        return v;
    }

}
