package de.fabiankrueger.coinapi.trades;


import io.coinapi.websocket.CoinAPIWebSocket;
import io.coinapi.websocket.CoinAPIWebSocketImpl;
import io.coinapi.websocket.model.Hello;
import io.coinapi.websocket.model.Trades;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class CoinapiWebsocketSDKTest {

    @Disabled
    @Test
    public void getTradeMessages() throws IOException, InterruptedException {
        String apiKey = System.getProperty("api.key");
        CountDownLatch countDownLatch = new CountDownLatch(1);
        CoinAPIWebSocket coinAPIWebSocket = new CoinAPIWebSocketImpl(false); // sandbox did not provide trades
        coinAPIWebSocket.setTradesInvoke(message -> {
            countDownLatch.countDown();
            Trades trade = (Trades) message;
            System.out.println(String.format("%tT %<tR (%<tN) %<tZ %s %s sequence: %d size: %s taker: %s", trade.getTimeExchange(), trade.getSymbolId(), trade.getPrice(), trade.getSequence(), trade.getSize(), trade.getTakerSide()));
        });
        Hello hello = new Hello();
        hello.setApiKey(apiKey);
        hello.setSubscribeDataType(new String[]{"trade"});
        hello.setHeartbeat(true);
        coinAPIWebSocket.sendHelloMessage(hello);
        countDownLatch.await();
        coinAPIWebSocket.closeConnect();
    }
}
