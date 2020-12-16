package com.github.skiff2011.authtest

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.widget.doOnTextChanged
import com.github.skiff2011.authtest.ui.TextInputLayoutViewState
import com.skiff2011.fieldvalidator.FieldAutoValidator
import com.skiff2011.fieldvalidator.condition.RegexCondition
import com.skiff2011.fieldvalidator.view.bindValidator
import kotlinx.android.synthetic.main.activity_main.*

const val NOTIFICATION_CHANNEL_ID = "channel_id"
const val NOTIFICATION_CHANNEL_NAME = "channel_name"

class MainActivity : AppCompatActivity() {

    private val biometricSecurityManager: BiometricSecurityManager by lazy {
        BiometricSecurityManager(this)
    }

    private val userManager: UserManager by lazy {
        UserManager(SharedPrefsPasswordStorageImpl(this))
    }

    private val validator = FieldAutoValidator { status ->
        check.isEnabled = status
        save.isEnabled = status
    }

    private var login: String by validator.validateable(
        initialValue = "",
        viewId = R.id.loginTil,
        condition = RegexCondition(
            error = "Enter correct string",
            regex = ".{1,20}".toRegex()
        ), viewState = TextInputLayoutViewState()
    )

    private var password: String by validator.validateable(
        initialValue = "",
        viewId = R.id.passwordTil,
        condition = RegexCondition(
            error = "Enter correct string",
            regex = ".{1,20}".toRegex()
        ), viewState = TextInputLayoutViewState()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        biometricBtn.setOnClickListener(::onBiometricClicked)
        passwordET.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                true
            } else {
                false
            }
        }
        save.setOnClickListener(::onSaveClicked)
        check.setOnClickListener(::onCheckClicked)
        login = loginET.text?.toString() ?: ""
        loginET.doOnTextChanged { text, _, _, _ ->
            login = text?.toString() ?: ""
        }
        passwordET.doOnTextChanged { text, _, _, _ ->
            password = text?.toString() ?: ""
        }
        password = passwordET.text?.toString() ?: ""
        validator.enableAutoValidation()
        bindValidator(loginTil, validator)
        bindValidator(passwordTil, validator)
    }

    override fun onResume() {
        super.onResume()
        biometricBtn.isEnabled =
            biometricSecurityManager.getBiometricStatus() is BiometricsAvailable
    }

    private fun onCheckClicked(view: View) {
        if (userManager.checkUser(login, password)) {
            onAuthSuccess()
        } else {
            onAuthFailure()
        }
    }

    private fun onSaveClicked(view: View) {
        userManager.saveUser(login, password)
        loginET.setText("")
        passwordET.setText("")
        toast(R.string.user_saved)
    }

    private fun onBiometricClicked(view: View) {
        when (biometricSecurityManager.getBiometricStatus()) {
            BiometricsAvailable -> biometricSecurityManager.startBiometric(::onBiometricCompleted)
            BiometricsNotSetUp -> onBiometricNotSetUp()
            is BiometricsUnavailable -> onBiometricUnavailable()
        }
    }

    private fun onBiometricCompleted(status: BiometricAuthStatus) {
        when (status) {
            is BiometricAuthSuccess -> onAuthSuccess()
            BiometricAuthFailure -> onAuthFailure()
            is BiometricAuthError -> onAuthError()
        }
    }

    private fun onBiometricNotSetUp() {
        toast(R.string.biometric_not_set_up)
    }

    private fun onBiometricUnavailable() {
        toast(R.string.biometric_not_available)
    }

    private fun onAuthSuccess() {
        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
        } else {
            Notification.Builder(this).setPriority(Notification.PRIORITY_MAX)
        }
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Auth status")
            .setContentText("Success")
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel =
                NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    enableVibration(true)
                    setShowBadge(true)
                    enableLights(true)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                    description = "notification channel description"
                }
            notificationManager.createNotificationChannel(notificationChannel)
        }

        NotificationManagerCompat.from(this).notify(1, notification)
    }

    private fun onAuthError() {
        toast(R.string.auth_error)
    }

    private fun onAuthFailure() {
        toast(R.string.auth_failure)
    }
}