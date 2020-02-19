package com.example.siciliamafia;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.List;

public class StaticMethods {

    final public static String PLAYER_ID_KEY = "UserIdKey";
    final public static String CURRENT_PLAYER_ID_KEY = "CurrentUserIdKey";
    final public static String ROLE_PLAYER = "Player";
    final public static String ROLE_HOST = "Host";
    final public static String ROLE_ADMIN = "Administrator";
    final public static String ROLE_PRESIDENT = "President";

    public static boolean isInternetConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null;
    }

    public static String listToString(List<Integer> numbers) {
        StringBuilder builder = new StringBuilder();
        for (int number : numbers) {
            builder.append(number);
            builder.append(", ");
        }
        builder.setLength(builder.length() - 2);
        return builder.toString();
    }

}
