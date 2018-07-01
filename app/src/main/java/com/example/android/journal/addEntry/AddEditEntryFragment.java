package com.example.android.journal.addEntry;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.journal.BaseFragment;
import com.example.android.journal.R;
import com.example.android.journal.models.Entry;

import static com.google.android.gms.common.internal.Preconditions.checkNotNull;

public class AddEditEntryFragment extends BaseFragment implements AddEditEntryContract.View {

    public static final String ADD_EDIT_ENTRY_FRAGMENT_TAG = "AEEFTAG";
    private static final String SAVED_ENTRY = "savedEntry";

    // Entry being edited
    private Entry currentEntry;

    private OnFragmentInteractionListener mListener;
    private AddEditEntryContract.Presenter mPresenter;

    // Fields for views
    EditText mEditText;
    Button mButton;

    public AddEditEntryFragment() {}

    public static AddEditEntryFragment newInstance() {
        return new AddEditEntryFragment();
    }

    @Override
    public void setCurrentEntry(Entry entry) {
        currentEntry = entry;
        mEditText.setText(currentEntry.body);
        mButton.setText("UPDATE");
    }

    @Override
    public void showWriteSuccessMessage() {
        Toast.makeText(getContext(), "Entry written successfully.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showWriteFailureMessage() {
        Toast.makeText(getContext(), "Failed to write entry.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(@NonNull AddEditEntryContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(SAVED_ENTRY)) {
            currentEntry = savedInstanceState.getParcelable(SAVED_ENTRY);
            mPresenter.setCurrentEntry(currentEntry);
        }
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_add_entry, container, false);

        initViews(rootView);

        return rootView;
    }

    @Override
    public void showEntryList() {
        mListener.showEntryList();
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
    public String getFragmentTag() {
        return ADD_EDIT_ENTRY_FRAGMENT_TAG;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(currentEntry != null) {
            outState.putParcelable(SAVED_ENTRY, currentEntry);
        }
    }

    /**
     * initViews is called from onCreate to init the member variable views
     */
    private void initViews(View rootView) {
        mEditText = rootView.findViewById(R.id.et_entry_description);

        mButton = rootView.findViewById(R.id.btn_save);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClicked();
            }
        });
    }

    /**
     * onSaveButtonClicked is called when the "save" button is clicked.
     * It retrieves user input and inserts that new Journal data into the underlying database.
     */
    public void onSaveButtonClicked() {
        String body = mEditText.getText().toString();

        mPresenter.saveEntry(body);
    }

    @Override
    public void showSignInUi() {
        mListener.showSignInFragment();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    public interface OnFragmentInteractionListener{
        void showEntryList();

        void showSignInFragment();
    }

}
