package edu.gvsu.cis.traxy

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.content_sign_up.*
import kotlinx.android.synthetic.main.content_sign_up.email
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {
    val EMAIL_REGEX = Pattern.compile(
        "[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}",
        Pattern.CASE_INSENSITIVE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        setSupportActionBar(findViewById(R.id.toolbar))

        fab.setOnClickListener { view ->
            val emailStr = email.text.toString()
            val pass1Str = password1.text.toString()
            val pass2Str = password2.text.toString()

            when {
                emailStr.length == 0 ->
                    Snackbar
                        .make(email, getString(R.string.email_requirred), Snackbar.LENGTH_LONG)
                        .show()
                !EMAIL_REGEX.matcher(emailStr).find() -> 
                    Snackbar
                        .make(email, getString(R.string.invalid_email), Snackbar.LENGTH_LONG)
                        .show()
                pass1Str.length == 0 || pass2Str.length == 0 ->
                    Snackbar
                        .make(email, getString(R.string.empty_password), Snackbar.LENGTH_LONG)
                        .show()
                !pass1Str.equals(pass2Str) ->
                    Snackbar
                        .make(email, getString(R.string.unmatch_password), Snackbar.LENGTH_LONG)
                        .show()

                !pass1Str.contains("traxy") ->
                    Snackbar
                            .make(password1, getString(R.string.incorrect_password), Snackbar.LENGTH_LONG)
                            .show()

                else -> {
                    val nextPlz = Intent(this@SignUpActivity, MainActivity::class.java)
                    nextPlz.putExtra("USER_EMAIL", emailStr);
                    finish()    // Teerminate this activity and remove it from the Activity stack
                    startActivity(nextPlz)
                }
            }
        }
    }
}