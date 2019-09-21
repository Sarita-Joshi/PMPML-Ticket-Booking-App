package com.example.myApplication;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class TicketAdapter extends FirestoreRecyclerAdapter<Ticket, TicketAdapter.TicketHolder> {



    public TicketAdapter(@NonNull FirestoreRecyclerOptions<Ticket> options) {
        super(options);
        Log.e("Ticket", "adapter constr");
    }

    @Override
    public void onError(FirebaseFirestoreException e) {
        Log.e("Adapter error", e.getMessage());
    }
    @Override
    protected void onBindViewHolder(@NonNull TicketHolder holder, int position, @NonNull Ticket model) {
        holder.source.setText(model.getSource());
        holder.depot.setText(model.getDepot());
        holder.destination.setText(model.getDestination());
        holder.busNo.setText(model.getBusNo());
        holder.tripNo.setText(model.getTripNo());
        holder.timestsmp.setText(model.getTimestamp().toDate().toString());
        holder.ticketNo.setText(model.getTicketNo());
        holder.people.setText(model.getPeople());
        holder.total.setText(String.valueOf(model.getTotal()));

        Log.e("Ticket", "bindviewholder");

    }

    @NonNull
    @Override
    public TicketHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ticket_template,parent,false);
        Log.e("Ticket", "createviewholder");
        return new TicketHolder(v);
    }

    class TicketHolder extends RecyclerView.ViewHolder{

        TextView source,destination,busNo,ticketNo,tripNo,people,timestsmp,depot,total,
                tv;

        public TicketHolder(View v){
            super(v);

            source = v.findViewById(R.id.textView_source);
            destination = v.findViewById(R.id.textView_destination);
            busNo = v.findViewById(R.id.textView_ticket_bus_no);
            ticketNo = v.findViewById(R.id.textView_ticket_no);
            tripNo = v.findViewById(R.id.textView_trip_no);
            people = v.findViewById(R.id.textView_number_of_people);
            timestsmp = v.findViewById(R.id.textView_timestamp);
            depot = v.findViewById(R.id.textView_depot_name);
            total = v.findViewById(R.id.textView_total_amount);
            tv = v.findViewById(R.id.title);

            fadeInAnimation(tv);
            Log.e("Ticket", "holder constr");


        }



        public void fadeInAnimation(final View view) {
            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator());
            fadeIn.setDuration(500);
            fadeIn.setRepeatCount(Animation.INFINITE);

            Animation fadeOut = new AlphaAnimation(1, 0);
            fadeOut.setInterpolator(new AccelerateInterpolator());
            fadeOut.setDuration(1000);
            fadeOut.setRepeatCount(Animation.INFINITE);
            fadeOut.setStartOffset(500 + fadeIn.getStartOffset());
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    // view.setVisibility(View.INVISIBLE);
                }
                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            fadeIn.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                   // view.setVisibility(View.VISIBLE);
                }
                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            view.startAnimation(fadeIn);
            view.startAnimation(fadeOut);

        }



    }





}
