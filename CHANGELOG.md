<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Changelog

This page documents user-facing changes.
For code changes, see [`CHANGELOG_CODE.md`][_-1].


  [_-1]: ./CHANGELOG_CODE.md


## [Unreleased]

<i>This section is currently empty.</i>


## [0.1.0-alpha-7] - 2025-02-02

See [the documentation][0.1.0-a7-1] for more information.

Latest tool versions at the time of release:

* Ruff: [0.9.4][0.1.0-a7-2]
* uv: [0.5.26][0.1.0-a7-3]
* Rye: [0.43.0][0.1.0-a7-4]


### Added

* RyeCharm now supports 2025.1 and 2024.3.2.
* Unsafe fixes can now be applied via the corresponding intention and action.
* When keys for Ruff configuration groups (e.g., `tool.ruff.lint`) are hovered,
  a list of its subkeys will now be shown,
  where each item can be clicked on to view its full description.
* Disabled rule codes in `# noqa` comments can now be re-enabled
  using an intention.


### Removed

* 2024.3.1 is no longer supported.
* The old project generator, package manager and SDK flavor
  have all been removed in favor of the native support added in 2024.3.2.


  [0.1.0-a7-1]: https://insyncwithfoo.github.io/ryecharm/
  [0.1.0-a7-2]: https://github.com/astral-sh/ruff/releases/tag/0.9.4
  [0.1.0-a7-3]: https://github.com/astral-sh/uv/releases/tag/0.5.26
  [0.1.0-a7-4]: https://github.com/astral-sh/rye/releases/tag/0.43.0


## [0.1.0-alpha-6] - 2025-01-20

See [the documentation][0.1.0-a6-1] for more information.

Latest tool versions at the time of release:

* Ruff: [0.9.2][0.1.0-a6-2]
* uv: [0.5.21][0.1.0-a6-3]
* Rye: [0.43.0][0.1.0-a6-4]


### Added

* Ruff's "Fix all" can now be used as an action.
  Previously, this functionality was only accessible via an intention.
* `optional-dependencies` ("extras") now have corresponding install buttons
  in the gutter similar to that of `dependency-groups`.
* Ruff's Markdown error messages are displayed as-is in tooltips by default.
  It is now possible to render them using a new setting.
* Links to Ruff rules in documentation popups are now resolved in-place,
  replacing the current popup content with the rule's documentation.
  Previously, such links would open the browser.


### Changed

* If a Ruff executable is not specified, one will be detected during execution.
  When Ruff is not installed but "Formatting" is enabled,
  the formatter will fail with an error notification.
  As PyCharm triggers the formatter automatically on paste,
  this might cause some confusion.
  The formatter will now not run unless the executable is specified directly
  using the Ruff setting panel.
* The output of `ruff config` is now no longer cached by default.
  This might result in a noticeable delay that would previously
  only happen in the first run.
  The old behaviour can be toggled using a new advanced setting.


### Fixed

* An error notification will now be displayed if
  an invalid range is given to the formatter.
  Previously, this would result in an `IndexOutOfBoundsException`.
* Due to a logic error, RyeCharm would naively run Ruff on Jupyter notebooks.
  This has been fixed.
  Jupyter notebooks have never been supported by any versions of RyeCharm.


  [0.1.0-a6-1]: https://insyncwithfoo.github.io/ryecharm/
  [0.1.0-a6-2]: https://github.com/astral-sh/ruff/releases/tag/0.9.2
  [0.1.0-a6-3]: https://github.com/astral-sh/uv/releases/tag/0.5.21
  [0.1.0-a6-4]: https://github.com/astral-sh/rye/releases/tag/0.43.0


## [0.1.0-alpha-5] - 2024-12-16

See [the documentation][0.1.0-a5-1] for more information.

Latest tool versions at the time of release:

* Ruff: [0.8.3][0.1.0-a5-2]
* uv: [0.5.9][0.1.0-a5-3]
* Rye: [0.43.0][0.1.0-a5-4]


### Added

* User-level `ruff.toml`/`.ruff.toml` file can now be opened using an action.
* "Unable to run command" notifications now have an action
  to view debug information.


### Changed

* `ruff` and `uv` executables from `PATH` are now prioritized during detection.
  Previously, those installed by Rye would be checked first.


### Removed

* 2024.3 is no longer supported.
* Timeout settings are removed entirely. To cancel long-running processes,
  use the cross buttons next to progress indicators.


### Fixed

* Previously, due to a bug in the executable detector, the UI might freeze.
  This has been fixed.
* Path input fields (e.g., configuration files) now function correctly.


  [0.1.0-a5-1]: https://insyncwithfoo.github.io/ryecharm/
  [0.1.0-a5-2]: https://github.com/astral-sh/ruff/releases/tag/0.8.3
  [0.1.0-a5-3]: https://github.com/astral-sh/uv/releases/tag/0.5.9
  [0.1.0-a5-4]: https://github.com/astral-sh/rye/releases/tag/0.43.0


## [0.1.0-alpha-4] - 2024-11-22

See [the documentation][0.1.0-a4-1] for more information.

Latest tool versions at the time of release:

* Ruff: [0.7.4][0.1.0-a4-2]
* uv: [0.5.4][0.1.0-a4-3]
* Rye: [0.42.0][0.1.0-a4-4]


### Added

* File-level diagnostics by Ruff can now be displayed as editor banners.

* <i>LSP4IJ</i> mode now respects many settings
  that were previously ignored due to technical limitations.

* It is now possible to snooze notifications on formatting failures.

* [PEP 735][0.1.0-a4-a-1] support is added:

  * Arrays under the `dependency-groups` table will have language injection
    similar to that of `project.optional-dependencies` and others.
  
  * Errors will be reported if:
  
    * Two groups have the same name, [normalized][0.1.0-a4-a-2] or otherwise.
    * A group [includes][0.1.0-a4-a-3] an invalid or non-existent group.
    * A group references itself.
  
  * Group references will be detected and highlighted on focus.

  * Groups can be installed by clicking their corresponding line markers.

* Usages of [`uv.dev-dependencies`][0.1.0-a4-a-4] are now reported.
  This field is deprecated as of [uv 0.4.27][0.1.0-a4-a-5];
  it should be replaced with `dependency-groups.dev`.

* Dependency specifier strings in `pyproject.toml` and `uv.toml`
  now have the currently installed versions of the dependency
  displayed next to them as inlay hints.

  By default, dependency specifiers in the following arrays are recognized:

  * `project.dependencies`
  * `project.optional-dependencies.*`
  * `dependency-groups.*`
  * \[`tool.uv`] `dev-dependencies`

* Links to Ruff options in documentation popups are now resolved in-place,
  replacing the current popup content with the option's documentation.
  Previously, such links would open the browser.


### Changed

* RyeCharm's documentation popups are now prioritized over that of LSP4IJ.
  Both will be displayed in a paged popup, with RyeCharm's as the first.
  Previously, LSP4IJ's would take precedence.
* Executable suggestion notifications will now automatically disappear
  after 30 seconds. Previously, they would remain indefinitely on the screen.
* `.pyw` files are now recognized as supported by Ruff,
  similar to `.py` and `.pyi` files.
* Commands and their outputs are no longer logged by default.
  To restore the old behaviour, enable the
  `insyncwithfoo.ryecharm.logging.commands` registry entry.
* The import optimizer and formatter will now show a notification
  if they cannot be run or if the process failed.


### Removed

* 2024.2 and older versions are no longer supported.


### Fixed

* <i>Native LSP client</i> mode now respects the "Report syntax errors" setting.
* Script metadata blocks will now have their trailing newlines stripped
  before being written back to the original file.
* New virtual environments created using the <i>uv</i> panel
  now have their names detected correctly.
  Previously, a threading error would be reported.
* Ruff's violation fixes with multiple edits are now applied correctly
  in <i>Command line</i> mode.
* The plugin now creates an informational-only
  project generation panel on IntelliJ IDEA.
  Previously, a `RuntimeException` would be thrown.


  [0.1.0-a4-1]: https://insyncwithfoo.github.io/ryecharm/
  [0.1.0-a4-2]: https://github.com/astral-sh/ruff/releases/tag/0.7.4
  [0.1.0-a4-3]: https://github.com/astral-sh/uv/releases/tag/0.5.4
  [0.1.0-a4-4]: https://github.com/astral-sh/rye/releases/tag/0.42.0
  [0.1.0-a4-a-1]: https://peps.python.org/pep-0735/
  [0.1.0-a4-a-2]: https://packaging.python.org/en/latest/specifications/name-normalization/#name-normalization
  [0.1.0-a4-a-3]: https://peps.python.org/pep-0735/#dependency-group-include
  [0.1.0-a4-a-4]: https://docs.astral.sh/uv/concepts/dependencies/#legacy-dev-dependencies
  [0.1.0-a4-a-5]: https://github.com/astral-sh/uv/releases/tag/0.4.27


## [0.1.0-alpha-3] - 2024-09-21

See [the documentation][0.1.0-a3-1] for more information.

Latest tool versions at the time of release:

* Ruff: [0.6.6][0.1.0-a3-2]
* uv: [0.4.14][0.1.0-a3-3]
* Rye: [0.39.0][0.1.0-a3-4]


### Added

* Script metadata blocks can now be edited in a separate editor.
  The new content will be written back when this editor is closed.
* Ruff can now be configured to fix all problems on save.
* Documentation is now provided when rule selectors
  specified in the following settings are hovered:

  * `lint.extend-fixable`
  * `lint.extend-ignore`
  * `lint.extend-per-file-ignores.*`
  * `lint.extend-safe-fixes`
  * `lint.extend-select`
  * `lint.extend-unfixable`
  * `lint.extend-unsafe-fixes`
  * `lint.fixable`
  * `lint.ignore`
  * `lint.per-file-ignores.*`
  * `lint.select`
  * `lint.unfixable`

  The corresponding deprecated top-level settings are also recognized.


### Changed

* Error and information notifications are now splitted to different groups.
* A few settings' serialization names are changed.
* Notifications are no longer shown when an on-save task fail.


### Fixed

* Ruff is now no longer run on `.rst` files.
  Due to how `.rst` files are implemented,
  they were incorrectly determined to be Python files.


  [0.1.0-a3-1]: https://insyncwithfoo.github.io/ryecharm/
  [0.1.0-a3-2]: https://github.com/astral-sh/ruff/releases/tag/0.6.6
  [0.1.0-a3-3]: https://github.com/astral-sh/uv/releases/tag/0.4.14
  [0.1.0-a3-4]: https://github.com/astral-sh/rye/releases/tag/0.39.0


## [0.1.0-alpha-2] - 2024-09-14

See [the documentation][0.1.0-a2-1] for more information.

Latest tool versions at the time of release:

* Ruff: [0.6.5][0.1.0-a2-2]
* uv: [0.4.10][0.1.0-a2-3]
* Rye: [0.39.0][0.1.0-a2-4]


### Added

* Autocompletions are now provided for `ruff` and `uv` commands
  when [the new terminal][0.1.0-a2-a-1] is used.

* [PEP 723][0.1.0-a2-a-2] inline script metadata blocks
  now have TOML [injected][0.1.0-a2-a-3].

* Various uv [PEP 508][0.1.0-a2-a-4] dependency arrays settings now have
  injections similar to [that of `project.dependencies`][0.1.0-a2-a-5]:

  * `constraint-dependencies`
  * `dev-dependencies`
  * `override-dependencies`
  * `upgrade-package`
  * `pip.upgrade-package`

  As a bonus, `project.optional-dependencies` is also supported.
  This monkeypatches [PY-71120][0.1.0-a2-a-6].

* Ruff's command line mode now supports
  "fix all" and "organize imports" as intentions.

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
    * TOML setting keys

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


  [Unreleased]: https://github.com/InSyncWithFoo/ryecharm/compare/v0.1.0-alpha-7..HEAD
  [0.1.0-alpha-7]: https://github.com/InSyncWithFoo/ryecharm/compare/v0.1.0-alpha-6..v0.1.0-alpha-7
  [0.1.0-alpha-6]: https://github.com/InSyncWithFoo/ryecharm/compare/v0.1.0-alpha-5..v0.1.0-alpha-6
  [0.1.0-alpha-5]: https://github.com/InSyncWithFoo/ryecharm/compare/v0.1.0-alpha-4..v0.1.0-alpha-5
  [0.1.0-alpha-4]: https://github.com/InSyncWithFoo/ryecharm/compare/v0.1.0-alpha-3..v0.1.0-alpha-4
  [0.1.0-alpha-3]: https://github.com/InSyncWithFoo/ryecharm/compare/v0.1.0-alpha-2..v0.1.0-alpha-3
  [0.1.0-alpha-2]: https://github.com/InSyncWithFoo/ryecharm/compare/v0.1.0-alpha-1..v0.1.0-alpha-2
  [0.1.0-alpha-1]: https://github.com/InSyncWithFoo/ryecharm/commits
