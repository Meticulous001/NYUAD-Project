package com.hackathon.sha3by.sha3by;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.text.Editable;

import com.google.firebase.database.Query;


import android.view.WindowManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

public class Activity3 extends AppCompatActivity
{

    private String lastText = "";
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String userName = sharedPref.getString("Name", "");
        int arabic = sharedPref.getInt("Arabic", 0);
        int avatar = sharedPref.getInt("Avatar", 0);
        final User currUser = new User(userName, arabic, avatar);


        String username = getIntent().getExtras().get("username").toString();
        String ourname = currUser.name;
        final String first;
        final String second;


        if (username.compareTo(ourname)>0) {first = username; second = ourname;}
        else {first = ourname; second = username;}

        Log.e("first",first.toString());
        Log.e("second",second.toString());
        Log.e("ourname",ourname.toString());
        Log.e("Username",username.toString());

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();


        setContentView(R.layout.activity3);
        Button fab = findViewById(R.id.b_send);


        final EditText input = (EditText) findViewById(R.id.input);




        input.addTextChangedListener(new TextWatcher() {


            @Override
            public void afterTextChanged(Editable s) {

                String arabicOnly  = s.toString().replaceAll("[a-zA-Z0-9?\\.!]","");
                Log.e("text",arabicOnly);

                if (s.toString().length()>3 && !(s.toString().equals(lastText) )){
                    lastText = s.toString();
                    final GetDifficulty gfd=new GetDifficulty(input);
                    gfd.execute(s.toString());

                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!input.getText().toString().equals("")) {

                    MessageStore.getInstance().firebasedatabase.getReference("message").child(first).child(second)
                            .push()
                            .setValue(new Message(input.getText().toString(),currUser.arabic)
                            );
                }


                input.setText("");
            }
        });


        Query query = MessageStore.getInstance().firebasedatabase.getReference("message").child(first).child(second);


        FirebaseListOptions<Message> firebaseListOptions = new FirebaseListOptions.Builder<Message>()
                .setQuery(query, Message.class).setLayout(R.layout.message).setLifecycleOwner(this).build();

        ListAdapter firebaseListAdapter = new FirebaseListAdapter<Message>(firebaseListOptions) {
            @Override
            protected void populateView(View v, Message model, int position) {

                String[] messageSplit = model.messageText.split(" ");
                SpannableString ss = new SpannableString(model.messageText);


                TextView tv = v.findViewById(R.id.tv);

                int counter =0;
                for (String i : messageSplit) {
                    final String word = i;
                    ss.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(View view) {
                            Log.e("word",word);
                            LayoutInflater li = LayoutInflater.from(context);
                            View promptView  = li.inflate(R.layout.prompt,null);

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                            alertDialogBuilder.setView(promptView);
                            alertDialogBuilder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                            alertDialogBuilder.setNegativeButton("Help" ,new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });

                            alertDialogBuilder.setCancelable(true);
                            alertDialogBuilder.setTitle(word);
                            TextView textview = new TextView(context);
                            //here API
                            textview.setText("Meaning: ");
                            textview.setText("dictionary");
                            new GetDefinition(textview).execute("http://18.216.224.250:8000/dictionary/"+ URLEncoder.encode(arabicOnly(word)));

                            alertDialogBuilder.setCancelable(false).setView(textview);

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();

                        }
                        @Override
                        public void updateDrawState(TextPaint ds) {
                            ds.setUnderlineText(false);
                        }
                    }, counter,counter+i.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    counter+=i.length()+1;
                }

                tv.setText(ss);
                if (model.language==1){
                    tv.setBackgroundResource(R.drawable.rounded_rectangle2);
                }
        else
                {
                    tv.setBackgroundResource(R.drawable.rounded_rectangle);
                }

                tv.setMovementMethod(LinkMovementMethod.getInstance());

            }

        };


        final ListView messagelist = findViewById(R.id.messages);

        messagelist
                .setAdapter(firebaseListAdapter);

    }

    public static String arabicOnly(String st)

    {
        String arabicOnly  = st.toString().replaceAll("[a-zA-Z0-9?\\.!]","");
        Log.e("text",arabicOnly);
        return arabicOnly;
    }

     class GetDefinition extends AsyncTask<String, Integer, String>
     {
         TextView tv;
         String inputLine;
         String result;
         GetDefinition(TextView tv)
         {

             this.tv = tv;

         }
         @Override
         protected String doInBackground(String... params){
             String stringUrl = params[0];


             try {
                 //Create a URL object holding our url

                 URL myUrl = new URL(stringUrl);
                 //Create a connection
                 HttpURLConnection connection =(HttpURLConnection)
                         myUrl.openConnection();
                 InputStreamReader streamReader = new
                         InputStreamReader(connection.getInputStream());
                 //Create a new buffered reader and String Builder
                 BufferedReader reader = new BufferedReader(streamReader);
                 StringBuilder stringBuilder = new StringBuilder();
                 //Check if the line we are reading is not null
                 while((inputLine = reader.readLine()) != null){
                     stringBuilder.append(inputLine +"\n");
                 }
                 //Close our InputStream and Buffered reader
                 reader.close();
                 streamReader.close();
                 //Set our result equal to our stringBuilder
                 result = stringBuilder.toString();

             } catch(Exception e) {
             Log.e("HASTPS ERROR",e.getMessage());
            }
             return result;

         }

         protected void onPostExecute(String result) {
             tv.setText(result);
         }
         }
    class GetDifficulty extends AsyncTask<String, Integer, JSONArray >
    {
        final EditText input;
        String inputLine;
        String result;
        String [] arabicList;
        String URL_2 ="http://18.216.224.250:8000/difficulty/";
        GetDifficulty(EditText input)
        {

             this.input = input;

        }
        @Override
        protected JSONArray doInBackground(String... params){
            String sentence = params[0];


            try {
                //Create a URL object holding our url
                String arabic=arabicOnly(sentence);
                Log.e("SPACE ERORR",arabic);
                arabicList=arabic.split(" ");
                Log.e( "koala",URL_2 + URLEncoder.encode(arabic).replace("+", "%20"));
                URL myUrl = new URL(URL_2 + URLEncoder.encode(arabic).replace("+", "%20"));
                //Create a connection
                HttpURLConnection connection =(HttpURLConnection)
                        myUrl.openConnection();
                InputStreamReader streamReader = new
                        InputStreamReader(connection.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();

                Log.e("SPACE ERORR",result);

                JSONArray son =new JSONArray(result);
                return son;

            } catch(Exception e) {
                Log.e("HASTPS ERROR",e.getMessage());
            }
            return null;

        }

        protected void onPostExecute(JSONArray son) {
            try{
                String sentence= input.getText().toString();
                Spannable spannable=new SpannableString(sentence.toString());
                for(int i=0;i<son.length();i++){
                    Integer wordIndex= son.getInt(i);
                    String badWord=arabicList[wordIndex];
                    Log.e("SPACE ERORR",badWord);

                    int pos= sentence.indexOf(badWord);

                    if(pos!=-1 && pos+badWord.length()<sentence.length()){
                        spannable.setSpan(new ForegroundColorSpan(Color.RED), pos, pos+badWord.length(), 0);
                    }

                    //change this
                    input.setText(spannable);
                }

            }
            catch(Exception e) {
                Log.e("HASTPS ERROR",e.getMessage());
            }

        }
    }
}



