package p;

import static java.util.Arrays.*;
import java.util.List;

class Bug {

    static final String[] side = new String[0];

    {
        List<String> asList = asList(side);
        if (true) {
            System.out.println(asList);
        } else {
            System.out.println(asList);
        }
    }
}
