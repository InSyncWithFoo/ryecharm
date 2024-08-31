RyeCharm can trigger Ruff to format a file on three events:

* File save (<kbd>Ctrl</kbd> + <kbd>S</kbd>, editor close, IDE close, etc.)
* Reformat (<kbd>Ctrl</kbd> + <kbd>Alt</kbd> + <kbd>L</kbd> or similar)
* Optimize imports (<kbd>Ctrl</kbd> + <kbd>Alt</kbd> + <kbd>O</kbd> or similar)

Unsupported files are not affected.


## File save

Everytime a file is [saved][1], automatically or manually,
RyeCharm will run Ruff on it if it is a supported file.

This is equivalent to running `ruff format --quiet` with the file as input.


## Reformat

This event happens when the [<i>Reformat Code</i>][2] action
(<kbd>Ctrl</kbd> + <kbd>Alt</kbd> + <kbd>L</kbd> or similar)
is triggered, possibly with some code selected.

This is equivalent to running `ruff format --quiet --range ...`
with the file and the selected range as input.


## Optimize imports

This event happens when the [<i>Optimize Imports</i>][3] action
(<kbd>Ctrl</kbd> + <kbd>Alt</kbd> + <kbd>O</kbd> or similar)
is triggered.

This is equivalent to running `ruff check --fix --exit-zero --quiet --select I`
with the file as input.


  [1]: https://www.jetbrains.com/help/pycharm/saving-and-reverting-changes.html
  [2]: https://www.jetbrains.com/help/pycharm/reformat-and-rearrange-code.html
  [3]: https://www.jetbrains.com/help/pycharm/creating-and-optimizing-imports.html#optimize-imports
