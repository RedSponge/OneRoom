package com.redsponge.oneroom;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.redsponge.redengine.assets.AssetSpecifier;

public class NumberPixelFont {

    private static TextureRegion[] numbers;

    private NumberPixelFont() {
    }

    public static TextureRegion[] getNumbers(AssetSpecifier assets) {
        if(numbers == null) {
            TextureAtlas atlas = assets.get("textures", TextureAtlas.class);
            numbers = new TextureRegion[10];
            for (int i = 0; i < numbers.length; i++) {
                numbers[i] = atlas.findRegion("number/" + i);
            }
        }
        return numbers;
    }

    public static void dispose() {
        numbers = null;
    }
}
