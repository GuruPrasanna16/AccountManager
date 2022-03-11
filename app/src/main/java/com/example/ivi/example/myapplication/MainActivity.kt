package com.example.ivi.example.myapplication

import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AccountManagerCallback
import android.accounts.AccountManagerFuture
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private var mAccountManager: AccountManager? = null
    private val mAccount = Account(
        AuthenticatorService.ACCOUNT_NAME,
        AuthAccountDetails.ACCOUNT_TYPE
    )

    var callback =
        AccountManagerCallback { accountManagerFuture: AccountManagerFuture<Bundle> ->
            try {
                val bnd = accountManagerFuture.result
                val stringBuilder = StringBuilder()
                stringBuilder.append(bnd.toString())
                if (bnd.containsKey(AccountManager.KEY_AUTHTOKEN)) {
                    val isValid: Boolean =
                        TokenGenerator.isValid(bnd.getString(AccountManager.KEY_AUTHTOKEN))
                    stringBuilder.append("\nvalid: $isValid")
                }
                showText(stringBuilder.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAccountManager = AccountManager.get(this)

        mAccountManager!!.addAccount(
            AuthAccountDetails.ACCOUNT_TYPE, AuthAccountDetails.AUTHTOKEN_TYPE,
            null, null, this, callback, null
        )
    }

    fun onGetAccounts(v: View?) {
        val accounts = mAccountManager!!.getAccountsByType(AuthAccountDetails.ACCOUNT_TYPE)
        if (accounts.isEmpty()) {
            Log.e("Test", "Account is empty")
            Toast.makeText(applicationContext, "No Account Available", Toast.LENGTH_SHORT).show()
        }
        val acText = StringBuilder()
        for (account in accounts) {
            val acDetails = """
            name: ${account.name}
            type: ${account.type}
            """.trimIndent()
            acText.append(acDetails)
            acText.append("\n")
        }
        showText(acText.toString())
    }

    fun FetchTokenFromBackend(v: View?): String {
        // Here I have hardcoded the Token but this place you need to fetch
        // the token from your server.
        var token: String = applicationContext.getResources().getString(R.string.JWTToken)
        try {
            token = TokenGenerator.generate()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return token
    }

    fun onPutToken(v: View?) {
        var token = FetchTokenFromBackend(v)
        Log.i("Test", "addAccount(): Token: $token")
        mAccountManager!!.setAuthToken(mAccount,AuthAccountDetails.AUTHTOKEN_TYPE, token)

        // Just showing what is stored in the

        mAccountManager!!.getAuthToken(
            mAccount, AuthAccountDetails.AUTHTOKEN_TYPE,
            null, this, callback, null
        )
    }

    fun showText(text: String?) {
        runOnUiThread {
            val tv = findViewById<TextView>(R.id.textView)
            tv.text = text
        }
    }
}