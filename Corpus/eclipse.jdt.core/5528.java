package test1;

import libs.MyMap;

public class Test1 {

    String test1(MyMap<String, Test1> map) {
        // key is OK (@Nullable), val is err (@NonNull)
        map.put(null, null);
        // err: key is @NonNull via eea
        Test1 v = map.get(null);
        // err: v is @Nullable via eea
        return v.toString();
    }
}
