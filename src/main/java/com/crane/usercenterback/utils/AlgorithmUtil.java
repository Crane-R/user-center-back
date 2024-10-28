package com.crane.usercenterback.utils;

import java.util.List;
import java.util.Objects;

/**
 * 算法工具类
 *
 * @Date 2024/10/28 17:53
 * @Author Crane Resigned
 */
public final class AlgorithmUtil {

    private AlgorithmUtil() {
    }

    /**
     * 编辑距离算法
     * <a href="https://blog.csdn.net/DBC_121/article/details/104198838">...</a>
     *
     * @author CraneResigned
     * @date 2024/10/28 17:54
     **/
    public static long minDistance(List<String> tagList1, List<String> tagList2) {
        int n = tagList1.size();
        int m = tagList2.size();

        if (n * m == 0) {
            return n + m;
        }

        int[][] d = new int[n + 1][m + 1];
        for (int i = 0; i < n + 1; i++) {
            d[i][0] = i;
        }

        for (int j = 0; j < m + 1; j++) {
            d[0][j] = j;
        }

        for (int i = 1; i < n + 1; i++) {
            for (int j = 1; j < m + 1; j++) {
                int left = d[i - 1][j] + 1;
                int down = d[i][j - 1] + 1;
                int leftDown = d[i - 1][j - 1];
                if (!Objects.equals(tagList1.get(i - 1), tagList2.get(j - 1))) {
                    leftDown += 1;
                }
                d[i][j] = Math.min(left, Math.min(down, leftDown));
            }
        }
        return d[n][m];
    }

}
