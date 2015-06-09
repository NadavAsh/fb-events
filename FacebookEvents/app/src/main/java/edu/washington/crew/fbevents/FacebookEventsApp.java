package edu.washington.crew.fbevents;

/**
 * Created by Ted on 5/26/15.
 */

import android.app.Application;
import android.app.usage.UsageEvents;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Ted on 5/12/2015.
 */

class FbEventRepository implements EventRepository {
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

interface EventRepository {
    /* @returns a event topic at index i */
    public FbEvent getEvent(int i);

    /* @returns the array list of topics */
    public ArrayList<FbEvent> getAllEvents();
}

class FbEvent {
    // @TODO: Leverage a date object for startTime and int for ID; kept it String for parsing JSON to preserve simplicity
    public static final String RSVP_ATTENDING = "attending";
    public static final String RSVP_MAYBE = "unsure";
    public static final String RSVP_DECLINE = "declined";
    public static final String RSVP_NOT_REPLIED = "not_replied";

    public String id;
    public String coverPhotoUrl;
    public String description;
    public String eventName;
    public String[] owner;
    public String location;
    public String startTime;
    public String endTime;
    public String timeZone;
    public String rsvpStatus;

    public static FbEvent fromJson(JSONObject json) throws JSONException {
        FbEvent newEvent = new FbEvent(json.getString("id"));
        if (json.has("description"))
            newEvent.setDescription(json.getString("description"));
        if (json.has("name"))
            newEvent.setEventName(json.getString("name"));
        if (json.has("cover_photo_url"))
            newEvent.setCoverPhotoUrl(json.getString("cover_photo_url"));
        if (json.has("start_time"))
            newEvent.setStartTime(formatDate(json.getString("start_time")));
        if (json.has("end_time"))
            newEvent.setStartTime(formatDate(json.getString("end_time")));
        if (json.has("timezone"))
            newEvent.setTimeZone(json.getString("timezone"));
        if (json.has("rsvp_status"))
            newEvent.setRsvpStatus(json.getString("rsvp_status"));

        if (json.has("place")) {
            JSONObject place = json.getJSONObject("place");
            if (place.has("name"))
                newEvent.setLocation(place.getString("name"));
        }

        if (json.has("cover")) {
            JSONObject cover = json.getJSONObject("cover");
            if (cover.has("source"))
                newEvent.setCoverPhotoUrl(cover.getString("source"));
        }

        return newEvent;
    }

    public static String formatDate(String date) {
        try {
            Log.d("FbEvent", date);
            DateFormat df = new DateFormat() {
                static final String FORMAT1 = "yyyy-MM-dd'T'HH:mm:ssZ";
                static final String FORMAT2 = "yyyy-MM-dd";
                final SimpleDateFormat sdf1 = new SimpleDateFormat(FORMAT1);
                final SimpleDateFormat sdf2 = new SimpleDateFormat(FORMAT2);
                @Override
                public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Date parse(String source, ParsePosition pos) {
                    if (source.length() - pos.getIndex() > FORMAT2.length())
                        return sdf1.parse(source, pos);
                    return sdf2.parse(source, pos);
                }
            };
            Date inDate = df.parse(date);

            SimpleDateFormat newFormat = new SimpleDateFormat("MMM dd, yyy HH:mm a");
            return newFormat.format(inDate);
        } catch (ParseException e) {
            return null;
        }
    }

    public FbEvent(String id) {
        this.id = id;
    }

    public FbEvent(String eventName, String startTime, String timeZone, String id, String rsvpStatus) {
        this.eventName = eventName;
        this.startTime = startTime;
        this.timeZone = timeZone;
        this.id = id;
        this.rsvpStatus = rsvpStatus;
    }

    @Override
    public String toString() {
        return this.eventName;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String[] getOwner() {
        return owner;
    }

    public void setOwner(String[] owner) {
        this.owner = owner;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getId() {
        return id;
    }

    public String getCoverPhotoUrl() {
        return coverPhotoUrl;
    }

    public void setCoverPhotoUrl(String coverPhotoUrl) {
        this.coverPhotoUrl = coverPhotoUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRsvpStatus() {
        return rsvpStatus;
    }

    public void setRsvpStatus(String rsvp) {
        rsvpStatus = rsvp;
    }
}

public class FacebookEventsApp extends Application {


    public static FacebookEventsApp instance = null;

    /* Protection at runtime */
    public FacebookEventsApp() {
        if (instance == null) {
            instance = this;
        } else {
            throw new RuntimeException("Cannot create more than one FacebookEventsApp");
        }
    }

}
