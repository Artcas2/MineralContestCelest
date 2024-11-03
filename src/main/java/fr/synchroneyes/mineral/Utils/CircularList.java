package fr.synchroneyes.mineral.Utils;

import java.util.LinkedList;

public class CircularList<E> extends LinkedList<E> {
    @Override
    public E get(int index) {
        return super.get(index % this.size());
    }
}

