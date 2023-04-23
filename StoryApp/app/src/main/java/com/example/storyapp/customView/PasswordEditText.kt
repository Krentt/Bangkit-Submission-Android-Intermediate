package com.example.storyapp.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.storyapp.R

class PasswordEditText : AppCompatEditText, View.OnTouchListener {

    private lateinit var showPassword: Drawable
    private lateinit var hidePassword: Drawable
    private var isPasswordVisible = false

    private fun init() {
        showPassword = ContextCompat.getDrawable(context, R.drawable.ic_show_password) as Drawable
        hidePassword = ContextCompat.getDrawable(context, R.drawable.ic_hide_password) as Drawable
        setOnTouchListener(this)
        showPassword()

        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if ((p0?.length ?: 0) < 8) {
                    error = "Password setidaknya terdiri dari 8 karakter!"
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //do nothing
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //do nothing
            }
        })


    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val showPasswordButtonStart: Float
            val showPasswordButtonEnd: Float
            var isButtonClicked = false
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                showPasswordButtonEnd = (showPassword.intrinsicWidth + paddingStart).toFloat()
                when {
                    event.x < showPasswordButtonEnd -> isButtonClicked = true
                }
            } else {
                showPasswordButtonStart =
                    (width - paddingEnd - showPassword.intrinsicWidth).toFloat()
                when {
                    event.x > showPasswordButtonStart -> isButtonClicked = true
                }
            }
            if (isButtonClicked) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        isPasswordVisible = !isPasswordVisible
                        showPassword()
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        // do nothing
                        return true
                    }
                    else -> return false
                }
            } else return false
        }
        return false
    }

    private fun showPassword() {
        if (isPasswordVisible) {
            setButtonDrawables(endOfTheText = showPassword)
            transformationMethod = null
        } else {
            setButtonDrawables(endOfTheText = hidePassword)
            transformationMethod = PasswordTransformationMethod.getInstance()
        }
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        hint = "Masukkan Password Anda"
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }


}