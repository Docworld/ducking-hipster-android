package com.psywerx.dh;

import com.google.analytics.tracking.android.EasyTracker;

public class Enemy extends PersonSprite {

    protected float r = 0.01f+Game.rand.nextFloat()/100;
    protected Texture colTexture = new Texture();
    protected Square col = new Square();
    protected float timeDead;
    protected boolean score = false;

    public Enemy() {
        reset();
    }
    void reset(){
        s.texture.spriteOffset = (int)(Math.random() * 12) + 4;
        s.texture.updateSprite();
        
        speed[1] = 0.01f;
        resize(0.2f);
        position[2] = 1;
        position[1] = -2f;
        removeMe = false;
        
        colTexture.enabled = true;
        colTexture.sprite = new int[]{9,6};
        colTexture.startSprite = new int[]{9,6};
        colTexture.size = new int[]{2,2};
        colTexture.anim = new int[]{1,0,1,2,1};
        colTexture.animSpeed = 0.5f;
        
        col.texture = colTexture;
        col.size = new float[]{0.2f, 0.2f, 0.25f};
        timeDead = 0f;
        score = false;
    }

    @Override
    public void tick(float theta) {
        super.tick(theta);
        if (position[1] > 10 || (SceneGraph.activeObjects.size() > 20 && position[1] > 5)) {
            // This needs to get moved:
            removeMe = true;
            Game.preloadedEnemies.push(this);
        }
        if(bb.position[1]+bb.size[1]/2 > Game.player1.sPosition){
            position[2] += speed[1] * theta * 0.112f;
            position[1] -= speed[1]*0.1f;
            if(!score){
                Game.top.increaseScore(1);
                score = true;
            }
        }
        col.position[0] = -100f;
        col.position[1] = -100f;
        col.position[2] = 0f;
        if (Utils.areColliding(this, Game.player1)) {
	    if (!Game.player1.dead) {
		if (Game.sound) {
		    Game.hit.seekTo(0);
		    Game.hit.start();
		}
		timeDead = 0;
            }
            else{
                timeDead+=theta;
            }
            if(timeDead > 2000){
                EasyTracker.getTracker().sendEvent("Game", "End", "Score", (long)Game.top.score);
                ((MainActivity)MyRenderer.context).newScore(Game.top.score);
                Game.state = 'E';
            }
            
            // Bug where player is in front of the collision:
            if(Game.player1.position[2]<position[2]){
                
                Game.player1.position[2] -= (0.01f + Game.player1.position[2] - position[2]);
                Game.player1.position[1] = position[1] + 0.01f;
            }
	    
            Game.player1.dead = true;
            Game.player1.move(0,  speed[1]*theta*0.05f, 0);
            Game.player1.position[2] += speed[1] * theta * 0.112f;
            Game.player1.position[1] -= speed[1]*0.1f;
            col.position = position.clone();
            col.position[0] += (Game.player1.position[0] - position[0])/2;
            col.position[2] -= 0.01f;
            col.texture.update(theta);
        }
        this.move(0, speed[1]*theta*0.05f, speed[1] * theta * 0.001f);
    }
    @Override
    public void draw(){
        super.draw();
        col.draw();
    }
}
