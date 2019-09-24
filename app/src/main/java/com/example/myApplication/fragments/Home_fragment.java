



package com.example.myApplication.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.fragment.app.Fragment;

import com.example.myApplication.R;
import com.example.myApplication.classes.Ticket;
import com.example.myApplication.adapters.TicketAdapter;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Home_fragment extends Fragment
        //implements OnMapReadyCallback, LocationListener, GoogleMap.OnPolylineClickListener
        {

    private static final String TAG = "MyActivity";
//    private Location currentLocation;
//    private FusedLocationProviderClient fusedLocationProviderClient;

EditText source, adult, children;
Button btn;
Spinner dest, bus_no;

ArrayList<String> list;
FirebaseUser mUser;
FirebaseFirestore db;
ProgressDialog pd;

    public Home_fragment()
    {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View v=inflater.inflate(R.layout.fragment_home_fragment,container,false);


                source=(EditText)v.findViewById(R.id.source);
                adult=(EditText)v.findViewById(R.id.adults);
                children=(EditText)v.findViewById(R.id.children);
                dest = v.findViewById(R.id.spinner2);
                dest.setEnabled(false);
                bus_no = v.findViewById(R.id.spinner);
                bus_no.setEnabled(false);
                btn = v.findViewById(R.id.button);
                mUser = FirebaseAuth.getInstance().getCurrentUser();
                db = FirebaseFirestore.getInstance();
                pd = new ProgressDialog(getContext());

                //fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
                //setUpMap();

                sourceTextWatcher();
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        //Yes button clicked
                                        pd.show();
                                        addToDB();
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        dialog.dismiss();
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();

                    }
                });



        return v;
    }

    private void sourceTextWatcher() {

        source.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER) && !source.getText().toString().isEmpty()) {
                    bus_no.setEnabled(true);
                    setBusNoSPinner(source.getText().toString());
                    return true;
                }
                return false;
            }
        });

        source.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(bus_no.isEnabled()){
                    bus_no.setAdapter(null);
                    bus_no.setEnabled(false);
                    dest.setAdapter(null);
                    dest.setEnabled(false);
                }
            }


            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setBusNoSPinner(String str) {

        final List<String> list_of_bus_no = new ArrayList<>();
        list_of_bus_no.add("Select bus number");

        db.collection("Route").whereArrayContains("stopsequence", str)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot documentSnapshot = task.getResult();
                if(task.getResult().isEmpty()){
                    Toast.makeText(getActivity(), "Invalid selection",Toast.LENGTH_SHORT).show();
                    return;
                }
                for(DocumentSnapshot doc : documentSnapshot){
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
                Toast.makeText(getActivity(), "exception",Toast.LENGTH_SHORT).show();
            }
        });

        bus_no.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0) {return;}
                setDestSpinnerList(bus_no.getSelectedItem().toString());
                dest.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

    }

    private void setDestSpinnerList(String str) {

        list = new ArrayList<String>();
        list.add("Select destination");
        db.collection("Route").document(str).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot==null){
                    Toast.makeText(getActivity(), "Invalid selection",Toast.LENGTH_SHORT).show();
                    return;
                }
                list = (ArrayList<String>)documentSnapshot.get("stopsequence");

                for(int i=1;;){
                    if(!list.get(i).equals(source.getText().toString()))
                    list.remove(i);
                    else
                        break;
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


    }

    private void addToDB(){

        final int total;
        final Map<String,Object> map = new HashMap<>();
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

        total = Integer.parseInt(adult.getText().toString()) *
                + Integer.parseInt(children.getText().toString()) * 10;
        map.put("total", total);
        map.put("timestamp", FieldValue.serverTimestamp());

        db.collection("User")
                .document(mUser.getPhoneNumber()).collection("Ticket")
                .document().set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showTicket((Timestamp) map.get("timestamp"));
                //Toast.makeText(getActivity(), "Ticket kadhla!!",Toast.LENGTH_SHORT);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Error" + e.getMessage(),Toast.LENGTH_SHORT);
            }
        });

        map.clear();
        //map.put("tokens",);

        db.collection("User").document(mUser.getPhoneNumber()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                DocumentSnapshot document = task.getResult();
                if(document!=null) {
                    long t = (long) document.get("tokens");
                    t = t - total;
                    map.put("tokens", t);
                    db.collection("User").document(mUser.getPhoneNumber()).update(map);
                }
            }
        });



    }

    private void showTicket(Timestamp time) {

        pd.dismiss();
        db.collection("User").document(mUser.getPhoneNumber()).collection("Ticket").whereEqualTo("timestamp",time)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Ticket model;
                for(QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                     model = documentSnapshot.toObject(Ticket.class);
                    final Dialog dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.ticket_template);
                    Window window = dialog.getWindow();
                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    TextView source,destination,busNo,ticketNo,tripNo,people,timestsmp,depot,total,
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
        });

    }

            private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
                if ((lat1 == lat2) && (lon1 == lon2)) {
                    return 0;
                }
                else {
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





//    private void setUpMap() {
//
//        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(getActivity(),new String[]{
//                    Manifest.permission.ACCESS_FINE_LOCATION},101);
//            return;
//        }
//
//        Task<Location> task = fusedLocationProviderClient.getLastLocation();
//        task.addOnSuccessListener(new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                if(location!=null){
//                    currentLocation = location;
//                    Map<String, Object> city = new HashMap<>();
//                    city.put("Lon", currentLocation.getLongitude());
//                    city.put("Lat", currentLocation.getLatitude());
//
//                    db.collection("User").document(mUser.getPhoneNumber())
//                            .set(city, SetOptions.merge())
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    Log.d(TAG, "DocumentSnapshot successfully written!");
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Log.w(TAG, "Error writing document", e);
//                                }
//                            });
//
//                    SupportMapFragment supportMapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map1);
//                    if(supportMapFragment!=null)
//                    supportMapFragment.getMapAsync(Home_fragment.this);
//                }else{
//                    Toast.makeText(getActivity(), "loc null",Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }
//
//
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//
//        LatLng latLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
//        MarkerOptions markerOptions = new MarkerOptions()
//                .position(latLng)
//                .title("You are here.");
//        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
//        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
//        googleMap.addMarker(markerOptions);
//
////        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
////                .clickable(true)
////                .add(
////                        new LatLng(-35.016, 143.321),
////                        new LatLng(-34.747, 145.592),
////                        new LatLng(-34.364, 147.891),
////                        new LatLng(-33.501, 150.217),
////                        new LatLng(-32.306, 149.248),
////                        new LatLng(-32.491, 147.309)));
////
////        // Position the map's camera near Alice Springs in the center of Australia,
////        // and set the zoom factor so most of Australia shows on the screen.
////        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-23.684, 133.903), 4));
////
////        // Set listeners for click events.
////        googleMap.setOnPolylineClickListener(this);
//
//
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//
//        Map<String, Object> city = new HashMap<>();
//        city.put("Lon", currentLocation.getLongitude());
//        city.put("Lat", currentLocation.getLatitude());
//
//        db.collection("User").document(FirebaseAuth.getInstance()
//                .getCurrentUser().getPhoneNumber())
//                .set(city, SetOptions.merge())
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d(TAG, "DocumentSnapshot successfully written!");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error writing document", e);
//                    }
//                });
//    }
//
//
//
//        private static final PatternItem DOT = new Dot();
//        private static final PatternItem GAP = new Gap(25);
////
//// Create a stroke pattern of a gap followed by a dot.
//        private  final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);
//
//        @Override
//        public void onPolylineClick(Polyline polyline) {
//            // Flip from solid stroke to dotted stroke pattern.
////            if ((polyline.getPattern() == null) || (!polyline.getPattern().contains(DOT))) {
////                polyline.setPattern(PATTERN_POLYLINE_DOTTED);
////            } else {
////                // The default pattern is a solid stroke.
////                polyline.setPattern(null);
////                polyline.setColor(0xffF9A825);
////            }
////
////            Toast.makeText(getActivity(), "Route type " ,
////                    Toast.LENGTH_SHORT).show();
//        }
//
//

            
}
