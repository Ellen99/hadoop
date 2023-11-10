package org.apache.hadoop.fs.s3a;

import java.util.ArrayList;
import java.util.List;

public class CartesianProductGenerator {
    public static List<Object[]> generate(Object[][] arrays) {
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
