package com.saharaj.moneytracker.sms.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class ParsedTransaction {

    @NonNull
    private final String bankName;

    @NonNull
    private final TransactionType type;

    private final long amount;

    @Nullable
    private final String transactionTo;

    private final long timestamp;

    @NonNull
    private final String rawMessage;

    public ParsedTransaction(
            @NonNull String bankName,
            @NonNull TransactionType type,
            long amount,
            @Nullable String transactionTo,
            long timestamp,
            @NonNull String rawMessage
    ) {
        this.bankName = bankName;
        this.type = type;
        this.amount = amount;
        this.transactionTo = transactionTo;
        this.timestamp = timestamp;
        this.rawMessage = rawMessage;
    }



    @NonNull
    public String getBankName() {
        return bankName;
    }

    @NonNull
    public TransactionType getType() {
        return type;
    }

    public long getAmount() {
        return amount;
    }

    @Nullable
    public String getTransactionTo() {
        return transactionTo;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @NonNull
    public String getRawMessage() {
        return rawMessage;
    }

    // ---------- Helpers ----------

    public double getAmountRupees() {
        return amount;
    }

    public enum TransactionType {
        CREDITED,
        DEBITED
    }
}
