package com.andrew.apollo.widgets;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.MediaColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.andrew.apollo.R;
import com.andrew.apollo.model.Song;
import com.andrew.apollo.utils.MusicUtils;
import com.andrew.apollo.utils.NavUtils;

public class TagEditorDialog extends AlertDialog implements TextWatcher {
	private LayoutInflater mInflater;
	private View mRootView;
	
	private Button mOkButton;
	
	private EditText mArtistValue;
	private EditText mTrackTitleValue;
	private EditText mAlbumValue;
	private EditText mYearValue;
	private EditText mCDNumberValue;
	private EditText mTrackNumberValue;
	private Song mDialogSong;
	
	private Context mContext;
	
	private List<EditText> editControls = new ArrayList<EditText>();
	
	public TagEditorDialog(final Context context, final Song mSong) {
		super(context);
		mContext = context;
		
		mDialogSong = mSong;
		
		this.setButton(DialogInterface.BUTTON_NEGATIVE, mContext.getString(R.string.cancel), (OnClickListener) null);
		this.setButton(DialogInterface.BUTTON_POSITIVE, mContext.getString(android.R.string.ok), new OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                saveTags();
            }
        });
		
		this.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface arg0) {
				mOkButton = getButton(DialogInterface.BUTTON_POSITIVE);
			}
		});
		
		setupDialog();
	}
	
	private void setupDialog() {
		mInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = mInflater.inflate(R.layout.song_tags_editor, null);
        
        mArtistValue = (EditText)mRootView.findViewById(R.id.tag_edit_dialog_artist_value);
        mTrackTitleValue = (EditText)mRootView.findViewById(R.id.tag_edit_dialog_title_value);
        mAlbumValue = (EditText)mRootView.findViewById(R.id.tag_edit_dialog_album_value);
        mYearValue = (EditText)mRootView.findViewById(R.id.tag_edit_dialog_year_value);
        mCDNumberValue = (EditText)mRootView.findViewById(R.id.tag_edit_dialog_cd_number_value);
        mTrackNumberValue = (EditText)mRootView.findViewById(R.id.tag_edit_dialog_track_number_value);
        
        editControls.add(mArtistValue);
        editControls.add(mTrackTitleValue);
        editControls.add(mAlbumValue);
        editControls.add(mCDNumberValue);
        editControls.add(mTrackNumberValue);
        
        mArtistValue.setText(mDialogSong.mArtistName);
        mTrackTitleValue.setText(mDialogSong.mSongName);
        mAlbumValue.setText(mDialogSong.mAlbumName);
        
        mYearValue.setText((mDialogSong.mYear == 0 ? "" : Integer.toString(mDialogSong.mYear)));
        mTrackNumberValue.setText(Integer.toString(mDialogSong.mTrackNumber));
        mCDNumberValue.setText(Integer.toString(mDialogSong.mCdNumber));
        
        mArtistValue.addTextChangedListener(this);
        mTrackTitleValue.addTextChangedListener(this);
        mAlbumValue.addTextChangedListener(this);
        mYearValue.addTextChangedListener(this);
        mCDNumberValue.addTextChangedListener(this);
        mTrackNumberValue.addTextChangedListener(this);
        
        setTitle(getContext().getResources().getString(R.string.tag_editor_title));
        setView(mRootView);
	}

	public void saveTags() {
		final ContentResolver resolver = mContext.getContentResolver();
        final ContentValues values = new ContentValues(5);
        
        if(!mYearValue.getText().toString().equals("")) {
        	mDialogSong.mYear = Integer.parseInt(mYearValue.getText().toString());
        	values.put(AudioColumns.YEAR, mYearValue.getText().toString());
        } else {
        	values.putNull(AudioColumns.YEAR);
        	mDialogSong.mYear = 0;
        }
   
        values.put(AudioColumns.ARTIST, mArtistValue.getText().toString());
        mDialogSong.mArtistName = mArtistValue.getText().toString();
        
        values.put(MediaColumns.TITLE, mTrackTitleValue.getText().toString());
        mDialogSong.mSongName = mTrackTitleValue.getText().toString();
        
        if(!mDialogSong.mAlbumName.equals(mAlbumValue.getText().toString())) {
        	values.put(AudioColumns.ALBUM, mAlbumValue.getText().toString());
        	mDialogSong.mAlbumName = mAlbumValue.getText().toString();
        }
        
        int trackValue = (Integer.valueOf(mCDNumberValue.getText().toString()) * 1000) + Integer.valueOf(mTrackNumberValue.getText().toString());
        values.put(AudioColumns.TRACK, String.valueOf(trackValue));
        
        mDialogSong.mTrackNumber = Integer.valueOf(mTrackNumberValue.getText().toString());
        mDialogSong.mCdNumber = Integer.valueOf(mCDNumberValue.getText().toString());
        
        resolver.update(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values,
                BaseColumns._ID + "=?", new String[] {
                    String.valueOf(mDialogSong.mSongId)
                }
        );
        
        NavUtils.goHome((Activity)mContext);
        
        mContext.getContentResolver().notifyChange(Uri.parse("content://media"), null);
        MusicUtils.refresh();
	}

	@Override
	public void afterTextChanged(Editable s) {
		if(areInputsFilled()) {
			mOkButton.setEnabled(true);
		} else {
			mOkButton.setEnabled(false);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}
	
	private boolean areInputsFilled() {
		for(EditText et : editControls) {
			if(et.length() == 0)
				return false;
		}
		return true;
	}
}
