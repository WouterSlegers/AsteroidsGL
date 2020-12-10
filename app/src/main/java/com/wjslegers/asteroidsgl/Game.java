package com.wjslegers.asteroidsgl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

import com.wjslegers.asteroidsgl.Entities.Asteroid;
import com.wjslegers.asteroidsgl.Entities.Border;
import com.wjslegers.asteroidsgl.Entities.Bullet;
import com.wjslegers.asteroidsgl.Entities.Debris;
import com.wjslegers.asteroidsgl.Entities.GLEntity;
import com.wjslegers.asteroidsgl.Entities.Player;
import com.wjslegers.asteroidsgl.Entities.Smoke;
import com.wjslegers.asteroidsgl.Entities.Star;
import com.wjslegers.asteroidsgl.Entities.Text;
import com.wjslegers.asteroidsgl.Input.InputManager;
import com.wjslegers.asteroidsgl.Utils.Camera;
import com.wjslegers.asteroidsgl.Utils.CollisionDetection;
import com.wjslegers.asteroidsgl.Utils.Jukebox;


import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static com.wjslegers.asteroidsgl.Utils.Utils.NANOS_TO_SECONDS;
import static com.wjslegers.asteroidsgl.Utils.Utils.SECONDS_TO_MILLI;
import static com.wjslegers.asteroidsgl.Utils.Utils.nextFloat;
import static com.wjslegers.asteroidsgl.Utils.Utils.nextInt;
import static com.wjslegers.asteroidsgl.config.ASTEROID_ADDED_PER_LEVEL;
import static com.wjslegers.asteroidsgl.config.ASTEROID_COLLISION;
import static com.wjslegers.asteroidsgl.config.ASTEROID_MAX_VERTICES;
import static com.wjslegers.asteroidsgl.config.ASTEROID_STARTING_COUNT;
import static com.wjslegers.asteroidsgl.config.BG_COLOR;
import static com.wjslegers.asteroidsgl.config.BULLET_COUNT;
import static com.wjslegers.asteroidsgl.config.CHANCE_BIG_ASTEROID;
import static com.wjslegers.asteroidsgl.config.DEBRIS_COUNT;
import static com.wjslegers.asteroidsgl.config.DEBRIS_PER_TIME;
import static com.wjslegers.asteroidsgl.config.METERS_TO_SHOW_Y;
import static com.wjslegers.asteroidsgl.config.PAUSE_TIME_ON_LEVEL_OVER;
import static com.wjslegers.asteroidsgl.config.SMOKE_COUNT;
import static com.wjslegers.asteroidsgl.config.SMOKE_PER_TIME;
import static com.wjslegers.asteroidsgl.config.STARTING_HEALTH;
import static com.wjslegers.asteroidsgl.config.STAR_COUNT;
import static com.wjslegers.asteroidsgl.config.TIME_BETWEEN_PERF_UPDATES;
import static com.wjslegers.asteroidsgl.config.UPDATES_PER_SECOND;
import static com.wjslegers.asteroidsgl.config.WORLD_HEIGHT;
import static com.wjslegers.asteroidsgl.config.WORLD_WIDTH;

public class Game extends GLSurfaceView implements GLSurfaceView.Renderer {
    public static final String TAG = "Game";
    public Player _player;
    public static volatile boolean _isRunning = true;
    private static boolean _wasRunning = false;
    public double _timerOnGameOver = 0.0;
    public int _score = 0;
    public int _level = 0;
    public static boolean _gameOver = false;
    public static boolean _levelOver = false;

    private Camera _camera = null;
    public InputManager _inputs = new InputManager(); //empty but valid default
    private Jukebox _jukebox = null;

    public ArrayList<Text> _texts = new ArrayList<>();
    private Smoke[] _smoke = new Smoke[SMOKE_COUNT];
    private Debris[] _debris = new Debris[DEBRIS_COUNT];
    private Bullet[] _bullets = new Bullet[BULLET_COUNT];
    private ArrayList<Star> _stars = new ArrayList<>();
    private ArrayList<Asteroid> _asteroids = new ArrayList<>();
    public ArrayList<Asteroid> _asteroidsToAdd = new ArrayList<>();

    // Create the projection Matrix. This is used to project the scene onto a 2D viewport.


    private final double dt = 1.0 / UPDATES_PER_SECOND; //update at fixed rate
    private double currentTime = 0.0;
    private double accumulator = 0.0;

    //to measure performance
    private double updateTimer = TIME_BETWEEN_PERF_UPDATES;
    private int frameCount = 0;
    private double totalFrameTime = 0;
    private float totalUpdates = 0;
    private double totalUpdateTime = 0;
    private double avgFrameTime = 0;
    private double avgUpdateTime = 0;


    public Game(Context context) {
        super(context);
        init(context);
    }

    public Game(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        GLEntity._game = this;
        setEGLContextClientVersion(2); //select OpenGL ES 2.0
        setPreserveEGLContextOnPause(true); //context *may* be preserved and thus *may* avoid slow reloads when switching apps.
        // we always re-create the OpenGL context in onSurfaceCreated, so we're safe either way.

        _camera = new Camera();
        _jukebox = new Jukebox(context);

        setRenderer(this);

    }

    private void actualUpdate(final double dt) {
        for (final Asteroid a : _asteroids) {
            a.update(dt);
        }
        for (final Bullet b : _bullets) {
            if (b.isDead()) {
                continue;
            } //skip
            b.update(dt);
        }
        for (final Smoke s : _smoke) {
            if (s.isDead()) {
                continue;
            } //skip
            s.update(dt);
        }
        for (final Debris d : _debris) {
            if (d.isDead()) {
                continue;
            } //skip
            d.update(dt);
        }
        _player.update(dt);
        _camera.lookAt(_player);
        collisionDetection();
        addAndRemoveEntities();

        if (_asteroids.isEmpty()) {
            _isRunning = false;
            _texts.get(2).setString(String.format("%1$s %2$d %3$s", getContext().getString(R.string.level), (_level + 1), getContext().getString(R.string.finished)));
            _levelOver = true;
            _timerOnGameOver = PAUSE_TIME_ON_LEVEL_OVER;
        }
    }

    private void updatePerformance(final double frameTime) {
        if (updateTimer <= 0) {
            //convenient for development, have to uncomment also in update and onSurfaceCreated
            /*
            _texts.get(4).setString("Updates: " + totalUpdates / frameCount + " at " + Math.round(1 / (totalFrameTime / frameCount)) + "FPS");
            avgFrameTime = totalFrameTime / frameCount * SECONDS_TO_MILLI;
            avgUpdateTime = totalUpdateTime / frameCount * SECONDS_TO_MILLI;
            _texts.get(5).setString("aut:" + Math.round(avgUpdateTime * 100) / 100.0 + " aft:" + Math.round(avgFrameTime * 100.0) / 100.0); //average update/frame time in nano seconds
            //totalUpdateTime = 0;
            //totalUpdates = 0;
            */

            _texts.get(4).setString(String.format("%1$d%2$s", (int) Math.round(1 / (totalFrameTime / frameCount)), getContext().getString(R.string.fps)));
            frameCount = 0;
            totalFrameTime = 0;
            updateTimer = TIME_BETWEEN_PERF_UPDATES;
        } else {
            frameCount++;
            updateTimer -= frameTime;
            totalFrameTime += frameTime;
        }
    }

    private void update() {
        double newTime = System.nanoTime() * NANOS_TO_SECONDS;
        double frameTime = newTime - currentTime;
        currentTime = newTime;

        if (_isRunning) {
            if (!_wasRunning) {//First time back from pause, want to forget time passed
                frameTime = 0;
                _wasRunning = true;
                if (_gameOver) {
                    restart();
                } else if (_levelOver) {
                    nextLevel();
                }
            }

            accumulator += frameTime;

            //double updateStart = System.nanoTime() * NANOS_TO_SECONDS;//dev

            while (accumulator >= dt) {
                actualUpdate(dt);
                accumulator -= dt;
                totalUpdates++; //dev
            }

            //double updateEnd = System.nanoTime() * NANOS_TO_SECONDS; //dev
            //totalUpdateTime += updateEnd - updateStart; //dev

            updatePerformance(frameTime);
            if (!_gameOver && !_levelOver) {
                updateStrings();
            }

        } else {//Not running
            if (_timerOnGameOver >= 0) {
                _timerOnGameOver -= frameTime;
            } else {
                _texts.get(3).setString(getContext().getString(R.string.press_to_play));
            }
            _wasRunning = false;
        }
    }


    private void render() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT); //clear buffer to background color

        _camera.update();

        for (final Star s : _stars) {
            s.render(_camera._VPM);
        }
        for (final Asteroid a : _asteroids) {
            a.render(_camera._VPM);
        }
        for (final Bullet b : _bullets) {
            if (b.isDead()) {
                continue;
            } //skip
            b.render(_camera._VPM);
        }
        for (final Smoke s : _smoke) {
            if (s.isDead()) {
                continue;
            } //skip
            s.render(_camera._VPM);
        }
        for (final Debris d : _debris) {
            if (d.isDead()) {
                continue;
            } //skip
            d.render(_camera._VPM);
        }
        _player.render(_camera._VPM);
        for (final Text t : _texts) {
            t.render(_camera._VPMInView);
        }
    }

    public void gameOver() {
        _isRunning = false;
        _gameOver = true;
        _texts.get(2).setString(String.format("%1$s %2$d", getContext().getString(R.string.game_over), _score));
        _timerOnGameOver = PAUSE_TIME_ON_LEVEL_OVER;
    }

    public void restart() {
        _level = 0;
        _score = 0;
        _player._health = STARTING_HEALTH;
        resetWorld();
    }

    public void nextLevel() {
        _level++;
        _player._health++;
        resetWorld();
    }

    public void resetWorld() {
        _gameOver = false;
        _levelOver = false;
        _player.resetValues();
        updateStrings();
        for (final Bullet b : _bullets) {
            b._isAlive = false;
        }
        spawnAsteroids();
    }

    public void updateStrings() {
        _texts.get(0).setString(String.format("%1$s: %2$d", getContext().getString(R.string.health), _player._health));
        _texts.get(1).setString(String.format("%1$s: %2$d %3$s: %4$d", getContext().getString(R.string.level), (_level + 1), getContext().getString(R.string.score), _score));
        _texts.get(2).setString("");
        _texts.get(3).setString("");
    }

    public boolean maybeFireBullet(final GLEntity source) {
        for (final Bullet b : _bullets) {
            if (b.isDead()) {
                b.fireFrom(source);
                return true;
            }
        }
        return false;
    }

    public boolean maybeExudeSmoke(final GLEntity source) {
        int times = SMOKE_PER_TIME;
        for (final Smoke s : _smoke) {
            if (s.isDead()) {
                s.exudeFrom(source);
                times--;
                if (times <= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean maybeSpreadDebris(final Asteroid source) {
        int times = DEBRIS_PER_TIME * source._scale;
        for (final Debris d : _debris) {
            if (d.isDead()) {
                d.spreadFrom(source);
                times--;
                if (times <= 0) {
                    return true;
                }
            }
        }
        return false;
    }


    private void collisionDetection() {
        for (final Bullet b : _bullets) {
            if (b.isDead()) {
                continue;
            } //skip dead bullets
            for (final Asteroid a : _asteroids) {
                if (b.isColliding(a)) {
                    if (a.isDead()) {
                        continue;
                    }
                    b.onCollision(a); //notify each entity so they can decide what to do
                    a.onCollision(b);
                }
            }
        }
        for (final Asteroid a : _asteroids) {
            if (a.isDead()) {
                continue;
            }
            if (_player.isColliding(a)) {
                _player.onCollision(a);
                a.onCollision(_player);
            }
        }

        if (ASTEROID_COLLISION) {
            Asteroid a, b;
            float theta = 0f;
            for (int i = 0; i < _asteroids.size() - 1; i++) {
                a = _asteroids.get(i);
                if (a.isDead()) {
                    continue;
                }
                for (int j = i + 1; j < _asteroids.size(); j++) {
                    b = _asteroids.get(j);
                    if (b.isDead()) {
                        continue;
                    }
                    if (a.isColliding(b)) {
                        a.asteroidCollision(b);//One calculates both!
                    }
                }
            }
        }
    }

    public void addAndRemoveEntities() {
        Asteroid temp;
        int count = _asteroids.size();
        for (int i = count - 1; i >= 0; i--) {
            temp = _asteroids.get(i);
            if (temp.isDead()) {
                _asteroids.remove(i);
            }
        }
        count = _asteroidsToAdd.size();
        for (int i = 0; i < count; i++) {
            temp = _asteroidsToAdd.get(i);
            _asteroids.add(temp);
        }
        _asteroidsToAdd.clear();

    }


    private void spawnAsteroids() {
        _asteroids.clear();
        _asteroidsToAdd.clear();

        int count = ASTEROID_STARTING_COUNT + ASTEROID_ADDED_PER_LEVEL*_level;
        final float[] impulse = {0, 0, 0};

        if (count >= 2 && nextFloat() > CHANCE_BIG_ASTEROID) {
            _asteroids.add(new Asteroid(nextFloat()*WORLD_WIDTH, WORLD_HEIGHT, 4, nextInt(ASTEROID_MAX_VERTICES), impulse));
            count -= 2;
        }

        for (int i = 0; i < count; i++) {
            if (i % 4 == 0) {
                _asteroids.add(new Asteroid(0, nextFloat() * WORLD_HEIGHT, 3, nextInt(ASTEROID_MAX_VERTICES), impulse));
            } else if (i % 4 == 1) {
                _asteroids.add(new Asteroid(WORLD_WIDTH, nextFloat() * WORLD_HEIGHT, 3, nextInt(ASTEROID_MAX_VERTICES), impulse));
            } else if (i % 4 == 2) {
                _asteroids.add(new Asteroid(nextFloat() * WORLD_WIDTH, 0, 3, nextInt(ASTEROID_MAX_VERTICES), impulse));
            } else {
                _asteroids.add(new Asteroid(nextFloat() * WORLD_WIDTH, WORLD_HEIGHT, 3, nextInt(ASTEROID_MAX_VERTICES), impulse));
            }
        }
    }

    @Override
    public void onSurfaceCreated(final GL10 unused, final EGLConfig config) {
        GLManager.buildProgram(); //compile, link and upload our GL program
        GLES20.glClearColor(BG_COLOR[0], BG_COLOR[1], BG_COLOR[2], BG_COLOR[3]); //set clear color

        _player = new Player(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f); // center the player in the world.
        for (int i = 0; i < STAR_COUNT; i++) {
            _stars.add(new Star());
        }
        spawnAsteroids();
        for (int i = 0; i < BULLET_COUNT; i++) {
            _bullets[i] = new Bullet();
        }
        for (int i = 0; i < SMOKE_COUNT; i++) {
            _smoke[i] = new Smoke();
        }
        for (int i = 0; i < DEBRIS_COUNT; i++) {
            _debris[i] = new Debris();
        }


        String s0 = "health";
        _texts.add(new Text(s0, 8, 6));
        String s1 = "level and score";
        _texts.add(new Text(s1, 8, 12));
        String s2 = "gamestate";
        _texts.add(new Text(s2, _camera.getMetersToShowX() / 4, _camera.getMetersToShowY() / 2));
        String s3 = "press to play";
        _texts.add(new Text(s3, _camera.getMetersToShowX() / 4, 6 + _camera.getMetersToShowY() / 2));
        String s4 = "performance";
        _texts.add(new Text(s4, 8, _camera.getMetersToShowY() - 6));
        //String s5 = "performance2";
        //_texts.add(new Text(s5, 8, _camera.getMetersToShowY() - 12));
    }

    @Override
    public void onSurfaceChanged(final GL10 unused, final int width, final int height) {
        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);
        if (!Jukebox._backgroundPlaying) {
            _jukebox.playBackgroundMusic();
        }
    }

    @Override
    public void onDrawFrame(final GL10 unused) {
        update();
        render();
    }

    public void playSound(final int soundID) {
        _jukebox.play(soundID, 0, 1);
    }

    public void setControls(final InputManager input) {
        _inputs = input;
    }


    public void onResume() {
        Log.d(TAG, "onResume");
        _isRunning = true;
        _jukebox.onResume();
        _inputs.onResume();
    }

    public void onPause() {
        Log.d(TAG, "onPause");
        _isRunning = false;
        _jukebox.onPause();
        _inputs.onPause();
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy");

        if (_jukebox != null) {
            _jukebox.destroy();
            _jukebox = null;
        }

        if (_jukebox != null) {
            _inputs.onStop();
            _inputs = null;
        }

        GLEntity._game = null;
    }
}
