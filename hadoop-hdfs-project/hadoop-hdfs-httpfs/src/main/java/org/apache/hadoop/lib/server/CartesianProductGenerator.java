package org.apache.hadoop.lib.server;

import java.util.ArrayList;
import java.util.List;


/**
 * A utility class to generate Cartesian product of given arrays.
 */
public class CartesianProductGenerator {
    public static List<Object[]> generate(Object[][] arrays) {
        if (arrays == null || arrays.length == 0) {
            throw new IllegalArgumentException("Input array cannot be null or empty.");
        }

        List<Object[]> result = new ArrayList<>();
        cartesianProductRec(arrays, result, 0, new Object[arrays.length]);
        return result;
    }

    private static void cartesianProductRec(Object[][] arrays, List<Object[]> result, int index, Object[] current) {
        if (index == arrays.length) {
            result.add(current.clone());
        } else {
            for (Object item : arrays[index]) {
                current[index] = item;
                cartesianProductRec(arrays, result, index + 1, current);
            }
        }
    }
}
