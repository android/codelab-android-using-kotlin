/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.myaddressbook

import android.graphics.drawable.Drawable
import android.content.SharedPreferences
import android.support.v4.content.ContextCompat
import android.widget.EditText
import android.widget.TextView
import com.google.gson.Gson

internal inline fun EditText.validateWith
        (passIcon: Drawable = ContextCompat.getDrawable(context, R.drawable.ic_pass),
         failIcon: Drawable = ContextCompat.getDrawable(context, R.drawable.ic_fail),
         validator: TextView.() -> Boolean): Boolean {

    setCompoundDrawablesWithIntrinsicBounds(null, null,
            if (validator()) passIcon else failIcon, null)
    return validator()
}

internal inline fun SharedPreferences.edit(func: SharedPreferences.Editor.() -> Unit) {
    val editor = edit()
    editor.func()
    editor.apply()
}

private val CONTACT_KEY = "contact_key"

internal fun SharedPreferences.Editor.putContacts(contacts: ArrayList<Contact>) {
    val contactSet = contacts.map { Gson().toJson(it) }.toSet()
    putStringSet(CONTACT_KEY, contactSet)
}

internal fun SharedPreferences.getContacts(): ArrayList<Contact> {
    val contactSet = getStringSet(CONTACT_KEY, HashSet<String>())
    return contactSet.mapTo(ArrayList()) { Gson().fromJson(it, Contact::class.java) }
}


