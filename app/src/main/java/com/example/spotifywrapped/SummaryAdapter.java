package com.example.spotifywrapped;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SummaryAdapter extends RecyclerView.Adapter<SummaryAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Card> SummaryList;
    private final Activity myActivity;

    // Constructor
    public SummaryAdapter(Context context, ArrayList<Card> courseModelArrayList, PastWrappedActivity pastWrappedActivity) {
        this.context = context;
        this.SummaryList = courseModelArrayList;
        this.myActivity = pastWrappedActivity;
    }

    @NonNull
    @Override
    public SummaryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wrapped_cards, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SummaryAdapter.ViewHolder holder, int position) {
        // to set data to textview and imageview of each card layout
        Card model = SummaryList.get(position);
        holder.dateTV.setText(model.getDate());
        holder.timeTV.setText(model.getTime());
        holder.termTV.setText(model.getTerm());

        System.out.println(position);

        System.out.println(holder.getAdapterPosition());
        holder.myCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WrappedActivity.class);
                intent.putExtra("term", SummaryList.get(holder.getAdapterPosition()).getLocation());
                context.startActivity(intent);
                myActivity.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        // this method is used for showing number of card items in recycler view
        return SummaryList.size();
    }

    // View holder class for initializing of your views such as TextView and Imageview
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView dateTV, timeTV, termTV;
        private final LinearLayout myCard;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTV = itemView.findViewById(R.id.TVDate);
            timeTV = itemView.findViewById(R.id.TVTime);
            termTV = itemView.findViewById(R.id.TVTerm);
            myCard = itemView.findViewById(R.id.myCard);
        }
    }
}