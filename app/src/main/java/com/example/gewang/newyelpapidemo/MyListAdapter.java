package com.example.gewang.newyelpapidemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Ge Wang on 3/17/2016.
 */
public class MyListAdapter extends ArrayAdapter<Restaurant>{

    public MyListAdapter(Context context, List<Restaurant> restaurants) {
        super(context, R.layout.list_view_row, restaurants);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View myView = layoutInflater.inflate(R.layout.list_view_row, parent, false);

        Restaurant restaurant = getItem(position);
        TextView resName = (TextView) myView.findViewById(R.id.list_view_text);
        RatingBar ratingBar = (RatingBar) myView.findViewById(R.id.list_view_rating_bar);

        resName.setText(restaurant.getName());
        ratingBar.setRating((float)restaurant.getRating());

        return myView;
    }
}
