package com.efulltech.efupay.e_school.api;

import org.json.JSONArray;

public interface ArrayResponseCallback {
    void done(JSONArray response, Exception e);
}
