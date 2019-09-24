package com.example.myApplication.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myApplication.LoginActivity;
import com.example.myApplication.R;
import com.example.myApplication.adapters.MyListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;


public class Profile_fragment extends Fragment {

    ListView listView;
    String[] listItem;

    public Profile_fragment()
    {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v=inflater.inflate(R.layout.fragment_profile_fragment,container,false);


        listView=(ListView)v.findViewById(R.id.options);

        FirebaseFirestore db=FirebaseFirestore.getInstance();
        DocumentReference docref=db.collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        Source source=Source.CACHE;

        docref.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful())
                {

                    DocumentSnapshot document=task.getResult();
                    if(document.exists())
                    {
                        TextView name=(TextView)v.findViewById(R.id.name_value);
                        TextView email=(TextView)v.findViewById(R.id.email_value);
                        TextView tokens=(TextView)v.findViewById(R.id.token_value);
                        TextView phone=(TextView)v.findViewById(R.id.phone_value);
                        name.setText(document.getString("name"));
                        email.setText(document.getString("email"));

                        int tokens1=document.getLong("tokens").intValue();

                        tokens.setText("Balance : " + Integer.toString(tokens1));
                        phone.setText(document.getId());
                    }
                }
                else
                {
                    TextView name=(TextView)v.findViewById(R.id.name_value);
                    TextView email=(TextView)v.findViewById(R.id.email_value);
                    TextView tokens=(TextView)v.findViewById(R.id.token_value);
                    TextView phone=(TextView)v.findViewById(R.id.phone_value);
                    name.setText("Fetching from db");
                    email.setText("Fetching from db");

//                    int tokens1=document.getLong("tokens").intValue();

                    tokens.setText("Fetching from db");

                    phone.setText("Fetching from db");
                }
            }
        });
        setUpOptions();

        //return inflater.inflate(R.layout.fragment_profile_fragment, container, false);
        return v;
    }

    private void setUpOptions() {


        listItem = getResources().getStringArray(R.array.array_options);
        final MyListAdapter adapter=new MyListAdapter(getActivity(), listItem);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // TODO Auto-generated method stub
                String value=adapter.getItem(position);
                Toast.makeText(getContext(),value,Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void logout()
    {
        FirebaseAuth firebaseAuth;
        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null){
                    //Do anything here which needs to be done after signout is complete
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                else {
                }
            }
        };
//Init and attach
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(authStateListener);

//Call signOut()
        firebaseAuth.signOut();
    }

}
