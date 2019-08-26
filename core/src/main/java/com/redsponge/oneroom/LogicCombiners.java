package com.redsponge.oneroom;

import javax.security.auth.login.LoginContext;

public class LogicCombiners {

    public static final LogicCombiner AND = (a, b) -> a && b;
    public static final LogicCombiner OR = (a, b) -> a || b;

}
