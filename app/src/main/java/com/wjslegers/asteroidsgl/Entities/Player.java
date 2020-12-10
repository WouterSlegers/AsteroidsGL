package com.wjslegers.asteroidsgl.Entities;

import android.graphics.PointF;
import android.opengl.GLES20;

import com.wjslegers.asteroidsgl.Utils.CollisionDetection;
import com.wjslegers.asteroidsgl.Utils.Jukebox;

import static com.wjslegers.asteroidsgl.Utils.Utils.TO_RADIANS;
import static com.wjslegers.asteroidsgl.Utils.Utils.clamp;
import static com.wjslegers.asteroidsgl.config.DRAG;
import static com.wjslegers.asteroidsgl.config.INVINCIBLE_COLOR;
import static com.wjslegers.asteroidsgl.config.INVINCIBLE_TIME;
import static com.wjslegers.asteroidsgl.config.MAX_ROTATION_VELOCITY;
import static com.wjslegers.asteroidsgl.config.MAX_VELOCITY;
import static com.wjslegers.asteroidsgl.config.MAX_VELOCITY_SQUARE;
import static com.wjslegers.asteroidsgl.config.PLAYER_COLOR;
import static com.wjslegers.asteroidsgl.config.PLAYER_HEIGHT;
import static com.wjslegers.asteroidsgl.config.PLAYER_WIDTH;
import static com.wjslegers.asteroidsgl.config.ROTATION_DRAG;
import static com.wjslegers.asteroidsgl.config.ROTATION_ACC;
import static com.wjslegers.asteroidsgl.config.STARTING_HEALTH;
import static com.wjslegers.asteroidsgl.config.THRUST;
import static com.wjslegers.asteroidsgl.config.TIME_BETWEEN_SHOTS;
import static com.wjslegers.asteroidsgl.config.TIME_BETWEEN_SMOKE;
import static com.wjslegers.asteroidsgl.config.WORLD_HEIGHT;
import static com.wjslegers.asteroidsgl.config.WORLD_WIDTH;


public class Player extends GLEntity {
    private static final String TAG = "Player";
    private Flame _flame = null;
    public int _health = 0;
    private double _timer = 0;
    public boolean _invincible = false;
    private double _invincibleCounter = 0;
    private double _smokeStart = 0;
    private double _bulletStart = 0;


    //Player.java
    public Player(final float x, final float y) {
        super();
        ID = "player";
        setColors(PLAYER_COLOR);
        _x = x;
        _y = y;
        _width = PLAYER_WIDTH;
        _height = PLAYER_HEIGHT;
        _health = STARTING_HEALTH;
        float vertices[] = { // in counterclockwise order:
                0.0f, 0.5f, 0.0f,    // top
                -0.5f, -0.5f, 0.0f,    // bottom left
                0.5f, -0.5f, 0.0f    // bottom right
        };
        _mesh = new Mesh(vertices, GLES20.GL_TRIANGLES);
        _mesh.setWidthHeight(_width, _height);
        _mesh.flipY();
        _flame = new Flame(this);
    }


    @Override
    public void update(final double dt){
        _timer += dt;
        if(_health <= 0){
            _game.updateStrings();//last update to show health = 0
            _game.gameOver();
        }

        if (_invincible) {
            _invincibleCounter += dt;
            if (_invincibleCounter >= INVINCIBLE_TIME) {
                _invincible = false;
                setColors(PLAYER_COLOR);
            }
        }

        _velR += (ROTATION_ACC) * _game._inputs._horizontalFactor;

        if(_game._inputs._pressingB){
            final float theta = (float) (_rotation* TO_RADIANS);
            _velX += (float)Math.sin(theta) * THRUST;
            _velY -= (float)Math.cos(theta) * THRUST;
            float absoluteSpeedSquare = _velX*_velX + _velY*_velY;
            if (absoluteSpeedSquare > MAX_VELOCITY_SQUARE){
                float speedClamp = (float) Math.sqrt(MAX_VELOCITY_SQUARE/absoluteSpeedSquare);
                _velX *= speedClamp;
                _velY *= speedClamp;
            }

            if (_timer - _smokeStart >= TIME_BETWEEN_SMOKE){
                _smokeStart = _timer;
                _game.maybeExudeSmoke(this);
            }
            _flame.updatePosition(this);
        } else {
            _flame._isAlive = false;
        }
        _velX *= DRAG;
        _velY *= DRAG;
        _velR *= ROTATION_DRAG;

        _velX = clamp(_velX, -MAX_VELOCITY, MAX_VELOCITY);
        _velY = clamp(_velY, -MAX_VELOCITY, MAX_VELOCITY);
        _velR = clamp(_velR, -MAX_ROTATION_VELOCITY, MAX_ROTATION_VELOCITY);


        if (_game._inputs._pressingA) {
            if (_timer - _bulletStart >= TIME_BETWEEN_SHOTS) {
                if (_game.maybeFireBullet(this)) {
                    _bulletStart = _timer;
                }
            }
        }

        super.update(dt);
    }

    @Override
    public void render(final float[] viewportMatrix){
        if (_flame._isAlive){
            _flame.render(viewportMatrix);
        }
        super.render(viewportMatrix);
    }

    public void resetValues(){
        _invincible = false;
        setColors(PLAYER_COLOR);
        _x = WORLD_WIDTH/2;
        _y = WORLD_HEIGHT/2;
        _velX = 0;
        _velY = 0;
    }

    @Override
    public void onCollision(GLEntity that) {
        if (!_invincible){
            _health--;
            _invincible = true;
            _invincibleCounter = 0;
            setColors(INVINCIBLE_COLOR);
            if (_health <= 0){
                _isAlive = false;
                _game.playSound(Jukebox.DEATH);
            } else {
                _game.playSound(Jukebox.HURT);
            }
        } else {
            _game.playSound(Jukebox.DENIED);
        }
    }

    @Override
    public boolean isColliding(final GLEntity that){
        if(!areBoundingSpheresOverlapping(this, that)){
            return false;
        }
        final PointF[] shipHull = getPointList();
        final PointF[] asteroidHull = that.getPointList();
        if(CollisionDetection.polygonVsPolygon(shipHull, asteroidHull)){
            return true;
        }
        return CollisionDetection.polygonVsPoint(asteroidHull, _x, _y); //finally, check if we're inside the asteroid
    }
}
