package com.example.myApplication.payment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myApplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {



    EditText custid,amount;
    String orderid;
    ImageView back;
    TextView t;
    int amt=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        getSupportActionBar().hide();

        //get runtime permission to read sms
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Button btn = (Button) findViewById(R.id.start_transaction);
        custid = (EditText) findViewById(R.id.custid);
        amount = (EditText) findViewById(R.id.amount);
        back = findViewById(R.id.back_button);
        Intent intent = getIntent();
        String str = intent.getStringExtra("balance");
        if(str !=null){
            t = findViewById(R.id.token_value);
            t.setText(str);
        }


        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        final CollectionReference mPay = FirebaseFirestore.getInstance()
                .collection("User").document(mUser.getPhoneNumber())
                .collection("Transaction");


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mPay.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot doc = task.getResult();
                        orderid = mUser.getPhoneNumber().substring(3) + doc.size();
                        amt = Integer.parseInt(amount.getText().toString());
                        Intent intent = new Intent(PaymentActivity.this, checksum.class);
                        intent.putExtra("orderid", orderid);
                        intent.putExtra("custid", mUser.getPhoneNumber().substring(3));
                        intent.putExtra("final_amount", amt);
                        Toast.makeText(PaymentActivity.this, orderid , Toast.LENGTH_SHORT ).show();
                        startActivityForResult(intent,1);

                    }
                });

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                // Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                finish();
            }
        });

        if (ContextCompat.checkSelfPermission(PaymentActivity.this, android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PaymentActivity.this, new String[]{android.Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Map<String,Object> map = new HashMap<>();
            map.put("amount", Integer.parseInt(amount.getText().toString()));
            map.put("timestamp", FieldValue.serverTimestamp());

                if(data.getStringExtra("TXN_RESPONSE").equals("TXN_SUCCESS")){
                    map.put("status", "success");
                    t.setText("Balance is Updated");
                    FirebaseFirestore.getInstance().collection("User")
                            .document(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                            .update("tokens", FieldValue.increment(amt));
                }
                else{
                    map.put("status", "failed");
                }

                FirebaseFirestore.getInstance().collection("User")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                        .collection("Transaction").document().set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.e("pmpml", "transaction updated!!!!");
                    }
                });

        }

    }


}
