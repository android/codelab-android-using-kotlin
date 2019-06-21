/*
 * Copyright 2018 Google Inc.
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

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.*
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.util.*

class ContactsActivity : AppCompatActivity(), TextWatcher {

    private lateinit var mContacts: ArrayList<Contact>
    private lateinit var mAdapter: ContactsAdapter

    private lateinit var mPrefs: SharedPreferences

    private lateinit var mFirstNameEdit: EditText
    private lateinit var mLastNameEdit: EditText
    private lateinit var mEmailEdit: EditText

    private var mEntryValid = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val fab = findViewById<FloatingActionButton>(R.id.fab)

        mPrefs = getPreferences(Context.MODE_PRIVATE)
        mContacts = loadContacts()
        mAdapter = ContactsAdapter(mContacts)

        setSupportActionBar(toolbar)
        setupRecyclerView()

        fab.setOnClickListener { showAddContactDialog(-1) }
    }

    /**
     * Loads the contacts from SharedPreferences, and deserializes them into
     * a Contact data type using Gson.
     */
    private fun loadContacts(): ArrayList<Contact> {
        val contactSet = mPrefs.getStringSet(CONTACT_KEY, HashSet())
        return contactSet.mapTo(ArrayList()) { Gson().fromJson(it, Contact::class.java) }
    }

    /**
     * Saves the contacts to SharedPreferences by serializing them with Gson.
     */
    private fun saveContacts() {
        val editor = mPrefs.edit()
        editor.clear()
        val contactSet = mContacts.map { Gson().toJson(it) }.toSet()
        editor.putStringSet(CONTACT_KEY, contactSet)
        editor.apply()
    }

    /**
     * Sets up the RecyclerView: empty data set, item dividers, swipe to delete.
     */
    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.contact_list)

        recyclerView.addItemDecoration(DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL))
        recyclerView.adapter = mAdapter

        // Implements swipe to delete
        val helper = ItemTouchHelper(
                object : ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                    override fun onMove(rV: RecyclerView,
                                        viewHolder: RecyclerView.ViewHolder,
                                        target: RecyclerView.ViewHolder): Boolean {
                        return false
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder,
                                          direction: Int) {
                        val position = viewHolder.adapterPosition
                        mContacts.removeAt(position)
                        mAdapter.notifyItemRemoved(position)
                        saveContacts()
                    }
                })

        helper.attachToRecyclerView(recyclerView)
    }

    /**
     * Shows the AlertDialog for entering a new contact and performs validation
     * on the user input.
     *
     * @param contactPosition The position of the contact being edited, -1
     * if the user is creating a new contact.
     */
    @SuppressLint("InflateParams")
    private fun showAddContactDialog(contactPosition: Int) {
        // Inflates the dialog view
        val dialogView = LayoutInflater.from(this)
                .inflate(R.layout.input_contact_dialog, null)

        mFirstNameEdit = dialogView.findViewById(R.id.edittext_firstname)
        mLastNameEdit = dialogView.findViewById(R.id.edittext_lastname)
        mEmailEdit = dialogView.findViewById(R.id.edittext_email)

        // Listens to text changes to validate after each key press
        mFirstNameEdit.addTextChangedListener(this)
        mLastNameEdit.addTextChangedListener(this)
        mEmailEdit.addTextChangedListener(this)

        // Checks if the user is editing an existing contact
        val editing = contactPosition > -1

        val dialogTitle = if (editing)
            getString(R.string.edit_contact)
        else
            getString(R.string.new_contact)

        // Builds the AlertDialog and sets the custom view. Pass null for
        // the positive and negative buttons, as you will override the button
        // presses manually to perform validation before closing the dialog
        val builder = AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle(dialogTitle)
                .setPositiveButton(R.string.save, null)
                .setNegativeButton(R.string.cancel, null)

        val dialog = builder.show()

        // If the contact is being edited, populates the EditText with the old
        // information
        if (editing) {
            val (firstName, lastName, email) = mContacts[contactPosition]
            mFirstNameEdit.setText(firstName)
            mFirstNameEdit.isEnabled = false
            mLastNameEdit.setText(lastName)
            mLastNameEdit.isEnabled = false
            mEmailEdit.setText(email)
        }
        // Overrides the "Save" button press and check for valid input
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            // If input is valid, creates and saves the new contact,
            // or replaces it if the contact is being edited
            if (mEntryValid) {
                if (editing) {
                    val editedContact = mContacts[contactPosition]
                    editedContact.email = mEmailEdit.text.toString()
                    mContacts[contactPosition] = editedContact
                    mAdapter.notifyItemChanged(contactPosition)
                } else {
                    val newContact = Contact(
                            mFirstNameEdit.text.toString(),
                            mLastNameEdit.text.toString(),
                            mEmailEdit.text.toString()
                    )

                    mContacts.add(newContact)
                    mAdapter.notifyItemInserted(mContacts.size)
                }
                saveContacts()
                dialog.dismiss()
            } else {
                // Otherwise, shows an error Toast
                Toast.makeText(this@ContactsActivity,
                        R.string.contact_not_valid,
                        Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_contacts, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        when (id) {
            R.id.action_clear -> {
                clearContacts()
                return true
            }
            R.id.action_generate -> {
                generateContacts()
                return true
            }
            R.id.action_sort_first -> {
                mContacts.sortBy { it.firstName }
                mAdapter.notifyDataSetChanged()
                return true
            }
            R.id.action_sort_last -> {
                mContacts.sortBy { it.lastName }
                mAdapter.notifyDataSetChanged()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }


    /**
     * Clears the contacts from SharedPreferences and the adapter, called from
     * the options menu.
     */
    private fun clearContacts() {
        mContacts.clear()
        saveContacts()
        mAdapter.notifyDataSetChanged()
    }

    /**
     * Generates mock contact data to populate the UI from a JSON file in the
     * assets directory, called from the options menu.
     */
    private fun generateContacts() {
        val contactsString = readContactJsonFile()
        try {
            val contactsJson = JSONArray(contactsString)
            for (i in 0 until contactsJson.length()) {
                val contactJson = contactsJson.getJSONObject(i)
                val contact = Contact(
                        contactJson.getString("first_name"),
                        contactJson.getString("last_name"),
                        contactJson.getString("email"))
                Log.d(TAG, "generateContacts: " + contact.toString())
                mContacts.add(contact)
            }

            mAdapter.notifyDataSetChanged()
            saveContacts()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    /**
     * Reads a file from the assets directory and returns it as a string.
     *
     * @return The resulting string.
     */
    private fun readContactJsonFile(): String? {
        var contactsString: String? = null
        try {
            val inputStream = assets.open("mock_contacts.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()

            contactsString = String(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return contactsString
    }

    /**
     * Override methods for the TextWatcher interface, used to validate user
     * input.
     */


    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int,
                                   i2: Int) {

    }

    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int,
                               i2: Int) {

    }

    /**
     * Validates the user input when adding a new contact each time the test
     * is changed.
     *
     * @param editable The text that was changed. It is not used as you get the
     * text from member variables.
     */
    override fun afterTextChanged(editable: Editable) {
        val notEmpty: TextView.() -> Boolean = { text.isNotEmpty() }
        val isEmail: TextView.() -> Boolean = { Patterns.EMAIL_ADDRESS.matcher(text).matches() }

        mEntryValid = mFirstNameEdit.validateWith(validator = notEmpty) and
                mLastNameEdit.validateWith(validator = notEmpty) and
                mEmailEdit.validateWith(validator = isEmail)
    }

    private inner class ContactsAdapter internal constructor(
            private val mContacts: ArrayList<Contact>) :
            RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

        override fun onCreateViewHolder(
                parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.contact_list_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(
                holder: ViewHolder, position: Int) {
            val (firstName, lastName, email) = mContacts[position]
            val fullName = "$firstName $lastName"
            holder.nameLabel.text = fullName
            holder.emailLabel.text = email
        }

        override fun getItemCount(): Int {
            return mContacts.size
        }

        internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var nameLabel: TextView = itemView.findViewById(R.id.textview_name)
            var emailLabel: TextView = itemView.findViewById(R.id.textview_email)

            init {
                itemView.setOnClickListener { showAddContactDialog(adapterPosition) }
            }
        }
    }

    companion object {

        private val CONTACT_KEY = "contact_key"
        private val TAG = ContactsActivity::class.java.simpleName
    }
}
