package de.fabiankrueger.coinapi.trades;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.coinapi.websocket.CoinAPIWebSocket;
import io.coinapi.websocket.interfaces.InvokeFunction;
import io.coinapi.websocket.model.Hello;
import io.coinapi.websocket.model.Trades;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.messaging.Message;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest(properties = "coinapi.api.key=123")
public class CoinApiWebsocketAdapterTest {
    @Autowired
    private MessageCollector messageCollector;

    @Autowired
    private Source source;

    @MockBean
    private CoinAPIWebSocket coinAPIWebSocket;

    @Test
    public void testTradesAdapter() throws Exception {

        // verify setTradesInvoke is called
        ArgumentCaptor<InvokeFunction> argumentCaptor = ArgumentCaptor.forClass(InvokeFunction.class);
        // and capture the method argument (a lambda)
        verify(coinAPIWebSocket).setTradesInvoke(argumentCaptor.capture());

        // also verify that hello message is sent
        verify(coinAPIWebSocket).sendHelloMessage(any(Hello.class));

        // create trade
        Trades trades = new Trades();
        final double price = 12.22;
        trades.setPrice(price);
        final String takerSide = "taker side";
        trades.setTakerSide(takerSide);

        // pass trade into the lambda which was captured
        argumentCaptor.getValue().preprocesMessages(trades);

        // retrieve received message from outbound channel
        Message message = this.messageCollector.forChannel(this.source.output()).poll(1, TimeUnit.SECONDS);
        ObjectMapper objectMapper = new ObjectMapper();
        Trades tradesReceived = objectMapper.readValue(message.getPayload().toString(), Trades.class);

        // and verify that the message received is same as the trade passed into the lambda
        assertThat(tradesReceived.getPrice()).isEqualTo(price);
        assertThat(tradesReceived.getTakerSide()).isEqualTo(takerSide);

    }

}
