package edu.washington.crew.fbevents;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class EventDetailsFragment extends Fragment {
    public static final String TAG = "EventDetailsFragment";

    public static final String EVENT_ID = "edu.washington.crew.fbevents.EVENT_ID";
    public static final String NAME = "edu.washington.crew.fbevents.NAME";
    public static final String DESCRIPTION = "edu.washington.crew.fbevents.DESCRIPTION";
    public static final String LOCATION = "edu.washington.crew.fbevents.LOCATION";
    public static final String START_TIME = "edu.washington.crew.fbevents.START_TIME";
    public static final String COVER_PHOTO = "edu.washington.crew.fbevents.COVER_PHOTO";

    private String eventId;
    private String name;
    private String description;
    private String location;
    private String start;
    private String coverPhoto;

    public static EventDetailsFragment newInstance(FbEvent eventDetails) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putString(EVENT_ID, eventDetails.getId());
        args.putString(NAME, eventDetails.getEventName());
        args.putString(DESCRIPTION, eventDetails.getDescription());
        args.putString(LOCATION, eventDetails.getLocation());
        Log.d("cover_photo", eventDetails.getCoverPhotoUrl());
        args.putString(COVER_PHOTO, eventDetails.getCoverPhotoUrl());

        try {
            SimpleDateFormat incomingFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            Date inDate = incomingFormat.parse(eventDetails.getStartTime());

            SimpleDateFormat newFormat = new SimpleDateFormat("MMM dd, yyy hh:mm a");
            args.putString(START_TIME, newFormat.format(inDate));
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
        }
        fragment.setArguments(args);
        return fragment;
    }

    public EventDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        eventId = args.getString(EVENT_ID);
        name = args.getString(NAME);
        description = args.getString(DESCRIPTION);
        location = args.getString(LOCATION);
        start = args.getString(START_TIME);
        coverPhoto = args.getString(COVER_PHOTO);
        Log.d("cover_photo", coverPhoto);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);
        TextView nameText = (TextView)view.findViewById(R.id.event_name);
        nameText.setText(name);
        TextView startTimeText = (TextView)view.findViewById(R.id.event_time);
        startTimeText.setText(start);
        TextView descriptionText = (TextView)view.findViewById(R.id.description);
        descriptionText.setText(description);
        TextView locationText = (TextView)view.findViewById(R.id.event_location);
        locationText.setText(location);

        final ImageView cover = (ImageView)view.findViewById(R.id.ivUserIcon);
        new Thread(new Runnable() {
            private Bitmap loadImageFromNetwork(String url){
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(url).getContent());
                    return bitmap;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            public void run(){
                final Bitmap bitmap = loadImageFromNetwork(coverPhoto);
                cover.post(new Runnable(){
                    public void run(){
                        cover.setImageBitmap(bitmap);
                    }
                });
            }
        }).start();

        return view;
    }

}
