package test.task.rest.helpers;

import java.util.HashMap;

public class SearchParams<K, V> extends HashMap<K, V> {

    public static SearchParams<String, String> searchParams() {
        return new SearchParams<>();
    }

    public SearchParams<K, V> e(K key, V value) {
        this.put(key, value);
        return this;
    }
}
