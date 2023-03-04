package cn.jho.srpc.core.server;

import cn.jho.srpc.core.anno.SrpcService;
import cn.jho.srpc.core.utils.MethodKeyUtils;
import java.lang.reflect.Method;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>ServicesRegistryImpl</p>
 *
 * @author JHO xu-jihong@qq.com
 */
public class ServiceRegistryImpl implements ServiceRegistry {

    private final Map<String, Entry<Method, Object>> registeredServiceMethods = new ConcurrentHashMap<>();

    @Override
    public void register(Object service) throws IllegalAccessException {
        Optional<Class<?>> opt = Arrays.stream(service.getClass().getInterfaces())
                .filter(clz -> clz.isAnnotationPresent(SrpcService.class))
                .findAny();

        Class<?> interfaceClazz = opt.orElseThrow(
                () -> new IllegalAccessException("无效的服务：" + service.getClass() + "，无法注册"));

        for (Method method : interfaceClazz.getMethods()) {
            SimpleEntry<Method, Object> entry = new SimpleEntry<>(method, service);
            this.registeredServiceMethods.put(MethodKeyUtils.formatMethodKey(method), entry);
        }

    }

    @Override
    public Entry<Method, Object> getServiceMethod(String methodKey) {
        return registeredServiceMethods.get(methodKey);
    }

}
