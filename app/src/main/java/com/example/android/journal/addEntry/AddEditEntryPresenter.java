package com.example.android.journal.addEntry;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.journal.auth.GoogleAuthenticationHelper;
import com.example.android.journal.database.DataSource;
import com.example.android.journal.database.RemoteDataSource;
import com.example.android.journal.models.Entry;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;

import static com.example.android.journal.auth.GoogleAuthenticationHelper.getUid;
import static com.example.android.journal.auth.GoogleAuthenticationHelper.getUname;
import static com.google.android.gms.common.internal.Preconditions.checkNotNull;

/**
 * Listens to user actions from the UI ({@link AddEditEntryFragment}), retrieves the data and updates
 * the UI as required.
 */
public class AddEditEntryPresenter implements AddEditEntryContract.Presenter {

    private final DataSource mDataSource;

    private final AddEditEntryContract.View mAddEntryView;

    @Nullable
    private Entry mEntry;

    /**
     * Creates a presenter for the add/edit view.
     *
     * @param mEntry to edit or null for a new entry
     * @param mAddEntryView the add/edit view
     */
    public AddEditEntryPresenter(@NonNull AddEditEntryContract.View mAddEntryView,
                                 @Nullable Entry mEntry) {
        this.mEntry = mEntry;
        this.mDataSource = checkNotNull(RemoteDataSource.getInstance(), "mDataSource cannot be null");
        this.mAddEntryView = checkNotNull(mAddEntryView);

        mAddEntryView.setPresenter(this);
    }

    @Override
    public void start() {
        if (!isNewEntry()) {
            populateEntry();
        }
    }

    @Override
    public void signOut(Activity mActivity) {
        GoogleAuthenticationHelper.signOut(mActivity, FirebaseAuth.getInstance(), this);
    }

    @Override
    public void switchToFront() {
        mAddEntryView.showSignInUi();
    }

    @Override
    public void saveEntry(String body) {
        if (isNewEntry()) {
            createEntry(body);
        } else {
            updateEntry(body);
        }
    }

    @Override
    public void populateEntry() {
        if (isNewEntry()) {
            throw new RuntimeException("populateEntry() was called but entry is new.");
        }
        mAddEntryView.setCurrentEntry(mEntry);
    }

    private boolean isNewEntry() {
        return mEntry == null;
    }

    @Override
    public void setCurrentEntry(Entry entry) {
        mEntry = entry;
    }

    private void createEntry(String body) {
        Date date = new Date();
        Entry entry = new Entry(getUid(), getUname(), body, date);
        mDataSource.writeNewEntry(entry, getCallback());
    }

    private void updateEntry(String body) {
        if (isNewEntry()) {
            throw new RuntimeException("updateEntry() was called but entry is new.");
        }
        Date date = new Date();
        mEntry.body = body;
        mEntry.updatedAt = date;
        mDataSource.updateEntry(mEntry, getCallback());
    }

    private DataSource.WriteEntryCallback getCallback() {
        return new DataSource.WriteEntryCallback() {
            @Override
            public void onWriteSuccess() {
                mAddEntryView.showWriteSuccessMessage();
                mAddEntryView.showEntryList();
            }

            @Override
            public void onWriteFailure() {
                mAddEntryView.showWriteFailureMessage();
                mAddEntryView.showEntryList();
            }
        };
    }
}
