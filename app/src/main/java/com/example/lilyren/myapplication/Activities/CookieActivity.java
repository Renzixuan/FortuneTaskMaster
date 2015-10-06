package com.example.lilyren.myapplication.Activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.lilyren.myapplication.R;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CookieActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cookie);
        Animation cookieAnimaiton = AnimationUtils.loadAnimation(this, R.anim.cookie_animation);
        ImageView cookieView = (ImageView)findViewById(R.id.cookie_image);
        cookieView.startAnimation(cookieAnimaiton);
        QuoteRequest quoteRequest = new QuoteRequest();
        quoteRequest.execute();
        //set quote
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cookie, menu);
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

    @Override
    protected void onResume() {
        super.onResume();

        Button okButton = (Button)findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public class QuoteRequest extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("http://api.forismatic.com/api/1.0/?method=getQuote&lang=en&format=json&json=?");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    String myString = stringBuilder.toString();
                    return myString;
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }

            Gson gson = new Gson();
            Quote newQuote = gson.fromJson(response, Quote.class);
            String realQuote = newQuote.getQuoteText();
            String quoteAuthor = newQuote.getQuoteAuthor();
            TextView textView = (TextView)findViewById(R.id.cookie_quote);
            textView.setText(realQuote);
            TextView authorView = (TextView)findViewById(R.id.author_textView);
            if (quoteAuthor.equals("")){
                authorView.setText("Anonymous");
            }
            else {
                authorView.setText(quoteAuthor);
            }
        }

        public class Quote {
            public String quoteText;
            public String quoteAuthor;
            public String senderName;
            public String senderLink;
            public String quoteLink;

            public String getQuoteText(){
                return quoteText;
            }
            public void setQuoteText(String quoteText){
                this.quoteText = quoteText;
            }

            public String getQuoteAuthor(){
                return quoteAuthor;
            }
            public void setQuoteAuthor(String quoteAuthor){
                this.quoteAuthor = quoteAuthor;
            }

            public String getSenderName(){
                return senderName;
            }
            public void setSenderName(String senderName){
                this.senderName = senderName;
            }

            public String getSenderLink(){
                return senderLink;
            }
            public void setSenderLink(String senderLink){
                this.senderLink = senderLink;
            }

            public String getQuoteLink(){
                return quoteLink;
            }
            public void setQuoteLink(String quoteLink){
                this.quoteLink = quoteLink;
            }
        }
    }
}
