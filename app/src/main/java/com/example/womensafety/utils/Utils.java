package com.example.womensafety.utils;

import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.util.Log;

import com.example.womensafety.helpers.Constants;
import com.example.womensafety.domain.models.ContactModel;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static void incrementCount(HelpRepo repo) {
        String countString = repo.getSharedPreferences(Constants.CONTACTS_LIMIT);
        int count = !countString.isEmpty()?Integer.parseInt(countString):0;
        Log.d("debug", countString);
        repo.setSharedPreferences(Constants.CONTACTS_LIMIT, String.valueOf(count+1));
    }

    public static void decrementCount(HelpRepo repo) {
        String countString = repo.getSharedPreferences(Constants.CONTACTS_LIMIT);
        int count = !countString.isEmpty()?Integer.parseInt(countString):0;
        Log.d("debug", countString);
        repo.setSharedPreferences(Constants.CONTACTS_LIMIT, String.valueOf(count-1));
    }

    public static String createLink(String lat, String lon) {
        return "http://maps.google.com/maps?q=loc:"+lat +"," +lon;
    }

    public static void sendMessages(String link, List<ContactModel> list, String message) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                for(ContactModel model: list) {
                    SmsManager.getDefault().sendTextMessage(model.getPhone(), null, message, null, null);
                }
            }
        });
    }

    public static String createMessage(String link, String name) {
        return "Hey There, I'm "+name+ " in Trouble!\n Sending my location: \n "+link;
    }
}
