package com.example.android.journal.viewholder;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;

import com.example.android.journal.R;
import com.example.android.journal.models.Entry;

public class EntryViewHolder extends RecyclerView.ViewHolder {

    public TextView bodyView;
    public TextView updatedAtView;
    public TextView authorView;

    public EntryViewHolder(View itemView) {
        super(itemView);

        authorView = itemView.findViewById(R.id.entryAuthor);
        bodyView = itemView.findViewById(R.id.entryBody);
        updatedAtView = itemView.findViewById(R.id.entryUpdatedAt);
    }

    public void bindToEntry(Entry entry) {
        authorView.setText(entry.author);
        bodyView.setText(entry.body);
        updatedAtView.setText(entry.updatedAt.toString());
    }
}
