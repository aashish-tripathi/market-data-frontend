package com.ashish.frontend.updaters;

import com.ashish.frontend.MarketDataViewer;
import com.ashish.frontend.broker.KafkaBroker;
import com.ashish.frontend.containers.MarketPriceContainer;
import com.ashish.marketdata.avro.MarketPrice;
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

public class MarketPriceUpdater extends Thread{
    private MarketPriceContainer pricesContainer;
    private KafkaConsumer<String, String> kafkaConsumer;
    private boolean kafka;

    public MarketPriceUpdater(MarketPriceContainer pricesContainer, boolean kafka) {
        this.pricesContainer = pricesContainer;
        this.kafka = kafka;
        this.kafkaConsumer = new KafkaBroker("localhost:9092").createConsumer(null);
        this.kafkaConsumer.subscribe(Arrays.asList("exsim.nse.marketprice"));
    }

    @Override
    public void run() {

        while (true) {
            pricesContainer.getLockObject().lock();
            try {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(java.time.Duration.ofMillis(10));
                for (ConsumerRecord<String, String> record : records) {
                    String symbol = record.key();
                    String data = record.value();
                    byte[] decoded = Base64.getDecoder().decode(data);
                    {
                        MarketPrice marketPrice = deSerealizeAvroHttpRequestJSON(decoded);
                        pricesContainer.setMarketPrice(marketPrice);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                pricesContainer.getLockObject().unlock();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
    }

    public MarketPrice deSerealizeAvroHttpRequestJSON(byte[] data) {
        DatumReader<MarketPrice> reader
                = new SpecificDatumReader<>(MarketPrice.class);
        Decoder decoder = null;
        try {
            decoder = DecoderFactory.get().jsonDecoder(MarketPrice.getClassSchema(), new String(data));
            return reader.read(null, decoder);
        } catch (IOException e) {
            //logger.error("Deserialization error:" + e.getMessage());
        }
        return null;
    }
}
