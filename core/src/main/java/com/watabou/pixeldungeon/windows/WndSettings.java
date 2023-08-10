/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
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
package com.watabou.pixeldungeon.windows;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.Editable;
import android.widget.EditText;


import com.watabou.noosa.Camera;
import com.nikita22007.multiplayer.noosa.audio.Sample;
import com.watabou.noosa.ui.Button;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.PixelDungeon;
import com.watabou.pixeldungeon.scenes.PixelScene;
import com.watabou.pixeldungeon.ui.CheckBox;
import com.watabou.pixeldungeon.ui.RedButton;
import com.watabou.pixeldungeon.ui.Window;

public class WndSettings extends Window {
	
	private static final String TXT_ZOOM_IN			= "+";
	private static final String TXT_ZOOM_OUT		= "-";
	private static final String TXT_ZOOM_DEFAULT	= "Default Zoom";

	private static final String TXT_SCALE_UP		= "Scale up UI";
	private static final String TXT_IMMERSIVE		= "Immersive mode";
	
	private static final String TXT_MUSIC	= "Music";
	
	private static final String TXT_SOUND	= "Sound FX";

	private static final String TXT_RELAY = "Online multiplayer";
	private static final String TXT_SET_SERVER_NAME = "Set server name";
	private static final String TXT_RELAY_SETTINGS = "Configure Relay";

	private static final String TXT_SWITCH_PORT	= "Switch to portrait";
	private static final String TXT_SWITCH_LAND	= "Switch to landscape";
	
	private static final int WIDTH		= 112;
	private static final int BTN_HEIGHT	= 20;
	private static final int GAP 		= 2;

	private RedButton btnZoomOut;
	private RedButton btnZoomIn;
	
	public WndSettings(boolean inGame ) {
		super();

		Button configureRelay = null;
		
		if (inGame) {
			int w = BTN_HEIGHT;
			
			btnZoomOut = new RedButton( TXT_ZOOM_OUT ) {
				@Override
				protected void onClick() {
					zoom( Camera.main.zoom - 1 );
				}
			};
			add( btnZoomOut.setRect( 0, 0, w, BTN_HEIGHT) );
			
			btnZoomIn = new RedButton( TXT_ZOOM_IN ) {
				@Override
				protected void onClick() {
					zoom( Camera.main.zoom + 1 );
				}
			};
			add( btnZoomIn.setRect( WIDTH - w, 0, w, BTN_HEIGHT) );
			
			add( new RedButton( TXT_ZOOM_DEFAULT ) {
				@Override
				protected void onClick() {
					zoom( PixelScene.defaultZoom );
				}
			}.setRect( btnZoomOut.right(), 0, WIDTH - btnZoomIn.width() - btnZoomOut.width(), BTN_HEIGHT ) );
			
			updateEnabled();
			
		} else {
			
			CheckBox btnScaleUp = new CheckBox( TXT_SCALE_UP ) {
				@Override
				protected void onClick() {
					super.onClick();
					PixelDungeon.scaleUp( checked() );
				}
			};
			btnScaleUp.setRect( 0, 0, WIDTH, BTN_HEIGHT );
			btnScaleUp.checked( PixelDungeon.scaleUp() );
			add( btnScaleUp );

			CheckBox btnImmersive = null;
			btnImmersive = new CheckBox( TXT_IMMERSIVE ) {
				@Override
				protected void onClick() {
					super.onClick();
					PixelDungeon.immerse( checked() );
				}
			};
			btnImmersive.setRect( 0, btnScaleUp.bottom() + GAP, WIDTH, BTN_HEIGHT );
			btnImmersive.checked( PixelDungeon.immersed() );
			btnImmersive.enable( android.os.Build.VERSION.SDK_INT >= 19 );
			add( btnImmersive );


			Button btnServerName = new RedButton(TXT_SET_SERVER_NAME) {
				@Override
				protected void onClick() {

					hide();
					//GameScene.show( new WndSetServerName() );
					final EditText input = new EditText(PixelDungeon.instance);
					input.setText(PixelDungeon.serverName());
					PixelDungeon.instance.runOnUiThread(() -> {
						new AlertDialog.Builder(PixelDungeon.instance)
								.setTitle(TXT_SET_SERVER_NAME)
								.setView(input)
								.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int whichButton) {
										Editable editable = input.getText();
										PixelDungeon.serverName(editable.toString());
										// deal with the editable
									}
								})
								.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int whichButton) {
										// Do nothing.
									}
								}).show();
					});
				}
			};
			btnServerName.setRect( 0, btnImmersive.bottom() + GAP, WIDTH, BTN_HEIGHT );
			add( btnServerName );


			CheckBox btnRelay;
			btnRelay = new CheckBox( TXT_RELAY ) {
				@Override
				protected void onClick() {
					super.onClick();
					PixelDungeon.onlineMode(!PixelDungeon.onlineMode());
					Sample.INSTANCE.play( Assets.SND_CLICK );
				}
			};
			btnRelay.setRect( 0, btnServerName.bottom() + GAP, WIDTH, BTN_HEIGHT );
			btnRelay.checked(PixelDungeon.onlineMode());
			add( btnRelay );

			configureRelay = new RedButton(TXT_RELAY_SETTINGS){
				@Override
				protected void onClick() {
					super.onClick();
					parent.parent.addToFront(new WndRelaySettings());
					hide();
				}
			};
			configureRelay.setRect( 0, btnRelay.bottom() + GAP, WIDTH, BTN_HEIGHT );
			add( configureRelay );

		}
		
		CheckBox btnMusic = new CheckBox( TXT_MUSIC ) {
			@Override
			protected void onClick() {
				super.onClick();
				PixelDungeon.music( checked() );
			}
		};
		btnMusic.setRect( 0, (configureRelay != null ? configureRelay.bottom() : BTN_HEIGHT) + GAP, WIDTH, BTN_HEIGHT );
		btnMusic.checked( PixelDungeon.music() );
		add( btnMusic );

		if (inGame) {
			
		} else {
			
			RedButton btnOrientation = new RedButton( orientationText() ) {
				@Override
				protected void onClick() {
					PixelDungeon.landscape( !PixelDungeon.landscape() );
				}
			};
			btnOrientation.setRect( 0, btnMusic.bottom() + GAP, WIDTH, BTN_HEIGHT );
			add( btnOrientation );
			
			resize( WIDTH, (int)btnOrientation.bottom() );
			
		}
	}
	
	private void zoom( float value ) {

		Camera.main.zoom( value );
		PixelDungeon.zoom( (int)(value - PixelScene.defaultZoom) );

		updateEnabled();
	}
	
	private void updateEnabled() {
		float zoom = Camera.main.zoom;
		btnZoomIn.enable( zoom < PixelScene.maxZoom );
		btnZoomOut.enable( zoom > PixelScene.minZoom );
	}
	
	private String orientationText() {
		return PixelDungeon.landscape() ? TXT_SWITCH_PORT : TXT_SWITCH_LAND;
	}
}
