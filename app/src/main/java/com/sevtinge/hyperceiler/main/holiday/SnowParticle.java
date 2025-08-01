/*
 * This file is part of HyperCeiler.

 * HyperCeiler is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.

 * Copyright (C) 2023-2025 HyperCeiler Contributions
 */
package com.sevtinge.hyperceiler.main.holiday;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.sevtinge.hyperceiler.ui.R;
import com.sevtinge.hyperceiler.main.holiday.weather.confetto.Confetto;
import com.sevtinge.hyperceiler.main.holiday.weather.confetto.ConfettoInfo;

public class SnowParticle extends Confetto {
	private Float prevX;
	private Float prevY;
	private final ConfettoInfo confettoInfo;
	private final Bitmap snowflake;
	private final float snowScale;
	//private float rainStretch;

	SnowParticle(Context context, ConfettoInfo confettoInfo) {
		super();
		this.confettoInfo = confettoInfo;
		snowScale = 0.6f - (float)Math.random() * 0.3f;
		//rainStretch = 1.5f + (float)Math.random() - 0.5f;
		snowflake = BitmapFactory.decodeResource(context.getResources(), R.drawable.snowflake);
	}

	public int getHeight() {
		return 0;
	}

	public int getWidth() {
		return 0;
	}

	public void reset() {
		super.reset();
		this.prevX = null;
		this.prevY = null;
	}

	protected void configurePaint(Paint paint) {
		super.configurePaint(paint);
		paint.setColor(-1);
		paint.setAntiAlias(true);
	}

	protected void drawInternal(Canvas canvas, Matrix matrix, Paint paint, float x, float y, float rotation, float percentageAnimated) {
		if (prevX == null || prevY == null) {
			prevX = x;
			prevY = y;
		}

		switch (confettoInfo.getPrecipType()) {
			case CLEAR:
				break;
//			case RAIN:
//				float dX = x - prevX;
//				float dY = y - prevY;
//				float x1 = prevX - dX * rainStretch;
//				float y1 = prevY - dY * rainStretch;
//				float x2 = x + dX * rainStretch;
//				float y2 = y + dY * rainStretch;
//				paint.setShader(new LinearGradient(x1, y1, x2, y2, new int[] { Color.TRANSPARENT, 0xb29aa3ad, 0xb29aa3ad, Color.TRANSPARENT }, new float[] { 0f, 0.45f, 0.55f, 1f }, Shader.TileMode.CLAMP));
//				canvas.drawLine(x1, y1, x2, y2, paint);
//				break;
			case SNOW:
				matrix.postScale(snowScale, snowScale);
				matrix.postRotate(rotation, snowflake.getWidth() / 2f, snowflake.getHeight() / 2f);
				matrix.postTranslate(x, y);
				canvas.drawBitmap(snowflake, matrix, paint);
				break;
		}
		prevX = x;
		prevY = y;
	}

	public final ConfettoInfo getConfettoInfo() {
		return this.confettoInfo;
	}
}
