package de.fabiankrueger.trades;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;

@Slf4j
@SpringBootApplication
@EnableBinding({Source.class, Sink.class})
@RequiredArgsConstructor
public class TradesProcessor {

    public static void main(String[] args) {
        SpringApplication.run(TradesProcessor.class, args);
    }

    private final Source source;

    @StreamListener(Sink.INPUT)
    @SendTo(Source.OUTPUT)
    public String processTrade(String trade) {
        ObjectMapper o = new ObjectMapper();
        try {
            final JsonNode jsonNode = o.readTree(trade);
            final JsonNode price = jsonNode.get("price");
            final JsonNode size = jsonNode.get("size");
            final double total = price.asDouble() * size.asDouble();
            ((ObjectNode)jsonNode).put("total", total);
            System.out.println(jsonNode.toString());
            return jsonNode.toString();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(String.format("Error while processing trade: %s", trade), e);
        }
    }

}
