package com.efulltech.efupay.e_school.db;

import android.provider.BaseColumns;

import org.json.JSONObject;

public final class UserContract {

    public UserContract(){}

    public static class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "clock_table";
        public static final String CLOCK_ID = "clock_id";
        public static final String CLOCK_CARD_SERIAL_NUMBER = "clock_card_serial_number";
        public static final String CLOCK_CARD_CODE = "clock_card_code";
        public static final String CLOCK_TIMESTAMP = "clock_timestamp";


        public static final String TABLE_NAME1 = "outgoing_notification";
        public static final String ID = "id";
        public static final String MESSAGE_ID = "message_id";
        public static final String MESSAGE_ISDN = "message_isdn";
        public static final String MESSAGE_CONTENT = "message_content";
        public static final String MESSAGE_DELIVERED = "message_delivered";
        public static final String STUDENT_CARD_CODE = "student_card_code";
        public static final String MESSAGE_SENT = "message_sent";
        public static final String STUDENT_CARD_ID = "card_id";
        public static final String DATE = "date";


        public static final String TABLE_NAME2 = "all_students";
        public static final String D_ID = "id";
        public static final String FIRST_NAME = "first_name";
        public static final String LAST_NAME = "last_name";
        public static final String CLASS = "class";
        public static final String PHOTO_URL = "photo_url";

    }
}
