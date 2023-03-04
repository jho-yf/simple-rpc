package cn.jho.srpc.core.protocol;

import static cn.jho.srpc.core.constant.SrpcProtocolConst.CONTENT_LENGTH;
import static cn.jho.srpc.core.constant.SrpcProtocolConst.PAYLOAD;
import static cn.jho.srpc.core.constant.SrpcProtocolConst.VERSION;

import cn.jho.srpc.core.utils.JacksonUtils;
import java.nio.charset.StandardCharsets;

/**
 * <p>SrpcResponse class.</p>
 *
 * @author JHO xu-jihong@qq.com
 */
public class SrpcResponse extends BaseSrpcProtocol {

    private SrpcResponsePayload payload;

    public SrpcResponsePayload getPayload() {
        return payload;
    }

    public void setPayload(SrpcResponsePayload payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        String payloadStr = JacksonUtils.writeValueAsString(payload);
        return VERSION + ":" + version
                + System.lineSeparator()
                + CONTENT_LENGTH + ":" + payloadStr.getBytes(StandardCharsets.UTF_8).length
                + System.lineSeparator()
                + System.lineSeparator()
                + PAYLOAD + ":" + payloadStr;
    }

}
