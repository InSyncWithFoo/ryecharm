package insyncwithfoo.ryecharm.rye.commands

import insyncwithfoo.ryecharm.CommandArguments


internal fun Rye.configDirectory() =
    config(CommandArguments("--show-path"))
