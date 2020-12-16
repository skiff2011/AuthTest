package com.github.skiff2011.authtest

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class BiometricSecurityManager(private val activity: FragmentActivity) {

    private val biometricManager = BiometricManager.from(activity)

    fun getBiometricStatus(): BiometricStatus =
        when (val result = biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricsAvailable
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricsNotSetUp
            else -> BiometricsUnavailable(result)
        }

    fun startBiometric(action: (BiometricAuthStatus) -> Unit) {
        val executor = ContextCompat.getMainExecutor(activity)
        val biometricPrompt = BiometricPrompt(activity, executor, object :
            BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                action(BiometricAuthError(errorCode, errString))
            }

            override fun onAuthenticationFailed() {
                action(BiometricAuthFailure)
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                action(BiometricAuthSuccess(result))
            }

        })
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(activity.getString(R.string.biometric_prompt_title))
            .setDeviceCredentialAllowed(false)
            .setNegativeButtonText(activity.getString(android.R.string.cancel))
            .setConfirmationRequired(false)
            .build()
        biometricPrompt.authenticate(promptInfo)
    }
}

sealed class BiometricStatus

object BiometricsAvailable : BiometricStatus()

object BiometricsNotSetUp : BiometricStatus()

data class BiometricsUnavailable(val code: Int) : BiometricStatus()

sealed class BiometricAuthStatus

class BiometricAuthSuccess(val result: BiometricPrompt.AuthenticationResult) : BiometricAuthStatus()

object BiometricAuthFailure : BiometricAuthStatus()

data class BiometricAuthError(val errorCode: Int, val errString: CharSequence) :
    BiometricAuthStatus()