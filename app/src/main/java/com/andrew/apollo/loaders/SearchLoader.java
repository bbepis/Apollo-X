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

package com.andrew.apollo.loaders;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio.AlbumColumns;
import android.provider.MediaStore.Audio.ArtistColumns;
import android.provider.MediaStore.MediaColumns;
import android.text.TextUtils;

import com.andrew.apollo.model.Song;
import com.andrew.apollo.utils.Lists;

/**
 * @author Andrew Neal (andrewdneal@gmail.com)
 */
public class SearchLoader extends WrappedAsyncTaskLoader<List<Song>> {

    /**
     * The result
     */
    private final ArrayList<Song> mSongList = Lists.newArrayList();

    /**
     * The {@link Cursor} used to run the query.
     */
    private Cursor mCursor;

    /**
     * Constructor of <code>SongLoader</code>
     * 
     * @param context The {@link Context} to use
     * @param query The search query
     */
    public SearchLoader(final Context context, final String query) {
        super(context);
        // Create the Cursor
        mCursor = makeSearchCursor(context, query);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Song> loadInBackground() {
        // Gather the data
        if (mCursor != null && mCursor.moveToFirst()) {
            do {
                // Copy the song Id
                long id = -1;

                // Copy the song name
                final String songName = mCursor.getString(mCursor
                        .getColumnIndexOrThrow(MediaColumns.TITLE));

                // Check for a song Id
                if (!TextUtils.isEmpty(songName)) {
                    id = mCursor.getLong(mCursor
                            .getColumnIndexOrThrow(BaseColumns._ID));
                }

                // Copy the album name
                final String album = mCursor.getString(mCursor
                        .getColumnIndexOrThrow(AlbumColumns.ALBUM));

                // Check for a album Id
                if (id < 0 && !TextUtils.isEmpty(album)) {
                    id = mCursor.getLong(mCursor
                            .getColumnIndexOrThrow(BaseColumns._ID));
                }

                // Copy the artist name
                final String artist = mCursor.getString(mCursor
                        .getColumnIndexOrThrow(ArtistColumns.ARTIST));
                
                // Check for a artist Id
                if (id < 0 && !TextUtils.isEmpty(artist)) {
                    id = mCursor.getLong(mCursor
                            .getColumnIndexOrThrow(BaseColumns._ID));
                }

                // TODO: year and tracknumber...
                
                // Create a new song
                final Song song = new Song(id, songName, artist, album, -1, 0, 0);

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
     * * @param context The {@link Context} to use.
     * 
     * @param query The user's query.
     * @return The {@link Cursor} used to perform the search.
     */
    public static final Cursor makeSearchCursor(final Context context, final String query) {
        return context.getContentResolver().query(
                Uri.parse("content://media/external/audio/search/fancy/" + Uri.encode(query)),
                new String[] {
                        BaseColumns._ID, MediaColumns.MIME_TYPE,
                        ArtistColumns.ARTIST, AlbumColumns.ALBUM,
                        MediaColumns.TITLE, "data1", "data2" //$NON-NLS-2$ 
                }, null, null, null);
    }

}
