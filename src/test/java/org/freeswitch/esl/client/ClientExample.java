package org.freeswitch.esl.client;

import org.freeswitch.esl.client.inbound.Client;
import org.freeswitch.esl.client.inbound.InboundConnectionFailure;
import org.freeswitch.esl.client.internal.IModEslApi.EventFormat;
import org.freeswitch.esl.client.transport.event.EslEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ClientExample {
    private static final Logger L = LoggerFactory.getLogger(ClientExample.class);

    public static void main(String[] args) {

        String password = "ClueCon";

        Client client = new Client();
        try {
            client.connect(new InetSocketAddress("localhost", 8021), password, 10);
        } catch (InboundConnectionFailure inboundConnectionFailure) {
            inboundConnectionFailure.printStackTrace();
        }

        client.addEventListener((ctx, event) ->
        {
            String eventName = event.getEventName();

            //这里仅演示了CHANNEL_开头的几个常用事件
            if (eventName.startsWith("CHANNEL_")) {
                String calleeNumber = event.getEventHeaders().get("Caller-Callee-ID-Number");
                String callerNumber = event.getEventHeaders().get("Caller-Caller-ID-Number");
                switch (eventName) {
                    case "CHANNEL_CREATE":
                        System.out.println("发起呼叫, 主叫：" + callerNumber + " , 被叫：" + calleeNumber);
                        break;
                    case "CHANNEL_BRIDGE":
                        System.out.println("用户转接, 主叫：" + callerNumber + " , 被叫：" + calleeNumber);
                        break;
                    case "CHANNEL_ANSWER":
                        System.out.println("用户应答, 主叫：" + callerNumber + " , 被叫：" + calleeNumber);
                        break;
                    case "CHANNEL_HANGUP":
                        String response = event.getEventHeaders().get("variable_current_application_response");
                        String hangupCause = event.getEventHeaders().get("Hangup-Cause");
                        System.out.println("用户挂断, 主叫：" + callerNumber + " , 被叫：" + calleeNumber + " , response:" + response + " ,hangup cause:" + hangupCause);
                        System.exit(0);
                        break;
                    default:
                        break;
                }
            } else {

                System.out.println("eventName: " + eventName);
            }

        });

        client.setEventSubscriptions(EventFormat.PLAIN, "all");
        CompletableFuture<EslEvent> result = client.sendBackgroundApiCommand("originate", "user/1002 &bridge(user/1001)");
        EslEvent event = null;
        try {
            event = result.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        String jobUuid = event.getEventHeaders().get("Job-UUID");
        System.out.println("异步回调:" + jobUuid);

    }
}
