package edu.washington.crew.fbevents;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.app.ListFragment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import edu.washington.crew.fbevents.api.FbPost;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 */
public class PostListFragment extends android.support.v4.app.ListFragment {
    public static final String TAG = "PostListFragment";
    public static final String EVENT_ID = "event_id";

    private String eventId;

    // TODO: Rename and change types of parameters
    public static PostListFragment newInstance(String eventId) {
        PostListFragment fragment = new PostListFragment();
        Bundle args = new Bundle();
        args.putString(EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PostListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            eventId = getArguments().getString(EVENT_ID);
        }

        updatePosts();
        // TODO: Change Adapter to display your content
    }

    @Override
    public void onStart() {
        super.onStart();
        ListView lv = (ListView)getView().findViewById(android.R.id.list);
        lv.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });
    }

    public void updatePosts() {
        GraphRequest request = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(),
                eventId + "/feed", new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        if (graphResponse.getError() != null) {
                            Log.e(TAG, graphResponse.getError().toString());
                            return;
                        }

                        ArrayList<FbPost> posts = new ArrayList<FbPost>();
                        try {
                            JSONArray data = graphResponse.getJSONObject()
                                    .getJSONArray("data");
                            for (int i = 0; i < data.length(); ++i) {
                                JSONObject jsonPost = data.getJSONObject(i);
                                FbPost post = FbPost.fromJson(jsonPost);
                                if (post != null) {
                                    posts.add(post);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ArrayAdapter<FbPost> adapter = new ArrayAdapter<FbPost>(getActivity(),
                                android.R.layout.simple_list_item_2, android.R.id.text1, posts) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                FbPost post = getItem(position);
                                View view = super.getView(position, convertView, parent);
                                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                                text1.setText(post.getMessage());
                                text2.setText(post.getName());

                                return view;
                            }
                        };
                        setListAdapter(adapter);
                    }
                });
        request.executeAsync();
    }
}
