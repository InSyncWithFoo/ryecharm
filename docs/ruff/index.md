[Ruff][1] is a linter and formatter.
While these features are mainly used via the command line,
they are also provided via [the language server protocol][2]
when Ruff is run as a language server.

RyeCharm makes use of both aforementioned ways
and allow choosing one over the other via [running modes][3].


## Supported files

Ruff itself supports `.py`, `.pyi` and `.ipynb` files.
This plugin has yet to support the lattermost.


  [1]: https://docs.astral.sh/ruff/
  [2]: https://microsoft.github.io/language-server-protocol/
  [3]: ../configurations/ruff.md#running-mode
