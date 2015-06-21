package edu.washington.crew.fbevents.api;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by nadavash on 6/20/15.
 */
public class FbEvent implements Parcelable {
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

    public FbEvent() { }

    public FbEvent(String id) {
        this.id = id;
    }

    public FbEvent(Parcel in) {
        id = in.readString();
        coverPhotoUrl = in.readString();
        description = in.readString();
        eventName = in.readString();
        in.readStringArray(owner);
        location = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        timeZone = in.readString();
        rsvpStatus = in.readString();
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

    public void setId(String id) {
        this.id = id;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(coverPhotoUrl);
        dest.writeString(description);
        dest.writeString(eventName);
        dest.writeStringArray(owner);
        dest.writeString(location);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeString(timeZone);
        dest.writeString(rsvpStatus);
    }

    public static final Parcelable.Creator<FbEvent> CREATOR
            = new Parcelable.Creator<FbEvent>() {
        public FbEvent createFromParcel(Parcel in) {
            return new FbEvent(in);
        }

        public FbEvent[] newArray(int size) {
            return new FbEvent[size];
        }
    };
}