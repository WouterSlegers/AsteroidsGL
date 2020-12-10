package com.wjslegers.asteroidsgl.Entities;

import android.graphics.PointF;

import com.wjslegers.asteroidsgl.Utils.CollisionDetection;
import com.wjslegers.asteroidsgl.Utils.Jukebox;

import static com.wjslegers.asteroidsgl.Utils.Utils.TO_RADIANS;
import static com.wjslegers.asteroidsgl.config.BULLET_COLOR;
import static com.wjslegers.asteroidsgl.config.BULLET_SPEED;
import static com.wjslegers.asteroidsgl.config.BULLET_TIME_TO_LIVE;

public class Bullet extends PointEntity {

    public Bullet() {
        super();
        setColors(BULLET_COLOR);
        _ttl = BULLET_TIME_TO_LIVE;
    }

    @Override
    public boolean isColliding(final GLEntity that){
        if(!areBoundingSpheresOverlapping(this, that)){ //quick rejection
            return false;
        }
        final PointF[] asteroidVerts = that.getPointList();
        return CollisionDetection.polygonVsPoint(asteroidVerts, _x, _y);
    }


    public void fireFrom(GLEntity source){
        final float theta = (float) (source._rotation* TO_RADIANS);
        _x = source._x + (float)Math.sin(theta) * (source._height*0.5f);
        _y = source._y - (float)Math.cos(theta) * (source._height*0.5f);
        _velX = source._velX;
        _velY = source._velY;
        _velX += (float)Math.sin(theta) * BULLET_SPEED;
        _velY -= (float)Math.cos(theta) * BULLET_SPEED;
        _ttl = BULLET_TIME_TO_LIVE;
        _isAlive = true;
        _game.playSound(Jukebox.LASER);
    }

}