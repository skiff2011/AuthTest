package com.github.skiff2011.authtest.ui

import com.google.android.material.textfield.TextInputLayout
import com.skiff2011.fieldvalidator.view.ValidationViewState

class TextInputLayoutViewState : ValidationViewState<TextInputLayout> {
    override fun hideError(view: TextInputLayout) {
        view.error = null
    }

    override fun showError(error: String, view: TextInputLayout) {
        view.error = error
    }
}