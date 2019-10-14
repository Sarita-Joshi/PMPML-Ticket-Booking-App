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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Password extends AppCompatActivity {


    EditText editText_password;
    Button b1;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

//        getActivity().finish();
//        Intent intent = new Intent(getActivity(), Password.class);
//        startActivity(intent);
//        Log.d(TAG, "signInWithCredential:success");
        editText_password=findViewById(R.id.editText_pass);
        b1=findViewById(R.id.password_next);
        db=FirebaseFirestore.getInstance();


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful())
                        {
                            DocumentSnapshot documentSnapshot=task.getResult();
                            String pass=documentSnapshot.get("password").toString();
                            if(pass.equals(editText_password.getText().toString()))
                            {
                                finish();
                                Intent intent = new Intent(Password.this,MainActivity.class);
                                startActivity(intent);
//                                Log.d(TAG, "signInWithCredential:success");
                            }
                            else
                            {
                                Toast.makeText(Password.this,"Password not correct",Toast.LENGTH_SHORT).show();
                                editText_password.setText("");

                            }
                        }


                    }
                });




            }
        });


    }
}
