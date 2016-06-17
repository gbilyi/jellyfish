package com.gordbilyi.jellyfish.im.utils;

import android.text.format.DateFormat;

import java.util.Date;

/**
 * Created by gordbilyi on 5/2/16.
 */
public class CommonUtils {

    //TODO: theese should not be hardcoded
    public static final String SERVICE_NAME = "jabber.at";
//    public static final String HOST = "159.203.13.129";
    public static final String USERNAME = "testbot";
    public static final String PASSWORD = "cherry1";

    public static final String USER_JID = USERNAME + "@" + SERVICE_NAME;

    public static String getFormattedDate(long millisecond) {
        return DateFormat.format("HH:mm MM/dd/yyyy", new Date(millisecond)).toString();
    }

}
