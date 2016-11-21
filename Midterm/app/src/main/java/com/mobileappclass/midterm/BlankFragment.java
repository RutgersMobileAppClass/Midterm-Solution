package com.mobileappclass.midterm;


import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 *
 * This fragment holds the logic for manipulating the list items when clicked.
 */
public class BlankFragment extends Fragment{

    TextView nameText;      // Displayed task name
    TextView timeText;      // Displayed time
    MainActivity activity;  // App context


    public BlankFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false);


    }

    /*
    We need to do everything in here.
    Only goal of this fragment is to display updated text when List items are clicked.
    So, if that's the case, it logically follows we must do all our work when the view has been created.
    If the view does not exist, we can't update those TextViews!
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // Get the arguments passed when the fragment was pushed to the UI thread stack
        // in the MainActivity. SEe the lv onItemClickedListener.
        Bundle bundle = getArguments();

        nameText = (TextView) view.findViewById(R.id.textName);
        timeText = (TextView) view.findViewById(R.id.textTime);
        activity = (MainActivity) getActivity();

        nameText.setText(bundle.getString("name"));
        timeText.setText(bundle.getString("time"));
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



    }

}
