package com.example.sheedah;

import android.provider.BaseColumns;

public class SheedahLocalDatabaseContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private SheedahLocalDatabaseContract() {}

    /* Inner class that defines the table contents */
    public static class RecentTransactions implements BaseColumns {
        public static final String TABLE_NAME = "recent_transactions";
        public static final String COLUMN_NAME_1 = "customer_name";
        public static final String COLUMN_NAME_2 = "transaction_amount";
        public static final String COLUMN_NAME_3 = "transaction_status";
        public static final String COLUMN_NAME_4 = "transaction_time";
        public static final String COLUMN_NAME_5 = "transaction_date";

    }

}
