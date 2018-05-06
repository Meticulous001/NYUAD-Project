package com.hackathon.sha3by.sha3by;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by october on 28/04/2018.
 */

public class MessageStore {

    private static MessageStore messageStore;

    public FirebaseDatabase firebasedatabase;


    private MessageStore(){
        firebasedatabase = FirebaseDatabase.getInstance();

    }

    public static MessageStore getInstance(){
        if (messageStore == null)
            messageStore = new MessageStore();
        return messageStore;
    }




}
