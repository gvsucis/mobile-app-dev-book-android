package edu.gvsu.cis.traxy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_login.*
import java.util.regex.Pattern

class LoginFragment:Fragment() {
    val EMAIL_REGEX = Pattern.compile(
        "[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}",
        Pattern.CASE_INSENSITIVE)
    lateinit var viewModel: UserDataViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signin.setOnClickListener { v ->
            val emailStr = email.text.toString()
            val passStr = password.text.toString()
            val shake = AnimationUtils.loadAnimation(this.context, R.anim.shake)
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
                    viewModel.userId.value = emailStr
                    findNavController().navigate(R.id.action_login2main)
                }
            }
        }
        register.setOnClickListener {
            findNavController().navigate(R.id.action_login2signup)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(UserDataViewModel::class.java)
    }
}