package com.ashish.frontend.containers;

import com.ashish.marketdata.avro.Trade;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MarketTradeContainer {
    private Lock lockObject = new ReentrantLock();
    private Trade trade;
    private Trade lastTrade;

    public MarketTradeContainer() {
        this.trade = new Trade();
        this.lastTrade = trade;
    }

    public Lock getLockObject() {
        return lockObject;
    }

    public Trade getTrade() {
        return trade;
    }

    public void setTrade(Trade trade) {
        this.trade = trade;
    }

    public Trade getLastTrade() {
        return lastTrade;
    }

    public void setLastTrade(Trade lastTrade) {
        this.lastTrade = lastTrade;
    }
}
