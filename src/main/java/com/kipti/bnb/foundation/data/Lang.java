package com.kipti.bnb.foundation.data;

import java.util.Locale;

/** Name-mangling helpers used during registration. Deliberately free of any client type. */
public final class Lang {

    private Lang() {
    }

    public static String asId(final String name) {
        return name.toLowerCase(Locale.ROOT);
    }

    public static String nonPluralId(final String id) {
        return id.endsWith("s") ? id.substring(0, id.length() - 1) : id;
    }
}
