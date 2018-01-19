package com.hcodestudio.movieapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hcodestudio.movieapp.R;
import com.hcodestudio.movieapp.model.Trailer;

import java.util.List;

/**
 * Created by hassan on 11/25/2017.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.MyViewHolder> {

    private Context context;
    private List<Trailer> trailerList;

    public TrailerAdapter(Context context, List<Trailer> trailerList) {
        this.context = context;
        this.trailerList = trailerList;
    }

    @Override
    public TrailerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailer_card, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapter.MyViewHolder holder, int position) {
        holder.trailer_title.setText(trailerList.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return trailerList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView trailer_title;
        ImageView thumbnail;

        public MyViewHolder(View itemView) {
            super(itemView);

            trailer_title = (TextView) itemView.findViewById(R.id.trailer_title);
            thumbnail = (ImageView) itemView.findViewById(R.id.imageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();

                    if(pos != RecyclerView.NO_POSITION){
                        Trailer trailerClicked = trailerList.get(pos);
                        String videoId = trailerList.get(pos).getKey();

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
                        intent.putExtra(" VIDEO_ID", videoId);
                        context.startActivity(intent);

                        Toast.makeText(view.getContext(), "You clicked " + trailerClicked.getName(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
