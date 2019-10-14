package com.example.myApplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class EditProfile extends AppCompatActivity {

    EditText name;
    EditText email;
    EditText password;
    FirebaseFirestore db;
    Button submit_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        name=(EditText)findViewById(R.id.editText_newname);
        email=(EditText)findViewById(R.id.editText_newemail);
        password=(EditText)findViewById(R.id.editText_newpassword);
        submit_btn=(Button)findViewById(R.id.button_submit_edit);
        db=FirebaseFirestore.getInstance();

        db.collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot=task.getResult();
                    name.setText(documentSnapshot.get("name").toString());
                    email.setText(documentSnapshot.get("email").toString());
                    password.setText(documentSnapshot.get("password").toString());

                }

            }
        });

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newname=name.getText().toString();
                String newemail=email.getText().toString();
                String newpassword=password.getText().toString();
                HashMap<String,Object>map=new HashMap<>();

                map.put("name",newname);
                map.put("email",newemail);
                map.put("password",newpassword);
                db.collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString()).update(map);
                name.setText("");
                email.setText("");
                password.setText("");
                Toast.makeText(EditProfile.this,"Credentials Updated",Toast.LENGTH_SHORT).show();



            }
        });



    }
}
