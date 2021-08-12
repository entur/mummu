package no.entur.mummu.util;

import java.util.Comparator;

public class NetexIdComparator implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        return Integer.compare(
                Integer.parseInt(o1.split(":")[2]),
                Integer.parseInt(o2.split(":")[2])
        );
    }
}
