package edu.washington.crew.fbevents.api;

import java.util.ArrayList;

/**
 * Created by nadavash on 6/20/15.
 */
public interface EventRepository {
    /* @returns a event topic at index i */
    public FbEvent getEvent(int i);

    /* @returns the array list of topics */
    public ArrayList<FbEvent> getAllEvents();
}
