package com.example.android.myaddressbook

import android.graphics.drawable.Drawable
import android.widget.EditText
import android.widget.TextView

internal inline fun EditText.validateWith(passIcon: Drawable?,
                                          failIcon: Drawable?,
                                          validator: TextView.() -> Boolean): Boolean {
    setCompoundDrawablesWithIntrinsicBounds(null, null,
            if (validator()) passIcon else failIcon, null)
    return validator()
}