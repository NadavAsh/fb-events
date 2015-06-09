package edu.washington.crew.fbevents;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.lang.reflect.Array;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;


public class EventDetailsFragment extends android.support.v4.app.Fragment {
    public static final String TAG = "EventDetailsFragment";

    public static final String EVENT_ID = "edu.washington.crew.fbevents.EVENT_ID";
    public static final String NAME = "edu.washington.crew.fbevents.NAME";
    public static final String DESCRIPTION = "edu.washington.crew.fbevents.DESCRIPTION";
    public static final String LOCATION = "edu.washington.crew.fbevents.LOCATION";
    public static final String START_TIME = "edu.washington.crew.fbevents.START_TIME";
    public static final String RSVP_STATUS = "edu.washington.crew.fmevents.RSVP_STATUS";

    private CallbackManager callbackManager;

    private String eventId;
    private String name;
    private String description;
    private String location;
    private String start;
    private String rsvpStatus;

    private boolean init;

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

        for (FbEvent event : FbEventRepository.FbEvents) {
            if (event.getId().equals(eventDetails.getId())) {
                args.putString(RSVP_STATUS, event.getRsvpStatus());
                break;
            }
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
        rsvpStatus = args.getString(RSVP_STATUS);
        callbackManager = CallbackManager.Factory.create();
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
        final TextView locationText = (TextView)view.findViewById(R.id.event_location);
        locationText.setText(location);

        RadioGroup radioGroup = (RadioGroup)view.findViewById(R.id.rsvp_radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (!init)
                    return;

                switch (checkedId) {
                    case R.id.attending_button:
                        rsvpStatus = FbEvent.RSVP_ATTENDING;
                        break;
                    case R.id.maybe_button:
                        rsvpStatus = "maybe";
                        break;
                    case R.id.decline_button:
                        rsvpStatus = FbEvent.RSVP_DECLINE;
                        break;
                }

                if (!AccessToken.getCurrentAccessToken().getPermissions().contains("rsvp_event")) {
                    LoginManager loginManager = LoginManager.getInstance();
                    loginManager.logInWithPublishPermissions(getActivity(),
                            Arrays.asList("rsvp_event"));
                    loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            publishRsvp();
                        }
                        @Override
                        public void onCancel() {
                            Log.d(TAG, "Login cancelled.");
                        }
                        @Override
                        public void onError(FacebookException e) {
                            Log.e(TAG, "Login failed.");
                        }
                    });
                } else {
                    publishRsvp();
                }
            }
        });

        switch (rsvpStatus) {
            case FbEvent.RSVP_ATTENDING:
                radioGroup.check(R.id.attending_button);
                break;
            case FbEvent.RSVP_MAYBE:
                radioGroup.check(R.id.maybe_button);
                break;
            case FbEvent.RSVP_DECLINE:
                radioGroup.check(R.id.decline_button);
                break;
        }

        init = true;

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void publishRsvp() {
        GraphRequest request = GraphRequest.newPostRequest(AccessToken.getCurrentAccessToken(),
                eventId + "/" + rsvpStatus, null, new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        Log.d(TAG, graphResponse.toString());
                    }
                });
        request.executeAsync();
    }

}
