package com.example.myApplication.fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myApplication.R;
import com.example.myApplication.classes.Ticket;
import com.example.myApplication.adapters.TicketAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


/**
 * A simple {@link Fragment} subclass.
 */
public class Ticket_fragment extends Fragment {


    TextView tv;
    FirebaseFirestore db;
    CollectionReference ticketRef;
    TicketAdapter adapter;
    RecyclerView recyclerView;

    public Ticket_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ticket_fragment, container, false);

        db = FirebaseFirestore.getInstance();
        ticketRef = db.collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                .collection("Ticket");
        recyclerView = view.findViewById(R.id.rv);

        setUpRecyclerView();
        return view;
    }

    private void setUpRecyclerView() {
        Log.e("Ticket", "setting up recycler view");
        Query query =
                db.collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                .collection("Ticket").orderBy("timestamp");

        FirestoreRecyclerOptions<Ticket> options = new FirestoreRecyclerOptions.Builder<Ticket>()
                .setQuery(query, Ticket.class)
               // .setLifecycleOwner(this)
                .build();

        adapter = new TicketAdapter(options,new TicketAdapter.ClickListener() {
            @Override public void onPositionClicked(int position) {
                // callback performed on click
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onStart() {
        Log.e("Ticket", "on start");
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        Log.e("Ticket", "on stop");
        super.onStop();
        adapter.stopListening();
    }


}
