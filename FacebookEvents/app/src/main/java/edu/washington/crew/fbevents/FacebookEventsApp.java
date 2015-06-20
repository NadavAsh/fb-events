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

import edu.washington.crew.fbevents.api.*;

/**
 * Created by Ted on 5/12/2015.
 */
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
