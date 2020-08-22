package edu.gvsu.cis.traxy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {
    val EMAIL_REGEX = Pattern.compile(
            "[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}",
            Pattern.CASE_INSENSITIVE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
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
//                    Snackbar
//                            .make(password, "Incorrrect Password", Snackbar.LENGTH_LONG)
//                            .show()

                else ->
                    Snackbar.make(signin, getString(R.string.login_verified), Snackbar.LENGTH_LONG)
                            .show()
            }
        }
    }
}