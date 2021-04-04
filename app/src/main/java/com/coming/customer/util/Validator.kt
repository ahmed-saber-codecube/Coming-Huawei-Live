package com.coming.customer.util


import android.content.Context
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.StringRes
import com.coming.customer.exception.ApplicationException
import com.google.android.material.textfield.TextInputLayout
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by hlink21 on 5/5/16.
 */
@Singleton
class Validator @Inject constructor(private val context: Context) {

    internal lateinit var subject: String
    internal lateinit var editText: TextView
    internal var messageBuilders: MutableList<MessageBuilder> = ArrayList()
    private var textInputLayout: TextInputLayout? = null

    fun submit(s: TextView): Validator {
        subject = s.text.toString()
        editText = s
        messageBuilders = ArrayList()
        return this
    }

    fun submit(s: TextView, textInputLayout: TextInputLayout): Validator {
        subject = s.text.toString()
        this.textInputLayout = textInputLayout
        messageBuilders = ArrayList()
        return this
    }


    fun checkEmpty(): MessageBuilder {
        return MessageBuilder(Type.EMPTY)
    }

    fun startWithZero(): MessageBuilder {
        return MessageBuilder(Type.START_WITH_ZERO)
    }

    fun checkEmptyWithoutTrim(): MessageBuilder {
        return MessageBuilder(Type.EMPTY_WITHOUT_TRIM)
    }

    fun checkValidEmail(): MessageBuilder {
        return MessageBuilder(Type.EMAIL)
    }

    fun checkMaxDigits(max: Int): MessageBuilder {

        return MessageBuilder(Type.MAX_LENGTH, max)
    }

    fun checkMinDigits(min: Int): MessageBuilder {
        return MessageBuilder(Type.MIN_LENGTH, min)
    }

    fun matchString(s: String): MessageBuilder {
        return MessageBuilder(Type.MATCH, s)
    }

    fun matchPatter(patter: String): MessageBuilder {
        return MessageBuilder(Type.PATTERN_MATCH, patter)
    }

    @Throws(ApplicationException::class)
    fun check(): Boolean {

        for (builder in messageBuilders) {

            try {
                when (builder.type) {

                    Type.EMPTY -> isEmpty(subject, builder.message, true)
                    Type.EMPTY_WITHOUT_TRIM -> isEmpty(subject, builder.message, false)
                    Type.EMAIL -> isValidEmail(subject, builder.message)
                    Type.MAX_LENGTH -> checkMaxDigits(subject, builder.validLength, builder.message)
                    Type.MIN_LENGTH -> checkMinDigits(subject, builder.validLength, builder.message)
                    Type.MATCH -> match(subject, builder.match, builder.message)
                    Type.PATTERN_MATCH -> matchPatter(subject, builder.match, builder.message)
                    Type.START_WITH_ZERO -> startWithZero(subject, builder.message)
                }

                if (textInputLayout != null) {
                    textInputLayout!!.error = null
                    Log.e("Validation", "No error")
                }

            } catch (e: ApplicationException) {

                editText.clearFocus()
                editText.requestFocus()

                //  new Handler().postDelayed(() -> showKeyboard(editText),500);

                if (textInputLayout != null) {
                    Log.e("Validation", " Error ")
                    if (!textInputLayout!!.isErrorEnabled)
                        textInputLayout!!.isErrorEnabled = true
                    textInputLayout!!.error = e.localizedMessage
                }
                e.type = ApplicationException.Type.VALIDATION
                throw e
            }


        }
        return true
    }

    @Throws(ApplicationException::class)
    internal fun isEmpty(subject: String?, errorMessage: String?, byTrimming: Boolean) {
        var subject: String? = subject ?: throw ApplicationException(errorMessage!!)

        if (byTrimming)
            subject = subject!!.trim { it <= ' ' }

        if (subject!!.isEmpty())
            throw ApplicationException(errorMessage!!)

    }

    @Throws(ApplicationException::class)
    internal fun startWithZero(subject: String?, errorMessage: String?) {
        var subject: String? = subject ?: throw ApplicationException(errorMessage!!)
        if (subject!!.startsWith("0"))
            throw ApplicationException(errorMessage!!)
    }

    @Throws(ApplicationException::class)
    internal fun isValidEmail(subject: String, errorMessage: String?) {
        if (!subject.matches(Patterns.EMAIL_ADDRESS.pattern().toRegex()))
            throw ApplicationException(errorMessage!!)
    }

    @Throws(ApplicationException::class)
    internal fun checkMinDigits(subject: String, min: Int, errorMessage: String?) {
        if (subject.length < min)
            throw ApplicationException(errorMessage!!)
    }

    @Throws(ApplicationException::class)
    internal fun checkMaxDigits(subject: String, max: Int, errorMessage: String?) {
        if (subject.length > max)
            throw ApplicationException(errorMessage!!)
    }

    @Throws(ApplicationException::class)
    internal fun match(subject: String, toMatch: String?, errorMessage: String?) {
        if (toMatch == null || subject != toMatch)
            throw ApplicationException(errorMessage!!)
    }

    @Throws(ApplicationException::class)
    internal fun matchPatter(subject: String?, pattern: String?, errorMessage: String?) {

        if (subject == null || !subject.matches(pattern!!.toRegex())) {
            throw ApplicationException(errorMessage!!)
        }
    }

    inner class MessageBuilder {
        val type: Type
        var message: String? = null
            private set

        var validLength: Int = 0
        var match: String? = null

        constructor(type: Type) {
            this.type = type
        }

        constructor(type: Type, validLength: Int) {
            this.type = type
            this.validLength = validLength
        }

        constructor(type: Type, match: String) {
            this.type = type
            this.match = match
        }


        fun errorMessage(message: String): Validator {
            this.message = message
            messageBuilders.add(this)
            return this@Validator
        }

        fun errorMessage(@StringRes message: Int): Validator {
            this.message = context.resources.getString(message)
            messageBuilders.add(this)
            return this@Validator
        }
    }

    enum class Type {
        EMPTY, EMAIL, MIN_LENGTH, MAX_LENGTH, MATCH, PATTERN_MATCH, EMPTY_WITHOUT_TRIM, START_WITH_ZERO
    }

    private fun showKeyboard(view: View) {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

}
