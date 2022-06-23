package com.ashish.frontend.containers;

import com.ashish.marketdata.avro.Order;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OrderExecutionsContainer {
    private Lock lockObject = new ReentrantLock();
    private Order order;
    private Order lastOrder;

    public OrderExecutionsContainer() {
        this.order = new Order();
        this.lastOrder = order;
    }

    public Lock getLockObject() {
        return lockObject;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Order getLastOrder() {
        return lastOrder;
    }

    public void setLastOrder(Order lastOrder) {
        this.lastOrder = lastOrder;
    }
}
