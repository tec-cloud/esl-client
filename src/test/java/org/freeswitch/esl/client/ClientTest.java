package org.freeswitch.esl.client;

import org.freeswitch.esl.client.inbound.Client;
import org.freeswitch.esl.client.transport.event.EslEvent;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ClientTest {

    private static class DemoEventListener implements IEslEventListener {

        @Override
        public void eventReceived(EslEvent event) {
            System.out.println("eventReceived:" + event.getEventName());
        }

        @Override
        public void backgroundJobResultReceived(EslEvent event) {
            System.out.println("backgroundJobResultReceived:" + event.getEventName());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        String host = "localhost";
        int port = 8021;
        String password = "ClueCon";
        int timeoutSeconds = 10;
        Client inboundClient = new Client(2, 8);
        try {
            inboundClient.connect(host, port, password, timeoutSeconds);
            inboundClient.addEventListener(new DemoEventListener());
            inboundClient.setEventSubscriptions("plain", "all");
        } catch (Exception e) {
            System.out.println("connect fail");
        }

        //单独起1个线程，定时检测连接状态
        ScheduledExecutorService service = new ScheduledThreadPoolExecutor(1);
        service.scheduleAtFixedRate(() -> {
            System.out.println(System.currentTimeMillis() + " " + inboundClient.canSend());
            if (!inboundClient.canSend()) {
                try {
                    //重连
                    inboundClient.connect(host, port, password, timeoutSeconds);
                    inboundClient.cancelEventSubscriptions();
                    inboundClient.setEventSubscriptions("plain", "all");
                } catch (Exception e) {
                    System.out.println("connect fail");
                }
            }
        }, 1, 500, TimeUnit.MILLISECONDS);

        System.out.println("other process ...");
    }
}
