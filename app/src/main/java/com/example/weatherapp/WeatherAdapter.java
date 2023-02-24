package com.example.weatherapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.viewHolder> {
    private Context context;
    private ArrayList<weatherRvModel> weatherRvModelArrayList;

    public WeatherAdapter(Context context, ArrayList<weatherRvModel> weatherRvModelArrayList) {
        this.context = context;
        this.weatherRvModelArrayList = weatherRvModelArrayList;
    }

    @NonNull
    @Override
    public WeatherAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_item,parent,false);
return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherAdapter.viewHolder holder, int position) {
       weatherRvModel  model = weatherRvModelArrayList.get(position);
       holder.tempTv.setText(model.getTemperature()+"°c");
        Picasso.get().load("http:".concat(model.getIcon())).into(holder.condIv);
        holder.windTv.setText(model.getWindSpeed()+"km/h");
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");
        try {
           Date t = input.parse(model.getTime());
           holder.timeTv.setText(output.format(t));
        }catch (ParseException e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return weatherRvModelArrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView timeTv,tempTv, windTv;
        ImageView condIv;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            timeTv = itemView.findViewById(R.id.tvTime);
            tempTv = itemView.findViewById(R.id.tvTemp);
            windTv = itemView.findViewById(R.id.tvWindSpeed);
            condIv = itemView.findViewById(R.id.imgCond);

        }
    }
}
