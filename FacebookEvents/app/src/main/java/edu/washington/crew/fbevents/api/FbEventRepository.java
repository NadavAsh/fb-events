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

    /* Generates events from Facebook API Json */
    public void generateEventsFromJson(InputStream is) throws IOException {
        // @TODO: Simplify Json reading; there's gotta be an easier way to do this
        // Maybe GSON?

        JsonReader reader = new JsonReader(new InputStreamReader(is, "UTF-8"));
        try {
            reader.beginObject();
            String token = reader.nextName();
            if (token.equals("data")) {
                String currentName;
                String currentStartTime;
                String currentEndTime;
                String currentTimezone;
                String currentId;
                String currentRsvpStatus;
                reader.beginArray();

                while (reader.hasNext()) {
                    reader.beginObject();
                    currentName = "";
                    currentStartTime = "";
                    currentTimezone = "";
                    currentId = "";
                    currentRsvpStatus = "";

                    while (reader.hasNext()) {
                        String name = reader.nextName();

                        if (name.equals("name")) {
                            currentName = reader.nextString();
                        } else if (name.equals("start_time")) {
                            currentStartTime = reader.nextString();
                        } else if (name.equals("end_time")) {
                            currentStartTime = reader.nextString();
                        } else if (name.equals("timezone")) {
                            currentTimezone = reader.nextString();
                        } else if (name.equals("id")) {
                            currentId = reader.nextString();
                        } else if (name.equals("rsvp_status")) {
                            currentRsvpStatus = reader.nextString();
                        }
                    }
                    reader.endObject();
                    FbEvents.add(new FbEvent(currentName, currentStartTime, currentTimezone, currentId, currentRsvpStatus));
                }
                reader.endArray();
            }
            /* Skip pager object for now */
            reader.nextName();
            reader.skipValue();
            reader.endObject();
        } finally{
            reader.close();
        }
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