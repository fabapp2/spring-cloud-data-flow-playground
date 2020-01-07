package de.fabiankrueger.coinapi.trades;

import io.coinapi.websocket.CoinAPIWebSocket;
import io.coinapi.websocket.CoinAPIWebSocketImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CoinApiWebsocketAdapterApp {

    public static void main(String[] args) {
        SpringApplication.run(CoinApiWebsocketAdapterApp.class, args);
    }

    @Value("${coinapi.api.isSandbox}")
    private boolean isSandbox;

    @Bean
    public CoinAPIWebSocket coinAPIWebSocket() {
        return new CoinAPIWebSocketImpl(isSandbox);
    }

}
