package com.hlidskialf.android.tonepicker;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.CheckedTextView;
import android.widget.TextView;
import java.io.IOException;

public class TonePicker extends ExpandableListActivity 
        implements View.OnClickListener
{
  public static final int REQUEST_GET_CONTENT=1;

  private TonePickerAdapter mAdapter;
  private ExpandableListView mListView;
  private LayoutInflater mInflater;

  private Intent mInitialIntent;
  private Uri mSelectedUri;
  private MediaPlayer mMediaPlayer;


  private class HeaderViewHolder
  {
    CheckedTextView text1;
    Uri uri;
  }
  private HeaderViewHolder mHeaderSilent, mHeaderExisting, mHeaderDefault;
  private TextView mFooterOthers;

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.tonepicker);

    Button b;
    b = (Button)findViewById(android.R.id.button1);
    b.setOnClickListener(new Button.OnClickListener() {
      public void onClick(View v) {
        finishWithUri(mSelectedUri);
      }
    });
    b = (Button)findViewById(android.R.id.button2);
    b.setOnClickListener(new Button.OnClickListener() {
      public void onClick(View v) {
        finish();
      }
    });

    Intent intent = getIntent();
    Uri existing_uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI);
    Uri default_uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI);
    //boolean show_bragi_slots = ! intent.getBooleanExtra(Bragi.EXTRA_STARTED_FROM_BRAGI, false);


    mInflater = getLayoutInflater();
    mListView = getExpandableListView();
    mListView.setItemsCanFocus(false);
    mListView.setChoiceMode(ExpandableListView.CHOICE_MODE_SINGLE);


    mHeaderSilent = _add_static_view(getString(R.string.silent_ringtone), null);
    if (existing_uri != null) {
      mHeaderExisting = _add_static_view(getString(R.string.existing_ringtone), existing_uri);
    }
    if (default_uri != null) {
      mHeaderDefault = _add_static_view(getString(R.string.default_ringtone), default_uri);
    }
    mFooterOthers = (TextView)mInflater.inflate(R.layout.tonepicker_footer,null);
    mFooterOthers.setText(R.string.other_apps);
    mFooterOthers.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
          Intent intent = new Intent(TonePicker.this, OtherIntentPicker.class);
          startActivityForResult(intent, REQUEST_GET_CONTENT);
          return;
        }
    });
    mListView.addFooterView(mFooterOthers, null, true);


    mAdapter = new TonePickerAdapter(this);
    setListAdapter(mAdapter);
  }

  private HeaderViewHolder _add_static_view(String label, Uri uri)
  {
    View view;
    HeaderViewHolder holder;

    view = mInflater.inflate(R.layout.tonepicker_child,null);
    view.setOnClickListener(this);
    holder = new HeaderViewHolder();
    holder.text1 = (CheckedTextView)view.findViewById(android.R.id.text1);
    if (uri == null) {
      holder.text1.setText(label);
    }
    else {
      Ringtone tone = RingtoneManager.getRingtone(this, uri);
      if (tone != null)
        holder.text1.setText(label+": "+tone.getTitle(this));
    }
    view.setTag(holder);
    mListView.addHeaderView(view, uri, true);
    return holder;
  }

  @Override
  public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
  {
    TonePickerAdapter.ToneCursor.Tone tone = (TonePickerAdapter.ToneCursor.Tone)mAdapter.getChild(groupPosition, childPosition);
    if (tone.uri != null) {
      if (mSelectedUri == tone.uri)
        stopRingtone();
      else
        playRingtone(tone.uri);
    }
    TonePickerAdapter.ViewHolder holder = (TonePickerAdapter.ViewHolder)v.getTag();
    holder.label.setChecked(true);
    mSelectedUri = tone.uri;
    mAdapter.setSelectedId(id);
    mListView.invalidateViews();
    _clear_header_views();
    return false;
  }

  private void _clear_header_views()
  {
    mHeaderSilent.text1.setChecked(false);
    if (mHeaderExisting != null) mHeaderExisting.text1.setChecked(false);
    if (mHeaderDefault != null) mHeaderDefault.text1.setChecked(false);
  }

  /* header/footer view click*/
  public void onClick(View v)
  {
    HeaderViewHolder holder = (HeaderViewHolder)v.getTag();

    _clear_header_views();
    holder.text1.setChecked(true);
    mSelectedUri = holder.uri;
    mAdapter.setSelectedId(-1);
    mListView.invalidateViews();

    stopRingtone();
    if (holder.uri != null)
      playRingtone(holder.uri);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    if (resultCode != RESULT_OK) return;

    if (requestCode == REQUEST_GET_CONTENT) {
      Uri uri =  data == null ? null : data.getData();
      if (uri == null) {
        uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
      }
      finishWithUri(uri);
    }

  }

  private void finishWithUri(Uri u)
  {
    Intent i = new Intent();
    i.putExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI, u);
    setResult(RESULT_OK, i);
    finish();
  }

  private void playRingtone(Uri uri) {
    stopRingtone();
    mMediaPlayer = new MediaPlayer();
    try {
      mMediaPlayer.setDataSource(this, uri);
      mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
      mMediaPlayer.prepare();
      mMediaPlayer.start();
    } catch (IOException e) {
      Log.w("TonePicker", "Unable to play track: "+uri != null ? uri.toString() : "null");
    }
  }

  private void stopRingtone() {
    if (mMediaPlayer != null) {
      mMediaPlayer.stop();
      mMediaPlayer.release();
      mMediaPlayer = null;
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    stopRingtone();
  }
}
