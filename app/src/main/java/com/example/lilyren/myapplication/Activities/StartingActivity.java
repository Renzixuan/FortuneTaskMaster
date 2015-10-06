package com.example.lilyren.myapplication.Activities;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lilyren.myapplication.Models.MyAdapter;
import com.example.lilyren.myapplication.Models.Task;
import com.example.lilyren.myapplication.R;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;


public class StartingActivity extends AppCompatActivity {
    //drawer
    private String[] mDrawerItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    public int notifID = 999;
    public static ArrayList <Task> result;
    public RecyclerView mainRecyclerView;
    private static Context context;
    private boolean vipSortAction = true;
    private static final int VERTICAL_SPACE = 0;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);
        context = getApplicationContext();
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(0xFFFFA500));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_starting, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case R.id.vip_sort:
                VIPsort(result, vipSortAction);
                createRecyclerView(result);
                vipSortAction = !vipSortAction;
                return true;
            case R.id.add_icon:
//                context = getApplicationContext();
                final Intent myIntent = new Intent(this, DetailsActivity.class);
                myIntent.putExtra("doneEdit", false);
                startActivity(myIntent);
                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Context secondContext = getApplicationContext();
        SharedPreferences mySharedPreference = secondContext.getSharedPreferences("mySharedPreference", Context.MODE_PRIVATE);
        SharedPreferences.Editor firstEditor = mySharedPreference.edit();
        //mySharedPreference.edit().clear().commit();
        Gson gson = new Gson();
        HashSet<String> jsonSet = (HashSet<String>) mySharedPreference.getStringSet("Task", new HashSet<String>());
        Iterator iterator = jsonSet.iterator();
        Task newTask = null;
        result = new ArrayList<>();
        while (iterator.hasNext()) {
            String currentJson = (String) iterator.next();
            newTask = gson.fromJson(currentJson, Task.class);
            result.add(newTask);
        }

        //sort result arraylist by Date
        Collections.sort(result, new Task());
        SetVisibility(result);

        //Drawer
        SetDrawerList();

            //compare dates to see if send notification
            SimpleDateFormat compareFormat = new SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault());
            Calendar c = Calendar.getInstance();
            Date currentDate = c.getTime();
            Date todayWithZeroTime = null;
            Date taskDate = null;
            try {
                todayWithZeroTime = compareFormat.parse(compareFormat.format(currentDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //set up notification
            Iterator iterator1 = (result).iterator();
            int notifCount = 0;
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
            while (iterator1.hasNext()) {
                try {
                    Task iteTask = (Task) iterator1.next();
                    String taskDateString = iteTask.getDate();
                    taskDate = compareFormat.parse(taskDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (todayWithZeroTime.equals(taskDate))
                    notifCount++;
            }

            if (notifCount > 1) {
                mBuilder.setContentTitle("You have " + notifCount + " tasks today");
                mBuilder.setContentText("Click to see details");
                mBuilder.setSmallIcon(R.drawable.rsz_1fortune_cookie);
                Intent resultIntent = new Intent(this, StartingActivity.class);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addParentStack(StartingActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(notifID, mBuilder.build());
            }
            else if (notifCount == 1){
                mBuilder.setContentTitle("You have a task today :)");
                mBuilder.setContentText(newTask.getDetails());
                mBuilder.setSmallIcon(R.drawable.rsz_1fortune_cookie);
                Intent resultIntent = new Intent(this, StartingActivity.class);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addParentStack(StartingActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(notifID, mBuilder.build());
            }

        }

    public void createRecyclerView (ArrayList result) {
        //create recycler view
        mainRecyclerView = (RecyclerView) findViewById(R.id.taskRecyclerView);
        MyAdapter mainAdapter = new MyAdapter(result);
        mainRecyclerView.setAdapter(mainAdapter);
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mainRecyclerView.addItemDecoration(new MyItemDecoration(VERTICAL_SPACE, getAppContext()));
    }

    public static Context getAppContext() {
        return StartingActivity.context;
    }

    public void VIPsort (ArrayList newResult, boolean vipOrNot){
        if (vipOrNot)
            Collections.sort(newResult, Task.VipComparator);
        else
            Collections.sort(newResult, new Task());
    }

    public void SetVisibility(ArrayList result){
        if (result.size() == 0) {
            findViewById(R.id.taskRecyclerView).setVisibility(View.GONE);
        } else {
            findViewById(R.id.taskRecyclerView).setVisibility(View.VISIBLE);
            findViewById(R.id.empty_message).setVisibility(View.GONE);
            createRecyclerView(result);
        }
    }

    public void SetDrawerList(){
        mDrawerItems = new String[]{"My Tasks","Archive","Calendar", "My Log"};
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout1);
        mDrawerList = (ListView)findViewById(R.id.my_drawer_listview);
        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_layout, mDrawerItems));
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        final Bundle logBundle = new Bundle();
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final LayoutInflater factory = getLayoutInflater();
                final View fragmentView = factory.inflate(R.layout.fragment_log, null);
                RelativeLayout mainView = (RelativeLayout) findViewById(R.id.main_view);
                switch (position) {
                    case 0:
                        mDrawerLayout.closeDrawers();
                        bar.setTitle("My Task List");
                        setContentView(R.layout.activity_starting);
                        SetVisibility(result);
                        SetDrawerList();
                        break;
                    case 1:
                        mainView = (RelativeLayout) findViewById(R.id.main_view);
                        mainView.setVisibility(View.GONE);
                        bar.setTitle("Archive");
                        supportInvalidateOptionsMenu();
                        mDrawerLayout.closeDrawers();
                        logBundle.putInt("choice", 1);
                        Fragment logFragment = new LogFragment();
                        logFragment.setArguments(logBundle);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, logFragment).commit();
                        break;
                    case 2:
                        mainView = (RelativeLayout) findViewById(R.id.main_view);
                        mainView.setVisibility(View.GONE);
                        bar.setTitle("Calendar");
                        bar.addOnMenuVisibilityListener(null);
                        mDrawerLayout.closeDrawers();
                        logBundle.putInt("choice", 2);
                        logFragment = new LogFragment();
                        logFragment.setArguments(logBundle);
                        fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, logFragment).commit();
                        break;
                    default:
                        mainView = (RelativeLayout) findViewById(R.id.main_view);
                        mainView.setVisibility(View.GONE);
                        bar.setTitle("My Log");
                        bar.addOnMenuVisibilityListener(null);
                        mDrawerLayout.closeDrawers();
                        logBundle.putInt("choice", 3);
                        logFragment = new LogFragment();
                        logFragment.setArguments(logBundle);
                        fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, logFragment).commit();
                        break;
                }

            }
        });
    }
}