package cn.jho.srpc.core.utils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * <p>MethodKeyUtils</p>
 *
 * @author JHO xu-jihong@qq.com
 */
public class MethodKeyUtils {

    private static final String METHOD_KEY_FORMAT = "%s#%s(%s)";

    private MethodKeyUtils() {

    }

    public static String formatMethodKey(Method method) {
        String paramTypes = Arrays.stream(method.getParameterTypes())
                .map(Class::getName)
                .collect(Collectors.joining(","));
        return DigestUtils.md5Hex(
                String.format(METHOD_KEY_FORMAT, method.getDeclaringClass().getName(), method.getName(), paramTypes));
    }

}
