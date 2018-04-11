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
package com.example.android.myaddressbook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ContactsActivity extends AppCompatActivity implements TextWatcher {

    private static final String CONTACT_KEY = "contact_key";
    private static final String TAG = ContactsActivity.class.getSimpleName();

    private ArrayList<Contact> mContacts;
    private ContactsAdapter mAdapter;

    private SharedPreferences mPrefs;

    private EditText mFirstNameEdit;
    private EditText mLastNameEdit;
    private EditText mEmailEdit;

    private boolean mEntryValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        Toolbar toolbar = findViewById(R.id.toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);

        mPrefs = getPreferences(Context.MODE_PRIVATE);
        mContacts = loadContacts();
        mAdapter = new ContactsAdapter(mContacts);

        setSupportActionBar(toolbar);
        setupRecyclerView();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddContactDialog(-1);
            }
        });
    }

    /**
     * Loads the contacts from SharedPreferences, and deserializes them into
     * a Contact data type using Gson.
     */
    private ArrayList<Contact> loadContacts() {
        Set<String> contactSet = mPrefs.getStringSet
                (CONTACT_KEY, new HashSet<String>());
        ArrayList<Contact> contacts = new ArrayList<>();
        for (String contactString : contactSet) {
            contacts.add(new Gson().fromJson(contactString, Contact.class));
        }
        return contacts;
    }

    /**
     * Saves the contacts to SharedPreferences by serializing them with Gson.
     */
    private void saveContacts() {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.clear();
        HashSet<String> contactSet = new HashSet<>();
        for (Contact contact : mContacts) {
            contactSet.add(new Gson().toJson(contact));
        }
        editor.putStringSet(CONTACT_KEY, contactSet);
        editor.apply();
    }

    /**
     * Sets up the RecyclerView: empty data set, item dividers, swipe to delete.
     */
    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.contact_list);

        recyclerView.addItemDecoration(new DividerItemDecoration
                (this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        // Implements swipe to delete
        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        (ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT)) {
                    @Override
                    public boolean onMove(RecyclerView rV,
                            RecyclerView.ViewHolder viewHolder,
                            RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder,
                            int direction) {
                        int position = viewHolder.getAdapterPosition();
                        mContacts.remove(position);
                        mAdapter.notifyItemRemoved(position);
                        saveContacts();
                    }
                });

        helper.attachToRecyclerView(recyclerView);
    }

    /**
     * Shows the AlertDialog for entering a new contact and performs validation
     * on the user input.
     *
     * @param contactPosition The position of the contact being edited, -1
     *                        if the user is creating a new contact.
     */
    @SuppressLint("InflateParams")
    private void showAddContactDialog(final int contactPosition) {
        // Inflates the dialog view
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.input_contact_dialog, null);

        mFirstNameEdit = dialogView.findViewById(R.id.edittext_firstname);
        mLastNameEdit = dialogView.findViewById(R.id.edittext_lastname);
        mEmailEdit = dialogView.findViewById(R.id.edittext_email);

        // Listens to text changes to validate after each key press
        mFirstNameEdit.addTextChangedListener(this);
        mLastNameEdit.addTextChangedListener(this);
        mEmailEdit.addTextChangedListener(this);

        // Checks if the user is editing an existing contact
        final boolean editing = contactPosition > -1;

        String dialogTitle = editing ? getString(R.string.edit_contact) :
                getString(R.string.new_contact);

        // Builds the AlertDialog and sets the custom view. Pass null for
        // the positive and negative buttons, as you will override the button
        // presses manually to perform validation before closing the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle(dialogTitle)
                .setPositiveButton(R.string.save, null)
                .setNegativeButton(R.string.cancel, null);

        final AlertDialog dialog = builder.show();

        // If the contact is being edited, populates the EditText with the old
        // information
        if (editing) {
            Contact editedContact = mContacts.get(contactPosition);
            mFirstNameEdit.setText(editedContact.getFirstName());
            mFirstNameEdit.setEnabled(false);
            mLastNameEdit.setText(editedContact.getLastName());
            mLastNameEdit.setEnabled(false);
            mEmailEdit.setText(editedContact.getEmail());
        }
        // Overrides the "Save" button press and check for valid input
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // If input is valid, creates and saves the new contact,
                        // or replaces it if the contact is being edited
                        if (mEntryValid) {
                            if (editing) {
                                Contact editedContact = mContacts
                                        .get(contactPosition);
                                editedContact.setEmail
                                        (mEmailEdit.getText().toString());
                                mContacts.set(contactPosition, editedContact);
                                mAdapter.notifyItemChanged(contactPosition);
                            } else {
                                Contact newContact = new Contact(
                                        mFirstNameEdit.getText().toString(),
                                        mLastNameEdit.getText().toString(),
                                        mEmailEdit.getText().toString()
                                );

                                mContacts.add(newContact);
                                mAdapter.notifyItemInserted(mContacts.size());
                            }
                            saveContacts();
                            dialog.dismiss();
                        } else {
                            // Otherwise, shows an error Toast
                            Toast.makeText(ContactsActivity.this,
                                    R.string.contact_not_valid,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contacts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_clear:
                clearContacts();
                return true;
            case R.id.action_generate:
                generateContacts();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Clears the contacts from SharedPreferences and the adapter, called from
     * the options menu.
     */
    private void clearContacts() {
        mContacts.clear();
        saveContacts();
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Generates mock contact data to populate the UI from a JSON file in the
     * assets directory, called from the options menu.
     */
    private void generateContacts() {
        String contactsString = readContactJsonFile();
        try {
            JSONArray contactsJson = new JSONArray(contactsString);
            for (int i = 0; i < contactsJson.length(); i++) {
                JSONObject contactJson = contactsJson.getJSONObject(i);
                Contact contact = new Contact(
                        contactJson.getString("first_name"),
                        contactJson.getString("last_name"),
                        contactJson.getString("email"));
                Log.d(TAG, "generateContacts: " + contact.toString());
                mContacts.add(contact);
            }

            mAdapter.notifyDataSetChanged();
            saveContacts();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads a file from the assets directory and returns it as a string.
     *
     * @return The resulting string.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private String readContactJsonFile() {
        String contactsString = null;
        try {
            InputStream inputStream = getAssets().open("mock_contacts.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            contactsString = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contactsString;
    }

    /**
     * Override methods for the TextWatcher interface, used to validate user
     * input.
     */


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1,
            int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1,
            int i2) {

    }

    /**
     * Validates the user input when adding a new contact each time the test
     * is changed.
     *
     * @param editable The text that was changed. It is not used as you get the
     *                 text from member variables.
     */
    @Override
    public void afterTextChanged(Editable editable) {
        boolean firstNameValid = !mFirstNameEdit.getText().toString().isEmpty();
        boolean lastNameValid = !mLastNameEdit.getText().toString().isEmpty();
        boolean emailValid = Patterns.EMAIL_ADDRESS
                .matcher(mEmailEdit.getText()).matches();

        Drawable failIcon = ContextCompat.getDrawable(this,
                R.drawable.ic_fail);
        Drawable passIcon = ContextCompat.getDrawable(this,
                R.drawable.ic_pass);

        mFirstNameEdit.setCompoundDrawablesWithIntrinsicBounds(null, null,
                firstNameValid ? passIcon : failIcon, null);
        mLastNameEdit.setCompoundDrawablesWithIntrinsicBounds(null, null,
                lastNameValid ? passIcon : failIcon, null);
        mEmailEdit.setCompoundDrawablesWithIntrinsicBounds(null, null,
                emailValid ? passIcon : failIcon, null);

        mEntryValid = firstNameValid & lastNameValid & emailValid;
    }

    private class ContactsAdapter extends
            RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

        private ArrayList<Contact> mContacts;

        ContactsAdapter(
                ArrayList<Contact> contacts) {
            mContacts = contacts;
        }

        @Override
        public ViewHolder onCreateViewHolder(
                ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.contact_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(
                ViewHolder holder, final int position) {
            final Contact currentContact = mContacts.get(position);
            String fullName = currentContact.getFirstName() + " " + currentContact.getLastName();
            holder.nameLabel.setText(fullName);
            holder.emailLabel.setText(currentContact.getEmail());
        }

        @Override
        public int getItemCount() {
            return mContacts.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView nameLabel;
            TextView emailLabel;

            ViewHolder(View itemView) {
                super(itemView);
                nameLabel = itemView.findViewById(R.id.textview_name);
                emailLabel = itemView.findViewById(R.id.textview_email);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showAddContactDialog(getAdapterPosition());
                    }
                });
            }
        }
    }
}
