package test.comments.block;

public class X15 {

    static class CharOperation {

        static boolean prefixEquals(char[] token, char[] proposalName, boolean b) {
            return false;
        }

        static boolean equals(char[] token, char[] proposalName, boolean b) {
            return false;
        }
    }

    int computeRelevanceForCaseMatching(char[] token, char[] proposalName) {
        if (CharOperation.prefixEquals(token, proposalName, /* do not ignore case */
        true)) {
            if (CharOperation.equals(token, proposalName, /* do not ignore case */
            true)) {
                return 0;
            } else {
                return 1;
            }
        } else {
            return 0;
        }
    }
}
