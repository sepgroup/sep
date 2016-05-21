package com.sepgroup.sep;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeremybrown on 2016-05-18.
 */
public abstract class Observable {

    private List<Observer> observers = new ArrayList<>();

    public void registerObserver(Observer o) {
        observers.add(o);
    }

    public void unregisterObserver(Observer o) {
        observers.remove(o);
    }

    public void updateObservers() {
        observers.forEach(o -> o.update());
    }
}
