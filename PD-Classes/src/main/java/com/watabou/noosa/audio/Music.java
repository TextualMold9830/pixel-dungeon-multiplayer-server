/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015  Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2016 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.watabou.noosa.audio;

public enum Music {
	
	INSTANCE;
	// to be removed?
//	private MediaPlayer player;
//
//	private String lastPlayed;
//	private boolean looping;
//
//	private boolean enabled = true;

	public void play( String assetName, boolean looping ) {
		// to be removed?
//
//		if (isPlaying() && lastPlayed.equals( assetName )) {
//			return;
//		}
//
//		stop();
//
//		lastPlayed = assetName;
//		this.looping = looping;
//
//		if (!enabled || assetName == null) {
//			return;
//		}
//
//		try {
//
//			AssetFileDescriptor afd = Game.instance.getAssets().openFd( assetName );
//
//			player = new MediaPlayer();
//			player.setAudioStreamType( AudioManager.STREAM_MUSIC );
//			player.setDataSource( afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength() );
//			player.setOnPreparedListener( this );
//			player.setOnErrorListener( this );
//			player.prepareAsync();
//
//		} catch (IOException e) {
//
//			player.release();
//			player = null;
//
//		}
	}
	
	public void mute() {
		//to be removed
//		lastPlayed = null;
//		stop();
	}


	
	public void pause() {
		// to be removed
//		if (player != null) {
//			player.pause();
//		}
	}
	
	public void resume() {
		// to be removed
//		if (player != null) {
//			player.start();
//			player.setLooping(looping);
//		}
	}
	
	public void stop() {
		// to be removed
//		if (player != null) {
//			player.stop();
//			player.release();
//			player = null;
//		}
	}
	
	public void volume( float value ) {
		// to be removed

//		if (player != null) {
//			player.setVolume( value, value );
//		}
	}
	

	public void enable( boolean value ) {
		// to be removed
//		enabled = value;
//		if (isPlaying() && !value) {
//			stop();
//		} else
//		if (!isPlaying() && value) {
//			play( lastPlayed, looping );
//		}
	}
	
}
