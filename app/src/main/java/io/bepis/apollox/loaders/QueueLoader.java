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

package io.bepis.apollox.loaders;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;

import io.bepis.apollox.model.Song;
import io.bepis.apollox.utils.Lists;

/**
 * Used to return the current playlist or queue.
 * 
 * @author Andrew Neal (andrewdneal@gmail.com)
 */
public class QueueLoader extends WrappedAsyncTaskLoader<List<Song>> {

    /**
     * The result
     */
    private final ArrayList<Song> mSongList = Lists.newArrayList();

    /**
     * The {@link Cursor} used to run the query.
     */
    private NowPlayingCursor mCursor;

    /**
     * Constructor of <code>QueueLoader</code>
     * 
     * @param context The {@link Context} to use
     */
    public QueueLoader(final Context context) {
        super(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Song> loadInBackground() {
        // Create the Cursor
        mCursor = new NowPlayingCursor(getContext());
        // Gather the data
        if (mCursor != null && mCursor.moveToFirst()) {
            do {
                // Copy the song Id
                final long id = mCursor.getLong(0);

                // Copy the song name
                final String songName = mCursor.getString(1);

                // Copy the artist name
                final String artist = mCursor.getString(2);

                // Copy the album name
                final String album = mCursor.getString(3);
                
                // Copy the song year
                final int year = mCursor.getInt(4);
                
                // Copy the track number
                final int trackNumber = mCursor.getInt(5);
                
                // Create a new song
                final Song song = new Song(id, songName, artist, album, -1, year, trackNumber);

                // Add everything up
                mSongList.add(song);
            } while (mCursor.moveToNext());
        }
        // Close the cursor
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
        return mSongList;
    }

    /**
     * Creates the {@link Cursor} used to run the query.
     * 
     * @param context The {@link Context} to use.
     * @return The {@link Cursor} used to run the song query.
     */
    public static final Cursor makeQueueCursor(final Context context) {
        final Cursor cursor = new NowPlayingCursor(context);
        return cursor;
    }
}
