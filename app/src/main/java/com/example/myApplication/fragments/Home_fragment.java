

package com.example.myApplication.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;


import com.example.myApplication.R;
import com.example.myApplication.classes.Ticket;
import com.example.myApplication.adapters.TicketAdapter;
import com.example.myApplication.payment.PaymentActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Home_fragment extends Fragment
        implements OnMapReadyCallback {

    private static final String TAG = "MyActivity";
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;

    EditText adult, children;
    Button btn, source_btn;
    Spinner dest, bus_no;
    TextView source;

    ArrayList<String> list;
    FirebaseUser mUser;
    FirebaseFirestore db;
    ProgressDialog pd;
    long adult_price = 5;
    long child_price = 5;
    long usertokens = 0;
    int total = 0;
    double source_lat = 0, source_lon = 0, dest_lat = 0, dest_lon = 0;
    int direction = 0;


    public Home_fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home_fragment, container, false);


        source = v.findViewById(R.id.source);
        adult = (EditText) v.findViewById(R.id.adults);
        children = (EditText) v.findViewById(R.id.children);
        dest = v.findViewById(R.id.spinner2);
        dest.setEnabled(false);
        bus_no = v.findViewById(R.id.spinner);
        bus_no.setEnabled(false);
        btn = v.findViewById(R.id.button);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        pd = new ProgressDialog(getContext());
        source_btn = v.findViewById(R.id.button_source);
        v.findViewById(R.id.map1).setVisibility(View.GONE);

        setUpMap();

        db.collection("User").document(mUser.getPhoneNumber()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                DocumentSnapshot document = task.getResult();
                if (document != null) {
                    usertokens = (long) document.get("tokens");
                }
            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                calculateTotal();
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                // pd.show();

                                if (total > usertokens)    //usertokens are always 0 here and total is correct
                                {
                                    DialogInterface.OnClickListener diListener = new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            switch (i) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    Intent intent = new Intent(getActivity(), PaymentActivity.class);
                                                    intent.putExtra("balance", Long.toString(usertokens));
                                                    startActivity(intent);
                                                    break;


                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    dialogInterface.dismiss();

                                                    break;
                                            }
                                        }
                                    };

                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                                    builder1.setMessage("Not Enough tokens ").setPositiveButton("Recharge", diListener).setNegativeButton("Cancel", diListener).show();
                                } else {
                                    addToDB();
                                }
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                dialog.dismiss();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Total no of tokens:" + Long.toString(total)).setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }
        });

        source_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(getActivity());
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(true);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });


        return v;
    }

    private void calculateTotal() {

        double double_distance = distance(source_lat, source_lon, dest_lat, dest_lon, "K");

        int int_distance = (int) double_distance;

        if (int_distance % 2 != 0) {
            int q = (int) int_distance / 2;
            int_distance = 2 * (q + 1);

        }


        db.collection("Price").whereEqualTo("KM", int_distance).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        //  Log.e(TAG,"zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz"+ doc.get("full").toString());

                        if (doc != null) {

                            adult_price = Long.parseLong(doc.get("full").toString());
                            child_price = Long.parseLong(doc.get("half").toString());

                            Log.e(TAG, "zzzzzzzzzzzzzzzzzzzzzzzzz" + adult_price + child_price);

                            total = (int) (Integer.parseInt(adult.getText().toString()) * adult_price + Integer.parseInt(children.getText().toString()) * child_price);

                            Log.e(TAG, "dfsdsdfsdsdgsdgsdgdgsdgsgdsdgs" + total);

                        }
                    }
                }
            }
        });

    }


    private void setBusNoSPinner(String str) {

        int t = 0;
        final List<String> list_of_bus_no = new ArrayList<>();
        list_of_bus_no.add("Select bus number");

        db.collection("Route").whereArrayContains("stopsequence", str)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot documentSnapshot = task.getResult();
                if (task.getResult().isEmpty()) {
                    Toast.makeText(getActivity(), "Invalid selection", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (DocumentSnapshot doc : documentSnapshot) {

                    list_of_bus_no.add(doc.getId());
                }


                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_list_item_1,
                        list_of_bus_no);
                bus_no.setAdapter(arrayAdapter);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "exception", Toast.LENGTH_SHORT).show();
            }
        });

        bus_no.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    return;
                }
                setDestSpinnerList(bus_no.getSelectedItem().toString());
                dest.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });


    }

    private int setDestSpinnerList(String str) {

        int t = 0;
        list = new ArrayList<String>();
        list.add("Select destination");
        db.collection("Route").document(str).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot == null) {
                    Toast.makeText(getActivity(), "Invalid selection", Toast.LENGTH_SHORT).show();
                    return;
                }
                list = (ArrayList<String>) documentSnapshot.get("stopsequence");


                if (direction == 0) {
                    int index = list.indexOf(source.getText().toString());

                    for (int i = index; i < list.size(); ) {
                        list.remove(i);
                    }
                } else if (direction == 1) {

                    for (int i = 0; ; ) {

                        if (list.get(i).equals(source.getText().toString())) {
                            list.remove(i);
                            break;
                        } else {

                            list.remove(i);
                        }
                    }

                }


                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_spinner_item,
                        list);
                dest.setAdapter(arrayAdapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Invalid selection", Toast.LENGTH_SHORT).show();
            }
        });

        dest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                db.collection("Stop").document(dest.getSelectedItem().toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            dest_lat = Double.parseDouble(documentSnapshot.get("Latitude").toString());
                            dest_lon = Double.parseDouble(documentSnapshot.get("Longitude").toString());
                            Log.e(TAG, "ppppppppppppppppppppppppppppppppppppppppp" + dest_lat + dest_lat);


                        }
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return t;

    }

    private void addToDB() {

        final Map<String, Object> map = new HashMap<>();
        map.put("source", source.getText().toString());
        map.put("destination", dest.getSelectedItem().toString());
        map.put("busNo", bus_no.getSelectedItem().toString());
        ArrayList<Integer> list = new ArrayList<>();
        list.add(Integer.parseInt(adult.getText().toString()));
        list.add(Integer.parseInt(children.getText().toString()));
        map.put("people", list);
        map.put("tripNo", "5");
        map.put("ticketNo", "534");
        map.put("depot", "Manapa");
        map.put("total", total);
        map.put("timestamp", FieldValue.serverTimestamp());

        db.collection("User").document(mUser.getPhoneNumber()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                DocumentSnapshot document = task.getResult();
                if (document != null) {
                    usertokens = (long) document.get("tokens");

                    Toast.makeText(getActivity(), Long.toString(usertokens), Toast.LENGTH_SHORT);

                    if (usertokens > total)   //User has enough tokens
                    {
                        db.collection("User")
                                .document(mUser.getPhoneNumber()).collection("Ticket")
                                .document().set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //showTicket((Timestamp) map.get("timestamp"));
                                //Toast.makeText(getActivity(), "Ticket kadhla!!",Toast.LENGTH_SHORT);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Error" + e.getMessage(), Toast.LENGTH_SHORT);
                            }
                        });

                        db.collection("User").document(mUser.getPhoneNumber())
                                .update("tokens", FieldValue.increment(-1 * total));
                    } else {
                        Toast.makeText(getActivity(), "Not enough tokens", Toast.LENGTH_SHORT);

                    }
                }
            }
        });
    }

    private void showTicket(Timestamp time) {

        // pd.dismiss();
        db.collection("User").document(mUser.getPhoneNumber()).collection("Ticket").whereEqualTo("timestamp", time)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Ticket model;
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        if (!documentSnapshot.exists()) return;
                        model = documentSnapshot.toObject(Ticket.class);
                        final Dialog dialog = new Dialog(getContext());
                        dialog.setContentView(R.layout.ticket_template);
                        Window window = dialog.getWindow();
                        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                        TextView source, destination, busNo, ticketNo, tripNo, people, timestsmp, depot, total,
                                tv;
                        source = dialog.findViewById(R.id.textView_source);
                        destination = dialog.findViewById(R.id.textView_destination);
                        busNo = dialog.findViewById(R.id.textView_ticket_bus_no);
                        ticketNo = dialog.findViewById(R.id.textView_ticket_no);
                        tripNo = dialog.findViewById(R.id.textView_trip_no);
                        people = dialog.findViewById(R.id.textView_number_of_people);
                        timestsmp = dialog.findViewById(R.id.textView_timestamp);
                        depot = dialog.findViewById(R.id.textView_depot_name);
                        total = dialog.findViewById(R.id.textView_total_amount);
                        tv = dialog.findViewById(R.id.title);

                        source.setText(model.getSource());
                        depot.setText(model.getDepot());
                        destination.setText(model.getDestination());
                        busNo.setText(model.getBusNo());
                        tripNo.setText(model.getTripNo());
                        timestsmp.setText(model.getTimestamp().toDate().toString());
                        ticketNo.setText(model.getTicketNo());
                        people.setText(model.getPeople());
                        total.setText(String.valueOf(model.getTotal()));
                        TicketAdapter.fadeInAnimation(tv);

                        dialog.show();

                        break;

                    }
                }
            }
        });

    }

    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        } else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            if (unit == "K") {
                dist = dist * 1.609344;
            } else if (unit == "N") {
                dist = dist * 0.8684;
            }
            return (dist);
        }
    }

    private void setUpMap() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            return;
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;

                    SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map1);
                    supportMapFragment.getMapAsync(Home_fragment.this);


                   // Toast.makeText(getActivity(), "" + currentLocation, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Turn on GPS", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title("You are here.");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
        googleMap.addMarker(markerOptions);

    }


    public void setTextViewText(String x) {
        source.setText(x);
        bus_no.setEnabled(true);

        db.collection("Stop").document(x).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    source_lat = Double.parseDouble(documentSnapshot.get("Latitude").toString());
                    source_lon = Double.parseDouble(documentSnapshot.get("Longitude").toString());
                    Log.e(TAG, "lllllllllllllllllllllllllllllllllllllllll" + source_lat + source_lon);


                }
            }
        });
        setBusNoSPinner(source.getText().toString());

    }

    public void setdirection(int d) {
        direction = d;

    }

}