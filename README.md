
esl-client
==============================================================================

[![Travis](https://img.shields.io/travis/mgodave/esl-client.svg)](https://travis-ci.org/mgodave/esl-client)
[![Maven Central](https://img.shields.io/maven-central/v/org.freeswitch.esl.client/org.freeswitch.esl.client.svg)](http://search.maven.org/#artifactdetails%7Corg.freeswitch.esl.client%7Corg.freeswitch.esl.client%7C0.9.2%7Cbundle)

**esl-client** is a Java-based Event Socket Library for the
[FreeSWITCH](https://freeswitch.org/) project.

This project is a fork of the unmaintained, original project at
<https://freeswitch.org/stash/projects/FS/repos/freeswitch-contrib/browse/dvarnes/java/esl-client>

Status: done


Inbound Example
------------------------------------------------------------------------------

```java
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

        //health-check
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

```

Outbound Example
------------------------------------------------------------------------------
```java
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

```

Authors
------------------------------------------------------------------------------

- [Dan Cunningham](mailto:dan.cunningham@readytalk.com)
- [Dave Rusek](mailto:dave.rusek@readytalk.com)
- [David Varnes](mailto:david.varnes@gmail.com) (original author)
- [Tobias Bieniek](https://github.com/Turbo87)
- [菩提树下的杨过](https://www.cnblogs.com/yjmyzz/)

License
------------------------------------------------------------------------------

**esl-client** is licensed under the [Apache License, version 2](LICENSE).

