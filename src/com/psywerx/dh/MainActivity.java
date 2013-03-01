package com.psywerx.dh;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

    private MyGLSurfaceView mGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        mGLView = new MyGLSurfaceView(this);
        
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        setContentView(mGLView);
    }
}

class MyGLSurfaceView extends GLSurfaceView {
    
    private boolean pausePossibility = false;
    
    public MyGLSurfaceView(Context context){
        super(context);
        // Set the Renderer for drawing on the GLSurfaceView
        setEGLContextClientVersion(2);
        setRenderer(new MyRenderer(context));
    }
    @Override
    public boolean onTouchEvent(MotionEvent e){
        
        float x = e.getX();
        float y = e.getY();
        float position = (x * 2.0f / Game.WIDTH - 1.0f);
        
        
        
        switch(e.getAction()){

        case MotionEvent.ACTION_DOWN:
            if(Game.state == 'E'){
                Game.reset();    
            }
            else if(Game.state == 'P'){
                Game.state = 'G';
            }

            else if(x < 120 && y < 120){
                pausePossibility = true;
                break;
            }
            pausePossibility = false;
            Game.moving = true;
            Game.position = position;
            Game.player1.direction[0] = position;
            
            
            break;
            
        case MotionEvent.ACTION_MOVE:
            if(pausePossibility) break;
            Game.position = position;
           
            Game.player1.direction[0] = position;
            break;
        case MotionEvent.ACTION_UP:
            if(pausePossibility && x < 120 && y < 120){
                Game.state = 'P';
            }
            Game.moving = false;
            Game.position = 0;
            Game.player1.direction[0] = 0f;
        }
        return true;
    }
    
}

class MyRenderer implements GLSurfaceView.Renderer{

    static protected Context context;
    private long prev;

    public MyRenderer(Context context) {
        MyRenderer.context = context;
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        
        long now = System.currentTimeMillis();
        
        Game.draw();
        Game.tick((float)(now - prev));
        prev = now;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Game.HEIGHT = height;
        Game.WIDTH = width;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GlProgram program = new GlProgram(context);
        Game.create(program);
        prev = System.currentTimeMillis();
    }
    
}