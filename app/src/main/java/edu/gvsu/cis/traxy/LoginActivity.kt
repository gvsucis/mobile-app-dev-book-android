package edu.gvsu.cis.traxy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.*
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {
    val EMAIL_REGEX = Pattern.compile(
            "[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}",
            Pattern.CASE_INSENSITIVE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        signin.setOnClickListener { v ->
            val emailStr = email.text.toString()
            val passStr = password.text.toString()
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
                !passStr.contains("traxy") ->
                    signin.startAnimation(shake)

                else -> {
                    val nextPlz = Intent(this@LoginActivity, MainActivity::class.java)
                    nextPlz.putExtra("USER_EMAIL", emailStr);
                    startActivity(nextPlz)
                }
            }
        }

        register.setOnClickListener { v ->
            val toRegister = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(toRegister)
        }
    }
}