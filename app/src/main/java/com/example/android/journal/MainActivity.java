/*
* Copyright (C) 2016 The Android Open Source Project
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

package com.example.android.journal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.journal.addEntry.AddEditEntryContract;
import com.example.android.journal.addEntry.AddEditEntryFragment;
import com.example.android.journal.addEntry.AddEditEntryPresenter;
import com.example.android.journal.database.RemoteDataSource;
import com.example.android.journal.entryList.EntryListContract;
import com.example.android.journal.entryList.EntryListFragment;
import com.example.android.journal.entryList.EntryListPresenter;
import com.example.android.journal.entryList.RecentEntriesFragment;
import com.example.android.journal.googleAuth.GoogleAuthenticationContract;
import com.example.android.journal.googleAuth.GoogleAuthenticationFragment;
import com.example.android.journal.googleAuth.GoogleAuthenticationPresenter;
import com.example.android.journal.menu.OptionsMenu;
import com.example.android.journal.models.Entry;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import static com.example.android.journal.auth.GoogleAuthenticationHelper.RC_SIGN_IN;


public class MainActivity extends AppCompatActivity implements
        GoogleAuthenticationFragment.OnFragmentInteractionListener,
        EntryListFragment.OnFragmentInteractionListener,
        AddEditEntryFragment.OnFragmentInteractionListener{

    private BaseFragment CURRENT_FRAGMENT;
    private static final String SAVED_FRAGMENT_TAG = "SFTAG";
    private Entry currentEntry;
    private BasePresenter currentPresenter;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();


        GoogleAuthenticationFragment googleAuthenticationFragment =
                GoogleAuthenticationFragment.newInstance();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, googleAuthenticationFragment,
                            googleAuthenticationFragment.getFragmentTag()).commit();
            CURRENT_FRAGMENT = googleAuthenticationFragment;

            // Instantiate the authentication presenter.
            instantiatePresenter(googleAuthenticationFragment);
        } else {
            String fragmentToRestoreTag = savedInstanceState.getString(SAVED_FRAGMENT_TAG);
            CURRENT_FRAGMENT =
                    (BaseFragment) getSupportFragmentManager().findFragmentByTag(fragmentToRestoreTag);
            instantiatePresenter(CURRENT_FRAGMENT);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVED_FRAGMENT_TAG, CURRENT_FRAGMENT.getFragmentTag());
    }

    @Override
    public void showEntryList() {
        RecentEntriesFragment recentEntriesFragment= RecentEntriesFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, recentEntriesFragment,
                        recentEntriesFragment.getFragmentTag()).commit();
        CURRENT_FRAGMENT = recentEntriesFragment;
        instantiatePresenter(recentEntriesFragment);
    }

    @Override
    public void showSignInFragment() {
        GoogleAuthenticationFragment googleAuthenticationFragment =
            GoogleAuthenticationFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, googleAuthenticationFragment,
                        googleAuthenticationFragment.getFragmentTag()).commit();
        CURRENT_FRAGMENT = googleAuthenticationFragment;

        // Instantiate the authentication presenter.
        instantiatePresenter(googleAuthenticationFragment);
    }

    @Override
    public void openEntry(@Nullable Entry entry) {
        setCurrentEntry(entry);
        AddEditEntryFragment addEditEntryFragment= AddEditEntryFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, addEditEntryFragment,
                        addEditEntryFragment.getFragmentTag()).commit();
        CURRENT_FRAGMENT = addEditEntryFragment;
        instantiatePresenter(addEditEntryFragment);
    }

    public void instantiatePresenter(BaseFragment fragment) {
        if (fragment instanceof GoogleAuthenticationFragment) {
            currentPresenter = new GoogleAuthenticationPresenter((GoogleAuthenticationContract.View) fragment, mAuth);
        } else if (fragment instanceof RecentEntriesFragment) {
            currentPresenter = new EntryListPresenter((EntryListContract.View) fragment);
        } else if (fragment instanceof AddEditEntryFragment) {
            currentPresenter = new AddEditEntryPresenter((AddEditEntryContract.View) fragment, currentEntry);
        }
    }

    public void setCurrentEntry(Entry currentEntry) {
        this.currentEntry = currentEntry;
    }

    @Override
    public void onBackPressed() {
        // Get the first fragment, there's only ever one cause they keep being replaced.
        switch (CURRENT_FRAGMENT.getFragmentTag()) {
            case AddEditEntryFragment.ADD_EDIT_ENTRY_FRAGMENT_TAG:
                showEntryList();
                break;
            default:
                super.onBackPressed();
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (OptionsMenu.onOptionsItemSelected(this, currentPresenter, item)) {
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
