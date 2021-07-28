package org.jnode.util;

import java.util.Collection;

public class StringUtils {
    public static String join(String s, CharSequence... sequences) {
        if (sequences == null)
            return "";
        int iMax = sequences.length - 1;
        if (iMax == -1)
            return "";

        StringBuilder b = new StringBuilder();
        for (int i = 0; ; i++) {
            b.append(sequences[i]);
            if (i == iMax)
                return b.append(']').toString();
            b.append(s);
        }
    }

    public static String join(char c, CharSequence... sequences) {
        return join(Character.toString(c), sequences);
    }

    public static String join(String s, Collection<? extends CharSequence> sequences) {
        return join(s, sequences.toArray(new CharSequence[]{}));
    }

    public static String join(char c, Collection<? extends CharSequence> sequences) {
        return join(Character.toString(c), sequences);
    }
}
