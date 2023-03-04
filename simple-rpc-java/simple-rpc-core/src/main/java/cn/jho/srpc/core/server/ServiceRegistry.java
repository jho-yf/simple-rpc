package cn.jho.srpc.core.server;

import java.lang.reflect.Method;
import java.util.Map.Entry;

/**
 * RPC服务注册中心 接口
 *
 * @author JHO xu-jihong@qq.com
 */
public interface ServiceRegistry {

    /**
     * 注册Rpc服务
     *
     * @param service 需要注册的Rpc服务
     * @throws IllegalAccessException 无效服务
     */
    void register(Object service) throws IllegalAccessException;

    /**
     * 根据methodKey获取method
     *
     * @param methodKey method key
     * @return {@link Method}
     */
    Entry<Method, Object> getServiceMethod(String methodKey);

}
