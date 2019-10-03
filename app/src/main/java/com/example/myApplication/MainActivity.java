package com.example.myApplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myApplication.fragments.Help_fragment;
import com.example.myApplication.fragments.Home_fragment;
import com.example.myApplication.fragments.Profile_fragment;
import com.example.myApplication.fragments.Ticket_fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView botnavview;
    Home_fragment home_fragment;
    Ticket_fragment ticket_fragment;
    Help_fragment help_fragment;
    Profile_fragment profile_fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("fragments", "oncreate");
       // getSupportActionBar().setTitle(Html.fromHtml("<font color=\"white\">" + getString(R.string.app_name) + "</font>"));
        botnavview=findViewById(R.id.bottom_nav);
        home_fragment=new Home_fragment();
        ticket_fragment=new Ticket_fragment();
        help_fragment=new Help_fragment();
        profile_fragment=new Profile_fragment();
        setFragment(home_fragment);
        Log.e("fragments", "oncreate");
        botnavview.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId())
                {
                    case R.id.home_nav:
                        Log.e("Fragment", "home");
                        setFragment(home_fragment);
                        return true;
                    case R.id.help_nav:
                        setFragment(help_fragment);
                        return true;

                    case R.id.ticket_nav:
                        setFragment(ticket_fragment);
                        return true;

                    case R.id.profile_nav:
                        setFragment(profile_fragment);
                        return true;

                    default:
                        Log.e("Fragment", "home");
                        setFragment(home_fragment);
                        return true;
                }

            }
        });

    }

    //@Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 0) {
//
//            if (resultCode == RESULT_OK) {
//                String contents = data.getStringExtra("SCAN_RESULT");
//                Toast.makeText(this, "Qr success",Toast.LENGTH_SHORT).show();
//                home_fragment.setTextViewText(contents);
//                Log.d("Fragment", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"+contents);
//
//
//
//
//            }
//            if(resultCode == RESULT_CANCELED){
//
//
//
//                Log.d("Fragment", "111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
//                home_fragment.setTextViewText("Noooo");
//                Toast.makeText(this, "Qr fail",Toast.LENGTH_SHORT).show();
//
//                //handle cancel
//            }
//        }
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.e("Scan*******", "Cancelled scan");

            } else {
                Log.e("Scan", "Scanned");


                String qrstring=result.getContents().toString();

                String[] values=qrstring.split(",");

                home_fragment.setTextViewText(values[0]);
                home_fragment.setdirection(Integer.parseInt(values[1]));
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setFragment(Fragment f)
    {
        Log.e("Fragment", "home");
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.main_frame, f, "");
        ft1.commit();
    }


}
