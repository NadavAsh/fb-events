package edu.washington.crew.fbevents;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import edu.washington.crew.fbevents.api.*;

public class EventAdapter extends ArrayAdapter<FbEvent> {
    public EventAdapter(Context context, ArrayList<FbEvent> events) {
        super(context, 0, events);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final FbEvent event = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_item, parent, false);
        }
        // Lookup view for data population
        TextView eventName = (TextView) convertView.findViewById(R.id.event_name);
        TextView startTime = (TextView) convertView.findViewById(R.id.event_time);
        TextView location = (TextView) convertView.findViewById(R.id.event_location);
        TextView rsvp = (TextView) convertView.findViewById(R.id.event_rsvp);
        // Populate the data into the template view using the data object
        eventName.setText(event.eventName);
        startTime.setText(event.startTime);
        rsvp.setText(event.rsvpStatus);
        // location.setText(event.location[0]);
        // Return the completed view to render on screen

        ImageView cover = (ImageView)convertView.findViewById(R.id.ivUserIcon);
        Picasso.with(getContext()).load(event.getCoverPhotoUrl()).into(cover);

        return convertView;
    }
}