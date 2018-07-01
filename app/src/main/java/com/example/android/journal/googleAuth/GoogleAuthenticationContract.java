package com.example.android.journal.googleAuth;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.example.android.journal.BasePresenter;
import com.example.android.journal.BaseView;
import com.example.android.journal.models.Entry;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface GoogleAuthenticationContract {

    interface View extends BaseView<Presenter> {

        void showProgressDialog();

        void dismissProgressDialog();

        void showEntryList();

        void showAuthenticationError();

        void showSignUpError();

        void performSignIn(Intent signInIntent);

        void performSignUp(Intent signUpIntent);

        void notifyUserNonExistent();
    }

    interface Presenter extends BasePresenter {

        void authenticate(Activity mActivity, int requestCode, Intent intent);

        void signUpUser(Activity mActivity, int requestCode, Intent intent);

        void signUp(Activity mActivity);

        void signIn(Activity mActivity);

        void signOut(Activity mActivity);
    }
}
