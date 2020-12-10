package com.wjslegers.asteroidsgl.Entities;

import android.graphics.PointF;

import com.wjslegers.asteroidsgl.Utils.CollisionDetection;
import com.wjslegers.asteroidsgl.Utils.Utils;

import static com.wjslegers.asteroidsgl.config.ASTEROID_COLOR;
import static com.wjslegers.asteroidsgl.config.DEBRIS_COLOR;
import static com.wjslegers.asteroidsgl.config.DEBRIS_DRAG;
import static com.wjslegers.asteroidsgl.config.DEBRIS_SPEED;
import static com.wjslegers.asteroidsgl.config.DEBRIS_SPEED_VAR;
import static com.wjslegers.asteroidsgl.config.DEBRIS_TIME_TO_LIVE;
import static com.wjslegers.asteroidsgl.config.DEBRIS_TTL_VAR;

public class Debris extends PointEntity {


    public Debris() {
        super();
        setColors(DEBRIS_COLOR);
        _ttl = DEBRIS_TIME_TO_LIVE;
    }

    @Override
    public boolean isColliding(final GLEntity that) {
        if (!areBoundingSpheresOverlapping(this, that)) { //quick rejection
            return false;
        }
        final PointF[] asteroidVerts = that.getPointList();
        return CollisionDetection.polygonVsPoint(asteroidVerts, _x, _y);
    }

    @Override
    public void update(double dt) {
        _velX *= DEBRIS_DRAG;
        _velY *= DEBRIS_DRAG;
        super.update(dt);
    }

    public void spreadFrom(GLEntity source) {
        final float theta = (float) (Utils.nextFloat() * 2 * Math.PI);
        _x = source._x + (float) Math.sin(theta) * (source._height * 0.5f);
        _y = source._y - (float) Math.cos(theta) * (source._height * 0.5f);
        _velX = (float) Math.sin(theta) * DEBRIS_SPEED * Utils.between(1-DEBRIS_SPEED_VAR, 1+DEBRIS_SPEED_VAR);
        _velY = -(float) Math.cos(theta) * DEBRIS_SPEED * Utils.between(1-DEBRIS_SPEED_VAR, 1+DEBRIS_SPEED_VAR);
        _ttl = DEBRIS_TIME_TO_LIVE* Utils.between(1-DEBRIS_TTL_VAR, 1+DEBRIS_TTL_VAR);
        _isAlive = true;
    }
}
