package com.example.lilyren.myapplication.Activities;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lilyren.myapplication.Models.MyAdapter;
import com.example.lilyren.myapplication.Models.Task;
import com.example.lilyren.myapplication.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int choice;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LogFragment newInstance(String param1, String param2) {
        LogFragment fragment = new LogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public LogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (getArguments() != null) {
            choice = getArguments().getInt("choice");
        }
        return inflater.inflate(R.layout.fragment_log, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater){
        super.onCreateOptionsMenu(menu, menuInflater);
        menu.clear();
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        LinearLayout archiveLayout = (LinearLayout)getView().findViewById(R.id.archive_layout);
        LinearLayout calendarLayout = (LinearLayout)getView().findViewById(R.id.calendar_layout);
        LinearLayout myLogLayout = (LinearLayout)getView().findViewById(R.id.my_log_layout);
        archiveLayout.setVisibility(View.GONE);
        calendarLayout.setVisibility(View.GONE);
        myLogLayout.setVisibility(View.GONE);

        if (choice == 1){
            //archive
            archiveLayout.setVisibility(View.VISIBLE);
            RecyclerView mainRecyclerView = (RecyclerView)getView().findViewById(R.id.archiveRecyclerView);
            Context secondContext = StartingActivity.getAppContext();
            SharedPreferences mySharedPreference;
            mySharedPreference = secondContext.getSharedPreferences("mySharedPreference", Context.MODE_PRIVATE);
            HashSet<String> archiveSet = (HashSet<String>) mySharedPreference.getStringSet("archiveTask", new HashSet<String>());
            Iterator iterator = archiveSet.iterator();
            Task newTask = null;
            Gson gson = new Gson();
            ArrayList <Task> archiveList = new ArrayList<>();
            while (iterator.hasNext()) {
                String currentJson = (String) iterator.next();
                newTask = gson.fromJson(currentJson, Task.class);
                archiveList.add(newTask);
            }
            MyAdapter mainAdapter = new MyAdapter(archiveList);
            mainRecyclerView.setAdapter(mainAdapter);
            mainRecyclerView.setLayoutManager(new LinearLayoutManager(StartingActivity.getAppContext()));
            mainRecyclerView.addItemDecoration(new MyItemDecoration(0, StartingActivity.getAppContext()));
        }

        else if (choice == 2){
            //calendar
            calendarLayout.setVisibility(View.VISIBLE);
        }

        else if (choice == 3){
            //my log
            myLogLayout.setVisibility(View.VISIBLE);
        }

        else
            System.out.println("whaaaaaa, invalid choice!!");
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        System.out.println("on attach called!!!");
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
