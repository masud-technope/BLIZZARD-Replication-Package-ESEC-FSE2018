class F {

    void foo() {
        if (local.useFlag == LocalVariableBinding.UNUSED && // unused (and non secret) local
        (local.declaration != null) && ((local.declaration.bits & AstNode.IsLocalDeclarationReachableMASK) != // declaration is reachable
        0)) {
        }
    }
}
