package com.wjslegers.asteroidsgl.Entities;

import android.graphics.PointF;

import com.wjslegers.asteroidsgl.Utils.CollisionDetection;
import com.wjslegers.asteroidsgl.Utils.Utils;

import static com.wjslegers.asteroidsgl.Utils.Utils.TO_RADIANS;
import static com.wjslegers.asteroidsgl.Utils.Utils.nextFloat;
import static com.wjslegers.asteroidsgl.config.SMOKE_COLOR;
import static com.wjslegers.asteroidsgl.config.SMOKE_DIE_OFF_DELAY;
import static com.wjslegers.asteroidsgl.config.SMOKE_TIME_TO_LIVE;
import static com.wjslegers.asteroidsgl.config.SPEED_OF_PLAYER;

public class Smoke extends PointEntity {


    public Smoke() {
        super();
        setColors(SMOKE_COLOR);
        _ttl = SMOKE_TIME_TO_LIVE;
    }

    @Override
    public boolean isColliding(final GLEntity that){
        if(!areBoundingSpheresOverlapping(this, that)){ //quick rejection
            return false;
        }
        final PointF[] asteroidVerts = that.getPointList();
        return CollisionDetection.polygonVsPoint(asteroidVerts, _x, _y);
    }


    public void exudeFrom(final GLEntity source){
        final float theta = (float) (source._rotation* TO_RADIANS);
        _x = source._x - (float)Math.sin(theta) * (source._height*0.5f) + Utils.between(-3f, 3f);
        _y = source._y + (float)Math.cos(theta) * (source._height*0.5f) + Utils.between(-3f, 3f);
        _velX = source._velX*SPEED_OF_PLAYER;
        _velY = source._velY*SPEED_OF_PLAYER;
        _ttl = SMOKE_TIME_TO_LIVE*(SMOKE_DIE_OFF_DELAY + (1-SMOKE_DIE_OFF_DELAY)*nextFloat());
        _isAlive = true;
    }
}
