package edu.washington.crew.fbevents.api;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nadavash on 6/20/15.
 */
public class FbPost {
    public String name;
    public String message;

    public static FbPost fromJson(JSONObject json) {
        FbPost post = new FbPost();
        try {
            if (json.has("from")) {
                JSONObject from = json.getJSONObject("from");
                if (from.has("name"))
                    post.setName(from.getString("name"));
            }

            if (json.has("message"))
                post.setMessage(json.getString("message"));
            else
                return null;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return post;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}