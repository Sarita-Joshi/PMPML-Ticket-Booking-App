package com.example.myApplication;

import android.app.Dialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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

import java.lang.ref.WeakReference;

public class TicketAdapter extends FirestoreRecyclerAdapter<Ticket, TicketAdapter.TicketHolder> {


    private ClickListener listener;


    public TicketAdapter(@NonNull FirestoreRecyclerOptions<Ticket> options, ClickListener listener) {
        super(options);
        this.listener = listener;
        Log.e("Ticket", "adapter constr");

    }

    @Override
    public void onError(FirebaseFirestoreException e) {
        Log.e("Adapter error", e.getMessage());
    }
    @Override
    protected void onBindViewHolder(@NonNull TicketHolder holder, final int position, @NonNull final Ticket model) {


        holder.source.setText(model.getSource() + " to");
        //holder.depot.setText(model.getDepot());
        holder.destination.setText(model.getDestination());
        holder.busNo.setText(model.getBusNo());
        //holder.tripNo.setText(model.getTripNo());
//        holder.timestsmp.setText(model.getTimestamp().toDate().toString());
//        holder.ticketNo.setText(model.getTicketNo());
        //holder.people.setText(model.getPeople());
        holder.total.setText(String.valueOf(model.getTotal()));

        Log.e("Ticket", "bindviewholder");


    }

    @NonNull
    @Override
    public TicketHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ticket_row,parent,false);
        Log.e("Ticket", "createviewholder");
        return new TicketHolder(v,listener);
    }

    class TicketHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView source,destination,busNo,ticketNo,tripNo,people,timestsmp,depot,total,
                tv;
        private WeakReference<ClickListener> listenerRef;

        public TicketHolder(View v, ClickListener listener){
            super(v);
            listenerRef = new WeakReference<>(listener);
            v.setOnClickListener(this);


            source = v.findViewById(R.id.textView_source);
            destination = v.findViewById(R.id.textView_destination);
            busNo = v.findViewById(R.id.textView_ticket_bus_no);
            ticketNo = v.findViewById(R.id.textView_ticket_no);
//            tripNo = v.findViewById(R.id.textView_trip_no);
//            people = v.findViewById(R.id.textView_number_of_people);
            timestsmp = v.findViewById(R.id.textView_timestamp);
//            depot = v.findViewById(R.id.textView_depot_name);
            total = v.findViewById(R.id.textView_total_amount);
//            tv = v.findViewById(R.id.title);

            Log.e("Ticket", "holder constr");


        }

        @Override
        public void onClick(View view) {

            Ticket model = getItem(getAdapterPosition());

            Log.e("ON CLICK",   "item clicked" + TicketAdapter.this.getItem(getAdapterPosition()));

            final Dialog dialog = new Dialog(view.getContext());
            dialog.setContentView(R.layout.ticket_template);
            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

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
            fadeInAnimation(tv);

            dialog.show();

        }
    }


    public static void fadeInAnimation(final View view) {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(300);
        fadeIn.setRepeatCount(Animation.INFINITE);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(600);
        fadeOut.setRepeatCount(Animation.INFINITE);
        fadeOut.setStartOffset(300 + fadeIn.getStartOffset());
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
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
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        view.startAnimation(fadeIn);
        view.startAnimation(fadeOut);

    }


    public interface ClickListener {

        void onPositionClicked(int position);

    }



}
