package org.eclipse.ecf.internal.examples.raspberrypi.management.consumer;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.eclipse.ecf.examples.raspberrypi.management.IRaspberryPiAsync;

public class RaspberryPiComponent {

    void bindRaspberryPi(IRaspberryPiAsync rpi) {
        CompletableFuture<Map<String, String>> future = rpi.getSystemPropertiesAsync();
        future.thenAccept(( map) -> {
            System.out.println("Found RaspberryPi");
            for (String key : map.keySet()) System.out.println("  " + key + "=" + map.get(key));
        });
    }
}
