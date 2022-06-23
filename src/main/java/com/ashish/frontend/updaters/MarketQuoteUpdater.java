package com.ashish.frontend.updaters;

import com.ashish.frontend.MarketDataViewer;
import com.ashish.frontend.broker.KafkaBroker;
import com.ashish.frontend.containers.MarketQuoteContainer;
import com.ashish.marketdata.avro.Quote;
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

public class MarketQuoteUpdater extends Thread {
    private MarketQuoteContainer quoteContainer;
    private KafkaConsumer<String, String> kafkaConsumer;

    private boolean kafka;

        public MarketQuoteUpdater(MarketQuoteContainer quoteContainer, boolean kafka) {
        this.quoteContainer = quoteContainer;
        this.kafka = kafka;
        try {
            this.kafkaConsumer = new KafkaBroker("localhost:9092").createConsumer(null);
            this.kafkaConsumer.subscribe(Arrays.asList("exsim.nse.quotes"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            quoteContainer.getLockObject().lock();
            try {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(java.time.Duration.ofMillis(10));
                for (ConsumerRecord<String, String> record : records) {
                    String symbol = record.key();
                    String data = record.value();
                    byte[] decoded = Base64.getDecoder().decode(data);
                    {
                        Quote quote = deSerealizeAvroHttpRequestJSON(decoded);
                        quoteContainer.setQuote(quote);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                quoteContainer.getLockObject().unlock();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
    }

    public Quote deSerealizeAvroHttpRequestJSON(byte[] data) {
        DatumReader<Quote> reader
                = new SpecificDatumReader<>(Quote.class);
        Decoder decoder = null;
        try {
            decoder = DecoderFactory.get().jsonDecoder(Quote.getClassSchema(), new String(data));
            return reader.read(null, decoder);
        } catch (IOException e) {
            //logger.error("Deserialization error:" + e.getMessage());
        }
        return null;
    }
}


