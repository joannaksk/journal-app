package com.example.android.journal.menu;

import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.android.journal.BasePresenter;
import com.example.android.journal.R;

/**
 * Created by jkisaakye on 30/06/2018.
 */

public class OptionsMenu {
    public static boolean onOptionsItemSelected(AppCompatActivity mActivity, BasePresenter presenter, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sign_out:
                presenter.signOut(mActivity);
                return true;
        }
        return false;
    }
}
