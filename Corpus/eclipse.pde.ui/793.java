package org.eclipse.pde.internal.ui.views.imagebrowser.filter;

import org.eclipse.pde.internal.ui.util.StringMatcher;
import org.eclipse.pde.internal.ui.views.imagebrowser.ImageElement;

/**
 *
 * Image filter that user string pattern like "my*icon", vs PatternFilter which
 * user regular expessions.
 *
 */
public class StringFilter implements IFilter {

    private final StringMatcher mPattern;

    public  StringFilter(final String pattern) {
        mPattern = new StringMatcher(pattern, true, false) {

            @Override
            public String toString() {
                return fPattern;
            }
        };
    }

    @Override
    public boolean accept(final ImageElement element) {
        //$NON-NLS-1$
        return mPattern.match(element.getPlugin() + "/" + element.getPath());
    }

    @Override
    public String toString() {
        //$NON-NLS-1$
        return "match " + mPattern.toString();
    }
}
