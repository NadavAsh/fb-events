package edu.washington.crew.fbevents;

/**
 * Created by Ted on 5/26/15.
 */

import android.app.Application;
import android.app.usage.UsageEvents;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Ted on 5/12/2015.
 */
public class FacebookEventsApp extends Application {

    public class FbEvent {
        private int id;
        private String coverPhotoUrl;
        private String description;
        private String eventName;
        private String[] owner;
        private String[] location;
        private Date startTime;
        private String timeZone;

        public FbEvent(int id) {
            this.id = id;
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

        public Date getStartTime() {
            return startTime;
        }

        public void setStartTime(Date startTime) {
            this.startTime = startTime;
        }

        public int getId() {
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

    public class FbEventRepository implements EventRepository {
        private ArrayList<FbEvent> FbEvents;

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
            // TODO: Parse JSON stream and store in this.FbEvents
        }

    }

    public interface EventRepository {
        /* @returns a event topic at index i */
        public FbEvent getEvent(int i);

        /* @returns the array list of topics */
        public ArrayList<FbEvent> getAllEvents();
    }

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
