package edu.washington.crew.fbevents;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.content.pm.*;
import android.util.Base64;

import com.facebook.*;
import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import edu.washington.crew.fbevents.api.FbEventRepository;

public class MainActivity extends ActionBarActivity implements EventListFragment.OnFragmentInteractionListener {

    public static final String TAG = "MainActivity";

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;

    private FbEventRepository eventRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        eventRepo = new FbEventRepository();
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(0xff3b5998));

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        /*try {
            repo.generateEventsFromJson(this.getResources().openRawResource(R.raw.data));
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, "Error: IO Exception", Toast.LENGTH_SHORT).show();
        }
        Log.i(TAG, repo.getAllEvents().toString());*/

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new EventListFragment())
                    .commit();
        }
        if (!FacebookSdk.isInitialized()) {
            FacebookSdk.sdkInitialize(getApplicationContext());
        }
        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                updateWithToken(newToken);
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateWithToken(AccessToken.getCurrentAccessToken());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        } else if (id == R.id.action_logout) {
            LoginManager.getInstance().logOut();
        } else if (id == R.id.action_refresh) {
            fetchEventData();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onFragmentInteraction(String id) {
        Intent eventDetailsIntent = new Intent(this, EventDetailsActivity.class);
        eventDetailsIntent.putExtra(EventDetailsFragment.EVENT_ID, id);
        startActivity(eventDetailsIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        super.onSaveInstanceState(savedInstanceState);
    }

    private void updateWithToken(AccessToken token) {
        Log.i(TAG, "Updating with token " + token);
        if (token == null || token.isExpired()) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
        } else {
            fetchEventData();
        }
    }

    private void fetchEventData() {
        FbEventRepository.FbEvents.clear();
        GraphRequest notReplied = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(),
                "me/events/not_replied",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        JSONObject jsonObject = graphResponse.getJSONObject();
                        if (jsonObject == null) {
                            Log.e(TAG, graphResponse.getError().getErrorMessage());
                            return;
                        }
                        Log.d(TAG, jsonObject.toString());
                        try {
                            eventRepo.generateFromJsonArray(jsonObject.getJSONArray("data"));
                        } catch (JSONException e) {
                            Log.d(TAG, e.getMessage());
                        }
                    }
                });
        Bundle params = new Bundle();
        params.putString("fields", "start_time,end_time,name,timezone,rsvp_status,cover");
        notReplied.setParameters(params);

        GraphRequest request = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(),
                "me/events",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        JSONObject jsonObject = graphResponse.getJSONObject();
                        if (jsonObject == null) {
                            Log.e(TAG, graphResponse.getError().getErrorMessage());
                            return;
                        }
                        Log.d(TAG, jsonObject.toString());
                        try {
                            eventRepo.generateFromJsonArray(jsonObject.getJSONArray("data"));
                        } catch (JSONException e) {
                            Log.d(TAG, e.getMessage());
                        }
                    }
                });
        request.setParameters(params);

        GraphRequestBatch batch = new GraphRequestBatch(notReplied, request);
        batch.addCallback(new GraphRequestBatch.Callback() {
            @Override
            public void onBatchCompleted(GraphRequestBatch graphRequestBatch) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new EventListFragment())
                        .commit();
            }
        });
        batch.executeAsync();
    }

}
