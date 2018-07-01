package com.example.android.journal.entryList;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.example.android.journal.auth.GoogleAuthenticationHelper;
import com.example.android.journal.database.DataSource;
import com.example.android.journal.database.RemoteDataSource;
import com.example.android.journal.models.Entry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.journal.auth.GoogleAuthenticationHelper.getUid;
import static com.google.android.gms.common.internal.Preconditions.checkNotNull;

/**
 * Listens to user actions from the UI ({@link EntryListFragment}), retrieves the data and updates the
 * UI as required.
 */
public class EntryListPresenter implements EntryListContract.Presenter {

    private final DataSource mDataSource;

    private final EntryListContract.View mEntryListView;

    public EntryListPresenter(@NonNull EntryListContract.View mEntryListView) {
        this.mDataSource = checkNotNull(RemoteDataSource.getInstance(), "mDataSource cannot be null");
        this.mEntryListView = checkNotNull(mEntryListView, "mEntryListView cannot be null!");

        mEntryListView.setPresenter(this);
    }

    @Override
    public void start() {
        mEntryListView.showProgressDialog();
    }

    @Override
    public void result(int requestCode, int resultCode) {
        mEntryListView.showSuccessfullySavedMessage();
    }

    @Override
    public Query getRecentEntriesQuery() {
        return RemoteDataSource.getRecentEntriesQuery();
    }


    @Override
    public void addNewEntry() {
        mEntryListView.showAddEditView(null);
    }

    @Override
    public void openEntry(@NonNull Entry entry) {
        checkNotNull(entry, "entry cannot be null!");
        mEntryListView.showAddEditView(entry);
    }

    @Override
    public void deleteEntry(@NonNull Entry entry) {
        mDataSource.deleteEntry(entry, getUid());
    }


    @Override
    public void signOut(Activity mActivity) {
        GoogleAuthenticationHelper.signOut(mActivity, FirebaseAuth.getInstance(), this);
    }

    @Override
    public void switchToFront() {
        mEntryListView.showSignInUi();
    }
}
