/*
 * Copyright (C) 2012 Andrew Neal Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package io.bepis.apollox.ui.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import io.bepis.apollox.R;
import io.bepis.apollox.ui.fragments.phone.MusicBrowserPhoneFragment;

/**
 * This class is used to display the {@link ViewPager} used to swipe between the
 * main {@link Fragment}s used to browse the user's music.
 * 
 * @author Andrew Neal (andrewdneal@gmail.com)
 */
public class HomeActivity extends BaseActivity {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    898);
        }

        // Load the music browser fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_base_content, new MusicBrowserPhoneFragment()).commit();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int setContentView() {
        return R.layout.activity_base;
    }
}
