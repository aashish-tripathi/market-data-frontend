package com.ashish.frontend.updaters;

import com.ashish.frontend.broker.KafkaBroker;
import com.ashish.frontend.containers.OrderExecutionsContainer;
import com.ashish.marketdata.avro.Order;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;

public class OrderExecutionsUpdater extends Thread {
    private OrderExecutionsContainer orderExecutionsContainer;
    private KafkaConsumer<String, String> kafkaConsumer;
    private boolean kafka;

    public OrderExecutionsUpdater(OrderExecutionsContainer tradeContainer, boolean kafka) {
        this.orderExecutionsContainer = tradeContainer;
        this.kafka = kafka;
        try {
            this.kafkaConsumer = new KafkaBroker("localhost:9092").createConsumer(null);
            this.kafkaConsumer.subscribe(Arrays.asList("exsim.nse.executions"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            orderExecutionsContainer.getLockObject().lock();
            try {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(java.time.Duration.ofMillis(10));
                for (ConsumerRecord<String, String> record : records) {
                    String symbol = record.key();
                    String data = record.value();
                    byte[] decoded = Base64.getDecoder().decode(data);
                    {
                        Order order = deSerealizeAvroHttpRequestJSON(decoded);
                        orderExecutionsContainer.setOrder(order);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                orderExecutionsContainer.getLockObject().unlock();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
    }

    public Order deSerealizeAvroHttpRequestJSON(byte[] data) {
        DatumReader<Order> reader
                = new SpecificDatumReader<>(Order.class);
        Decoder decoder = null;
        try {
            decoder = DecoderFactory.get().jsonDecoder(Order.getClassSchema(), new String(data));
            return reader.read(null, decoder);
        } catch (IOException e) {
            //logger.error("Deserialization error:" + e.getMessage());
        }
        return null;
    }
}
