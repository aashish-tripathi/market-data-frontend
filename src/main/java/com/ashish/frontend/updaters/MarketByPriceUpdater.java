package com.ashish.frontend.updaters;

import com.ashish.frontend.MarketDataViewer;
import com.ashish.frontend.broker.KafkaBroker;
import com.ashish.frontend.containers.MarketDepthContainer;
import com.ashish.marketdata.avro.MarketByPrice;
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

public class MarketByPriceUpdater extends Thread{
    private MarketDepthContainer marketDepthContainer;
    private KafkaConsumer<String, String> kafkaConsumer;
    private boolean kafka;

    public MarketByPriceUpdater(MarketDepthContainer marketDepthContainer, boolean kafka) {
        this.marketDepthContainer = marketDepthContainer;
        this.kafka = kafka;
        try {
            this.kafkaConsumer = new KafkaBroker("localhost:9092").createConsumer(null);
            this.kafkaConsumer.subscribe(Arrays.asList("exsim.nse.marketbyprice"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            marketDepthContainer.getLockObject().lock();
            try {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(java.time.Duration.ofMillis(10));
                for (ConsumerRecord<String, String> record : records) {
                    String symbol = record.key();
                    String data = record.value();
                    byte[] decoded = Base64.getDecoder().decode(data);
                    MarketByPrice marketByPrice = deSerealizeAvroHttpRequestJSON(decoded);
                    marketDepthContainer.setMarketByPrice(marketByPrice);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                marketDepthContainer.getLockObject().unlock();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
    }

    public MarketByPrice deSerealizeAvroHttpRequestJSON(byte[] data) {
        DatumReader<MarketByPrice> reader
                = new SpecificDatumReader<>(MarketByPrice.class);
        Decoder decoder = null;
        try {
            decoder = DecoderFactory.get().jsonDecoder(MarketByPrice.getClassSchema(), new String(data));
            return reader.read(null, decoder);
        } catch (IOException e) {
            //logger.error("Deserialization error:" + e.getMessage());
        }
        return null;
    }
}
