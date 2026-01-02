package com.saharaj.moneytracker.sms.parser;

import com.saharaj.moneytracker.sms.model.ParsedTransaction;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsRegexParser extends SmsParser {

    private static final Pattern AMOUNT_PATTERN = Pattern.compile(
            "(?i)(?:rs\\.?|inr|₹)\\s?([0-9,]+(?:\\.[0-9]{1,2})?)"
    );

    private static final Pattern MERCHANT_TRANSFER = Pattern.compile(
            "(?i)\\btransfer\\s+to\\s+([A-Z ]{3,})"
    );

    private static final Pattern MERCHANT_PRIMARY = Pattern.compile(
            "(?i)\\b(?:to|at|towards|info:)\\s+([A-Za-z0-9&.\\-_ ]{3,})"
    );

    private static final Pattern MERCHANT_UPI = Pattern.compile(
            "(?i)\\bupi[-\\s:]?([a-z0-9._@-]+)"
    );

    private static final Pattern MERCHANT_POS = Pattern.compile(
            "(?i)\\bpos\\s+([a-z0-9 &.\\-]{3,})"
    );

    @Override
    public ParsedTransaction parse(String body) {
        if (body == null) return null;

        String text = body.toLowerCase(Locale.ROOT);

        // 1️⃣ Detect transaction type
        ParsedTransaction.TransactionType type;
        if (text.contains("credit") || text.contains("credited") || text.contains("received")) {
            type = ParsedTransaction.TransactionType.CREDITED;
        } else if (text.contains("debit") || text.contains("debited")
                || text.contains("spent") || text.contains("withdrawn")
                || text.contains("paid")) {
            type = ParsedTransaction.TransactionType.DEBITED;
        } else {
            return null; // Not a transaction SMS
        }

        // 2️⃣ Extract amount
        Matcher amountMatcher = AMOUNT_PATTERN.matcher(body);
        if (!amountMatcher.find()) return null;

        String amountStr = amountMatcher.group(1).replace(",", "");
        long amountPaise = Math.round(Double.parseDouble(amountStr) * 100);

        // 3️⃣ Extract merchant
        String merchant = extractMerchant(body);

        // 4️⃣ Build parsed object
        return new ParsedTransaction(
                detectBank(body),   // bank name
                type,
                amountPaise,
                merchant,
                System.currentTimeMillis(),
                body
        );
    }

    // ---------------- Merchant extraction ----------------

    private String extractMerchant(String body) {
        Matcher m;

        m = MERCHANT_PRIMARY.matcher(body);
        if (m.find()) return normalizeMerchant(m.group(1));

        m = MERCHANT_UPI.matcher(body);
        if (m.find()) return normalizeMerchant(m.group(1).split("@")[0]);

        m = MERCHANT_POS.matcher(body);
        if (m.find()) return normalizeMerchant(m.group(1));

        return "UNKNOWN";
    }

    private String normalizeMerchant(String merchant) {
        return merchant
                .replaceAll("(?i)\\b(ltd|pvt|limited|private)\\b", "")
                .replaceAll("[^A-Za-z0-9 ]", "")
                .trim()
                .toUpperCase(Locale.ROOT);
    }

    // ---------------- Bank detection (simple heuristic) ----------------

    private String detectBank(String body) {
        String text = body.toUpperCase(Locale.ROOT);
        if (text.contains("ICICI")) return "ICICI";
        if (text.contains("SBI")) return "SBI";
        if (text.contains("HDFC")) return "HDFC";
        return "UNKNOWN";
    }
}
