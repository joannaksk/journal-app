package com.example.android.journal.database;

import android.support.annotation.NonNull;

import com.example.android.journal.models.Entry;
import com.example.android.journal.models.User;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.example.android.journal.auth.GoogleAuthenticationHelper.getUid;

/**
 * Created by jkisaakye on 29/06/2018.
 */

public class RemoteDataSource implements DataSource{

    private static RemoteDataSource INSTANCE;

    public static RemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RemoteDataSource();
        }
        return INSTANCE;
    }

    // Prevent direct instantiation.
    private RemoteDataSource() {}

    public static Query getRecentEntriesQuery() {
        String myUserId = getUid();
        return FirebaseDatabase.getInstance().getReference().child("user-entries").child(myUserId)
                .limitToFirst(100);
    }

    @Override
    public void userExists(String userId, final UserExistanceCallback callback) {
        DatabaseReference mDb = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        mDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                callback.onUserExists();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onUserNonExistent();
            }
        });
    }

    @Override
    public void writeNewUser(String userId, String name, String email) {
        DatabaseReference mDb = FirebaseDatabase.getInstance().getReference();
        User user = new User(name, email);
        mDb.child("users").child(userId).setValue(user);
    }

    @Override
    public void deleteEntry(Entry entry, String userId) {
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        // Delete entry at /user-entries/$userId/$entryId and at
        // /entries/$entryId simultaneously
        String key = entry.id;
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/entries/" + key, null);
        childUpdates.put("/user-entries/" + userId + "/" + key, null);
        mDatabaseReference.updateChildren(childUpdates);
    }

    @Override
    public void writeNewEntry(Entry entry, final WriteEntryCallback callback) {
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        // Create new entry at /user-entries/$userId/$entryId and at
        // /entries/$entryId simultaneously
        String key = mDatabaseReference.child("entries").push().getKey();
        entry.id = key;
        Map<String, Object> entryValues = entry.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/entries/" + key, entryValues);
        childUpdates.put("/user-entries/" + getUid() + "/" + key, entryValues);

        mDatabaseReference.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                callback.onWriteSuccess();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onWriteFailure();
            }
        }). addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                callback.onWriteFailure();
            }
        });
    }

    @Override
    public void updateEntry(Entry entry, final WriteEntryCallback callback) {
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        // Update entry at /user-entries/$userId/$entryId and at
        // /entries/$entryId simultaneously
        Map<String, Object> entryValues = entry.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/entries/" + entry.id, entryValues);
        childUpdates.put("/user-entries/" + getUid() + "/" + entry.id, entryValues);
        mDatabaseReference.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                callback.onWriteSuccess();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onWriteFailure();
            }
        }). addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                callback.onWriteFailure();
            }
        });
    }
}
