<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Code changelog

This page documents code changes.
For user-facing changes, see [`CHANGELOG.md`][_-1].


  [_-1]: ./CHANGELOG.md


## [Unreleased]

<i>This section is currently empty.</i>


## [0.1.0-alpha-3] - 2024-09-21

### Dependencies

* [@astral-sh/setup-uv][_0.1.0-a3-d1]: 2.1.0 &rarr; 3.1.0
* [Qodana][_0.1.0-a3-d2]: 2024.1.9 &rarr; 2024.2.3
* [`kotlinx-serialization-json`][_0.1.0-a3-d3]: 1.7.2 &rarr; 1.7.3
* [MkDocs][_0.1.0-a3-d4]: 1.6.0 &rarr; 1.6.1
* [Material for MkDocs][_0.1.0-a3-d5]: 9.5.33 &rarr; 9.5.35


  [_0.1.0-a3-d1]: https://github.com/astral-sh/setup-uv
  [_0.1.0-a3-d2]: https://github.com/JetBrains/qodana-action
  [_0.1.0-a3-d3]: https://github.com/Kotlin/kotlinx.serialization
  [_0.1.0-a3-d4]: https://github.com/mkdocs/mkdocs
  [_0.1.0-a3-d5]: https://github.com/squidfunk/mkdocs-material


## [0.1.0-alpha-2] - 2024-09-14

### Dependencies

* [@astral-sh/setup-uv][_0.1.0-a2-d1]: 1.0.0 &rarr; 2.1.0
* [LSP4IJ][_0.1.0-a2-d2]: 0.4.0 &rarr; 0.5.0


  [_0.1.0-a2-d1]: https://github.com/astral-sh/setup-uv
  [_0.1.0-a2-d2]: https://github.com/redhat-developer/lsp4ij


## [0.1.0-alpha-1] - 2024-09-07

Commands and settings integrated:

* Ruff
  * [Language server settings][_0.1.0-a1-1]:
    * Root:
      * `configuration`
      * `fixAll`
      * `organizeImports`
      * `showSyntaxErrors`
      * `logLevel`
      * `logFile`
    * `codeAction`:
      * `disableRuleComment.enable`
      * `fixViolation.enable`
    * `lint`:
      * `enable`
  * [Commands][_0.1.0-a1-2]:
    * `check`: Run Ruff on the given files or directories (default)
    * `format`: Run the Ruff formatter on the given files or directories
    * `rule`: Explain a rule (or all rules)
    * `config`: List or describe the available configuration options
    * `linter`: List all supported upstream linters
    * `version`: Display Ruff's version
    * `clean`: Clear any caches in the current directory and any subdirectories
    * `server`: Run the language server

* uv
  * [Commands][_0.1.0-a1-3]:
    * `init`: Create a new project
    * `add`: Add dependencies to the project
    * `remove`: Remove dependencies from the project
    * `sync`: Update the project's environment
    * `pip`: Manage Python packages with a pip-compatible interface
      * `list`: List, in tabular format, packages installed in an environment
    * `venv`: Create a virtual environment
    * `version`: Display uv's version

* Rye
  * [Commands][_0.1.0-a1-4]:
    * `show`: Prints the current state of the project
    * `version`: Get or set project version
    * `config`: Reads or updates the Rye configuration
      * `--show-path`: Print the path to the config


  [_0.1.0-a1-1]: https://docs.astral.sh/ruff/editors/settings/
  [_0.1.0-a1-2]: https://docs.astral.sh/ruff/configuration/#full-command-line-interface
  [_0.1.0-a1-3]: https://docs.astral.sh/uv/reference/cli/
  [_0.1.0-a1-4]: https://rye.astral.sh/guide/commands/


  [Unreleased]: https://github.com/InSyncWithFoo/ryecharm/compare/v0.1.0-alpha-3..HEAD
  [0.1.0-alpha-3]: https://github.com/InSyncWithFoo/ryecharm/compare/v0.1.0-alpha-2..v0.1.0-alpha-3
  [0.1.0-alpha-2]: https://github.com/InSyncWithFoo/ryecharm/compare/v0.1.0-alpha-1..v0.1.0-alpha-2
  [0.1.0-alpha-1]: https://github.com/InSyncWithFoo/ryecharm/commits
