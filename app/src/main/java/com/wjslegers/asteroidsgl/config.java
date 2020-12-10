package com.wjslegers.asteroidsgl;

public class config {

    //general settings
    public final static float WORLD_WIDTH = 230f; //all dimensions are in meters
    public final static float WORLD_HEIGHT = 120f;
    public final static float METERS_TO_SHOW_X = 180f;
    public final static float METERS_TO_SHOW_Y = 0f; //Gets set automatically if 0
    public final static float LINE_WIDTH = 8f;
    public final static double WIDTH_POINTS = 10.0; //Goes into C code, careful!
    public final static double PAUSE_TIME_ON_LEVEL_OVER = 1;
    public final static float BG_COLOR[] = {0.04f, 0.16f, 0.36f, 1f}; //RGBA
    public final static float TEXT_COLOR[] = {0.8f, 0.8f, 0.8f, 1f}; //RGBA
    public final static float TEXT_SIZE = 0.6f;
    public final static double UPDATES_PER_SECOND = 120.0;
    public final static double TIME_BETWEEN_PERF_UPDATES = 0.5;

    //player movement
    public static final float MAX_VELOCITY = 160f;
    public static final float MAX_VELOCITY_SQUARE = MAX_VELOCITY*MAX_VELOCITY;
    public static final float MAX_ROTATION_VELOCITY = 360f;
    public static final float ROTATION_ACC = 15f;
    public static final float ROTATION_DRAG = 0.96f;
    public static final float THRUST = 2f;
    public static final float DRAG = 0.99f;
    public final static float FOLLOW_PERCENTAGE = 0.26f; //camera

    //player other
    public static final float PLAYER_WIDTH = 7.5f;
    public static final float PLAYER_HEIGHT = 12f;
    public static final int STARTING_HEALTH = 3;
    public static final float INVINCIBLE_TIME = 1.8f;
    public static final float INVINCIBLE_ALPHA = 0.5f;
    public static final float TIME_BETWEEN_SHOTS = 0.32f; //seconds.
    public static final float[] PLAYER_COLOR = {0.45f, 0f, 0.48f, 1f};
    public static final float[] INVINCIBLE_COLOR = {0.8f, 0.1f, 0.1f, 1f};
    public static final float[] FLAME_COLOR = {1f, 1f, 1f, 1f};
    public static final float FLAME_SKEW = 10f;
    public static final int FLAME_SKEW_REFRESH = 10;
    public static final float FLAME_SIZE = 5f;

    //asteroid
    public static final float ASTEROID_SIZE = 7f; //gets multiplied by _scale, so size of smallest asteroid
    public static final boolean ASTEROID_COLLISION = true;
    public static final int ASTEROID_STARTING_COUNT = 2;
    public static final int ASTEROID_ADDED_PER_LEVEL = 2;
    public static final float CHANCE_BIG_ASTEROID = 0.5f;
    public static final float ASTEROID_MAX_VEL = 45f;
    public static final int ASTEROID_MAX_VERTICES = 12;
    public static final float IMPULSE_PERCENTAGE = 0.3f; //new impulse vs old speed on asteroid breaking apart
    public static final float SIZE_TWO_SPEED_FACTOR = 0.85f;
    public static final float SIZE_THREE_SPEED_FACTOR = 0.7f;
    public static final float CHANCE_TO_SPAWN = 0.5f;
    public static final int SCORE_SCALE_ONE = 5;
    public static final int SCORE_SCALE_TWO = 4;
    public static final int SCORE_SCALE_THREE = 2;
    public static final float[] ASTEROID_COLOR = {0.95f, 0.95f, 0.95f, 1f};
    public static final int MAX_NUMBER_POINTS = 15;

    //point effects
    //stars
    public static final int STAR_COUNT = 100;
    public static final float MIN_YELLOW_VALUE = 0.3f;
    public static final float START_INTENSITY = 0.85f;
    //smoke
    public static final float TIME_BETWEEN_SMOKE = 0.06f;
    public static final int SMOKE_PER_TIME = 4;
    public static final float SMOKE_TIME_TO_LIVE = 1.6f;
    public static final float SMOKE_DIE_OFF_DELAY = 0.5f;
    public static final float SPEED_OF_PLAYER = 0.1f;
    public static final float[] SMOKE_COLOR = {0.55f, 0.55f, 0.55f, 1f};
    public static final int SMOKE_COUNT = (int) (SMOKE_PER_TIME * ((SMOKE_TIME_TO_LIVE / TIME_BETWEEN_SMOKE) + 1));
    //debris
    public static final int DEBRIS_COUNT = 60;
    public static final int DEBRIS_PER_TIME = 6; //will be multiplied by _scale of Asteroid
    public static final float DEBRIS_SPEED = 100f;
    public static final float DEBRIS_SPEED_VAR = 0.2f;
    public static final float DEBRIS_TIME_TO_LIVE = 2.0f;
    public static final float DEBRIS_TTL_VAR = 0.2f;
    public static final float DEBRIS_DRAG = 0.97f;
    public static final float[] DEBRIS_COLOR = {0.85f, 0.85f, 0.85f, 1f};
    //bullet
    public static final float BULLET_SPEED = 50f; //will be multiplied by _scale of Asteroid
    public static final float BULLET_TIME_TO_LIVE = 2.5f; //seconds
    public static final int BULLET_COUNT = (int) (BULLET_TIME_TO_LIVE / TIME_BETWEEN_SHOTS) + 1;
    public static final float[] BULLET_COLOR = {1f, 0f, 0.04f, 1f};

}
