package com.example.android.journal.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.journal.BasePresenter;
import com.example.android.journal.R;
import com.example.android.journal.database.DataSource;
import com.example.android.journal.database.RemoteDataSource;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


/**
 * Created by jkisaakye on 29/06/2018.
 */

public class GoogleAuthenticationHelper {
    private static final String LOG_TAG = GoogleAuthenticationHelper.class.getSimpleName();
    public static final int RC_SIGN_IN = 9001;
    public static final int RC_SIGN_UP = 9002;

    public interface AuthCompletionCallback {

        void onSuccess();

        void onFailure();

        void onUserNonExistent();
    }

    public interface SignUpCompletionCallback {

        void onSuccess();

        void onFailure();
    }

    private static String getUsernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    private static GoogleSignInClient configureGoogleSignIn(Context mContext) {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(mContext.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        return GoogleSignIn.getClient(mContext, gso);
    }

    private static void onAuthSuccess(Activity mActivity, FirebaseUser user, AuthCompletionCallback callback) {
        callback.onSuccess();
    }

    private static void onSignUpSuccess(Activity mActivity, FirebaseUser user, SignUpCompletionCallback callback) {
        callback.onSuccess();
    }

    private static void performFirebaseAuthWithGoogle(final Activity mActivity,
                                                      final FirebaseAuth mAuth,
                                                      GoogleSignInAccount acct,
                                                      final AuthCompletionCallback callback) {
        Log.d(LOG_TAG, "performFirebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(LOG_TAG, "signInWithCredential:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            RemoteDataSource.getInstance().userExists(user.getUid(), new DataSource.UserExistanceCallback() {
                                @Override
                                public void onUserExists() {
                                    onAuthSuccess(mActivity, user, callback);
                                }

                                @Override
                                public void onUserNonExistent() {
                                    Log.w(LOG_TAG, "signInWithCredential:failure User does not exist in data source");
                                    callback.onUserNonExistent();
                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(LOG_TAG, "signInWithCredential:failure", task.getException());
                            callback.onFailure();
                        }
                    }
                });
    }

    private static void performFirebaseSignUpWithGoogle(final Activity mActivity,
                                                      final FirebaseAuth mAuth,
                                                      GoogleSignInAccount acct,
                                                      final SignUpCompletionCallback callback) {
        Log.d(LOG_TAG, "performFirebaseSignUpWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(LOG_TAG, "signUpWithCredential:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            RemoteDataSource.getInstance().writeNewUser(
                                    user.getUid(),
                                    user.getDisplayName(),
                                    user.getEmail());
                            callback.onSuccess();


                        } else {
                            // If sign up fails, display a message to the user.
                            Log.w(LOG_TAG, "signUpWithCredential:failure", task.getException());
                            callback.onFailure();
                        }
                    }
                });
    }

    public static void authenticate(Activity mActivity,
                                    final FirebaseAuth mAuth,
                                    int requestCode,
                                    Intent intent,
                                    final AuthCompletionCallback callback) {
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                performFirebaseAuthWithGoogle(mActivity, mAuth, account, callback);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(LOG_TAG, "Google sign in failed", e);
            }
        }
    }

    public static void signUp(Activity mActivity,
                                    final FirebaseAuth mAuth,
                                    int requestCode,
                                    Intent intent,
                                    final SignUpCompletionCallback callback) {
        // Result returned from launching the Intent from GoogleSignInApi.getSignUpIntent(...);
        if (requestCode == RC_SIGN_UP) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
            try {
                // Google Sign Up was successful, sign up with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                performFirebaseSignUpWithGoogle(mActivity, mAuth, account, callback);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(LOG_TAG, "Google sign in failed", e);
            }
        }
    }

    public static Intent getSignInIntent(Activity mActivity) {
        return configureGoogleSignIn(mActivity).getSignInIntent();
    }

    public static Intent getSignUpIntent(Activity mActivity) {
        return configureGoogleSignIn(mActivity).getSignInIntent();
    }

    public static void signOut(Activity mActivity, FirebaseAuth mAuth, final BasePresenter mPresenter) {
        mAuth.signOut();

        // Google sign out
        configureGoogleSignIn(mActivity).signOut().addOnCompleteListener(mActivity,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mPresenter.switchToFront();
                    }
                });
    }

    public static String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public static String getUname() {
        return FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
    }
}
