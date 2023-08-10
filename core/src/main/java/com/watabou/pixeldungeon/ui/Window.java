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
package com.watabou.pixeldungeon.ui;

import com.nikita22007.multiplayer.server.desktop.Log;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.NinePatch;
import com.watabou.pixeldungeon.Chrome;
import com.watabou.pixeldungeon.Settings;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.effects.ShadowBox;
import com.watabou.pixeldungeon.scenes.PixelScene;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.HashMap;

public class Window extends Group {

	protected int width;
	protected int height;

	protected ShadowBox shadow;
	protected NinePatch chrome;

	//todo: memory leak. Remove entries when hero removes
	//todo: use Hero.NetworkID instead of Hero?
	public static HashMap<Hero, HashMap<Integer, Window>> windows = new HashMap<>(Settings.maxPlayers);
	public static HashMap<Hero, Integer> idCounter = new HashMap<>(Settings.maxPlayers); // contains last used Window.id for each hero

	private Hero ownerHero;
	//Each window CURRENTLY open for ownerHero has a unique id. Two windows can have the same id only with different ownerHero.
	private int id;

	public static final int TITLE_COLOR = 0xFFFF44;

	public Window() {
		this(0, 0, Chrome.get(Chrome.Type.WINDOW));
	}

	public Window(Hero hero) {
		this();
		attachToHero(hero);
	}

	protected synchronized void attachToHero(Hero hero) {
		if (getId() > 0) {
			if (hero != getOwnerHero()) {
				assert false;
			}
			return;
		}
		setOwnerHero(hero);
		if (!idCounter.containsKey(hero)) {
			idCounter.put(hero, 0);
		}
		if (!windows.containsKey(hero)) {
			windows.put(hero, new HashMap<>(3));
		}
		setId(idCounter.get(hero) + 1);
		idCounter.put(hero, getId());
		windows.get(hero).put(getId(), this);
	}

	public Window( int width, int height ) {
		this( width, height, Chrome.get( Chrome.Type.WINDOW ) );
	}
			
	public Window( int width, int height, NinePatch chrome ) {
		super();

		this.chrome = chrome;
		
		this.width = width;
		this.height = height;
		
		shadow = new ShadowBox();
		shadow.am = 0.5f;
		shadow.camera = PixelScene.uiCamera.visible ? 
			PixelScene.uiCamera : Camera.main;
		add( shadow );
		
		chrome.x = -chrome.marginLeft();
		chrome.y = -chrome.marginTop();
		chrome.size( 
			width - chrome.x + chrome.marginRight(),
			height - chrome.y + chrome.marginBottom() );
		add( chrome );
		
		camera = new Camera( 0, 0, 
			(int)chrome.width, 
			(int)chrome.height, 
			PixelScene.defaultZoom );
		camera.x = (int)(Game.width - camera.width * camera.zoom) / 2;
		camera.y = (int)(Game.height - camera.height * camera.zoom) / 2;
		camera.scroll.set( chrome.x, chrome.y );
		Camera.add( camera );
		
		shadow.boxRect( 
			camera.x / camera.zoom, 
			camera.y / camera.zoom, 
			chrome.width(), chrome.height );

	}

	public static void OnButtonPressed(Hero hero, int ID, int button, @Nullable JSONObject res) {
		try {

		if (button == -1){
			windows.get(hero).get(ID).onBackPressed();
		} else  {
			windows.get(hero).get(ID).onSelect(button, res);
		}
		} catch (NullPointerException e){
			Log.i("Window", "No such window.");
		}

	}

	public void resize( int w, int h ) {
		this.width = w;
		this.height = h;
		
		chrome.size( 
			width + chrome.marginHor(),
			height + chrome.marginVer() );
		
		camera.resize( (int)chrome.width, (int)chrome.height );
		camera.x = (int)(Game.width - camera.screenWidth()) / 2;
		camera.y = (int)(Game.height - camera.screenHeight()) / 2;
		
		shadow.boxRect( camera.x / camera.zoom, camera.y / camera.zoom, chrome.width(), chrome.height );
	}
	
	public void hide() {
		if (parent != null) {
			parent.erase(this);
		}
		destroy();
	}
	
	@Override
	public void destroy() {
		super.destroy();
		
		Camera.remove( camera );

		if (getOwnerHero() != null) {
			Window removed = windows.get(ownerHero).remove(getId());
			if ((removed != null) && (removed != this)) {
				throw new AssertionError("Removed window is not current Window");
			}
		}
	}

	public void onBackPressed() {
		hide();
	}

	public void onMenuPressed() {
	}

	public void onSelect(int button, JSONObject args){
		onSelect(button);
	}

	protected void onSelect(int button){

	}

	public Hero getOwnerHero() {
		return ownerHero;
	}

	private void setOwnerHero(Hero ownerHero) {
		this.ownerHero = ownerHero;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
