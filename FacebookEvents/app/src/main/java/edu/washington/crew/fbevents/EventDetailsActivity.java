package edu.washington.crew.fbevents;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONException;
import org.json.JSONObject;

import edu.washington.crew.fbevents.R;

public class EventDetailsActivity extends ActionBarActivity {
    public static final String TAG = "EventDetailsActivity";

    private FbEvent eventModel;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        Intent intent = getIntent();
        eventId = intent.getStringExtra(EventDetailsFragment.EVENT_ID);

        updateEventDetails();
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
                            getFragmentManager().beginTransaction()
                                    .replace(R.id.container, eventDetails)
                                    .commit();
                        } catch (JSONException e) {
                            Log.e(TAG, "Failed to parse event JSON: " + e.getMessage());
                        }
                    }
                });
        Bundle bundle = new Bundle();
        bundle.putString("fields", "id,name,description,start_time,end_time,place");
        request.setParameters(bundle);
        request.executeAsync();
    }
}
