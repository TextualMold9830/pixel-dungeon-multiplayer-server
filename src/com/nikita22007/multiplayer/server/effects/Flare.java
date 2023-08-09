/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 * Copyright (C) 2021-2023 Nikita SHaposhnikov
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
package com.nikita22007.multiplayer.server.effects;

import com.watabou.noosa.Group;
import com.watabou.noosa.Visual;
import com.watabou.pixeldungeon.network.SendData;
import com.watabou.utils.PointF;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

public final class Flare {

	private float duration = 0;

	private boolean lightMode = true;

	private int color;
	private final int nRays;
	private final float radius;
	private final float angle;

	private float angularSpeed;
	private PointF pos;

	public Flare( int nRays, float radius ) {

		color = 0;
		pos = new PointF(0,0);

		this.nRays = nRays;
		this.radius = radius;

		angle = 45;
		angularSpeed = 180;

	}

	public Flare hardlight(int color ) {
		this.color = color;
		return this;
	}

	public Flare color( int color, boolean lightMode ) {
		this.lightMode = lightMode;
		hardlight( color );

		return this;
	}

	public void point(PointF pos){
		this.pos = pos;
	}

	public void show(@NotNull Visual visual, float duration ) {
		show(visual.parent, visual.center(),duration);
	}

	public void show(Group ignoredParent, PointF pos, float duration ) {
		point( pos );

		this.duration = duration;

		SendThis();
	}
	public void SendThis(){
		try{
			JSONObject actionObj = new JSONObject();
			actionObj.put("action_type", "flare_visual");
			actionObj.put("position_x", pos.x);
			actionObj.put("position_y", pos.y);
			actionObj.put("color", color);
			actionObj.put("duration", duration);
			actionObj.put("light_mode", lightMode);
			actionObj.put("rays", nRays);
			actionObj.put("radius", radius);
			actionObj.put("angle", angle);
			actionObj.put("angular_speed", angularSpeed);
			SendData.sendCustomActionForAll(actionObj);
		}catch (JSONException ignore){}
	}

	@Contract ("_ ->this")
	public Flare setAngularSpeed(float angularSpeed) {
		this.angularSpeed = angularSpeed;
		return this;
	}
}
