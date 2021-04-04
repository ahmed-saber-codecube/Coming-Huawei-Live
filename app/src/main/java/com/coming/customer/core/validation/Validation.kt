package com.coming.customer.core.validation

import android.view.View

abstract class Validation {

    private lateinit var validator: FormValidator

    internal fun initValidator(validator: FormValidator) {
        this.validator = validator
    }

    protected fun <T : View> bindViewValidation(view: T, condition: (T) -> Validated): T = validator.bindValidation(view, condition)
    protected fun bindSuccessConditionAction(view: View, action: ValidationConditionAction): View = validator.bindSuccessConditionAction(view, action)
    protected fun bindErrorValidationAction(view: View, action: ValidationConditionAction): View = validator.bindErrorValidationAction(view, action)
}

internal fun <T : Validation> T.with(validator: FormValidator): T {
    this.initValidator(validator)
    return this
}