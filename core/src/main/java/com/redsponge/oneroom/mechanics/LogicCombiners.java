package com.redsponge.oneroom.mechanics;

public class LogicCombiners {

    public static final LogicCombiner AND = (a, b) -> a && b;
    public static final LogicCombiner OR = (a, b) -> a || b;

}
