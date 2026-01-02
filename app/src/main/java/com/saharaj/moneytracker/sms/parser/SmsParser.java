package com.saharaj.moneytracker.sms.parser;

import com.saharaj.moneytracker.sms.model.ParsedTransaction;

import java.util.Arrays;
import java.util.Locale;

public abstract class SmsParser {

    protected final String[] senderNames = {
            "ICICI", "SBI", "HDFC"
    };


    protected final String[] TXN_KEYWORDS = {
            "debit", "debited", "spent", "purchase",
            "credit", "credited", "received",
            "withdrawn", "paid"
    };

    public  boolean isTransactionMessage(String body) {
        if (body == null) return false;

        String text = body.toLowerCase(Locale.ROOT);

        return Arrays.stream(TXN_KEYWORDS)
                .anyMatch(text::contains);
    }

    public  boolean isBankSender(String sender) {
        if (sender == null)
            return false;

        sender = sender.toUpperCase(Locale.ROOT);
        return Arrays.stream(senderNames).anyMatch(
                sender::contains
        );
    }

    public abstract ParsedTransaction parse (String body);
}
