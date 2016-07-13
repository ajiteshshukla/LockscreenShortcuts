package com.acubeapps.lockscreen.shortcuts.adstore;

import android.annotation.SuppressLint;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by anshul.srivastava on 09/06/16.
 */
public final class IdGenerator {

    private IdGenerator() {

    }

    @SuppressLint("TrulyRandom")
    private static final Random RAND = new SecureRandom();

    public static long nextId() {
        long result = 0;
        while (result <= 0) {
            result = RAND.nextLong();
        }
        result >>= 1;
        return result;
    }
}

