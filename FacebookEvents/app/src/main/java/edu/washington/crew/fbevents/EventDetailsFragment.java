package edu.washington.crew.fbevents;

import android.app.Activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Intent;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.*;
import org.json.JSONException;

import java.io.InputStream;
import java.net.URL;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.facebook.*;
import com.facebook.login.*;

import java.lang.reflect.Array;
import android.widget.*;

import com.facebook.*;

import java.text.*;
import java.text.ParseException;
import java.util.*;

import edu.washington.crew.fbevents.api.FbEvent;
import edu.washington.crew.fbevents.api.FbEventRepository;


public class EventDetailsFragment extends android.support.v4.app.Fragment {
    public static final String TAG = "EventDetailsFragment";

    public static final String EVENT_MODEL = "edu.washington.crew.fbevents.EVENT_MODEL";
    public static final String EVENT_ID = "edu.washington.crew.fbevents.EVENT_ID";
    public static final String NAME = "edu.washington.crew.fbevents.NAME";
    public static final String DESCRIPTION = "edu.washington.crew.fbevents.DESCRIPTION";
    public static final String LOCATION = "edu.washington.crew.fbevents.LOCATION";
    public static final String START_TIME = "edu.washington.crew.fbevents.START_TIME";
    public static final String COVER_PHOTO = "edu.washington.crew.fbevents.COVER_PHOTO";
    public static final String RSVP_STATUS = "edu.washington.crew.fbevents.RSVP_STATUS";

    private CallbackManager callbackManager;
    private LoginManager loginManager;

    private FbEvent eventModel;

    private boolean init;

    public static EventDetailsFragment newInstance(FbEvent eventDetails) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putString(EVENT_ID, eventDetails.getId());
        args.putString(NAME, eventDetails.getEventName());
        args.putString(DESCRIPTION, eventDetails.getDescription());
        args.putString(LOCATION, eventDetails.getLocation());
        args.putString(COVER_PHOTO, eventDetails.getCoverPhotoUrl());
        args.putString(START_TIME, eventDetails.getStartTime());

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

        if (savedInstanceState != null) {
            eventModel = savedInstanceState.getParcelable(EVENT_MODEL);
        } else {
            eventModel = new FbEvent();

            Bundle args = getArguments();
            eventModel.setId(args.getString(EVENT_ID));
            eventModel.setEventName(args.getString(NAME));
            eventModel.setDescription(args.getString(DESCRIPTION));
            eventModel.setLocation(args.getString(LOCATION));
            eventModel.setCoverPhotoUrl(args.getString(COVER_PHOTO));
            eventModel.setStartTime(args.getString(START_TIME));
            eventModel.setRsvpStatus(args.getString(RSVP_STATUS));
        }

        callbackManager = CallbackManager.Factory.create();

        loginManager = LoginManager.getInstance();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);
        TextView nameText = (TextView)view.findViewById(R.id.event_name);
        nameText.setText(eventModel.getEventName());
        TextView startTimeText = (TextView)view.findViewById(R.id.event_time);
        startTimeText.setText(eventModel.getStartTime());
        TextView descriptionText = (TextView)view.findViewById(R.id.description);
        descriptionText.setText(eventModel.getDescription());
        final TextView locationText = (TextView)view.findViewById(R.id.event_location);
        locationText.setText(eventModel.getLocation());

        final ImageView cover = (ImageView)view.findViewById(R.id.ivUserIcon);
        if (eventModel.getCoverPhotoUrl() == null) {
            cover.setVisibility(View.GONE);
        } else {
            new Thread(new Runnable() {
                private Bitmap loadImageFromNetwork(String url) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
                        return bitmap;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                public void run() {
                    try {
                        final Bitmap bitmap = loadImageFromNetwork(eventModel.getCoverPhotoUrl());
                        cover.post(new Runnable() {
                            public void run() {
                                cover.setImageBitmap(bitmap);
                            }
                        });

                    } catch (Exception e) {
                        Log.d("Image", "There is no photo for this event");
                        e.printStackTrace();
                    }
                }
            }).start();
        }


        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.rsvp_radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (!init)
                    return;

                switch (checkedId) {
                    case R.id.attending_button:
                        eventModel.setRsvpStatus(FbEvent.RSVP_ATTENDING);
                        break;
                    case R.id.maybe_button:
                        eventModel.setRsvpStatus("maybe");
                        break;
                    case R.id.decline_button:
                        eventModel.setRsvpStatus(FbEvent.RSVP_DECLINE);
                        break;
                }

                if (!AccessToken.getCurrentAccessToken().getPermissions().contains("rsvp_event")) {
                    loginManager.logInWithPublishPermissions(getActivity(),
                            Arrays.asList("rsvp_event"));

                } else {
                    publishRsvp();
                }
            }
        });

        switch (eventModel.getRsvpStatus()) {
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(EVENT_MODEL, eventModel);
    }

    private void publishRsvp() {
        GraphRequest request = GraphRequest.newPostRequest(AccessToken.getCurrentAccessToken(),
                eventModel.getId() + "/" + eventModel.getRsvpStatus(), null, new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        Log.d(TAG, graphResponse.toString());
                    }
                });
        request.executeAsync();
    }

}
