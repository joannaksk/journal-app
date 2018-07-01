package com.example.android.journal.googleAuth;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.example.android.journal.addEntry.AddEditEntryFragment;
import com.example.android.journal.auth.GoogleAuthenticationHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.google.android.gms.common.internal.Preconditions.checkNotNull;

/**
 * Listens to user actions from the UI ({@link AddEditEntryFragment}), retrieves the data and updates
 * the UI as required.
 */
public class GoogleAuthenticationPresenter implements GoogleAuthenticationContract.Presenter {

    private final GoogleAuthenticationContract.View mAuthView;
    private FirebaseAuth mAuth;

    /**
     * Creates a presenter for the add/edit view.
     *
     * @param mAuthView the authentication view
     */
    public GoogleAuthenticationPresenter(
                                         @NonNull GoogleAuthenticationContract.View mAuthView,
                                         @NonNull FirebaseAuth mAuth) {
        this.mAuthView = checkNotNull(mAuthView);
        this.mAuth = mAuth;
        mAuthView.setPresenter(this);
    }

    @Override
    public void authenticate(Activity mActivity, int requestCode, Intent intent) {
        GoogleAuthenticationHelper.authenticate(
                mActivity,
                mAuth,
                requestCode,
                intent,
                new GoogleAuthenticationHelper.AuthCompletionCallback() {
                    @Override
                    public void onSuccess() {
                        mAuthView.dismissProgressDialog();
                        mAuthView.showEntryList();
                    }

                    @Override
                    public void onFailure() {
                        mAuthView.dismissProgressDialog();
                        mAuthView.showAuthenticationError();
                    }

                    @Override
                    public void onUserNonExistent() {
                        mAuthView.dismissProgressDialog();
                        mAuthView.notifyUserNonExistent();
                    }
                });
    }


    @Override
    public void signUpUser(Activity mActivity, int requestCode, Intent intent) {
        GoogleAuthenticationHelper.signUp(
                mActivity,
                mAuth,
                requestCode,
                intent,
                new GoogleAuthenticationHelper.SignUpCompletionCallback() {
                    @Override
                    public void onSuccess() {
                        mAuthView.dismissProgressDialog();
                        mAuthView.showEntryList();
                    }

                    @Override
                    public void onFailure() {
                        mAuthView.dismissProgressDialog();
                        mAuthView.showSignUpError();
                    }
                });
    }


    @Override
    public void signUp(Activity mActivity) {
        mAuthView.showProgressDialog();
        Intent signUpIntent = GoogleAuthenticationHelper.getSignUpIntent(mActivity);
        mAuthView.performSignUp(signUpIntent);
    }

    @Override
    public void signIn(Activity mActivity) {
        mAuthView.showProgressDialog();
        Intent signInIntent = GoogleAuthenticationHelper.getSignInIntent(mActivity);
        mAuthView.performSignIn(signInIntent);
    }

    @Override
    public void signOut(Activity mActivity) {
        GoogleAuthenticationHelper.signOut(mActivity, mAuth, this);
    }

    @Override
    public void switchToFront() {
        mAuthView.showSignInUi();
    }

    @Override
    public void start() {
//        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
}
