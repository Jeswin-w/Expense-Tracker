package com.saharaj.moneytracker.smsreader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

public class BankSmsReader {

    private final String[] senderNames;
    private final String[] smsFields;
    //smsFields = new String[]{"address", "body", "date"}

    private final Context context;

    BankSmsReader(Context context, String[] senderNames, String[] smsFields) {
        this.senderNames = Arrays.stream(senderNames)
                .map(s -> s.toUpperCase(Locale.ROOT))
                .toArray(String[]::new);
        this.smsFields = smsFields;
        this.context = context.getApplicationContext();
    }


    public void readInboxSms() {
        Uri inboxUri = Uri.parse("content://sms/inbox");

        try (Cursor cursor = context.getContentResolver()
                .query(inboxUri, smsFields,
                        null, null, "date DESC")) {

            if (cursor == null) return;

            while (cursor.moveToNext()) {
                String sender = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));

                if (!isBankSender(sender)) continue;

                // parse & store
            }

        } catch (IllegalArgumentException ex) {

        }

    }

    private boolean isBankSender(String sender) {
        if (sender == null)
                return false;

        sender = sender.toUpperCase(Locale.ROOT);
        return Arrays.stream(senderNames).anyMatch(
                sender::contains
        );
    }
}

