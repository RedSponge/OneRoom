package com.redsponge.oneroom.texturepacker;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class PackTextures {

    public static void main(String[] args) {
        TexturePacker.processIfModified("assets", "../assets/textures/", "textures");
    }

}
