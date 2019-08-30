package com.redsponge.oneroom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Music.OnCompletionListener;
import com.badlogic.gdx.utils.Disposable;
import com.redsponge.redengine.utils.Logger;

public class GradualMusicHandler implements Disposable {

    private String[] paths;
    private Music currentMusic;
    private int queued;
    private int current;
    private Music queuedMusic;
    private OnCompletionListener onCompletion;

    public GradualMusicHandler(String... paths) {
        this.paths = paths;
        queued = -1;
        onCompletion = new ChangeOnComplete();
    }

    public void transitionTo(int index) {
        if(current == index) return;
        current = index;
        queued = index;
        if(queuedMusic != null) queuedMusic.dispose();
        queuedMusic = Gdx.audio.newMusic(Gdx.files.internal(paths[index]));
        queuedMusic.setOnCompletionListener(new ChangeOnComplete());
        queuedMusic.setVolume(0.5f);
        Logger.log(this, "Loaded new music!");

        if(currentMusic == null) {
            currentMusic = queuedMusic;
            Logger.log(this, "there was no current!");
            currentMusic.play();
            queuedMusic = null;
            queued = -1;
        }
    }

    @Override
    public void dispose() {
        if(currentMusic != null) currentMusic.dispose();
    }

    class ChangeOnComplete implements OnCompletionListener {
        @Override
        public void onCompletion(Music music) {
            Logger.log(this, "YO");
            if(queued != -1) {
                currentMusic.dispose();
                currentMusic = queuedMusic;
                currentMusic.play();
                queuedMusic = null;
                queued = -1;
                Logger.log(this, "SWAP!");
            } else {
                currentMusic.setPosition(0);
                currentMusic.play();
            }
        }
    }
}
