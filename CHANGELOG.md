<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Changelog

This page documents user-facing changes.
For code changes, see [`CHANGELOG_CODE.md`][_-1].


  [_-1]: ./CHANGELOG_CODE.md


## [Unreleased]

<i>This section is currently empty.</i>


## [0.1.0-alpha-14] - 2025-06-29

See [the documentation][0.1.0-a14-1] for more information.

Latest tool versions at the time of release:

* Ruff: [0.12.1][0.1.0-a14-2]
* uv: [0.7.16][0.1.0-a14-3]
* Rye: [0.44.0][0.1.0-a14-4]
* ty: [0.0.1-alpha.12][0.1.0-a14-5]


### Changed

* The "Bump project version" intention now uses uv instead of Rye.


### Removed

* 2025.1.1.1 and lower are no longer supported.
* RyeCharm-provided uv run configuration type and factories have been removed.
  Use native uv run configurations instead.
* ty's completion setting has been removed.
  It has never worked, and ty itself no longer recognizes it.


### Fixed

* Previously, the native client did not support `textDocument/diagnostic`.
  2025.1.2 added support for it, but the client does not send such requests
  by default, resulting in no diagnostics being displayed.
  This has been fixed.
* Prior to this version, ty's diagnostics might be out-of-sync.
  This is no longer an issue.
* Due to an UI bug, the formatter and import optimizer would not work
  until Ruff's executable input field is modified.
  Now, instead of being prefilled, executable fields will have
  accompanying "Detect" buttons.


  [0.1.0-a14-1]: https://insyncwithfoo.github.io/ryecharm/
  [0.1.0-a14-2]: https://github.com/astral-sh/ruff/releases/tag/0.12.1
  [0.1.0-a14-3]: https://github.com/astral-sh/uv/releases/tag/0.7.16
  [0.1.0-a14-4]: https://github.com/astral-sh/rye/releases/tag/0.44.0
  [0.1.0-a14-5]: https://github.com/astral-sh/ty/releases/tag/0.0.1-alpha.12


## [0.1.0-alpha-13] - 2025-05-18

See [the documentation][0.1.0-a13-1] for more information.

Latest tool versions at the time of release:

* Ruff: [0.11.10][0.1.0-a13-2]
* uv: [0.7.5][0.1.0-a13-3]
* Rye: [0.44.0][0.1.0-a13-4]
* ty: [0.0.1-alpha.5][0.1.0-a13-5]


### Added

* Lock files for standalone scripts (`*.py.lock`)
  will now be recognized as TOML files.
* Provisional ty integration has been added.


### Removed

* 2024.3 and lower are no longer supported.


### Fixed

* Previously, dependency tree popups might not be displayed correctly
  if the interpreter cannot be detected by uv.
  Now, RyeCharm will always pass the project's interpreter explicitly.
* Ruff's global inspection will now be run after first saving documents
  to avoid out-of-sync results.
* `pyproject.toml` files used to be passed to Ruff's server,
  which only ever expects Python files.
  This has been fixed.


  [0.1.0-a13-1]: https://insyncwithfoo.github.io/ryecharm/
  [0.1.0-a13-2]: https://github.com/astral-sh/ruff/releases/tag/0.11.10
  [0.1.0-a13-3]: https://github.com/astral-sh/uv/releases/tag/0.7.5
  [0.1.0-a13-4]: https://github.com/astral-sh/rye/releases/tag/0.44.0
  [0.1.0-a13-5]: https://github.com/astral-sh/ty/releases/tag/0.0.1-alpha.5


## [0.1.0-alpha-12] - 2025-04-20

See [the documentation][0.1.0-a12-1] for more information.

Latest tool versions at the time of release:

* Ruff: [0.11.6][0.1.0-a12-2]
* uv: [0.6.14][0.1.0-a12-3]
* Rye: [0.44.0][0.1.0-a12-4]


### Added

* Standalone scripts ([PEP 723][0.1.0-a12-a-1] scripts)
  can now be run as uv run configurations.
* Rule codes in rule violation tooltips are now hyperlinked.
  Clicking on this link would show the description of that rule.
  Note that the link is only available when "Render tooltips" is enabled
  and the tooltip format includes the code.
* Dependency trees are now shown for specifiers in arrays
  paired with the following TOML keys:

  * `project.dependencies`
  * `project.optional-dependencies.*`
  * `build-system.requires`
  * `dependency-groups.*`
  * \[`tool`] `uv.constraint-dependencies`
  * \[`tool`] `uv.dev-dependencies`
  * \[`tool`] `uv.override-dependencies`
  * \[`tool`] `uv.upgrade-package`
  * \[`tool`] `uv.pip.upgrade-package`

  The command line options to be used can be configured
  using relevant settings in the <i>uv</i> panel.


### Changed

* RyeCharm will now log a user-visible error
  if Ruff outputs path-less diagnostics during global inspection.
  This behaviour is expected to be exceedingly rare, if not non-existent.


### Fixed

* Previously, [`W191`][0.1.0-a12-f-1] violations might not be
  displayed correctly. This has been fixed.


### Removed

* The inspection named "Ruff inspection" has been removed.
  Previously, if this inspection were disabled,
  no diagnostics would be shown in <i>Command line</i> mode.
  The recommended alternative is to disable the "Linting" setting.


  [0.1.0-a12-1]: https://insyncwithfoo.github.io/ryecharm/
  [0.1.0-a12-2]: https://github.com/astral-sh/ruff/releases/tag/0.11.6
  [0.1.0-a12-3]: https://github.com/astral-sh/uv/releases/tag/0.6.14
  [0.1.0-a12-4]: https://github.com/astral-sh/rye/releases/tag/0.44.0
  [0.1.0-a12-a-1]: https://peps.python.org/pep-0723/
  [0.1.0-a12-f-1]: https://docs.astral.sh/ruff/rules/too-many-newlines-at-end-of-file/


## [0.1.0-alpha-11] - 2025-04-06

See [the documentation][0.1.0-a11-1] for more information.

Latest tool versions at the time of release:

* Ruff: [0.11.4][0.1.0-a11-2]
* uv: [0.6.12][0.1.0-a11-3]
* Rye: [0.44.0][0.1.0-a11-4]


### Added

* RyeCharm can now be configured to offer unsafe fixes
  in an editor context even if configuration files say otherwise.
* <i>[Inspect Code...][0.1.0-a11-a-1]</i> will now also run Ruff.
* Project scripts and global tools can now be run as uv run configurations.
  For project scripts, they can be configured automatically
  using either run line markers shown in `pyproject.toml` or
  the corresponding actions in the menu when a subkey is right-clicked.
* [PEP 751][0.1.0-a11-a-2]'s `pylock.toml` now has a specialized icon.


### Changed

* Ruff's annotator is now prioritized over other annotators
  (e.g., of other plugins).


### Fixed

* "Fix all", "Organize imports" and "Fix all similar violations"
  intentions now work correctly for injected files.
* Ruff's annotator will now also run on `pyproject.toml`
  with regards to the [`RUF200`][0.1.0-a11-f-1] rule.
* Previously, due to a logic error, a `NullPointerException` might be thrown
  in certain circumstances. This has been fixed.


  [0.1.0-a11-1]: https://insyncwithfoo.github.io/ryecharm/
  [0.1.0-a11-2]: https://github.com/astral-sh/ruff/releases/tag/0.11.4
  [0.1.0-a11-3]: https://github.com/astral-sh/uv/releases/tag/0.6.12
  [0.1.0-a11-4]: https://github.com/astral-sh/rye/releases/tag/0.44.0
  [0.1.0-a11-a-1]: https://www.jetbrains.com/help/pycharm/running-inspections.html#run-all-inspections
  [0.1.0-a11-a-2]: https://peps.python.org/pep-0751/
  [0.1.0-a11-f-1]: https://docs.astral.sh/ruff/rules/invalid-pyproject-toml


## [0.1.0-alpha-10] - 2025-03-23

See [the documentation][0.1.0-a10-1] for more information.

Latest tool versions at the time of release:

* Ruff: [0.11.2][0.1.0-a10-2]
* uv: [0.6.9][0.1.0-a10-3]
* Rye: [0.44.0][0.1.0-a10-4]


### Added

* RyeCharm now also displays documentation popups for
  rule selectors defined within script metadata blocks
  when such blocks are opened in a new editor tab using the related intention.
  It should be noted that Ruff [does not support][0.1.0-a10-a-1]
  reading configurations from script metadata blocks yet.
* Single-rule selectors in TOML files and `# noqa` comments
  can now be "[folded][0.1.0-a10-a-2]" into that rule's name.
  Such foldings are displayed in their folded forms by default;
  this can be toggled using two settings in the <i>Ruff</i> panel.
* Commands run by RyeCharm are now logged by a new "RyeCharm logs" tool window,
  which is hidden by default but can be made available
  via the <i>More tool windows</i> menu.
* If Backspace is pressed when an empty comment within a script metadata block
  is edited, RyeCharm will now remove that line and move the cursor
  back to the preceding line automatically.
* uv is now available as a [run configuration][0.1.0-a10-a-3].


### Changed

* Previously, prefix selectors that only select preview rules
  (e.g., `CPY0`) would not have documentation popups displayed on hover.
  This is due to RyeCharm not passing `--preview` when querying data,
  and getting no list in return. It now does.
  A new shortcoming of this approach is that resolved rule lists
  might contain rules that are not actually enabled.
* The linter table is now [speed-searchable][0.1.0-a10-c-1].
* <i>Edit script metadata fragment</i> now uses a better logic to determine
  the offset the cursor of the new editor should be placed at.
  The result is expected to be more intuitive for users.


### Fixed

* The <i>Edit script metadata fragment</i> intention and TOML injection for
  script metadata blocks are now available during indexing.
* Redirected codes now have their documentation popups correctly displayed,
  similar to that of a prefix.


  [0.1.0-a10-1]: https://insyncwithfoo.github.io/ryecharm/
  [0.1.0-a10-2]: https://github.com/astral-sh/ruff/releases/tag/0.11.2
  [0.1.0-a10-3]: https://github.com/astral-sh/uv/releases/tag/0.6.9
  [0.1.0-a10-4]: https://github.com/astral-sh/rye/releases/tag/0.44.0
  [0.1.0-a10-a-1]: https://github.com/astral-sh/ruff/issues/10457
  [0.1.0-a10-a-2]: https://www.jetbrains.com/help/pycharm/working-with-source-code.html#code_folding
  [0.1.0-a10-a-3]: https://www.jetbrains.com/help/pycharm/run-debug-configuration.html
  [0.1.0-a10-c-1]: https://www.jetbrains.com/help/pycharm/speed-search-in-the-tool-windows.html


## [0.1.0-alpha-9] - 2025-03-09

See [the documentation][0.1.0-a9-1] for more information.

Latest tool versions at the time of release:

* Ruff: [0.9.10][0.1.0-a9-2]
* uv: [0.6.5][0.1.0-a9-3]
* Rye: [0.44.0][0.1.0-a9-4]


### Added

* `.python-version` and `py.typed` now have specialized icons.
* Previously, in light mode, `uv.lock`'s icon has bad contrast.
  It now uses a new, more accessible icon.
* Non-topmost TOML key segments in a Ruff option documentation popup
  (e.g., `ruff` and `lint` in `ruff.lint.select`)
  are now clickable.
  They can be used to navigate between options.
* Toggling the registry entry used to be the only way to disable Red Knot.
  Now, it can also be disabled using an UI setting.
* Typing `script` at the top-level of a `.py` file now suggests
  a live template that expands to a PEP 723 script metadata block.
* Script metadata blocks now displays `# /// script` when folded
  instead of the default `...` for multicomment blocks.
* Temporary script metadata files
  (created by the "Edit script metadata fragment" intention)
  now have automatic JSON schema mapping.


### Changed

* The bundled plugin <i>Terminal</i> is now an optional dependency.


### Removed

* 2024.3.2 is no longer supported.


### Fixed

* Previously, Ruff's language server would not work correctly
  if the client was LSP4IJ.
  This has been fixed.
* "Unsuppress rule" intention now works as expected
  instead of causing a threading error.


  [0.1.0-a9-1]: https://insyncwithfoo.github.io/ryecharm/
  [0.1.0-a9-2]: https://github.com/astral-sh/ruff/releases/tag/0.9.10
  [0.1.0-a9-3]: https://github.com/astral-sh/uv/releases/tag/0.6.5
  [0.1.0-a9-4]: https://github.com/astral-sh/rye/releases/tag/0.44.0


## [0.1.0-alpha-8] - 2025-02-17

See [the documentation][0.1.0-a8-1] for more information.

Latest tool versions at the time of release:

* Ruff: [0.9.6][0.1.0-a8-2]
* uv: [0.6.0][0.1.0-a8-3]
* Rye: [0.43.0][0.1.0-a8-4]


### Added

* Previously, pressing Enter in the middle of a script metadata block
  would break it, causing the code to no longer being highlighted as TOML.
  Now, doing so will insert a new empty comment.
* When a rule selector in a TOML configuration file is hovered,
  RyeCharm will now display a popup showing all rules matching that selector.
* It is now possible to fix all violations of the same code using a quick fix.


### Changed

* Code actions provided by RyeCharm are now ordered,
  with Ruff violation fixes being at the top of the code action popup.


### Fixed

* Previously, the documentation for a rule would not be shown
  if both its code and the cursor were placed at the very end of a comment.
  This has been fixed.


  [0.1.0-a8-1]: https://insyncwithfoo.github.io/ryecharm/
  [0.1.0-a8-2]: https://github.com/astral-sh/ruff/releases/tag/0.9.4
  [0.1.0-a8-3]: https://github.com/astral-sh/uv/releases/tag/0.5.26
  [0.1.0-a8-4]: https://github.com/astral-sh/rye/releases/tag/0.43.0


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


  [Unreleased]: https://github.com/InSyncWithFoo/ryecharm/compare/v0.1.0-alpha-14..HEAD
  [0.1.0-alpha-14]: https://github.com/InSyncWithFoo/ryecharm/compare/v0.1.0-alpha-13..v0.1.0-alpha-14
  [0.1.0-alpha-13]: https://github.com/InSyncWithFoo/ryecharm/compare/v0.1.0-alpha-12..v0.1.0-alpha-13
  [0.1.0-alpha-12]: https://github.com/InSyncWithFoo/ryecharm/compare/v0.1.0-alpha-11..v0.1.0-alpha-12
  [0.1.0-alpha-11]: https://github.com/InSyncWithFoo/ryecharm/compare/v0.1.0-alpha-10..v0.1.0-alpha-11
  [0.1.0-alpha-10]: https://github.com/InSyncWithFoo/ryecharm/compare/v0.1.0-alpha-9..v0.1.0-alpha-10
  [0.1.0-alpha-9]: https://github.com/InSyncWithFoo/ryecharm/compare/v0.1.0-alpha-8..v0.1.0-alpha-9
  [0.1.0-alpha-8]: https://github.com/InSyncWithFoo/ryecharm/compare/v0.1.0-alpha-7..v0.1.0-alpha-8
  [0.1.0-alpha-7]: https://github.com/InSyncWithFoo/ryecharm/compare/v0.1.0-alpha-6..v0.1.0-alpha-7
  [0.1.0-alpha-6]: https://github.com/InSyncWithFoo/ryecharm/compare/v0.1.0-alpha-5..v0.1.0-alpha-6
  [0.1.0-alpha-5]: https://github.com/InSyncWithFoo/ryecharm/compare/v0.1.0-alpha-4..v0.1.0-alpha-5
  [0.1.0-alpha-4]: https://github.com/InSyncWithFoo/ryecharm/compare/v0.1.0-alpha-3..v0.1.0-alpha-4
  [0.1.0-alpha-3]: https://github.com/InSyncWithFoo/ryecharm/compare/v0.1.0-alpha-2..v0.1.0-alpha-3
  [0.1.0-alpha-2]: https://github.com/InSyncWithFoo/ryecharm/compare/v0.1.0-alpha-1..v0.1.0-alpha-2
  [0.1.0-alpha-1]: https://github.com/InSyncWithFoo/ryecharm/commits/v0.1.0-alpha-1
