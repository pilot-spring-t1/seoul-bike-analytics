package com.metanet.seoulbike.websocket.handler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MyWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessionSet = ConcurrentHashMap.newKeySet();

    public MyWebSocketHandler() {
        log.info("웹소켓 핸들러 인스턴스 생성");
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessionSet.add(session);
        log.info("새 접속 세션 추가: {}", session.getId());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        log.info("수신 메시지: {}", message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessionSet.remove(session);
        log.info("접속 세션 삭제: {}", session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("웹소켓 에러! session={}", session.getId(), exception);
    }

    @PostConstruct
    public void startDummyPush() {
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    long time = System.currentTimeMillis();
                    int lottoNum = (int) (Math.random() * 45 + 1);
                    TextMessage message =
                        new TextMessage("{\"time\":" + time + ",\"lotto\":" + lottoNum + "}");

                    for (WebSocketSession session : sessionSet) {
                        if (session.isOpen()) {
                            session.sendMessage(message);
                        }
                    }

                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    log.error("스레드 중단", e);
                    Thread.currentThread().interrupt();
                    break;
                } catch (IOException e) {
                    log.error("메시지 전송 실패", e);
                }
            }
        });

        t.setDaemon(true);
        t.start();
    }
}