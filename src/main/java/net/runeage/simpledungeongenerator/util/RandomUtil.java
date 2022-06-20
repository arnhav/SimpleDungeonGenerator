package net.runeage.simpledungeongenerator.util;

import java.util.Random;

public class RandomUtil {
    static Random rand = new Random();

    public static int randomWithRange(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }

    public static boolean prob(double percent) {
        return rand.nextDouble() * 100 < percent;
    }
}
