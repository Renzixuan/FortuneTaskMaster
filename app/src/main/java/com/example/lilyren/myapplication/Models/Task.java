package com.example.lilyren.myapplication.Models;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lilyren.myapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by lilyren on 2015-09-15.
 */
public class Task implements Comparator<Task> {
    private String taskName;
    private Boolean taskNumber = false;
    private String taskDate;

    public String getDetails() {
        return taskName;
    }

    public void setDetails(String details) {
        this.taskName = details;
    }

    public String getDate() {
        return taskDate;
    }

    public void setDate(String date) {
        this.taskDate = date;
    }

    public Boolean getNumber() {
        return taskNumber;
    }

    public void setNumber(Boolean number) {
        this.taskNumber = number;
    }

    public static Comparator<Task> VipComparator = new Comparator<Task>() {
        @Override
        public int compare(Task task1, Task task2) {
            String task1String = task1.getDate();
            String task2String = task2.getDate();
            boolean task1Vip = task1.getNumber();
            boolean task2Vip = task2.getNumber();
            DateFormat format = new SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault());
            Date date1 = null;
            Date date2 = null;
            try {
                date1 = format.parse(task1String);
                date2 = format.parse(task2String);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (task1Vip != task2Vip) {
                if (task1Vip)
                    return -1;
                else
                    return 1;
            } else {
                return date1.compareTo(date2);
            }
        }

    };

    @Override
    public int compare(Task task1, Task task2) {
        String task1String = task1.getDate();
        String task2String = task2.getDate();
        boolean task1Vip = task1.getNumber();
        boolean task2Vip = task2.getNumber();
        DateFormat format = new SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault());
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = format.parse(task1String);
            date2 = format.parse(task2String);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date1.equals(date2)) {
            if (task1Vip != task2Vip && task1Vip) {
                return -1;
            } else if (task1Vip != task2Vip && task2Vip) {
                return 1;
            }
        }
        return date1.compareTo(date2);
    }
}


