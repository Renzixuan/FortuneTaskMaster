package com.example.lilyren.myapplication.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import com.example.lilyren.myapplication.Models.Task;
import com.example.lilyren.myapplication.R;
import com.google.gson.Gson;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

public class DetailsActivity extends FragmentActivity implements DatePickerFragment.OnFragmentInteractionListener {

    public HashSet<String> resultStringSet = new HashSet<>();
    public String newDate;
    public Date pickedDate;
    public String detailStringCheck;
    public boolean vipTask = false;
    public boolean editDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Calendar initialCal = Calendar.getInstance();
        pickedDate = initialCal.getTime();

        Button confirmButton = (Button)findViewById(R.id.confirm_button);
        TextView detailsTitle = (TextView)findViewById(R.id.details_title);
        confirmButton.setEnabled(false);
        final Button vipButton = (Button)findViewById(R.id.vip_button);
        vipButton.setBackgroundResource(R.drawable.dis_vip_crown);

        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy",java.util.Locale.getDefault());
        newDate = df.format(c.getTime());

        findViewById(R.id.set_date_button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDatePicker();
            }

        });

        TextView dateText = (TextView)findViewById(R.id.date_textView);
        dateText.setText(newDate);

        EditText detailText = (EditText)findViewById(R.id.details_editText);
        detailText.addTextChangedListener(new TextWatcher() {
            Button confirmButton = (Button)findViewById(R.id.confirm_button);
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                confirmButton.setEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                detailStringCheck = ((EditText) findViewById(R.id.details_editText)).getText().toString();
                if (detailStringCheck.length() == 0)
                    confirmButton.setEnabled(false);
                else
                    confirmButton.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //determine to show Doneedit or empty Textview
        Context transContext = getApplicationContext();
        SharedPreferences mySharedPreference = transContext.getSharedPreferences("mySharedPreference", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mySharedPreference.edit();

        editDone = getIntent().getBooleanExtra("editDone", false);
        final String details = getIntent().getStringExtra("detailJson");
        final String date = getIntent().getStringExtra("dateJson");
        final Boolean vip = getIntent().getBooleanExtra("vipJson", false);
        if (editDone) {
            detailsTitle.setText("Edit or Enhance Your Task");
            detailText.setText(details);
            detailText.setSelection(details.length());
            dateText.setText(date);
            if (vip){
                vipButton.setBackgroundResource(R.drawable.vip_crown);
                vipTask = true;
            }
            else{
                vipButton.setBackgroundResource(R.drawable.dis_vip_crown);
                vipTask = false;
            }
        }


        //buttons
        findViewById(R.id.vip_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!vipTask){
                    vipButton.setBackgroundResource(R.drawable.vip_crown);
                }
                else {
                    vipButton.setBackgroundResource(R.drawable.dis_vip_crown);
                }
                vipTask = !vipTask;
            }

        });

        findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    finish();
                }
        });

        //sync with calendar button
        findViewById(R.id.sync_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    addCalendarEvent();
                }
        });


        //confirm button actions!!
        findViewById(R.id.confirm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context firstContext = getApplicationContext();
                EditText details_temp = (EditText) findViewById(R.id.details_editText);

                final String details_string = details_temp.getText().toString();

                //shared preference!!!
                SharedPreferences mySharedPreference = firstContext.getSharedPreferences("mySharedPreference", Context.MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = mySharedPreference.edit();
                Task taskData = new Task();
                taskData.setDetails(details_string);
                taskData.setDate(newDate);
                HashSet<String> jsonSet = (HashSet<String>)mySharedPreference.getStringSet("Task", new HashSet<String>());
                Gson gson = new Gson();
                Task tempTask = new Task();

                //delete the task before the edited version
                tempTask.setDate(date);
                tempTask.setDetails(details);
                tempTask.setNumber(vip);
                String tempTaskJson = gson.toJson(tempTask);
                Iterator iterator = jsonSet.iterator();
                while (iterator.hasNext()) {
                    String currentJson = (String) iterator.next();
                    if (tempTaskJson.equals(currentJson))
                        break;
                }
                jsonSet.remove(tempTaskJson);

                taskData.setNumber(vipTask);
                String json = gson.toJson(taskData);
                jsonSet.add(json);
                prefsEditor.remove("Task");
                prefsEditor.apply();
                prefsEditor.putStringSet("Task", jsonSet).apply();
                finish();
            }
        });
    }


    private void addCalendarEvent() {
        Calendar myCalendar = Calendar.getInstance();
        Intent calendarIntent = new Intent(Intent.ACTION_EDIT);
        calendarIntent.setType("vnd.android.cursor.item/event");
        long startTime = pickedDate.getTime() + 1000*60*60*24;
        calendarIntent.putExtra("beginTime", myCalendar.getTimeInMillis());
        calendarIntent.putExtra("allDay", true);
        calendarIntent.putExtra("endTime", startTime);
        calendarIntent.putExtra("title", detailStringCheck);
//        calendarIntent.putExtra("description", detailStringCheck);
        startActivity(calendarIntent);
    }

    private void showDatePicker() {
        final DatePickerFragment dateFragment =  DatePickerFragment.newInstance(null, null);
        Calendar calendar = Calendar.getInstance();
        Bundle dateArgs = new Bundle();
        dateArgs.putInt("year", calendar.get(Calendar.YEAR));
        dateArgs.putInt("month", calendar.get(Calendar.MONTH)+1);
        dateArgs.putInt("day", calendar.get(Calendar.DAY_OF_MONTH));
        dateFragment.setArguments(dateArgs);
        dateFragment.setCallBack(ondate);
        dateFragment.show(getFragmentManager(), "DatePicker");
    }

        OnDateSetListener ondate = new OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month,
                              int day) {
            month = month + 1;
            pickedDate = null;
            SimpleDateFormat format = new SimpleDateFormat("MM dd,yyyy",java.util.Locale.getDefault());
            try {
                pickedDate = format.parse(showDate(year, month, day));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat newFormat = new SimpleDateFormat("MMM dd, yyyy",java.util.Locale.getDefault() );
            newDate = newFormat.format(pickedDate);
            TextView dateText = (TextView)findViewById(R.id.date_textView);
            dateText.setText(newDate);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
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

    private String showDate(int year, int month, int day){
        String dateString = month + " " + day + "," + year;
        return dateString;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }


}

