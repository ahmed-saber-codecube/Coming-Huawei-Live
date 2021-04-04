package com.coming.customer.core.validation

import android.view.View

internal typealias Validated = Boolean
internal typealias ViewCondition = () -> Validated
internal typealias ValidationConditionAction = (View) -> Unit

class FormValidator(private val view: View) {

    private val conditions: MutableMap<View, ViewCondition> = mutableMapOf()
    private val unknownConditions: MutableMap<View, ViewCondition> = mutableMapOf()
    private val successConditions: MutableMap<View, ValidationConditionAction> = mutableMapOf()

    internal fun <T : View> bindValidation(view: T, condition: (T) -> Validated): T {
        conditions[view] = { condition(view) }
        return view
    }

    internal fun bindSuccessConditionAction(view: View, action: ValidationConditionAction): View {
        successConditions[view] = action
        return view
    }

    internal fun bindErrorValidationAction(view: View, action: ValidationConditionAction): View {
        view.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && conditions[view]?.invoke() == false) action(view)
        }
        return view
    }

    fun checkIfViewCanBeEnable() {
        view.isEnabled = conditions.filter {
            if (it.value()) successConditions[it.key]?.invoke(it.key)
            !it.value()
        }.isEmpty() && unknownConditions.filter {
            val validated = it.value()
            if (validated) successConditions[it.key]?.invoke(it.key)
            !it.value()
        }.isEmpty()
    }
}

fun View.enableWhen(conditions: DefaultValidations.() -> Unit) = FormValidator(this).also {
    DefaultValidations().with(it).conditions()
    this.viewTreeObserver.addOnDrawListener {
        it.checkIfViewCanBeEnable()
    }
}

fun <T : Validation> View.enableWhenUsing(validator: T, conditions: T.() -> Unit) = FormValidator(this).also {
    validator.with(it).conditions()
    this.viewTreeObserver.addOnDrawListener {
        it.checkIfViewCanBeEnable()
    }
}