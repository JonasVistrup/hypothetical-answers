package Jonas;

import java.util.Iterator;

public class Utils {
    public static <T> Iterable<T> concat(Iterable<T> t1, Iterable<T> t2){
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    final Iterator<T> iterator1 = t1.iterator();
                    final Iterator<T> iterator2 = t2.iterator();
                    @Override
                    public boolean hasNext() {
                        return iterator1.hasNext() || iterator2.hasNext();
                    }

                    @Override
                    public T next() {
                        return iterator1.hasNext()? iterator1.next():iterator2.next();
                    }
                };
            }
        };
    }

}
