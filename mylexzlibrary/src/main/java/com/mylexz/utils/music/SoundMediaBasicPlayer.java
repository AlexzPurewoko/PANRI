/*
 * Copyright (C) 2018 by Alexzander Purwoko Widiantoro
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
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the University nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
 
 /*
  * SoundMediaBasicPlayer.java
  * For play the music with UI that have been builded programmatically
  * Simple usages :
  * LinearLayout rootLayout = (LinearLayout) findViewById(R.id.linear);
  * SoundMediaBasicPlayer player = new SoundMediaBasicPlayer(this, null);
  * rootLayout.addView(player.getLayout());
  * player.setMusic(R.raw.music, "Music");
  */
package com.mylexz.utils.music;

import java.io.Closeable;
import java.io.IOException;
import android.content.Context;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.media.MediaPlayer;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Gravity;
import android.support.annotation.Nullable;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.RawRes;
import android.os.AsyncTask;
import android.util.Log;
import android.content.res.AssetFileDescriptor;
import java.io.FileInputStream;
import com.mylexz.utils.R;

public class SoundMediaBasicPlayer implements Closeable, SeekBar.OnSeekBarChangeListener
{
	// Fields
	// for selecting storage
	public static final int ASSETS_MUSIC_LOCATION = 0x0000f6;
	public static final int RESOURCES_MUSIC_LOCATION = 0x00006f;
	public static final int FILE_MUSIC_LOCATION = 0x00006e;
	
	public static final int STOP_BY_USER = 0x00007f;
	public static final int STOP_ITSELF  = 0x0000f7;
	public static int UPDATE_MILISECONDS = 500;
	// declare an icon play and pause button
	private int iconPlayResId;
	private int iconPauseResId;
	//Current activity
	private Context mContext;
	// Button play and pause
	public ImageButton mPlayer;
	// for seeking song position
	public SeekBar mSeekAction;
	// for show a song time text
	public TextView mTextTimer;
	// a rootView layout
	private LinearLayout mRootView;
	// store a path into selected music
	private Object currentMusic;
	// a listener to perform any operations from outside class
	private OnAnyActionListener listener;
	// declare a storage music locations
	private int musicLocation;
	// store the currentSeekPosition
	private long currentSeekPosition;
	// store the maxPosition;
	private long maxSeekPosition;
	// store the current music name
	private String musicName;
	// store the current MediaPlayer
	private MediaPlayer mMusicPlay;
	// store the file in asset
	private AssetFileDescriptor afd;
	// for getting a fd from file
	private FileInputStream soundFileStream;
	// for indicating proses
	private boolean mIsPlaying;
	private boolean mIsPaused;
	private boolean mIsStopped;
	private boolean mIsPrepared;
	// Handler 
	//Handler process;
	UpdateMediaProgress mediaProcess;
	
	/*
	 * Constructor() {@link SoundMediaBasicPlayer}
	 * @param ctx The Context to the current activity
	 * @param iconPlayResId The Resources id to change the icon play button
	 * @param iconPauseResId The Resources id to change the icon pause button
	 * @param resBackground The Resource id to change the background color, the value can be Drawable
	 * @param colorTimerText The Color value in an int, Sets the Timer text Color
	 * @param colorPlayImage The Background Color for icon player, The value is Color int, not Resource id
	 * @param listener The listener, to get the Lifecycle Callbacks from this class
	 * @noreturn
	 */
	public SoundMediaBasicPlayer(@NonNull Context ctx, @DrawableRes int iconPlayResId, @DrawableRes int iconPauseResId, int resBackground, int colorTimerText, int colorPlayImage, @Nullable SoundMediaBasicPlayer.OnAnyActionListener listener){
		// saves the parameters
		this.iconPlayResId = iconPlayResId;
		this.iconPauseResId = iconPauseResId;
		this.mContext = ctx;
		this.listener = listener;
		// Layout Parameters
		LinearLayout.LayoutParams playerParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.5f);
		playerParams.gravity = Gravity.CENTER;
		LinearLayout.LayoutParams seekParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 5.0f);
		seekParams.gravity = Gravity.CENTER | Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		LinearLayout.LayoutParams statTimerParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.5f);
		statTimerParams.gravity = Gravity.CENTER | Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		
		// sets the layout
		mRootView = (LinearLayout) View.inflate(ctx, R.layout.act_linearbar, null);
		mRootView.setWeightSum(8);
		mRootView.setBackgroundResource(resBackground);
		
		// sets the widget
		mPlayer = new ImageButton(ctx);
		mPlayer.setLayoutParams(playerParams);
		mPlayer.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					// TODO: Implement this method
					mPlayerClicked();
				}
				
			
		});
		// sets the first icon player into play icon
		mPlayer.setImageResource(iconPlayResId);
		mPlayer.setBackgroundColor(colorPlayImage);
		mSeekAction = new SeekBar(ctx);
		mSeekAction.setLayoutParams(seekParams);
		mSeekAction.setOnSeekBarChangeListener(this);
		mSeekAction.setProgress(0);
		mTextTimer = new TextView(ctx);
		mTextTimer.setLayoutParams(statTimerParams);
		mTextTimer.setTextColor(colorTimerText);
		mTextTimer.setText("00:00");
		mTextTimer.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
		// add all widgets into layout
		mRootView.addView(mPlayer);
		mRootView.addView(mSeekAction);
		mRootView.addView(mTextTimer);
		mMusicPlay = null;
		currentMusic = null;
		this.mIsPrepared = false;
		this.mIsPlaying = this.mIsPaused = false; this.mIsStopped = true;
		currentSeekPosition = musicLocation = -1;
		musicName = "";		
	}
	/*
	 * Default Constructor() {@link SoundMediaBasicPlayer}
	 * @param ctx The Context to the current activity
	 * @param listener The listener, to get the Lifecycle Callbacks from this class
	 * @noreturn
	 */
	public SoundMediaBasicPlayer(@NonNull Context ctx, @Nullable SoundMediaBasicPlayer.OnAnyActionListener listener){
		this(ctx, android.R.drawable.ic_media_play, android.R.drawable.ic_media_pause, android.R.drawable.dialog_holo_dark_frame, Color.WHITE, Color.TRANSPARENT, listener);
	}
	/*
	 * Get The {@link SoundMediaBasicPlayer} layout for user playing and paused the music
	 * @return mRootView The {@link SoundMediaBasicPlayer} layout for controling the music player
	 */
	public LinearLayout getLayout(){
		return mRootView;
	}
	/*
	 * Sets the update progress milliseconds for Timer text and SeekBar
	 * @param newUpdateMillis The new {@UPDATE_MILISECONDS}
	 * @return this class {@link SoundMediaBasicPlayer}
	 */
	public SoundMediaBasicPlayer setUpdateProgressMillis(int newUpdateMillis){
		this.UPDATE_MILISECONDS = newUpdateMillis;
		return this;
	}
	/*
	 * Sets the listener to receive the callbacks class lifecycle
	 * @param listener The new listener {@SoundMediaBasicPlayer.OnAnyActionListener}
	 * @return this class {@link SoundMediaBasicPlayer}
	 */
	public SoundMediaBasicPlayer setOnAnyActionListener(SoundMediaBasicPlayer.OnAnyActionListener listener){
		this.listener = listener;
		return this;
	}
	/*
	 * Prepare and set a music to play from a File
	 * @param filePath @NonNull String The Path into music file
	 * @param musicName The Current music name for identifying
	 * @throws IOException If any I/O Errors or file can't be attached or file not found
	 * @return this class {@link SoundMediaBasicPlayer}
	 */
	public SoundMediaBasicPlayer setMusicFromFile(@NonNull String filePath, String musicName) throws IOException{
		if(filePath == null)return this;
		if(filePath.isEmpty())return this;
		this.musicName = musicName;
		musicLocation = FILE_MUSIC_LOCATION;
		if(mMusicPlay != null){
			mediaProcess.cancel(true);
			if(isPlaying())mMusicPlay.stop();
			mMusicPlay.release();
			mMusicPlay = null;
			soundFileStream.close();
			soundFileStream = null;
			mSeekAction.setProgress(0);
			mTextTimer.setText("00:00");
			if(listener!=null)listener.onMusicStopped(mMusicPlay, musicName, currentSeekPosition, STOP_BY_USER);
			mIsPlaying = false;
			mIsStopped = true;
			mPlayer.setImageResource(iconPlayResId);
		}
		mIsStopped = false;
		currentMusic = filePath;
		// Getting file descriptor
		soundFileStream = new FileInputStream(filePath);
		mMusicPlay = new MediaPlayer();
		mMusicPlay.setDataSource(soundFileStream.getFD());
		mMusicPlay.prepare();
		mSeekAction.setMax(mMusicPlay.getDuration());
		maxSeekPosition = mMusicPlay.getDuration();
		mIsPrepared = true;
		if(listener!=null)listener.onMusicChange(mMusicPlay, musicName, currentMusic, musicLocation);
		return this;
	}
	/*
	 * Prepare and set a music to play from in asset directory
	 * @param assetPath @NonNull String The Path into music file in asset directory
	 * @param musicName The Current music name for identifying
	 * @throws IOException If any I/O Errors or file can't be attached or file not found
	 * @return this class {@link SoundMediaBasicPlayer}
	 */
	public SoundMediaBasicPlayer setMusic(@NonNull String assetPath, String musicName) throws IOException{
		this.musicName = musicName;
		musicLocation = ASSETS_MUSIC_LOCATION;
		if(mMusicPlay != null){
			mediaProcess.cancel(true);
			if(isPlaying())mMusicPlay.stop();
			mMusicPlay.release();
			mMusicPlay = null;
			afd.close();
			afd = null;
			mSeekAction.setProgress(0);
			mTextTimer.setText("00:00");
			if(listener!=null)listener.onMusicStopped(mMusicPlay, musicName, currentSeekPosition, STOP_BY_USER);
			mIsPlaying = false;
			mIsStopped = true;
			mPlayer.setImageResource(iconPlayResId);
		}
		mIsStopped = false;
		currentMusic = assetPath;
		afd = mContext.getAssets().openFd(assetPath);
		mMusicPlay = new MediaPlayer();
		mMusicPlay.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
		mMusicPlay.prepare();
		mSeekAction.setMax(mMusicPlay.getDuration());
		maxSeekPosition = mMusicPlay.getDuration();
		mIsPrepared = true;
		if(listener!=null)listener.onMusicChange(mMusicPlay, musicName, currentMusic, musicLocation);
		return this;
	}
	/*
	 * Prepare and set a music to play from a Raw Resources
	 * @param resRawId The Raw Resource id to the given music file
	 * @param musicName The Current music name for identifying
	 * @throws IOException If any I/O Errors or file can't be attached or file not found
	 * @return this class {@link SoundMediaBasicPlayer}
	 */
	public SoundMediaBasicPlayer setMusic(@RawRes int resRawId, @NonNull String musicName) throws IOException, IllegalStateException{
		this.musicName = musicName;
		musicLocation = RESOURCES_MUSIC_LOCATION;
		if(mMusicPlay != null){
			mediaProcess.cancel(true);
			if(isPlaying())mMusicPlay.stop();
			mMusicPlay.release();
			mMusicPlay = null;
			mSeekAction.setProgress(0);
			mTextTimer.setText("00:00");
			if(listener!=null)listener.onMusicStopped(mMusicPlay, musicName, currentSeekPosition, STOP_BY_USER);
			mIsPlaying = false;
			mIsStopped = true;
			mPlayer.setImageResource(iconPlayResId);
		}
		mIsStopped = false;
		currentMusic = resRawId;
		mMusicPlay = MediaPlayer.create(this.mContext, resRawId);
		mSeekAction.setMax(mMusicPlay.getDuration());
		maxSeekPosition = mMusicPlay.getDuration();
		mIsPrepared = true;
		if(listener!=null)listener.onMusicChange(mMusicPlay, musicName, currentMusic, musicLocation);
		return this;
	}
	/*
	 * Play the music programmatically
	 * causes app crashes if not call setMusic() before playing
	 * we have been sets for the executor with {@link AsyncTask.THREAD_POOL_EXECUTOR} for executing the multiple playing simultaneously
	 * @noreturn
	 */
	public void playMusic(){
		if(isPlaying())return;
		mIsPlaying = true;
		mIsPaused = mIsStopped = false;
		mPlayer.setImageResource(iconPauseResId);
		mMusicPlay.start();
		mediaProcess = new UpdateMediaProgress();
		mediaProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	/*
	 * Pause the music play programmatically
	 * @return this class {@link SoundMediaBasicPlayer}
	 */
	public SoundMediaBasicPlayer pauseMusic(){
		if(!isPlaying())return this;
		mMusicPlay.pause();
		mIsPlaying = mIsStopped = false;
		mIsPaused = true;
		mPlayer.setImageResource(iconPlayResId);
		return this;
	}
	/*
	 * Stop the music programmatically and release the MediaPlayer instance to
	 * prepare for playing new music.
	 * You should call setMusic() even if you have to play a music again
	 * @throws IOException If any I/O Errors or file can't be attached or file not found
	 * @noreturn
	 */
	public void stopMusic() throws IOException{
		mIsPrepared = false;
		mIsPaused = false;
		mediaProcess.cancel(true);
		if(isPlaying())mMusicPlay.stop();
		if(musicLocation == ASSETS_MUSIC_LOCATION)afd.close();
		if(musicLocation == FILE_MUSIC_LOCATION)soundFileStream.close();
		mMusicPlay.release();
		mMusicPlay = null;
		mSeekAction.setProgress(0);
		mTextTimer.setText("00:00");
		if(listener!=null)listener.onMusicStopped(mMusicPlay, musicName, currentSeekPosition, STOP_BY_USER);
		mIsPlaying = false;
		mIsStopped = true;
		mPlayer.setImageResource(iconPlayResId);
	}
	/*
	 * Get the music condition if there is playing or not
	 * @return mIsPlaying true if music is playing and false if its not
	 */
	public boolean isPlaying(){
		return mIsPlaying;
	}
	/*
	 * Get the music condition if there is stopped or not
	 * @return mIsStopped true if music is stopped and false if its not
	 */
	public boolean isStopped(){
		return mIsStopped || mIsPaused;
	}
	/*
	 * Get the music name
	 * @return musicName The current music name
	 */
	public String getMusicName(){
		return musicName;
	}
	/*
	 * Close this class and release resources, similar to stopMusic
	 * @overriden from java.io.Closeable
	 */
	@Override
	public void close() throws IOException
	{
		// TODO: Implement this method
		stopMusic();
	}
	private String milliSecondsToTimer(long milliseconds) {
		String finalTimerString = "";
		String secondsString = "";
		String minutesString = "";
		// Convert total duration into time
		int hours = (int) (milliseconds / (1000 * 60 * 60));
		int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
		int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
		// Add hours if there
		if (hours > 0) {
			finalTimerString = hours + ":";
		}
		// Prepending 0 to seconds if it is one digit
		if (seconds < 10) {
			secondsString = "0" + seconds;
		} 
		else { 
			secondsString = "" + seconds;
		}
		if(minutes < 10) minutesString = "0" + minutes;
		else minutesString = "" + minutes;
		finalTimerString = finalTimerString + minutesString + ":" + secondsString;
		// return timer string
		return finalTimerString;
	}
	private void mPlayerClicked(){
		if(isPlaying()){
			pauseMusic();
		}
		else{
			if(mIsStopped && !mIsPrepared)return;
			playMusic();
		}
	}
	
	@Override
	public void onProgressChanged(SeekBar p1, int p2, boolean p3)
	{
		// TODO: Implement this method
		if(mMusicPlay != null && p3){
			mMusicPlay.seekTo(p2);
			mTextTimer.setText(milliSecondsToTimer(p2));
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar p1)
	{
		// TODO: Implement this method
	}

	@Override
	public void onStopTrackingTouch(SeekBar p1)
	{
		// TODO: Implement this method
	}
	private class UpdateMediaProgress extends AsyncTask<Void, Void, Void>
	{

		@Override
		protected Void doInBackground(Void[] p1)
		{
			// TODO: Implement this method
			while(mMusicPlay.isPlaying()){
				publishProgress();
				try
				{
					Thread.sleep(UPDATE_MILISECONDS);
				}
				catch (InterruptedException e)
				{Log.e(this.getClass().getName(), "InterruptedException!",e);}
				
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Void[] values)
		{
			// TODO: Implement this method
			super.onProgressUpdate(values);
			currentSeekPosition = mMusicPlay.getCurrentPosition();
			mSeekAction.setProgress((int)currentSeekPosition);
			mTextTimer.setText(milliSecondsToTimer(currentSeekPosition));
			if(listener!=null)listener.onMusicPlayed(mMusicPlay, musicName, currentSeekPosition);
		}

		@Override
		protected void onPostExecute(Void result)
		{
			// TODO: Implement this method
			super.onPostExecute(result);
			if(currentSeekPosition >= maxSeekPosition){
				mIsPlaying = false;
				mIsPaused = false;
				mIsStopped = true;
				mIsPrepared = true;
				if(listener!=null)listener.onMusicStopped(mMusicPlay, musicName, currentSeekPosition, STOP_ITSELF);
				mPlayer.setImageResource(iconPlayResId);
			}
			else {
				mIsPlaying = false;
				mIsPaused = true;
				if(listener!=null)listener.onMusicStopped(mMusicPlay, musicName, currentSeekPosition, STOP_BY_USER);
				mPlayer.setImageResource(iconPlayResId);
			}
		}
		
		
	}
	public static abstract interface OnAnyActionListener {
		// method will called when change the music
		public abstract void onMusicChange(MediaPlayer media, String musicName, Object locations, int location_type);
		// method will called immediately when music is playing untill its stopped
		public abstract void onMusicPlayed(MediaPlayer media, String musicName, long currentSeekPosition);
		// method will called when the music is stopped or force stopped(by user or by itself)
		public abstract void onMusicStopped(MediaPlayer media, String musicName, long currentSeekPosition, int stopType);
	}
	
}
