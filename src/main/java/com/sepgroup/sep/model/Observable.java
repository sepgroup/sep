package com.sepgroup.sep.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeremybrown on 2016-05-18.
 */
public abstract class Observable {

    private List<Observer> observers = new ArrayList<>();

    /**
     * Register an observer to this object to be notified when this object is updated.
     * @param o the Observer to register
     */
    public void registerObserver(Observer o) {
        observers.add(o);
    }

    /**
     * Unregister an observer from the list of registered Observers.
     * @param o the Observer to unregister
     */
    public void unregisterObserver(Observer o) {
        observers.remove(o);
    }

    /**
     * Notify all observers in this object's list of Observers to update.
     */
    public void updateObservers() {
        observers.forEach(o -> o.update());
    }
}
