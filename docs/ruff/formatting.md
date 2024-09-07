RyeCharm can trigger Ruff to format a file on three events:

* Reformat (<kbd>Ctrl</kbd> + <kbd>Alt</kbd> + <kbd>L</kbd>)
* Optimize imports (<kbd>Ctrl</kbd> + <kbd>Alt</kbd> + <kbd>O</kbd>)
* File save (<kbd>Ctrl</kbd> + <kbd>S</kbd>, editor tab close, IDE close, etc.)

Unsupported files are not affected.


## Reformat

This event happens when the [<i>Reformat Code</i>][1] action
(<kbd>Ctrl</kbd> + <kbd>Alt</kbd> + <kbd>L</kbd>)
is triggered, possibly with some code selected.

In <i>Command line</i> mode, this is equivalent to
running `ruff format --quiet --range ...`
with the file and the selected range as input.


## Optimize imports

This event happens when the [<i>Optimize Imports</i>][2] action
(<kbd>Ctrl</kbd> + <kbd>Alt</kbd> + <kbd>O</kbd>) is triggered.

This is equivalent to running `ruff check --fix --exit-zero --quiet --select I`
with the file as input.


## File save

This event happens when a file is [saved][3], automatically or manually.

This is equivalent to running `ruff format --quiet` with the file as input.


  [1]: https://www.jetbrains.com/help/pycharm/reformat-and-rearrange-code.html
  [2]: https://www.jetbrains.com/help/pycharm/creating-and-optimizing-imports.html#optimize-imports
  [3]: https://www.jetbrains.com/help/pycharm/saving-and-reverting-changes.html
