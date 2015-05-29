package edu.washington.crew.fbevents;

/**
 * Created by Ted on 5/26/15.
 */

import android.app.Application;
import android.app.usage.UsageEvents;
import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Ted on 5/12/2015.
 */

class FbEventRepository implements EventRepository {
    private ArrayList<FbEvent> FbEvents;

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
                        Log.i("Reader", name);

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
            /* Skip pager object */
            reader.nextName();
            reader.skipValue();
            reader.endObject();
        } finally{
            reader.close();
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

    private String id;
    private String coverPhotoUrl;
    private String description;
    private String eventName;
    private String[] owner;
    private String[] location;
    private String startTime;
    private String timeZone;
    private String rsvpStatus;

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

    public String[] getLocation() {
        return location;
    }

    public void setLocation(String[] location) {
        this.location = location;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
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
}

public class FacebookEventsApp extends Application {


    public static FacebookEventsApp instance = null;

    /* Protection at runtime */
    public FacebookEventsApp () {
        if (instance == null) {
            instance = this;
        } else {
            throw new RuntimeException("Cannot create more than one FacebookEventsApp");
        }
    }

}
