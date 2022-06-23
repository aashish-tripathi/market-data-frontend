package com.ashish.frontend.containers;

import com.ashish.marketdata.avro.MarketPrice;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MarketPriceContainer {
    private Lock lockObject = new ReentrantLock();
    private MarketPrice marketPrice;

    public MarketPriceContainer() {
        this.marketPrice = new MarketPrice();
    }

    public Lock getLockObject() {
        return lockObject;
    }

    public MarketPrice getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(MarketPrice marketPrice) {
        this.marketPrice = marketPrice;
    }
}
