package com.joe.tubonge

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import kotlinx.android.synthetic.main.activity_sign_in.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask

class SignInActivity : AppCompatActivity() {

    private val signInProviders =
        listOf(AuthUI.IdpConfig.EmailBuilder().setAllowNewAccounts(true).setRequireName(true).build())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        account_sign_in.setOnClickListener {
            val intent = AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(signInProviders)
                .setLogo(R.drawable.ic_fire_emoji).build()
            startActivityForResult(intent, SIGN_IN_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Int.SIZE_BITS) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                constraint_layout.longSnackbar("Setting up your account...")
                progressBar.visibility = View.VISIBLE
                // TODO: Initialize current user in Firestore
                startActivity(intentFor<MainActivity>().newTask().clearTask())
            } else if (resultCode == Activity.RESULT_CANCELED) {
                if (response == null) return

                when (response.error?.errorCode) {
                    ErrorCodes.NO_NETWORK -> {
                        constraint_layout.longSnackbar("No network")
                        progressBar.visibility = View.GONE
                    }
                    ErrorCodes.UNKNOWN_ERROR -> {
                        constraint_layout.longSnackbar("Unknown error")
                        progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    companion object {
        const val SIGN_IN_REQUEST = 1
    }
}