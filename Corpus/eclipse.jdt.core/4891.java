class F {

    void foo() {
        if (local.useFlag == LocalVariableBinding.UNUSED && // unused (and non secret) local
        (local.declaration != null) && // declaration is reachable
        ((local.declaration.bits & AstNode.IsLocalDeclarationReachableMASK) != 0)) {
        }
    }
}
