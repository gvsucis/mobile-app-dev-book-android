package edu.gvsu.cis.traxy

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.content_sign_up.*
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {
    val EMAIL_REGEX = Pattern.compile(
        "[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}",
        Pattern.CASE_INSENSITIVE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        setSupportActionBar(findViewById(R.id.toolbar))

//        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
fab.setOnClickListener { _ ->
        val emailStr = email.text.toString()
        val pass1Str = password1.text.toString()
        val pass2Str = password2.text.toString()
        val shake = AnimationUtils.loadAnimation(this, R.anim.shake)
        when {
            emailStr.length == 0 ->
                Snackbar
                    .make(email, getString(R.string.email_requirred), Snackbar.LENGTH_LONG)
                    .show()
            !EMAIL_REGEX.matcher(emailStr).find() ->
                Snackbar
                    .make(email, getString(R.string.invalid_email), Snackbar.LENGTH_LONG)
                    .show()
            !pass1Str.contains("traxy") || !pass1Str.equals(pass2Str)->
                fab.startAnimation(shake)

            else -> {
                val nextPlz = Intent(this@SignUpActivity, MainActivity::class.java)
                nextPlz.putExtra("USER_EMAIL", emailStr);
                finish()
                startActivity(nextPlz)
            }
        }
    {}}
    }
}