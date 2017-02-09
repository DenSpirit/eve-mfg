package eve.apol.yamlparsing;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class PeekIterator<E> implements Iterator<E> {
    
    private E first;
    private Iterator<E> it;
    
    public static <E> PeekIterator<E> iterator(Iterator<E> it) {
        PeekIterator<E> newIt = new PeekIterator<>();
        newIt.it = it;
        newIt.first = it.hasNext() ? it.next() : null;
        return newIt;
    }

    @Override
    public boolean hasNext() {
        return first != null;
    }

    @Override
    public E next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        E next = first;
        first = it.hasNext() ? it.next() : null;
        return next;
    }
    
    public E peek() {
        return first;
    }

}
