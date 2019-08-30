package com.redsponge.oneroom;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.redsponge.redengine.assets.Asset;
import com.redsponge.redengine.assets.AssetSpecifier;
import com.redsponge.redengine.assets.atlas.AtlasAnimation;

public class EndAnimationAssets extends AssetSpecifier {

    public EndAnimationAssets(AssetManager am) {
        super(am);
    }

    @Asset("textures/end_textures.atlas")
    private TextureAtlas textures;

    @AtlasAnimation(animationName = "end", atlas = "textures", length = 6, frameDuration = 4.1f)
    private Animation<TextureRegion> endAnimation;
}
