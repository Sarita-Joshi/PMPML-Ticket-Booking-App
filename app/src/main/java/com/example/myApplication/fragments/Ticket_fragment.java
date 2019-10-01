package com.example.myApplication.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myApplication.LoginActivity;
import com.example.myApplication.R;
import com.example.myApplication.adapters.RecyclerItemTouchHelper;
import com.example.myApplication.classes.Ticket;
import com.example.myApplication.adapters.TicketAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class Ticket_fragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener{


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
        setHasOptionsMenu(true);
        db = FirebaseFirestore.getInstance();
        ticketRef = db.collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                .collection("Ticket");
        recyclerView = view.findViewById(R.id.rv);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);


        setUpRecyclerView();
        return view;
    }

    private void setUpRecyclerView() {
        Log.e("Ticket", "setting up recycler view");
        Query query =
                db.collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                .collection("Ticket").orderBy("timestamp", Query.Direction.DESCENDING);

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

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
            adapter.deleteItem(viewHolder.getAdapterPosition());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.tickets, menu);
    }

    //handle option clicks

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.action_delete_all:
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                adapter.deleteAll();
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
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
