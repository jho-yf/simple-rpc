package cn.jho.srpc.core.protocol;

import java.util.Map;

/**
 * <p>SrpcRequestPayLoad class.</p>
 *
 * @author JHO xu-jihong@qq.com
 */
public class SrpcRequestPayload {

    private String methodName;
    private Map<String, Object> parameters;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

}
