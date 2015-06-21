package edu.washington.crew.fbevents;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Message;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import edu.washington.crew.fbevents.R;
import edu.washington.crew.fbevents.api.FbEvent;

public class EventDetailsActivity extends ActionBarActivity {
    LoginManager loginManager;

    public static final String TAG = "EventDetailsActivity";

    private String eventId;
    private FbEvent eventModel;
    private String postContent;
    private List<String> attending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        attending = new ArrayList<String>();

        Intent intent = getIntent();
        eventId = intent.getStringExtra(EventDetailsFragment.EVENT_ID);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.post_container, PostListFragment.newInstance(eventId))
                    .commit();

            updateEventDetails();
            getAttending();
        }

        Button submitButton = (Button) findViewById(R.id.posts_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText postText = (EditText) findViewById(R.id.posts_content);
                postContent = postText.getText().toString();

                if (postContent != null && !postContent.isEmpty()) {
                    Log.i("PERMISSIONS", AccessToken.getCurrentAccessToken().getPermissions().toString());
                    if (!AccessToken.getCurrentAccessToken().getPermissions().contains("publish_actions")) {
                        loginManager = LoginManager.getInstance();
                        Collection<String> permissions = Arrays.asList("publish_actions");
                        loginManager.logInWithPublishPermissions(EventDetailsActivity.this, permissions);
                    }

                    Bundle parameters = new Bundle();
                    parameters.putString("message", postContent);
                    GraphRequest request = new GraphRequest(
                            AccessToken.getCurrentAccessToken(),
                            eventId + "/feed",
                            parameters,
                            HttpMethod.POST);
                    request.executeAsync();

                    postText.setText("");
                    Toast.makeText(EventDetailsActivity.this, "Event message posted!", Toast.LENGTH_SHORT).show();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms
                            for (Fragment f : getSupportFragmentManager().getFragments()) {
                                if (f instanceof PostListFragment) {
                                    ((PostListFragment) f).updatePosts();
                                    break;
                                }
                            }
                        }
                    }, 1000);

                } else {
                    Toast.makeText(EventDetailsActivity.this, "Message can't be blank.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        final ActionBar actionBar = getSupportActionBar();
        final ScrollView scroll = (ScrollView)findViewById(R.id.scrollView);
        scroll.getViewTreeObserver().addOnScrollChangedListener(
                new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                ColorDrawable alpha = new ColorDrawable(getResources().getColor(R.color.com_facebook_blue));
                float photoHeight = getResources().getDimension(R.dimen.event_details_photo_height)
                        - getResources().getDimension(R.dimen.abc_action_bar_default_height_material);
                int scrollY = scroll.getScrollY();
                float ratio = (float)scrollY / photoHeight;
                ratio = Math.min(ratio, 1);
                actionBar.setElevation(ratio * 16);
                alpha.setAlpha((int) (ratio * 255));
                actionBar.setBackgroundDrawable(alpha);

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    private void updateEventDetails() {
        GraphRequest request = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(),
                eventId, new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        if (graphResponse.getError() != null) {
                            Log.e(TAG, graphResponse.getError().getErrorMessage());
                            return;
                        }
                        try {
                            eventModel = FbEvent.fromJson(graphResponse.getJSONObject());

                            EventDetailsFragment eventDetails =
                                    EventDetailsFragment.newInstance(eventModel);
                            getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                    .replace(R.id.container, eventDetails)
                                    .commit();
                        } catch (JSONException e) {
                            Log.e(TAG, "Failed to parse event JSON: " + e.getMessage());
                        }
                    }
                });
        Bundle bundle = new Bundle();
        bundle.putString("fields", "id,name,description,start_time,end_time,place,cover");
        request.setParameters(bundle);
        request.executeAsync();
    }

    public void getAttending() {
        GraphRequest request = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(),
                eventId + "/attending", new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        if (graphResponse.getError() != null) {
                            Log.e(TAG, graphResponse.getError().getErrorMessage());
                            return;
                        }
                        try {
                            JSONObject object = graphResponse.getJSONObject();
                            JSONArray attendees = object.getJSONArray("data");
                            final Button attendingB = (Button) findViewById(R.id.attending);
                            attendingB.setText(attendingB.getText().toString() + "" + attendees.length());
                            /*
                            for (int i = 0; i < attendees.length(); i++) {
                                try {
                                    JSONObject attendee = attendees.getJSONObject(i);
                                    attending.add(attendee.getString("name"));
                                } catch (JSONException e){
                                    Log.d(TAG, e.getMessage());
                                }
                            }
                            attendingB.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final AlertDialog show = new AlertDialog.Builder(EventDetailsActivity.this).create();
                                    show.setTitle("Attending");
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String message = "";
                                            for (int i = 0; i < attending.size(); i++) {
                                                message += attending.get(i) + "\n";
                                            }
                                            final String messageF = message;
                                            attendingB.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Log.d(TAG, messageF);
                                                    show.setMessage(messageF);
                                                }
                                            });
                                        }
                                    });
                                    show.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new Message());
                                    show.show();
                                }

                            });
                            */


                        } catch (JSONException e) {
                            Log.e(TAG, "Failed to parse event JSON: " + e.getMessage());
                        }
                    }
                });
        request.executeAsync();
    }
}
