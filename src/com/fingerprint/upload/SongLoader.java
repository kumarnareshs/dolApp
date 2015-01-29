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

package com.fingerprint.upload;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;

/**
 * Used to query {@link MediaStore.Audio.Media.EXTERNAL_CONTENT_URI} and return
 * the songs for a particular artist.
 * 
 * @author Andrew Neal (andrewdneal@gmail.com)
 */
public class SongLoader  {

    /**
     * The result
     */
    private final List<Song> mSongList =new ArrayList<Song>();

    /**
     * The {@link Cursor} used to run the query.
     */
    private Cursor mCursor;

    /**
     * {@inheritDoc}
     */
    public List<Song> loadInBackground(Context context) {
        // Create the Cursor
        mCursor = makeArtistSongCursor(context );
        // Gather the data
        //TODO: remove this
        int i=0;
        if (mCursor != null && mCursor.moveToFirst()) {
            do {
            	i++;
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
                
                final String Path = mCursor.getString(5);
                
                final String MIMEType = mCursor.getString(6);

                // Convert the duration into seconds
                final int durationInSecs = (int) duration / 1000;

                // Create a new song
                final Song song = new Song(id, songName, artist, album, durationInSecs,Path,MIMEType);

                // Add everything up
                mSongList.add(song);
            } while (mCursor.moveToNext()&&i<11);
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
     * @param artistId The Id of the artist the songs belong to.
     * @return The {@link Cursor} used to run the query.
     */
    public static final Cursor makeArtistSongCursor(final Context context) {
    	 final Long artistId = null;
        // Match the songs up with the artist
        final StringBuilder selection = new StringBuilder();
        selection.append(AudioColumns.IS_MUSIC + "=1");
        selection.append(" AND " + AudioColumns.TITLE + " != ''");
      //  selection.append(" AND " + AudioColumns.ARTIST_ID + "=" + artistId);
        return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] {
                        /* 0 */
                        BaseColumns._ID,
                        /* 1 */
                        AudioColumns.TITLE,
                        /* 2 */
                        AudioColumns.ARTIST,
                        /* 3 */
                        AudioColumns.ALBUM,
                        /* 4 */
                        AudioColumns.DURATION,
                        AudioColumns.DATA,
                        AudioColumns.MIME_TYPE
                }, selection.toString(), null,
                null);
    }

}
