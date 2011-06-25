/*
 * Copyright (C) 2011 The Android Open Source Project
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

package android.inputmethodservice;

import com.android.internal.view.menu.MenuBuilder;
import com.android.internal.view.menu.MenuPopupHelper;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * ExtractEditLayout provides an ActionMode presentation for the
 * limited screen real estate in extract mode.
 *
 * @hide
 */
public class ExtractEditLayout extends LinearLayout {
    ExtractActionMode mActionMode;
    Button mExtractActionButton;
    Button mEditButton;

    public ExtractEditLayout(Context context) {
        super(context);
    }

    public ExtractEditLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public ActionMode startActionModeForChild(View sourceView, ActionMode.Callback cb) {
        final ExtractActionMode mode = new ExtractActionMode(cb);
        if (mode.dispatchOnCreate()) {
            mode.invalidate();
            mExtractActionButton.setVisibility(INVISIBLE);
            mEditButton.setVisibility(VISIBLE);
            mActionMode = mode;
            return mode;
        }
        return null;
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        mExtractActionButton = (Button) findViewById(com.android.internal.R.id.inputExtractAction);
        mEditButton = (Button) findViewById(com.android.internal.R.id.inputExtractEditButton);
        mEditButton.setOnClickListener(new OnClickListener() {
            public void onClick(View clicked) {
                if (mActionMode != null) {
                    new MenuPopupHelper(getContext(), mActionMode.mMenu, clicked).show();
                }
            }
        });
    }

    private class ExtractActionMode extends ActionMode implements MenuBuilder.Callback {
        private ActionMode.Callback mCallback;
        MenuBuilder mMenu;

        public ExtractActionMode(Callback cb) {
            mMenu = new MenuBuilder(getContext());
            mMenu.setCallback(this);
            mCallback = cb;
        }

        @Override
        public void setTitle(CharSequence title) {
            // Title will not be shown.
        }

        @Override
        public void setTitle(int resId) {
            // Title will nor be shown.
        }

        @Override
        public void setSubtitle(CharSequence subtitle) {
            // Subtitle will not be shown.
        }

        @Override
        public void setSubtitle(int resId) {
            // Subtitle will not be shown.
        }

        @Override
        public void setCustomView(View view) {
            // Custom view is not supported here.
        }

        @Override
        public void invalidate() {
            mMenu.stopDispatchingItemsChanged();
            try {
                mCallback.onPrepareActionMode(this, mMenu);
            } finally {
                mMenu.startDispatchingItemsChanged();
            }
        }

        public boolean dispatchOnCreate() {
            mMenu.stopDispatchingItemsChanged();
            try {
                return mCallback.onCreateActionMode(this, mMenu);
            } finally {
                mMenu.startDispatchingItemsChanged();
            }
        }

        @Override
        public void finish() {
            if (mActionMode != this) {
                // Not the active action mode - no-op
                return;
            }

            mCallback.onDestroyActionMode(this);
            mCallback = null;

            mExtractActionButton.setVisibility(VISIBLE);
            mEditButton.setVisibility(INVISIBLE);

            mActionMode = null;
        }

        @Override
        public Menu getMenu() {
            return mMenu;
        }

        @Override
        public CharSequence getTitle() {
            return null;
        }

        @Override
        public CharSequence getSubtitle() {
            return null;
        }

        @Override
        public View getCustomView() {
            return null;
        }

        @Override
        public MenuInflater getMenuInflater() {
            return new MenuInflater(getContext());
        }

        @Override
        public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
            return mCallback.onActionItemClicked(this, item);
        }

        @Override
        public void onMenuModeChange(MenuBuilder menu) {
        }

    }
}