package com.mobileappclass.midterm;


import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 *
 * This fragment handles the heavy lifting for the editTexts.
 * When displayed, and the confirm button is pushed, needs to:
 * (1) update the list contents
 * (2) fire the AsyncTask to run in background
 */
public class TaskFragment extends Fragment {



    MainActivity activity;


    public TaskFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task, container, false);
    }


    /*
    Most of our concern is making sure the UI is done properly.
    We do all our work in here because, if the View is created, then we (and the user)
    can access the EditText objects. So we know they are working with the app.
     */

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Made final because of the anonymous inner class below...
        final EditText editName = (EditText) view.findViewById(R.id.editName);
        final EditText editTime = (EditText) view.findViewById(R.id.editTime);


        Button confirmButton = (Button) view.findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // If the fields are empty (""), don't let the user proceed!
                if(editName.getText().toString().compareTo("") == 0  || editTime.getText().toString().compareTo("") ==0){
                    Toast.makeText(activity, "Enter all fields!", Toast.LENGTH_SHORT).show();
                    return; // <-- Quickly exits the onClickListener
                }

                // Grab fields
                String name = editName.getText().toString();
                String time = editTime.getText().toString();

                // Fire the AsyncTask!!!
                activity.createAsyncTask(name,time);

                // Grab reference to the listView in the activity
                // Note we have reference to the activity through onActivityCreated
                // but getActivity() will probably work well here, too
                ListView lv = (ListView) activity.findViewById(R.id.taskList);

                // Hack !! We have the activity, so just update those parameters directly.
                // Why not?
                activity.list.add(name);
                activity.times.add(time);

                // Calls to setAdapter forces the view to redraw itself.
                lv.setAdapter(new ArrayAdapter(activity, android.R.layout.simple_list_item_1, activity.list));

            }
        });
    }


    /*
    Use this method to guarantee, without a doubt, that you will have access to the main app
    context when you need it the most.
     */

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(isAdded()){
            activity = (MainActivity)getActivity();
        }
    }
}
