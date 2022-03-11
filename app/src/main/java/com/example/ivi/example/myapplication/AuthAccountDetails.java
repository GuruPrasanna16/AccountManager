package com.example.ivi.example.myapplication;

import android.text.TextUtils;

import org.json.JSONObject;

import java.time.Instant;
import java.util.Base64;

public class AuthAccountDetails {
    //Account type id
    public static final String ACCOUNT_TYPE = "com.fisker.connectedprofile";
    //Auth token types
    public static final String AUTHTOKEN_TYPE = "JWT";
    private static final String EXP = "exp";

    public static boolean isExpired(String token) {
        boolean res = true;
        try {
            final String[] parts = token.split("\\.");
            if (parts != null && parts.length == 3) {
                final String body = new String(Base64.getUrlDecoder().decode(parts[1]));
                if (!TextUtils.isEmpty(body)) {
                    final JSONObject jBody = new JSONObject(body);
                    final long exp = jBody.getLong(EXP);
                    final long now = Instant.now().getEpochSecond();
                    if (exp > now) {
                        res = false;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
}