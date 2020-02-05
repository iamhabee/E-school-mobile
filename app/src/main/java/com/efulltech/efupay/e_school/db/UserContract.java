package com.efulltech.efupay.e_school.db;

import android.provider.BaseColumns;

public final class UserContract {

    public UserContract(){}

    public static class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "clock_table";
        public static final String CLOCK_ID = "clock_id";
        public static final String CLOCK_CARD_SERIAL_NUMBER = "clock_card_serial_number";
        public static final String CLOCK_CARD_CODE = "clock_card_code";
        public static final String CLOCK_TIMESTAMP = "clock_timestamp";

    }
}
