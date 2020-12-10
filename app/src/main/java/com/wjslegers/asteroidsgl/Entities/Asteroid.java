package com.wjslegers.asteroidsgl.Entities;

import android.graphics.PointF;
import android.opengl.GLES20;

import com.wjslegers.asteroidsgl.Utils.CollisionDetection;
import com.wjslegers.asteroidsgl.Utils.Jukebox;
import com.wjslegers.asteroidsgl.Utils.Utils;

import static com.wjslegers.asteroidsgl.Utils.Utils.TO_RADIANS;
import static com.wjslegers.asteroidsgl.Utils.Utils.between;
import static com.wjslegers.asteroidsgl.Utils.Utils.nextInt;
import static com.wjslegers.asteroidsgl.config.ASTEROID_COLOR;
import static com.wjslegers.asteroidsgl.config.ASTEROID_MAX_VEL;
import static com.wjslegers.asteroidsgl.config.ASTEROID_SIZE;
import static com.wjslegers.asteroidsgl.config.CHANCE_TO_SPAWN;
import static com.wjslegers.asteroidsgl.config.IMPULSE_PERCENTAGE;
import static com.wjslegers.asteroidsgl.config.MAX_VELOCITY_SQUARE;
import static com.wjslegers.asteroidsgl.config.SCORE_SCALE_ONE;
import static com.wjslegers.asteroidsgl.config.SCORE_SCALE_THREE;
import static com.wjslegers.asteroidsgl.config.SCORE_SCALE_TWO;
import static com.wjslegers.asteroidsgl.config.SIZE_THREE_SPEED_FACTOR;
import static com.wjslegers.asteroidsgl.config.SIZE_TWO_SPEED_FACTOR;
import static com.wjslegers.asteroidsgl.config.THRUST;

public class Asteroid extends GLEntity {
    public int _scale = 0;
    private int _points = 0;

    public Asteroid(final float x, final float y, int size, int points, float[] velocity) {
        if (points < 3) {
            points = 3;
        } //triangles or more, please. :)
        ID = "asteroid";

        _scale = size;
        _points = points;
        _x = x;
        _y = y;
        _width = ASTEROID_SIZE * _scale;
        _height = _width;
        setColors(ASTEROID_COLOR);

        float speedMultiplier = SIZE_THREE_SPEED_FACTOR;
        if (size == 2) {
            speedMultiplier = SIZE_TWO_SPEED_FACTOR;
        } else if (size == 1) {
            speedMultiplier = 1f;
        }

        _velX = Utils.between(-ASTEROID_MAX_VEL * speedMultiplier, ASTEROID_MAX_VEL * speedMultiplier);
        _velY = Utils.between(-ASTEROID_MAX_VEL * speedMultiplier, ASTEROID_MAX_VEL * speedMultiplier);
        _velR = Utils.between(-ASTEROID_MAX_VEL * speedMultiplier, ASTEROID_MAX_VEL * speedMultiplier);
        if (velocity[0] != 0 || velocity[1] != 0) {
            _velX = (1 - IMPULSE_PERCENTAGE) * velocity[0] + IMPULSE_PERCENTAGE * _velX;
            _velY = (1 - IMPULSE_PERCENTAGE) * velocity[1] + IMPULSE_PERCENTAGE * _velY;
            _velR = (1 - IMPULSE_PERCENTAGE) * velocity[2] + IMPULSE_PERCENTAGE * _velR;
        }

        final double radius = _width * 0.5;
        final float[] verts = Mesh.generateLinePolygon(points, radius);
        _mesh = new Mesh(verts, GLES20.GL_LINES);
        _mesh.setWidthHeight(_width, _height);
    }

    //something resembling pool physics
    public void asteroidCollision(final Asteroid that) {
        float distance = that.radius() + radius();
        float[] normal = {(that._x - _x) / distance, (that._y - _y) / distance};
        float[] velocityDelta = {_velX - that._velX, _velY - that._velY};

        float dotProduct = velocityDelta[0] * normal[0] + velocityDelta[1] * normal[1];

        if (dotProduct > 0) {
            float[] impulse = {dotProduct * normal[0], dotProduct * normal[1]};
            _velX -= impulse[0];
            _velY -= impulse[1];
            that._velX += impulse[0];
            that._velY += impulse[1];
        }
    }

    @Override
    public void onCollision(GLEntity that) {
        //Bullet or player
        if (that != _game._player) {//Bullet
            _game.playSound(Jukebox.EXPLOSION);
            if (_scale == 1) {
                _game._score += SCORE_SCALE_ONE;
            } else if (_scale == 2) {
                _game._score += SCORE_SCALE_TWO;
            } else {
                _game._score += SCORE_SCALE_THREE;
            }
        }

        _game.maybeSpreadDebris(this);

        float[] velocity = getVelocity();
        if (_scale >= 2) {
            _game._asteroidsToAdd.add(new Asteroid(left(), _y, _scale - 1, 2+ nextInt(_points), velocity)); //Smaller asteroids often more pointy
            _game._asteroidsToAdd.add(new Asteroid(right(), _y, _scale - 1, 2+ nextInt(_points), velocity));
            if (_scale == 2 && Utils.nextFloat() > CHANCE_TO_SPAWN) {
                _game._asteroidsToAdd.add(new Asteroid(_x, top(), _scale - 1, 2+ nextInt(_points), velocity));
            }
        }
        super.onCollision(that); //_isAlive = false;
    }

    @Override
    public boolean isColliding(final GLEntity that) {
        if (!areBoundingSpheresOverlapping(this, that)) {
            return false;
        }
        if (this._mesh._vertexCount >= 10 && that._mesh._vertexCount >= 10) {
            return true; //5 or more vertices is considered a circle so we already checked
        }

        final PointF[] thisAsteroidHull = getPointList();
        final PointF[] thatAsteroidHull = that.getPointList();
        return CollisionDetection.polygonVsPolygon(thisAsteroidHull, thatAsteroidHull);
    }


}
