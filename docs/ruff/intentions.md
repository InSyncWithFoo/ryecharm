## Fix all

This intention is always available,
given that the [running mode][1] is set to <i>Command line</i>.

This is equivalent to running `ruff check --fix` with the file as input.


## Fix all, unsafe included

This intention is always available.

This is equivalent to running `ruff check --fix --unsafe-fixes`
with the file as input.


## Organize imports

This intention is always available,
given that the [running mode][1] is set to <i>Command line</i>.

It sorts the imports of the file, but does not remove those unused,
unlike <i>[Optimize imports][2]</i>.

This is equivalent to running `ruff check --fix select I`
with the file as input.


## Reenable rule

This intention is available when the cursor is placed
in the range of a `# noqa` rule code.

It removes that rule code in-place.
If that code is the only code, the entire comment will be removed.

=== "Before"
    ![](../assets/ruff-intentions-reenable-rule-demo-before.png)

=== "After"
    ![](../assets/ruff-intentions-reenable-rule-demo-after.png)


  [1]: running-modes.md
  [2]: formatting.md#optimize-imports
