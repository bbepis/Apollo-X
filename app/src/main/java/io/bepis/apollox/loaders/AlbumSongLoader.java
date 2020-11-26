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
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.MediaColumns;

import io.bepis.apollox.model.Song;
import io.bepis.apollox.utils.Lists;
import io.bepis.apollox.utils.PreferenceUtils;

/**
 * Used to query {@link MediaStore.Audio.Media.EXTERNAL_CONTENT_URI} and return
 * the Song for a particular album.
 * 
 * @author Andrew Neal (andrewdneal@gmail.com)
 */
public class AlbumSongLoader extends WrappedAsyncTaskLoader<List<Song>> {

    /**
     * The result
     */
    private final ArrayList<Song> mSongList = Lists.newArrayList();

    /**
     * The {@link Cursor} used to run the query.
     */
    private Cursor mCursor;

    /**
     * The Id of the album the songs belong to.
     */
    private final Long mAlbumID;

    /**
     * Constructor of <code>AlbumSongHandler</code>
     * 
     * @param context The {@link Context} to use.
     * @param albumId The Id of the album the songs belong to.
     */
    public AlbumSongLoader(final Context context, final Long albumId) {
        super(context);
        mAlbumID = albumId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Song> loadInBackground() {
        // Create the Cursor
        mCursor = makeAlbumSongCursor(getContext(), mAlbumID);
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

                // Copy the duration
                final long duration = mCursor.getLong(4);
                
                // Make the duration label
                final int seconds = (int) (duration / 1000);

                // Copy the year
                final int year = mCursor.getInt(5);
                
                // Copy the track number
                final int trackNumber = mCursor.getInt(6);
                
                // Create a new song
                final Song song = new Song(id, songName, artist, album, seconds, year, trackNumber);

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
     * @param context The {@link Context} to use.
     * @param albumId The Id of the album the songs belong to.
     * @return The {@link Cursor} used to run the query.
     */
    public static final Cursor makeAlbumSongCursor(final Context context, final Long albumId) {
        // Match the songs up with the artist
        final StringBuilder selection = new StringBuilder();
        selection.append(AudioColumns.IS_MUSIC + "=1");
        selection.append(" AND " + MediaColumns.TITLE + " != ''");
        selection.append(" AND " + AudioColumns.ALBUM_ID + "=" + albumId);
        return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] {
                        /* 0 */
                        BaseColumns._ID,
                        /* 1 */
                        MediaColumns.TITLE,
                        /* 2 */
                        AudioColumns.ARTIST,
                        /* 3 */
                        AudioColumns.ALBUM,
                        /* 4 */
                        AudioColumns.DURATION,
                        /* 5 */
                        AudioColumns.YEAR,
                        /* 6 */
                        AudioColumns.TRACK
                }, selection.toString(), null,
                PreferenceUtils.getInstance(context).getAlbumSongSortOrder());
    }

}
