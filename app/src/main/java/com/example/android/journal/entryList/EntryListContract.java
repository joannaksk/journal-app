package com.example.android.journal.entryList;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.journal.BasePresenter;
import com.example.android.journal.BaseView;
import com.example.android.journal.models.Entry;
import com.google.firebase.database.Query;

import java.util.List;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface EntryListContract {

    interface View extends BaseView<Presenter> {

        void showProgressDialog();

        void dismissProgressDialog();

        void showAddEditView(@Nullable Entry entry);

        void showSuccessfullySavedMessage();

        boolean isActive();
    }

    interface Presenter extends BasePresenter {

        void result(int requestCode, int resultCode);

        Query getRecentEntriesQuery();

        void addNewEntry();

        void openEntry(@NonNull Entry entry);

        void deleteEntry(@NonNull Entry entry);
    }
}
