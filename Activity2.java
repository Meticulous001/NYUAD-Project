package com.hackathon.sha3by.sha3by;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Activity2 extends AppCompatActivity {
    private List<User> users = new ArrayList<>();
    UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity2);


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String userName = sharedPref.getString("Name", "");
        int arabic = sharedPref.getInt("Arabic", 0);
        int avatar = sharedPref.getInt("Avatar", 0);
        final User currUser = new User(userName, arabic, avatar);

        TextView currentuser = findViewById(R.id.currentuser);
        ImageView currentuserimage = findViewById(R.id.currentuserimage);

        currentuser.setText(currUser.name);
        Log.e("WOO","nnnnn2nnnnnnnnn");
        Log.e("WOO",userName);
        Log.e("WOO",currUser.name);
        Log.e("WOO","nnnnnnnnnnnnnn");

        if(currUser.avatar!=0){
            currentuserimage.setImageResource(R.mipmap.hij);
        } else {
            currentuserimage.setImageResource(R.mipmap.avatar3);
        }



        ListView listView = findViewById(R.id.listview1);
        adapter = new UserAdapter(this, users);

        listView.setAdapter(adapter);




        MessageStore.getInstance().firebasedatabase.getReference().child("users").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        users.clear();
                        for(DataSnapshot userValue: dataSnapshot.getChildren()){

                            User user = userValue.getValue(User.class);
                            Log.e("testbug1",user.name+" "+user.arabic+" "+ user.avatar);
                            if(user.name!=null && user.name.length()>1 && user.arabic != currUser.arabic) users.add(user);
                        }
                        Log.e("WOO", "USERS CHANGED");
                        Log.e("WOO", Integer.toString(users.size()));
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

    }

    public class UserAdapter extends ArrayAdapter<User> {
        private Context mContext;
        private List<User> usersList = new ArrayList<>();

        public UserAdapter(Context context, List<User> list) {
            super(context, 0 , list);
            mContext = context;
            usersList = list;
        }


        class customListener implements View.OnClickListener{
            public User user;
            public customListener(User user){
                super();
                this.user  = user;

            }
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity2.this, Activity3.class);
                intent.putExtra("username", this.user.name);
                startActivity(intent);
            }

        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            User user = users.get(position);

            if(convertView==null){
                convertView=getLayoutInflater().inflate(R.layout.user, parent, false);
            }


            convertView.setOnClickListener(new customListener(user));
            TextView koala = convertView.findViewById(R.id.username);
            koala.setText(user.name);
            Log.e("WOO", "RENDERING");

            ImageView myAvatar=convertView.findViewById(R.id.userAvatar);
                if(user.avatar!=0){
                    myAvatar.setImageResource(R.mipmap.hij);
                } else {
                    myAvatar.setImageResource(R.mipmap.avatar3);
                }


            return convertView;
        }
    }

}

