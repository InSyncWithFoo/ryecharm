[Rye][1] was meant to be an all-in-one toolchain and package manager.
Most of its features are now supported by uv,
and it itself is going to be deprecated sometime in the future.

Thus, only the following subcommands are supported:

* `show`: Show the current project's information
* `version`: Change the project's version
* `config --show-path`: Show the path to Rye's configuration file

Replacements:

* Formatting and linting: [Use Ruff][2] directly.
* Testing: [Use Pytest][3] directly.
* Project and package management: [Use uv][4] directly.



  [1]: https://rye.astral.sh/
  [2]: ../ruff/index.md
  [3]: https://www.jetbrains.com/help/pycharm/pytest.html
  [4]: ../uv/index.md
