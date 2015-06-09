package edu.washington.crew.fbevents;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collection;

import edu.washington.crew.fbevents.R;

public class EventDetailsActivity extends ActionBarActivity {
    LoginManager loginManager;

    public static final String TAG = "EventDetailsActivity";

    private FbEvent eventModel;
    private String eventId;
    private String postContent;

    private AccessTokenTracker accessTokenTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        Intent intent = getIntent();
        eventId = intent.getStringExtra(EventDetailsFragment.EVENT_ID);

<<<<<<< HEAD
        updateEventDetails();
        // getAttending();
=======
        if (savedInstanceState == null)
            updateEventDetails();

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
                } else {
                    Toast.makeText(EventDetailsActivity.this, "Message can't be blank.", Toast.LENGTH_SHORT).show();
                }
            }
        });
>>>>>>> 1bbfb986442289346dce465c018c49b27340837b
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

    /*
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
                            eventModel = FbEvent.fromJson(graphResponse.getJSONObject());

                            EventDetailsFragment eventDetails =
                                    EventDetailsFragment.newInstance(eventModel);
                            getFragmentManager().beginTransaction()
                                    .replace(R.id.container, eventDetails)
                                    .commit();
                        } catch (JSONException e) {
                            Log.e(TAG, "Failed to parse event JSON: " + e.getMessage());
                        }
                    }
                });
        request.executeAsync();
    }
    */
}
