package org.example.orafucharles.e_school.db;

import android.provider.BaseColumns;

public final class UserContract {

    public UserContract(){}

    public static class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "clock_table";
        public static final String CLOCK_ID = "clock_id";
        public static final String CLOCK_CARD_NUMBER = "clock_card_number";
        public static final String CLOCK_TIMESTAMP = "clock_timestamp";

    }
}
