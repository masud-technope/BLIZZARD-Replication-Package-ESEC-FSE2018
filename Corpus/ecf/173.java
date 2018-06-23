package org.eclipse.ecf.internal.bulletinboard.commons.parsing;

import java.util.regex.Pattern;

public class DefaultPatternDescriptor {

    public static IPatternDescriptor defaultIdAndName(Pattern pattern) {
        return new BasePatternDescriptor(pattern, new String[] { IPatternDescriptor.ID_PARAM, IPatternDescriptor.NAME_PARAM });
    }

    public static IPatternDescriptor reverseIdAndName(Pattern pattern) {
        return new BasePatternDescriptor(pattern, new String[] { IPatternDescriptor.NAME_PARAM, IPatternDescriptor.ID_PARAM });
    }

    public static IPatternDescriptor defaultCustom(Pattern pattern, String[] parameters) {
        return new BasePatternDescriptor(pattern, parameters);
    }
}
