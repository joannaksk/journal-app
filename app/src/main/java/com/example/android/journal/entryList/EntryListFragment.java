package com.example.android.journal.entryList;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.journal.BaseFragment;
import com.example.android.journal.R;
import com.example.android.journal.googleAuth.GoogleAuthenticationFragment;
import com.example.android.journal.models.Entry;
import com.example.android.journal.viewholder.EntryViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.List;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;
import static com.example.android.journal.auth.GoogleAuthenticationHelper.getUid;
import static com.google.android.gms.common.internal.Preconditions.checkNotNull;

public abstract class EntryListFragment extends BaseFragment implements EntryListContract.View {

    protected EntryListContract.Presenter mPresenter;

    private FirebaseRecyclerAdapter<Entry, EntryViewHolder> mAdapter;

    private RecyclerView mRecyclerView;

    private LinearLayoutManager mManager;

    private OnFragmentInteractionListener mListener;

    public EntryListFragment() {}

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mManager);

        // Initialize the adapter and attach it to the RecyclerView
        // Set up FirebaseRecyclerAdapter with the Query
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Entry>()
                .setQuery(getQuery(), Entry.class)
                .build();

        mAdapter = new FirebaseRecyclerAdapter<Entry, EntryViewHolder>(options) {

            @Override
            public EntryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new EntryViewHolder(inflater.inflate(R.layout.entry_layout, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(EntryViewHolder viewHolder, int position, final Entry entry) {
                final DatabaseReference entryRef = getRef(position);

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPresenter.openEntry(entry);
                    }
                });

                // Bind Post to ViewHolder.
                viewHolder.bindToEntry(entry);
            }
        };

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                dismissProgressDialog();
                mAdapter.unregisterAdapterDataObserver(this);
            }
        });

        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(
                getActivity().getApplicationContext(), VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                Entry entryToDelete = (Entry) mAdapter.getItem(position);
                mPresenter.deleteEntry(entryToDelete);
            }
        }).attachToRecyclerView(mRecyclerView);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement EntryListFragment.OnFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_all_entries, container, false);

        // Set the RecyclerView to its corresponding view
        mRecyclerView = rootView.findViewById(R.id.rv_entries);
        mRecyclerView.setHasFixedSize(true);

        FloatingActionButton fabButton = rootView.findViewById(R.id.fab_add_entry);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.addNewEntry();
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public abstract Query getQuery();

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showAddEditView(@Nullable Entry entry) {
        mListener.openEntry(entry);
    }

    @Override
    public void showSuccessfullySavedMessage() {
        Toast.makeText(getActivity(), "Entry saved successfully.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSignInUi() {
        mListener.showSignInFragment();
    }

    @Override
    public void setPresenter(EntryListContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    public interface OnFragmentInteractionListener{
        void openEntry(@Nullable Entry entry);

        void showSignInFragment();
    }

}
