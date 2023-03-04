package cn.jho.srpc.core.server;

import static cn.jho.srpc.core.constant.SrpcProtocolConst.COLON_SEPARATOR;
import static cn.jho.srpc.core.constant.SrpcProtocolConst.PAYLOAD;
import static cn.jho.srpc.core.constant.SrpcProtocolConst.PROTOCOL_VERSION_V1;

import cn.jho.srpc.core.SrpcRuntimeException;
import cn.jho.srpc.core.protocol.SrpcRequest;
import cn.jho.srpc.core.protocol.SrpcRequestPayload;
import cn.jho.srpc.core.protocol.SrpcResponse;
import cn.jho.srpc.core.protocol.SrpcResponsePayload;
import cn.jho.srpc.core.utils.JacksonUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.Socket;
import java.util.Map;
import java.util.Map.Entry;

/**
 * {@link cn.jho.srpc.core.anno.SrpcService}任务
 *
 * @author JHO xu-jihong@qq.com
 */
public class ServiceTask implements Runnable {

    private final Socket client;
    private final ServiceRegistry serviceRegistry;

    public ServiceTask(Socket client, ServiceRegistry serviceRegistry) {
        this.client = client;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void run() {
        // TODO 校验版本
        SrpcRequest srpcRequest = read();
        SrpcRequestPayload payload = srpcRequest.getPayload();

        Entry<Method, Object> entry = serviceRegistry.getServiceMethod(payload.getMethodName());
        Method method = entry.getKey();
        Object obj = entry.getValue();

        Map<String, Object> payloadParameters = payload.getParameters();

        Parameter[] params = method.getParameters();
        Object[] args = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            Parameter param = params[i];
            Object value = payloadParameters.get(params[i].getName());
            args[i] = JacksonUtils.convertValue(value, param.getType());
        }

        try {
            Object result = method.invoke(obj, args);
            SrpcResponse srpcResponse = new SrpcResponse();

            srpcResponse.setVersion(PROTOCOL_VERSION_V1);

            SrpcResponsePayload responsePayload = new SrpcResponsePayload();
            responsePayload.setReturnValue(result);
            srpcResponse.setPayload(responsePayload);

            response(srpcResponse);
        } catch (IllegalAccessException e) {
            throw new SrpcRuntimeException("调用方法失败", e);
        } catch (InvocationTargetException e) {
            throw new SrpcRuntimeException("目标方法执行失败", e);
        } catch (IOException e) {
            throw new SrpcRuntimeException(e);
        }

    }

    private void response(SrpcResponse response) throws IOException {
        OutputStream out = client.getOutputStream();
        out.write(response.toPacket());
        out.flush();
        out.close();
    }

    private SrpcRequest read() {
        /*
         *  version:1\r\n
         *  content-length:1024\r\n
         *  \r\n
         *  payload:xxxx
         */
        SrpcRequest request = new SrpcRequest();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

            // 读取版本
            String versionLine = reader.readLine();
            request.setVersion(versionLine.split(COLON_SEPARATOR)[1]);

            // 读取content length
            String contentLengthLine = reader.readLine();
            request.setContentLength(Long.valueOf(contentLengthLine.split(COLON_SEPARATOR)[1]));

            // 读取空行
            int len = System.lineSeparator().length();
            char[] cbuf = new char[len];
            reader.read(cbuf, 0, len);

            // 读取payload
            len = Math.toIntExact(request.getContentLength()) + (PAYLOAD + COLON_SEPARATOR).length();
            cbuf = new char[len];
            reader.read(cbuf, 0, len);
            String payloadLine = new String(cbuf);
            SrpcRequestPayload payload = JacksonUtils.readValue(payloadLine.split(COLON_SEPARATOR, 2)[1],
                    SrpcRequestPayload.class);
            request.setPayload(payload);

            return request;
        } catch (Exception e) {
            throw new SrpcRuntimeException("Failed to read SrpcRequest.", e);
        }
    }

}
