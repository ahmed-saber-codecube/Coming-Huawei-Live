package com.coming.customer.core.validation

import android.util.Patterns
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import java.util.regex.Pattern

class DefaultValidations : Validation() {

    fun EditText.isFollowingRegex(pattern: String): EditText = bindViewValidation(this) {
        if (pattern.isNotEmpty() && pattern.isNotBlank()) {
            it.text.isNotEmpty() && it.text.matches(Regex(pattern))
        } else {
            it.text.isNotEmpty() && it.text.isNotBlank()
        }
    }

    fun EditText.isFilled(): EditText = bindViewValidation(this) { it.text.isNotBlank() && it.text.isNotEmpty() }

    fun EditText.isEmail(): EditText = bindViewValidation(this) {
        it.text.trim().isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(it.text).matches()
    }

    fun EditText.isFullName(): EditText = bindViewValidation(this) { it.text.trim().length > 5 }

    fun EditText.isValidPassword(): EditText = bindViewValidation(this) {
        Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[*@#\$%!\\-_?&])(?=\\S+\$).{6,}").matcher(it.text.toString()).matches()
    }

    fun EditText.isPhone(): EditText = bindViewValidation(this) {
        it.text.isNotEmpty() && Patterns.PHONE.matcher(it.text).matches() && Pattern.compile(".{9}$").matcher(it.text).matches()
    }

    fun CheckBox.isAccepted(): View = bindViewValidation(this) {
        this.isChecked
    }

    infix fun View.onValidationSuccess(action: ValidationConditionAction): View = bindSuccessConditionAction(this, action)

    infix fun View.onValidationError(action: ValidationConditionAction): View = bindErrorValidationAction(this, action)
}