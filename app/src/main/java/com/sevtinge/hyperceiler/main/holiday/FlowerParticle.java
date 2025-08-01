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
import android.view.Surface;

import com.sevtinge.hyperceiler.ui.R;
import com.sevtinge.hyperceiler.main.holiday.weather.confetto.Confetto;
import com.sevtinge.hyperceiler.main.holiday.weather.confetto.ConfettoInfo;

import java.util.Random;

public class FlowerParticle extends Confetto {
    private final ConfettoInfo confettoInfo;
    private final Bitmap petal;
    private float petalScale;

    FlowerParticle(Context context, ConfettoInfo confettoInfo) {
        super();
        this.confettoInfo = confettoInfo;
        this.petalScale = 0.6f - (float)Math.random() * 0.15f;
        int[] petals = {R.drawable.confetti1, R.drawable.confetti1, R.drawable.confetti2, R.drawable.confetti2, R.drawable.confetti3, R.drawable.confetti3, R.drawable.petal};
        this.petal = BitmapFactory.decodeResource(context.getResources(), petals[new Random().nextInt(petals.length)]);

        int rotation = context.getDisplay().getRotation();
        if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
            this.petalScale *= 1.5f;
        }
    }

    @Override
    public int getHeight() {
        return petal.getHeight();
    }

    @Override
    public int getWidth() {
        return petal.getWidth();
    }

    public void reset() {
        super.reset();
    }

    public void configurePaint(Paint paint) {
        super.configurePaint(paint);
        paint.setColor(-1);
        paint.setAntiAlias(true);
    }

    @Override
    protected void drawInternal(Canvas canvas, Matrix matrix, Paint paint, float x, float y, float rotation, float percentageAnimated) {
        switch (confettoInfo.getPrecipType()) {
            case CLEAR:
                break;
            case SNOW:
                matrix.postScale(petalScale, petalScale);
                matrix.postRotate(rotation, petal.getWidth() / 2f, petal.getHeight() / 2f);
                matrix.postTranslate(x, y);
                canvas.drawBitmap(petal, matrix, paint);
                break;
        }
    }

    public final ConfettoInfo getConfettoInfo() {
        return this.confettoInfo;
    }
}
