package com.saharaj.moneytracker.sms.parser;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MerchantExtractor {

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

     String extractMerchant(String body) {
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
}
