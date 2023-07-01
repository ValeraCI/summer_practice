package com.ledivax.util;

import java.util.Arrays;

public class Unpacker {
    public static double[] convertToPrimitiveDoubleArray(Double[] inputArray) {
        return Arrays.stream(inputArray)
                .mapToDouble(Double::doubleValue)
                .toArray();
    }
}
