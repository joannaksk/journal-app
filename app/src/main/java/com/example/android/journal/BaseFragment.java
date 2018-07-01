package com.example.android.journal;


import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.support.annotation.VisibleForTesting;

/**
 * Created by jkisaakye on 30/06/2018.
 */

public abstract class BaseFragment extends Fragment {

    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public abstract String getFragmentTag();

    @Override
    public void onStop() {
        super.onStop();
        dismissProgressDialog();
    }
}
