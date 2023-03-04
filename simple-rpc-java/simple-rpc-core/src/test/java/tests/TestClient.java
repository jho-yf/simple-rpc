package tests;

import cn.jho.srpc.core.client.SrpcClientFactory;
import cn.jho.srpc.core.server.SrpcServer;
import cn.jho.srpc.core.server.SrpcServerImpl;
import cn.jho.srpc.idl.Ping;
import cn.jho.srpc.idl.PingService;
import cn.jho.srpc.idl.Pong;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * <p>TestClient class.</p>
 *
 * @author JHO xu-jihong@qq.com
 */
class TestClient extends Assertions {

    @BeforeAll
    static void openSrpcServer() {
        Thread thread = new Thread(() -> {
            SrpcServer srpcServer = new SrpcServerImpl();

            try {
                srpcServer.register((PingService) ping -> {
                    return new Pong("[Srpc]接收到消息：" + ping.getName());
                });
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            srpcServer.start();
        });
        thread.start();
    }

    @Test
    void testPingPong() {
        SrpcClientFactory factory = new SrpcClientFactory();
        PingService service = factory.getService(PingService.class);
        Ping ping = new Ping();
        ping.setName("ping");
        Pong pong = service.ping(ping);
        assertNotNull(pong);
        assertEquals("[Srpc]接收到消息：ping", pong.getMsg());
    }

}
