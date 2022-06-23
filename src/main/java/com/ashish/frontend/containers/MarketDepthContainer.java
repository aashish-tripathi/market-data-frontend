package com.ashish.frontend.containers;

import com.ashish.marketdata.avro.MarketByPrice;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MarketDepthContainer {
    private Lock lockObject = new ReentrantLock();
    private MarketByPrice marketByPrice;

    public MarketDepthContainer() {
        this.marketByPrice = new MarketByPrice();
    }

    public Lock getLockObject() {
        return lockObject;
    }

    public MarketByPrice getMarketByPrice() {
        return marketByPrice;
    }

    public void setMarketByPrice(MarketByPrice marketByPrice) {
        this.marketByPrice = marketByPrice;
    }
}
