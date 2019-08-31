package com.redsponge.oneroom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Disposable;

public class GradualMusicHandler implements Disposable {

    private String[] paths;
    private Music currentMusic;
    private int queued;
    private int current;
    private Music queuedMusic;
    public GradualMusicHandler(String... paths) {
        this.paths = paths;
        queued = -1;
        current = -1;
    }

    public void transitionTo(int index, boolean copyPosition) {
        if(current == index) return;
        Music newMusic = Gdx.audio.newMusic(Gdx.files.internal(paths[index]));
        newMusic.play();
        newMusic.setLooping(true);
        if(copyPosition) {
            float pos = currentMusic.getPosition();
            newMusic.setPosition(pos);
        }
        if(currentMusic != null) currentMusic.dispose();
        currentMusic = newMusic;
        currentMusic.setVolume(0.2f);
    }

    @Override
    public void dispose() {
        if(currentMusic != null) currentMusic.dispose();
    }

    public Music getCurrent() {
        return currentMusic;
    }
}
