package com.example.myApplication.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.PatternMatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.myApplication.LoginActivity;
import com.example.myApplication.MainActivity;
import com.example.myApplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;


public class EnterNumberFragment extends Fragment {

    Button sendOTP;
    EditText phonenNmber;
    String TAG = "mapsapp";
    String mVerificationId;

    PhoneAuthProvider.ForceResendingToken mResendToken;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    FirebaseAuth firebaseAuth;


    public EnterNumberFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_enter_number, container, false);

        sendOTP = v.findViewById(R.id.buttonContinue);
        phonenNmber = v.findViewById(R.id.userPhone);
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            //start prof
            getActivity().finish();
            startActivity(new Intent(getActivity(), MainActivity.class));
        }



        Bundle bundle = getArguments();
        if(bundle!=null) {
            String str = bundle.getString("NumBack");
            if (str != null) {
                phonenNmber.setText(str);
            }
        }

        onSendOTPClicked();
        return v;
    }

    private void onSendOTPClicked() {
        Log.d(TAG, "on opt clicked:");
        sendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(phonenNmber.getText().toString().isEmpty())
                    return;
                Bundle bundle = new Bundle();
                bundle.putString("Number", "+91" + phonenNmber.getText().toString());


                VerifyOPTFragment v = new VerifyOPTFragment();
                v.setArguments(bundle);
                FragmentTransaction ft1 = getActivity().getSupportFragmentManager().beginTransaction();
                ft1.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
                ft1.addToBackStack(null);
                ft1.replace(R.id.main_frame_1, v, "");
                ft1.commit();


            }
        });


    }

}

