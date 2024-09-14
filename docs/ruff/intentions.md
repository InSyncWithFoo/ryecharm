## Fix all

This intention is always available,
given that the [running mode][1] is set to <i>Command line</i>.

This is equivalent to running `ruff check --fix` with the file as input.


## Organize imports

This intention is always available,
given that the [running mode][1] is set to <i>Command line</i>.

It sorts the imports of the file, but does not remove those unused,
unlike <i>[Optimize imports][2]</i>.

This is equivalent to running `ruff check --fix select I`
with the file as input.


  [1]: running-modes.md
  [2]: formatting.md#optimize-imports
