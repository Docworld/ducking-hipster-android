package com.psywerx.dh;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class Game {

    static GlProgram program;
    static int WIDTH = 100;
    static int HEIGHT = 100;
    private static Background bg;
    private static ScoreBoard top;
    
    private static boolean gameCreated = false;
    
    protected static Player player1;
    protected static float position;
    protected static float smoothPosition;

    static void create(GlProgram program) {
        if(gameCreated) return;
        Game.program = program;
        
        
        
        //bg = new Background();
        //SceneGraph.activeObjects.add(bg);
        
        top = new ScoreBoard();
        SceneGraph.activeObjects.add(top);
        
        player1 = new Player();
        player1.move(0.5f, 0, 0);
        SceneGraph.activeObjects.add(player1);
        gameCreated = true;
        
    }

    static void tick(Float theta) {
        SceneGraph.tick(theta);
        top.increaseScore(1);
        Game.smoothPosition = 0.9f*Game.smoothPosition + 0.1f*Game.position;

    }

    static void draw() {
        resetFrame();
        SceneGraph.draw();
    }

    private static void resetFrame() {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glClearColor(1f, 1f, 1f, 1f);

        GLES20.glClear(GLES20.GL_STENCIL_BUFFER_BIT
                | GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(program.program);
        GLES20.glUniform1i(program.samplerLoc, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, program.texture);

        float ratio = WIDTH / (float) HEIGHT;
        float[] model_view_projection = new float[16]; // Gets sent to the
        float[] model_projection = new float[16];

        Matrix.setLookAtM(model_projection, 0, 0, 0, -1f, 0, 0, 0, 0, 1, 0);
        Matrix.frustumM(model_view_projection, 0, ratio, -ratio, -1, 1,
                0.9999f, 40);
        Matrix.rotateM(model_projection, 0, smoothPosition*-10, 0, 1, 0);
        float[] projection = new float[16];
        
        Matrix.multiplyMM(projection, 0, model_view_projection, 0,
                model_projection, 0);
        GLES20.glUniformMatrix4fv(program.projectionMatrixLoc, 1, false,
                projection, 0);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
//        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
    }
}