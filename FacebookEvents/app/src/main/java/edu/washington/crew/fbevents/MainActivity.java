package edu.washington.crew.fbevents;

import android.app.Fragment;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.facebook.FacebookSdk;

import java.io.IOException;

import edu.washington.crew.fbevents.FacebookEventsApp.*;


public class MainActivity extends ActionBarActivity implements EventFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        FacebookSdk.sdkInitialize(getApplicationContext());
//
//        Intent loginIntent = new Intent(this, LoginActivity.class);
//        startActivity(loginIntent);

        /* Test code to get all events from repo */


        // If repo has not been instantiated
        FbEventRepository repo = new FbEventRepository();
        try {
            repo.generateEventsFromJson(this.getResources().openRawResource(R.raw.data));
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, "Error: IO Exception", Toast.LENGTH_SHORT).show();
        }
        Log.i("MainActivity", repo.getAllEvents().toString());

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new EventFragment())
                    .commit();
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onFragmentInteraction(String string){
        //you can leave it empty
    }
}
