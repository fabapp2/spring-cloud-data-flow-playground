package de.fabiankrueger.coinapi.trades;

import io.coinapi.websocket.CoinAPIWebSocket;
import io.coinapi.websocket.model.Hello;
import io.coinapi.websocket.model.Trades;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;


@Component
@RequiredArgsConstructor
@EnableBinding(Source.class)
public class CoinApiWebSocketAdapter {

    @Value("${coinapi.api.key}")
    private String apiKey;

    private final CoinAPIWebSocket coinAPIWebSocket;
    private final Source source;

    @PostConstruct
    public void connect() {
        try {
            coinAPIWebSocket.setTradesInvoke(message -> {
                Trades trades = (Trades) message;
                sendInboundTradeToOutboundChannel(trades);
            });

            Hello hello = new Hello();
            hello.setApiKey(apiKey);
            hello.setSubscribeDataType(new String[]{"trade"});
            hello.setHeartbeat(true);

            coinAPIWebSocket.sendHelloMessage(hello);
        } catch (Exception e) {
            throw new CoinApiWebSocketAdapterException(e);
        }
    }

    private void sendInboundTradeToOutboundChannel(Trades trades) {
        source.output().send(MessageBuilder.withPayload(trades).build());
    }

    @PreDestroy
    public void closeConnection() {
        try {
            coinAPIWebSocket.closeConnect();
        } catch (Exception e) {
            throw new CoinApiWebSocketAdapterException(e);
        }
    }

}
