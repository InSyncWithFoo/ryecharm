package insyncwithfoo.ryecharm


internal interface Builder


internal operator fun <B : Builder> B.invoke(block: B.() -> Unit) {
    this.apply(block)
}
