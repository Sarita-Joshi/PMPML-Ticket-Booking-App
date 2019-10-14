package com.example.myApplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class UserDetails extends AppCompatActivity {

    EditText email;
    EditText name;
    EditText password;
    private String TAG="abcd";

    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        btn=(Button)findViewById(R.id.next_btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email=(EditText)findViewById(R.id.editText_email);
                name=(EditText)findViewById(R.id.editText_name);
                password=(EditText)findViewById(R.id.editText_password);
                FirebaseFirestore db=FirebaseFirestore.getInstance();
                Map<String, Object> user1 = new HashMap<>();
                user1.put("name",name.getText().toString());
                user1.put("email",email.getText().toString());
                user1.put("password",password.getText().toString());
                user1.put("tokens",50);


                Toast.makeText(UserDetails.this,FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(), Toast.LENGTH_SHORT).show();


                db.collection("User").document(FirebaseAuth.getInstance()
                        .getCurrentUser().getPhoneNumber())
                        .set(user1)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });
                Intent i1=new Intent(UserDetails.this,MainActivity.class);
                startActivity(i1);

            }
        });

    }



    public void onNext()
    {
        email=(EditText)findViewById(R.id.editText_email);
        name=(EditText)findViewById(R.id.editText_name);

        FirebaseFirestore db=FirebaseFirestore.getInstance();
        Map<String, Object> user1 = new HashMap<>();
        user1.put("name",name.getText().toString());
        user1.put("email",email.getText().toString());
        user1.put("tokens",50);



        Toast.makeText(UserDetails.this,FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(), Toast.LENGTH_SHORT).show();

        db.collection("User").document(FirebaseAuth.getInstance()
                .getCurrentUser().getPhoneNumber())
                .set(user1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
        Intent i1=new Intent(UserDetails.this,MainActivity.class);
        startActivity(i1);



    }
}
