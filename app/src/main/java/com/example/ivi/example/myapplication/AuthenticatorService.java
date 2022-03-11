package com.example.ivi.example.myapplication;


import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

public class AuthenticatorService extends Service {
    public static final String ACCOUNT_NAME = "TestAccount";
    private static final String TAG = "AuthenticatorService";

    @Override
    public IBinder onBind(Intent intent) {
        AccountAuthenticator accountAuthenticator = new AccountAuthenticator(this);
        return accountAuthenticator.getIBinder();
    }

    public class AccountAuthenticator extends AbstractAccountAuthenticator {
        private final Context mContext;
        private final AccountManager mAccountManager;

        public AccountAuthenticator(Context context) {
            super(context);
            mContext = context;
            mAccountManager = AccountManager.get(mContext);
        }

        @Override
        public Bundle editProperties(AccountAuthenticatorResponse accountAuthenticatorResponse, String s) {
            return null;
        }

        @Override
        public Bundle addAccount(AccountAuthenticatorResponse accountAuthenticatorResponse, String s, String s1,
                                 String[] strings, Bundle options) throws NetworkErrorException {
            final Account account = new Account(ACCOUNT_NAME, AuthAccountDetails.ACCOUNT_TYPE);
            boolean res = mAccountManager.addAccountExplicitly(account, "", null);
            Log.i(TAG, ((res) ? "addAccount(): SUCCESS!" : "FAIL"));

            String token = mContext.getResources().getString(R.string.JWTToken);
            try {
                token = TokenGenerator.generate();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.i(TAG, "addAccount(): Token: " + token);
            mAccountManager.setAuthToken(account, AuthAccountDetails.AUTHTOKEN_TYPE, token);
            mAccountManager.setAccountVisibility(account,
                    "com.example.ivi.example.myapplication",
                    AccountManager.VISIBILITY_VISIBLE);

            Bundle bundle = new Bundle();
            bundle.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, res);

            return bundle;
        }

        @Override
        public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse,
                                         Account account, Bundle bundle) throws NetworkErrorException {
            return null;
        }

        @Override
        public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account,
                                   String authTokenType, Bundle options) throws NetworkErrorException {
            String authToken = mAccountManager.peekAuthToken(account, authTokenType);

            if (!TextUtils.isEmpty(authToken)) {
                final Bundle result = new Bundle();
                result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
                result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
                result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
                return result;
            }
            return null;
        }

        @Override
        public String getAuthTokenLabel(String s) {
            return null;
        }

        @Override
        public Bundle updateCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse,
                                        Account account, String s, Bundle bundle) throws NetworkErrorException {
            return null;
        }

        @Override
        public Bundle hasFeatures(AccountAuthenticatorResponse accountAuthenticatorResponse,
                                  Account account, String[] strings) throws NetworkErrorException {
            return null;
        }
    }
}