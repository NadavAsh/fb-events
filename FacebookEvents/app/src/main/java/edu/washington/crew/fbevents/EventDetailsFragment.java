package edu.washington.crew.fbevents;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.*;

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

    private String eventId;
    private String name;
    private String description;
    private String location;
    private String start;

    public static EventDetailsFragment newInstance(FbEvent eventDetails) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putString(EVENT_ID, eventDetails.getId());
        args.putString(NAME, eventDetails.getEventName());
        args.putString(DESCRIPTION, eventDetails.getDescription());
        args.putString(LOCATION, eventDetails.getLocation());

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

        return view;
    }

}
