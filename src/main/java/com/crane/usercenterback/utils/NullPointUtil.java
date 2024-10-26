package com.crane.usercenterback.utils;

import com.crane.usercenterback.common.ErrorStatus;
import com.crane.usercenterback.exception.BusinessException;

import java.lang.reflect.Field;

/**
 * 判空指针工具类
 *
 * @Date 2024/10/26 11:03
 * @Author Crane Resigned
 */
public final class NullPointUtil {

    private NullPointUtil() {
    }

    /**
     * 传入任意对象，反射获取属性进行判空
     *
     * @author CraneResigned
     * @date 2024/10/26 11:09
     **/
    public static void checkNullPoint(String message, Object object) {
        if (object == null) {
            throw new BusinessException(ErrorStatus.NULL_ERROR, message);
        }
        Field[] fields = object.getClass().getFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(object);
                if (value == null) {
                    throw new BusinessException(ErrorStatus.NULL_ERROR, message);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void checkNullPoint(Object object) {
        checkNullPoint(null, object);
    }

    public static void checkNullPoint(Object... objects) {
        for (Object object : objects) {
            checkNullPoint(null, object);
        }
    }

    public static void checkNullPoint(String message, Object... objects) {
        for (Object object : objects) {
            checkNullPoint(message, object);
        }
    }

}
