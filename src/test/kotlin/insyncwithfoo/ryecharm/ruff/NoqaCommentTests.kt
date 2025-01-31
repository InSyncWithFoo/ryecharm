package insyncwithfoo.ryecharm.ruff


internal fun comment(text: String, elementOffset: Int = 0) =
    NoqaComment.parse(text, elementOffset)!!
