package com.saharaj.moneytracker.sms.parser;

import com.saharaj.moneytracker.sms.model.ParsedTransaction;
import com.saharaj.moneytracker.sms.parser.exception.TypeParserException;

import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsRegexParser extends SmsParser {

    private static final Pattern DEBIT_ACCT_PATTERN =
            Pattern.compile("(?i).*acct.*debited.*");

    private static final Pattern DEBIT_FOR_BY_PATTERN =
            Pattern.compile("(?i).*debited\\s+(for|by).*");

    private static final Pattern CREDIT_TO_PATTERN =
            Pattern.compile("(?i).*credited\\s+to.*");

    private static final Pattern CREDIT_IN_PATTERN =
            Pattern.compile("(?i).*credited\\s+in.*");

    private final MerchantExtractor merchantExtractor;

    private static final Pattern AMOUNT_PATTERN = Pattern.compile(
            "(?i)(?:rs\\.?|inr|₹)\\s?([0-9,]+(?:\\.[0-9]{1,2})?)"
    );

    SmsRegexParser(MerchantExtractor merchantExtractor) {
        this.merchantExtractor = merchantExtractor;
    }



    @Override
    public ParsedTransaction parse(String body) {
        if (body == null) return null;

        String text = body.toLowerCase(Locale.ROOT);

        // 1️⃣ Detect transaction type
        ParsedTransaction.TransactionType type = detectTransactionType(text)
                .orElseThrow(() -> new TypeParserException("Unable to detect transaction type from SMS: " + text));


        // 2️⃣ Extract amount
        Matcher amountMatcher = AMOUNT_PATTERN.matcher(body);
        if (!amountMatcher.find()) return null;

        String amountStr = amountMatcher.group(1).replace(",", "");
        long amountPaise = Math.round(Double.parseDouble(amountStr) * 100);

        // 3️⃣ Extract merchant
        String merchant = merchantExtractor.extractMerchant(body);

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

    private Optional<ParsedTransaction.TransactionType> detectTransactionType (String body) {
            if (body == null) return Optional.empty();

            String text = body.toLowerCase(Locale.ROOT);

            // 1️⃣ Debit tied to your account (highest priority)
            if (DEBIT_ACCT_PATTERN.matcher(text).find()
                    || DEBIT_FOR_BY_PATTERN.matcher(text).find()
                    || text.contains("spent")
                    || text.contains("withdrawn")) {
                return Optional.of(ParsedTransaction.TransactionType.DEBITED);
            }

            // 2️⃣ Credit tied to your account
            if (CREDIT_TO_PATTERN.matcher(text).find()
                    || CREDIT_IN_PATTERN.matcher(text).find()) {
                return Optional.of(ParsedTransaction.TransactionType.CREDITED);
            }

            return Optional.empty();
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
