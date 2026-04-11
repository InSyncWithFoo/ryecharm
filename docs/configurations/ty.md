## Executable

The ty executable to be used for all operations.

If it is not provided, RyeCharm will attempt
to find one in PATH.


## Configuration file

If specified, this file will be passed to the language server.

This corresponds to the [`configurationFile`][1] language server setting.


## Running mode

The manner in which the executable is invoked.

Default: <i>Disabled</i>

!!! info "See <i>[Running modes][2]</i> for more information."


## Enable language services

Whether to enable language features
(e.g., code completion, hover, go to definition).

This corresponds to the [`disableLanguageServices`][3] language server setting.

Default: `true`


## Diagnostics

Whether diagnostics should be shown.

Default: `true`


### Report syntax errors

Whether diagnostics for syntax errors should be shown.

This corresponds to the [`showSyntaxErrors`][4] language server setting.

Default: `false`


### Diagnostic mode

Modify this option to control the number of files
for which the language server will analyze and report diagnostics.

This corresponds to the [`diagnosticMode`][5] language server setting.

Default: <i>Open files only</i>


## Completions

Whether autocompletions should be offered.

Default: `true`


### Auto-import

Whether auto-import suggestions should be offered.

This corresponds to the [`completions.autoImport`][7]
language server setting.

Default: `true`


## Inlay hints

Whether inlay hints should be shown.

Default: `true`


### Variable types

Whether to show the types of variables as inline hints.

This corresponds to the [`inlayHints.variableTypes`][6] language server setting.

Default: `true`


### Call argument names

Whether to show argument names in call expressions as inline hints.

This corresponds to the [`inlayHints.callArgumentNames`][7]
language server setting.

Default: `true`


## Go to definition

Whether PyCharm's <i>Go to Declaration</i>
and related features should defer to ty.

Default: `true`


## Log level

The amount of logs the language server will emit.

This corresponds to the [`logLevel`][8] language server setting.

Default: <i>Information</i>


## Log file

The file to which the logs will be written.

This corresponds to the [`logFile`][9] language server setting.


  [1]: https://docs.astral.sh/ty/reference/editor-settings/#configurationfile
  [2]: ../ruff/running-modes.md
  [3]: https://docs.astral.sh/ty/reference/editor-settings/#disablelanguageservices
  [4]: https://docs.astral.sh/ty/reference/editor-settings/#showsyntaxerrors
  [5]: https://docs.astral.sh/ty/reference/editor-settings/#diagnosticmode
  [6]: https://docs.astral.sh/ty/reference/editor-settings/#autoimport
  [7]: https://docs.astral.sh/ty/reference/editor-settings/#callargumentnames
  [8]: https://docs.astral.sh/ty/reference/editor-settings/#loglevel
  [9]: https://docs.astral.sh/ty/reference/editor-settings/#logfile
