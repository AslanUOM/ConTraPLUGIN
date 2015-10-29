package com.aslan.contra.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Patterns;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by gobinath on 10/29/15.
 */
public class Utility {
    public static SharedPreferences getSharedPreference(Context ctx) {
        SharedPreferences preferences = ctx.getSharedPreferences("com.aslan.contra", Context.MODE_PRIVATE);
        return preferences;
    }

    public static boolean isFirstRun(Context ctx) {
        SharedPreferences preferences = getSharedPreference(ctx);
        boolean firstRun = preferences.getBoolean(Constants.FIRST_RUN, true);
        if (firstRun) {
            // Change the preference
            //preferences.edit().putBoolean(Constants.FIRST_RUN, false).commit();
        }
        return firstRun;
    }

    public static List<String> getAccountEmail(Context ctx) {
        List<String> emails = new ArrayList<>();
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(ctx).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                String possibleEmail = account.name;
                emails.add(possibleEmail);
            }
        }
        return emails;
    }
}
