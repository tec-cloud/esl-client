package org.freeswitch.esl.client;

import org.freeswitch.esl.client.outbound.SocketClient;
import org.freeswitch.esl.client.outbound.example.SimpleHangupPipelineFactory;

public class SocketClientTest {

    public static void main(String[] args) {
        SocketClient client = new SocketClient(8086, new SimpleHangupPipelineFactory(), 2, 16);
        client.start();
        System.out.println("started ...");
    }
}
