package com.ashish.frontend.containers;

import com.ashish.marketdata.avro.Quote;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MarketQuoteContainer {
    private Lock lockObject = new ReentrantLock();
    private Quote quote;
    private Quote lastQuote;

    public MarketQuoteContainer() {
        this.quote = new Quote();
        this.lastQuote = quote;
    }

    public Lock getLockObject() {
        return lockObject;
    }

    public Quote getQuote() {
        return quote;
    }

    public void setQuote(Quote quote) {
        this.quote = quote;
    }

    public Quote getLastQuote() {
        return lastQuote;
    }

    public void setLastQuote(Quote lastQuote) {
        this.lastQuote = lastQuote;
    }
}
