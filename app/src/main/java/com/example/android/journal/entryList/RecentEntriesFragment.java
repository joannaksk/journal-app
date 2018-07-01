package com.example.android.journal.entryList;

import com.google.firebase.database.Query;

public class RecentEntriesFragment extends EntryListFragment {
    private static final String RECENT_ENTRIES_FRAGMENT_TAG = "REFTAG";

    public RecentEntriesFragment() {}

    public static RecentEntriesFragment newInstance() {
        return new RecentEntriesFragment();
    }

    @Override
    public Query getQuery() {
        return getRecentEntriesQuery();
    }

    private Query getRecentEntriesQuery() {
        return mPresenter.getRecentEntriesQuery();
    }

    @Override
    public String getFragmentTag() {
        return RECENT_ENTRIES_FRAGMENT_TAG;
    }
}
