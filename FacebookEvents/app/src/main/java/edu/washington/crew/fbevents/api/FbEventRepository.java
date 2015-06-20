package edu.washington.crew.fbevents.api;

import android.util.JsonReader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by nadavash on 6/20/15.
 */
public class FbEventRepository implements EventRepository {
    public static final String TAG = "FbEventRepository";

    public static ArrayList<FbEvent> FbEvents;

    public FbEventRepository () {
        FbEvents = new ArrayList<FbEvent>();
    }

    @Override
    public FbEvent getEvent(int i) {
        return FbEvents.get(i);
    }

    @Override
    public ArrayList<FbEvent> getAllEvents() {
        return FbEvents;
    }

    public void generateFromJsonArray(JSONArray events) {
        for (int i = 0; i < events.length(); ++i) {
            try {
                JSONObject eventObject = events.getJSONObject(i);
                FbEvents.add(FbEvent.fromJson(eventObject));
            } catch (JSONException e) {
                Log.d(TAG, e.getMessage());
            }
        }
    }

}