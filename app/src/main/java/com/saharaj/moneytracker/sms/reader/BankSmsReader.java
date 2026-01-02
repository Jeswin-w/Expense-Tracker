package com.saharaj.moneytracker.sms.reader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.saharaj.moneytracker.sms.parser.SmsParser;

public class BankSmsReader {

    private final String[] smsFields = new String[]{"address", "body", "date"};

    private final Context context;
    private final SmsParser smsDataParser;

    BankSmsReader(Context context, SmsParser smsDataParser) {

        this.context = context.getApplicationContext();
        this.smsDataParser = smsDataParser;
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

                if (!smsDataParser.isBankSender(sender) && !smsDataParser.isTransactionMessage(body)) continue;

                smsDataParser.parse(body);





                // parse & store
            }

        } catch (IllegalArgumentException ex) {

        }

    }


}

