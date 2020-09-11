package edu.gvsu.cis.traxy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.content_sign_up.*
import kotlinx.android.synthetic.main.fragment_sign_up.*
import java.util.regex.Pattern

class SignUpFragment : Fragment() {
    val EMAIL_REGEX = Pattern.compile(
        "[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}",
        Pattern.CASE_INSENSITIVE)
     val viewModel by activityViewModels<UserDataViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab_add.setOnClickListener { _ ->
            val emailStr = email.text.toString()
            val pass1 = password1.text.toString()
            val pass2 = password2.text.toString()

            when {
                emailStr.length == 0 ->
                    Snackbar
                        .make(email, getString(R.string.email_requirred), Snackbar.LENGTH_LONG)
                        .show()
                !EMAIL_REGEX.matcher(emailStr).find() ->
                    Snackbar
                        .make(email, getString(R.string.invalid_email), Snackbar.LENGTH_LONG)
                        .show()
                pass1.length == 0 || pass2.length == 0 ->
                    Snackbar
                        .make(email, getString(R.string.empty_password), Snackbar.LENGTH_LONG)
                        .show()
                !pass1.equals(pass2) ->
                    Snackbar
                        .make(email, getString(R.string.unmatch_password), Snackbar.LENGTH_LONG)
                        .show()
                else -> {
                    viewModel.signUpWithEmailAndPassword(emailStr, pass1)
                    viewModel.userId.observe(this.viewLifecycleOwner) { uid ->
                        if (uid != null)
                            findNavController().navigate(R.id.action_signup2main)
                        else
                            Snackbar
                                .make(email, "Unable to create account",
                                    Snackbar.LENGTH_LONG)
                                .show()
                    }
                }
            }
        }
    }
}