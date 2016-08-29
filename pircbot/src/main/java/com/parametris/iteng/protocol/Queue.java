package com.parametris.iteng.protocol;

import java.util.Vector;

public class Queue {

    private final Vector<Object> queue = new Vector<>();

    public void add(Object object) {
        synchronized (queue) {
            queue.addElement(object);
            queue.notify();
        }
    }

    public Object next() {
        Object object;

        synchronized (queue) {
            if (queue.size() == 0) {
                try {
                    queue.wait();
                } catch (InterruptedException e) {
                    return null;
                }
            }

            try {
                object = queue.firstElement();
                queue.removeElementAt(0);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new InternalError("TERJADI BALAPAN LIAR");
            }
        }

        return object;
    }

    public int size() {
        return queue.size();
    }
}
