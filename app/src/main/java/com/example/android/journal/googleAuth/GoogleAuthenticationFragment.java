package com.example.android.journal.googleAuth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.journal.BaseFragment;
import com.example.android.journal.R;

import static com.example.android.journal.auth.GoogleAuthenticationHelper.RC_SIGN_IN;
import static com.example.android.journal.auth.GoogleAuthenticationHelper.RC_SIGN_UP;
import static com.google.android.gms.common.internal.Preconditions.checkNotNull;

/**
 * Created by jkisaakye on 29/06/2018.
 */

public class GoogleAuthenticationFragment extends BaseFragment implements
        GoogleAuthenticationContract.View,
        View.OnClickListener {

    private static final String GOOGLE_AUTHENTICATION_FRAGMENT_TAG = "GAFTAG";

    private OnFragmentInteractionListener mListener;
    private GoogleAuthenticationContract.Presenter mPresenter;

    public GoogleAuthenticationFragment() {}

    public static GoogleAuthenticationFragment newInstance() {
        return new GoogleAuthenticationFragment();
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_google_authentication, container, false);

        // Button listeners
        rootView.findViewById(R.id.btn_sign_in).setOnClickListener(this);
        rootView.findViewById(R.id.btn_sign_up).setOnClickListener(this);

        setPresenter(mPresenter);
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RC_SIGN_IN:
                mPresenter.authenticate(getActivity(), requestCode, data);
                break;
            default:
                mPresenter.signUpUser(getActivity(), requestCode, data);
                break;
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement GoogleAuthenticationFragment.OnFragmentInteractionListener");
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_sign_in) {
            mPresenter.signIn(getActivity());
        } else if (i == R.id.btn_sign_up) {
            mPresenter.signUp(getActivity());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void setPresenter(GoogleAuthenticationContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void showSignInUi() {
        mListener.showSignInFragment();
    }

    @Override
    public void showEntryList() {
        mListener.showEntryList();
    }

    @Override
    public void showAuthenticationError() {
        Toast.makeText(getActivity(), "Google authentication failed!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSignUpError() {
        Toast.makeText(getActivity(), "Google sign up failed!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void performSignIn(Intent signInIntent) {
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void performSignUp(Intent signUpIntent) {
        startActivityForResult(signUpIntent, RC_SIGN_UP);
    }

    @Override
    public void notifyUserNonExistent() {
        Toast.makeText(getContext(), "Please sign up to use Journal.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public String getFragmentTag() {
        return GOOGLE_AUTHENTICATION_FRAGMENT_TAG;
    }

    public interface OnFragmentInteractionListener{
        void showEntryList();

        void showSignInFragment();
    }
}
