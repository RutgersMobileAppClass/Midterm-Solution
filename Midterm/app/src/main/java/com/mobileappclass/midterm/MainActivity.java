package com.mobileappclass.midterm;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.app.FragmentTransaction;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    public ListView listView; // Reference to the listView object
    public ArrayList<String> times = new ArrayList<String>();   // Contains the list item titles
    public ArrayList<String> list = new ArrayList<String>();    // Contains all the times, in string form
    public static final String MIDTERM_PREFERENCES = "MidtermPreferences";  // Static reference to the SharedPreferences
    public SharedPreferences sharedPreferences;     // Used to save program state after onDEstroy is called



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Stop the EditText object from popping up on program execution
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Use MODE_PRIVATE because we're doing a soft read/write within this activity only
        sharedPreferences = getSharedPreferences(MIDTERM_PREFERENCES,MODE_PRIVATE);

        // If the sharedPreferences object *exists*, then prepare the list contents
        if(sharedPreferences.contains("exists")){
            list = new ArrayList<>(sharedPreferences.getStringSet("list",null));
            times = new ArrayList<>(sharedPreferences.getStringSet("times",null));
       }


        // However, if savedInstanceState exists it takes priority over SHaredPreferences
        // Why? savedInstanceState is for things like orientation changes -- quick, more
        // likely to occur than the SharedPref. SharedPref is a one time deal between onDestroys.
        // If savedInst is null, then program has executed for the first time after being closed
        if(savedInstanceState != null){
            list = savedInstanceState.getStringArrayList("list");
            times = savedInstanceState.getStringArrayList("times");

        }



        // Prepare to show the TaskFragment display
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.rightFragment, new TaskFragment());
        fragmentTransaction.commit();

        // Inflate the listView with the saved data from either SharedPref or SavedInst
        listView = (ListView) findViewById(R.id.taskList);
        listView.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, list));

        // Set the onclicklistener for the children...
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                // grab the title and the time
                // Note: can also do list.get(position)
                String taskTitle = listView.getItemAtPosition(position).toString();
                String taskTime =  times.get(position);

                // instantiate the fragment, pass the values so can be used when the view is created
                BlankFragment frag = new BlankFragment();
                Bundle variables = new Bundle();
                variables.putString("name",taskTitle);
                variables.putString("time",taskTime);
                frag.setArguments(variables);

                // create a fragment tranasaction, commit it to the UI thread
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.rightFragment, frag);
                fragmentTransaction.commit();

            }
        });


    }


    // Save the values ...
    // OnPAuse is guaranteed to always be called, so best place to save
    @Override
    protected void onPause() {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Need to do some trickery here -- we convert the arraylist to a set
        // so that we can save it in the SharedPreferences
        editor.putStringSet("list",new HashSet<String>(list));
        editor.putStringSet("times",new HashSet<String>(times));

        // This string is only here so we can check at runtime if there is a sharedPref object
        editor.putString("exists","exists");
        editor.commit();

        super.onPause();

    }


    /*
    If the add button is clicked, pop in the fragment that represents that
     */
    public void addClick(View view) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.rightFragment, new TaskFragment());
        fragmentTransaction.commit();
    }

    /*
    In the event this gets called, save the only thing the specifications care about:
    the list of names and the times.
    We do not care about the persistence of the fragment object, but isn't hard to fix anyway.
     */

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("list",list);
        outState.putStringArrayList("times",times);



    }

    /*
    ASyncTask to handle the timer.
    A Service would be better -- some of the timer tasks could be very long.
    However, for the purposes of the midterm? THis is a quick solution.
    We take Strings as the input parameter ...
    usage: TimerTask.execute(NAME IN STRING FORM, TIME IN STRING FORM)
     */

    public class TimerTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            // Get the name and the time out from that array
            String taskName = strings[0];
            String taskTime = strings[1];

            // Convert the time to an integer
            int time = Integer.parseInt(taskTime);

            // Not mcuh else to it ... put the background thread to sleep for [time] seconds
            try {
                Thread.sleep(time*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            /*
            Trickery! We need the task name and the task time for the toast.
            Can get around it by concatening them and sending them as a single string to
            onPostExecute.
            Remember that the outout of doInBackGround is sent to onPostExecute.
            THat is why the return type of doInBackground is the parameter for onPostExecute.
             */
            return taskName + "," + taskTime;
        }


        @Override
        protected void onPostExecute(String output) {
            // Split the concatenated string from doInBackGround
            String strings[] = output.split(",");
            String taskName = strings[0];
            String taskTime = strings[1];

            // And toast!
            Toast.makeText(MainActivity.this, "Task " + taskName + " completed in " + taskTime + " seconds", Toast.LENGTH_SHORT).show();

        }
    }

    /*
    A helper method so that the fragment can properly interact with the ASyncTask.
    You can't do a getActivity().(new [Class]) call inside the fragment. Scope is all wrong.
    Solution? Use this method. Make a call from getActivity.createAsyncTask(....).
     */
    public void createAsyncTask(String name, String time){
        new TimerTask().execute(name,time);
    }


}
