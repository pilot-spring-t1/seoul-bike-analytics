package com.metanet.seoulbike.sse;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Controller
public class SseController {

    @GetMapping("/sse/memory")
    public SseEmitter memoryStream() {
        SseEmitter emitter = new SseEmitter(0L); // 타임아웃 없음

        Thread thread = new Thread(() -> {
            try {
                MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

                while (true) {
                    MemoryUsage memoryUsage = memoryBean.getHeapMemoryUsage();

                    long time = System.currentTimeMillis() + 32400000; // +9시간
                    int committed = (int) (memoryUsage.getCommitted() / (1024 * 1024));
                    int max = (int) (memoryUsage.getMax() / (1024 * 1024));
                    int used = (int) (memoryUsage.getUsed() / (1024 * 1024));

                    Map<String, Object> data = new LinkedHashMap<>();
                    data.put("time", time);
                    data.put("used", used);
                    data.put("max", max);
                    data.put("committed", committed);

                    emitter.send(SseEmitter.event()
                            .name("memory")
                            .reconnectTime(5000)
                            .data(data));

                    Thread.sleep(5000);
                }

            } catch (IOException e) {
                emitter.completeWithError(e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        thread.setDaemon(true);
        thread.start();

        return emitter;
    }
}