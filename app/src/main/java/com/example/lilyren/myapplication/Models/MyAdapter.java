package com.example.lilyren.myapplication.Models;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.lilyren.myapplication.Activities.CookieActivity;
import com.example.lilyren.myapplication.Activities.DetailsActivity;
import com.example.lilyren.myapplication.Activities.StartingActivity;
import com.example.lilyren.myapplication.R;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import static android.support.v4.app.ActivityCompat.startActivity;

/**
 * Created by lilyren on 15-09-17.
 */


public class MyAdapter extends RecyclerView.Adapter <MyAdapter.TaskViewHolder> {

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        public TextView taskDetail;
        public TextView taskDate;
        public ImageView taskVip;
        public Button taskDone;
        public RelativeLayout taskContainer;

        public TaskViewHolder(View itemView) {
            super(itemView);
            taskDetail = (TextView)itemView.findViewById(R.id.itemText);
            taskDate = (TextView)itemView.findViewById(R.id.date);
            taskVip = (ImageView)itemView.findViewById(R.id.vip_image);
            taskDone = (Button)itemView.findViewById(R.id.done_button);
            taskContainer = (RelativeLayout)itemView.findViewById(R.id.itemContainer);
        }
    }

    private ArrayList<Task> taskData;

    public MyAdapter(ArrayList<Task> taskList) {
        this.taskData = taskList;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View taskView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitems, parent, false);
        return new TaskViewHolder(taskView);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, final int position) {
        holder.taskDetail.setText(taskData.get(position).getDetails());
        holder.taskDate.setText(taskData.get(position).getDate());
        if (taskData.get(position).getNumber()){
            holder.taskVip.setBackgroundResource(R.drawable.vip_crown);
        }
        else {
            holder.taskVip.setBackgroundResource(R.drawable.dis_vip_crown);
        }

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 100 );
        holder.taskContainer.setLayoutParams(params);

        holder.taskContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task selectedTask = taskData.get(position);
                String detailJson = selectedTask.getDetails();
                String dateJson = selectedTask.getDate();
                Boolean vipJson = selectedTask.getNumber();

                Intent detailsIntent = new Intent(StartingActivity.getAppContext(), DetailsActivity.class);
                detailsIntent.putExtra("editDone", true);
                detailsIntent.putExtra("detailJson", detailJson);
                detailsIntent.putExtra("dateJson", dateJson);
                detailsIntent.putExtra("vipJson", vipJson);
                detailsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                StartingActivity.getAppContext().startActivity(detailsIntent);
            }
        });

        holder.taskDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson removeGson = new Gson();
                Task removedTask = (Task) taskData.get(position);
                String removeJson = removeGson.toJson(removedTask);

                //now change the sharedpreference
                Context secondContext = StartingActivity.getAppContext();
                SharedPreferences mySharedPreference;
                mySharedPreference = secondContext.getSharedPreferences("mySharedPreference", Context.MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = mySharedPreference.edit();
                HashSet<String> removeSet = (HashSet<String>) mySharedPreference.getStringSet("Task", new HashSet<String>());
                HashSet<String> archiveSet = (HashSet<String>) mySharedPreference.getStringSet("archiveTask", new HashSet<String>());
                Iterator iterator = removeSet.iterator();
                Gson gson = new Gson();
                Task newTask = null;
                while (iterator.hasNext()) {
                    String currentJson = (String) iterator.next();
                    if (currentJson.equals(removeJson)) {
                        archiveSet.add(currentJson);
                        iterator.remove();
                    }
                    prefsEditor.remove("Task").apply();
                    prefsEditor.putStringSet("Task", removeSet).apply();
                    prefsEditor.remove("archiveTask").apply();
                    prefsEditor.putStringSet("archiveTask", archiveSet);
                }

                //change fortuneCount and go to new activity
                int newCount = mySharedPreference.getInt("fortuneCount", Context.MODE_PRIVATE);
                if (newCount == 4){
                    newCount = 0;
                    prefsEditor.remove("fortuneCount");
                    prefsEditor.apply();
                    prefsEditor.putInt("fortuneCount", newCount).apply();
                    Intent newIntent = new Intent(secondContext, CookieActivity.class);
                    newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    StartingActivity.getAppContext().startActivity(newIntent);
                }
                else if (taskData.size() == 1){
                    prefsEditor.remove("fortuneCount");
                    prefsEditor.apply();
                    prefsEditor.putInt("fortuneCount", newCount).apply();
                    Intent newIntent = new Intent(secondContext, CookieActivity.class);
                    newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    StartingActivity.getAppContext().startActivity(newIntent);
                }
                else if (newCount < 4){
                    newCount++;
                    prefsEditor.remove("fortuneCount");
                    prefsEditor.apply();
                    prefsEditor.putInt("fortuneCount", newCount).apply();
                }
                else{
                    prefsEditor.remove("fortuneCount");
                    prefsEditor.apply();
                    prefsEditor.putInt("fortuneCount", 0).apply();
                }
                taskData.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(0, taskData.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskData.size();
    }

}
