<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Changelog

This page documents user-facing changes.
For code changes, see [`CHANGELOG_CODE.md`][_-1].


  [_-1]: ./CHANGELOG_CODE.md


## [Unreleased]

### Removed

* PyCharm 2024.2.1 and older are no longer supported.


## [0.1.0-alpha-2] - 2024-09-14

See [the documentation][0.1.0-a2-1] for more information.

Latest tool versions at the time of release:

* Ruff: [0.6.4][0.1.0-a2-2]
* uv: [0.4.10][0.1.0-a2-3]
* Rye: [0.39.0][0.1.0-a2-4]


### Added

* Autocompletions are now provided for `ruff` and `uv` commands
  when [the new terminal][0.1.0-a2-a-1] is used.

* [PEP 723][0.1.0-a2-a-2] inline script metadata blocks
  now have TOML [injected][0.1.0-a2-a-3].

* Various uv [PEP 508][0.1.0-a2-a-4] dependency arrays settings now have
  injections similar to [that of `project.dependencies`][0.1.0-a2-a-5]:

  * \[`tool.uv`] `constraint-dependencies`
  * \[`tool.uv`] `dev-dependencies`
  * \[`tool.uv`] `override-dependencies`
  * \[`tool.uv`] `upgrade-package`
  * \[`tool.uv`] `pip.upgrade-package`

  As a bonus, `project.optional-dependencies.*` is also supported.
  This monkeypatches [PY-71120][0.1.0-a2-a-6].

* Ruff's command line mode now supports
  "fix all" and "organize import" as intentions.

### Changed

* Projects not generated using the <i>uv</i> panel can now
  independently configure whether uv should be used as the package manager.

### Fixed

* "Bump project version" now no longer runs when
  the project's path cannot be located.
  This helps avoiding accidental resources usage and unwanted results.


  [0.1.0-a2-1]: https://insyncwithfoo.github.io/ryecharm/
  [0.1.0-a2-2]: https://github.com/astral-sh/ruff/releases/tag/0.6.5
  [0.1.0-a2-3]: https://github.com/astral-sh/uv/releases/tag/0.4.10
  [0.1.0-a2-4]: https://github.com/astral-sh/rye/releases/tag/0.39.0
  [0.1.0-a2-a-1]: https://blog.jetbrains.com/idea/2024/02/the-new-terminal-beta-is-now-in-jetbrains-ides/
  [0.1.0-a2-a-2]: https://peps.python.org/pep-0723/
  [0.1.0-a2-a-3]: https://www.jetbrains.com/help/pycharm/using-language-injections.html
  [0.1.0-a2-a-4]: https://peps.python.org/pep-0508/
  [0.1.0-a2-a-5]: https://www.jetbrains.com/help/pycharm/pyproject-toml-support.html#specify-project-dependencies
  [0.1.0-a2-a-6]: https://youtrack.jetbrains.com/issue/PY-71120


## [0.1.0-alpha-1] - 2024-09-07

RyeCharm is an all-in-one plugin for working with
[Astral][0.1.0-a1-1]-backed tools. Supported features include:

* [Ruff][0.1.0-a1-2]:
  * Linting:
    * On-the-fly (command line tool)
    * Language server
  * Quick fixes:
    * Fix all violations
    * Organize imports
    * Disable rule for line
    * Automatic fix
  * Formatting:
    * On save
    * On reformat
    * On optimize imports
  * Documentation on hover:
    * Rule codes
    * TOML options

* [uv][0.1.0-a1-3]:
  * Package management
  * Project generation
  * Synchronizing from editor

* [Rye][0.1.0-a1-4]:
  * Version bumping from editor

See [the documentation][0.1.0-a1-5] for more information.


  [0.1.0-a1-1]: https://astral.sh/
  [0.1.0-a1-2]: https://docs.astral.sh/ruff/
  [0.1.0-a1-3]: https://docs.astral.sh/uv/
  [0.1.0-a1-4]: https://rye.astral.sh/
  [0.1.0-a1-5]: https://insyncwithfoo.github.io/ryecharm


  [Unreleased]: https://github.com/InSyncWithFoo/ryecharm/compare/v0.1.0-alpha-2..HEAD
  [0.1.0-alpha-2]: https://github.com/InSyncWithFoo/ryecharm/compare/v0.1.0-alpha-1..v0.1.0-alpha-2
  [0.1.0-alpha-1]: https://github.com/InSyncWithFoo/ryecharm/commits
