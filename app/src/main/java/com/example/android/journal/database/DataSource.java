package com.example.android.journal.database;

import android.support.annotation.NonNull;

import com.example.android.journal.models.Entry;

import java.util.List;

/**
 * Main entry point for accessing entry data.
 * <p>
 */
public interface DataSource {

    interface WriteEntryCallback {

        void onWriteSuccess();

        void onWriteFailure();
    }

    interface UserExistanceCallback {

        void onUserExists();

        void onUserNonExistent();
    }

    void userExists(String userId, UserExistanceCallback callback);

    void writeNewUser(@NonNull String userId, @NonNull String name, @NonNull String email);

    void deleteEntry(@NonNull Entry entry, @NonNull String userId);

    void writeNewEntry(@NonNull Entry entry, @NonNull WriteEntryCallback callback);

    void updateEntry(@NonNull Entry entry, @NonNull WriteEntryCallback callback);
}
