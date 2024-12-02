There are three running modes:
<i>Command line</i>, <i>LSP4IJ</i> and <i>Native LSP client</i>.

These modes all have their own pros and cons.
While RyeCharm will try to maintain consistency where possible,
a few features/settings are only available in one mode but not another.

The default mode is <i>Command line</i>.
However, it is recommended to use either of the other two modes
for performance and technical reasons:

* If you are using a paid IDE, prefer the <i>Native LSP client</i> mode,
  especially if other language server plugins, bundled or third-party,
  are present. This will help avoiding class loading errors (see below).
* Otherwise, prefer the <i>LSP4IJ</i> mode.

See <i>[Linting][1]</i> and <i>[Formatting][2]</i> for more information.


## <i>Command line</i> mode

In <i>Command line</i> mode, RyeCharm will invoke
the provided executable to retrieve necessary information
for linting, formatting and `# noqa` documentation popups.

This is in contrary to the language server modes,
in which there is only one long-running process
that handles these operations via the language server protocol (LSP).

For features not directly available via LSP,
the executable will still be invoked.
This is done to provide a consistent experience across all modes.


## <i>Native LSP client</i> and <i>LSP4IJ</i> modes

These two modes make use of Ruff's [language server capabilities][3],
available via the `server` subcommand.

The differences between them are that of the client libraries.
To use the native client, you must be using a paid IDE.
On the other hand, [LSP4IJ][5] can be installed on any IDE.


| Mode                | <i>Native LSP client</i> | <i>LSP4IJ</i>             |
|---------------------|--------------------------|---------------------------|
| Client              | [Built-in][4]            | [LSP4IJ][5] (third-party) |
| Can be used on      | Paid IDEs                | All IDEs                  |
| Supported features* | [Few][6]                 | [Much wider range][7]     |
| UI integrations     | Better                   | Good                      |
| API stability       | Unstable                 | Unstable                  |

<small>\* [Ruff's features][8] are supported equally well by both.</small>

!!! warning
    On a (paid) IDE with both clients available, a `LinkageError`
    will be thrown if <i>Native LSP client</i> is selected.
    This is [a known limitation][9].


  [1]: ./linting.md
  [2]: ./formatting.md
  [3]: https://docs.astral.sh/ruff/editors/#language-server-protocol
  [4]: https://plugins.jetbrains.com/docs/intellij/language-server-protocol.html
  [5]: https://plugins.jetbrains.com/plugin/23257-lsp4ij
  [6]: https://plugins.jetbrains.com/docs/intellij/language-server-protocol.html#supported-features
  [7]: https://github.com/redhat-developer/lsp4ij/blob/main/docs/LSPSupport.md
  [8]: https://docs.astral.sh/ruff/editors/features/
  [9]: https://github.com/redhat-developer/lsp4ij/issues/459
