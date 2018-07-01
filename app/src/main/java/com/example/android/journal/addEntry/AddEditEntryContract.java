/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.journal.addEntry;

import com.example.android.journal.BasePresenter;
import com.example.android.journal.BaseView;
import com.example.android.journal.models.Entry;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface AddEditEntryContract {

    interface View extends BaseView<Presenter> {

        void setCurrentEntry(Entry entry);

        void showWriteSuccessMessage();

        void showWriteFailureMessage();

        void showEntryList();

        boolean isActive();
    }

    interface Presenter extends BasePresenter {

        void setCurrentEntry(Entry entry);

        void saveEntry(String body);

        void populateEntry();
    }
}
