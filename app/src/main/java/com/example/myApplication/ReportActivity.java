package com.example.myApplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.myApplication.fragments.Appreport;
import com.example.myApplication.fragments.Busreport;
import com.example.myApplication.fragments.Qrreport;

import java.util.ArrayList;
import java.util.List;

public class ReportActivity extends AppCompatActivity {


    Button app_button,bus_button,qr_button;

    Fragment appfragment,qrfragment,busfragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        app_button=findViewById(R.id.app_issue);
        bus_button=findViewById(R.id.busissue);
        qr_button=findViewById(R.id.qr_button);

        appfragment=new Appreport();
        busfragment=new Busreport();
        qrfragment=new Qrreport();







        app_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setFragment(appfragment);

            }
        });

        bus_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setFragment(busfragment);


            }
        });


        qr_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragment(qrfragment);

            }
        });





    }

    private void setFragment(Fragment f)
    {
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.report_frame, f, "");
        ft1.commit();
    }
}
