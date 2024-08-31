There are three linting modes:
<i>Command line</i>, <i>LSP4IJ</i> and <i>Native LSP client</i>.


## <i>Command line</i> mode

If this mode is selected, RyeCharm will invoke
the provided executable after each change made in the editor.

This is equivalent to running `ruff --no-fix --exit-zero --quiet`
with the file as input.


## <i>Native LSP client</i> and <i>LSP4IJ</i> modes

These two modes make use of Ruff's language server capabilities.
The differences between them are that of the client libraries.

| Mode              | <i>Native LSP client</i> | <i>LSP4IJ</i>             |
|-------------------|--------------------------|---------------------------|
| Client            | [Built-in][1]            | [LSP4IJ][2] (third-party) |
| Can be used on    | Paid IDEs                | All IDEs                  |
| Features support* | [Few][3]                 | [Much greater][4]         |
| UI integrations   | Better                   | Good                      |
| Customizations    | Easy to implement        | Limited                   |
| API stability     | Unstable                 | Unstable                  |

<small>\* [Ruff's features][5] are supported equally well by both.</small>

It is recommended to use the native client where available.

!!! warning

    On a (paid) IDE with both clients available, a `LinkageError`
    will be thrown if <i>Native LSP client</i> is selected.
    This is [a known limitation][6].


  [1]: https://plugins.jetbrains.com/docs/intellij/language-server-protocol.html
  [2]: https://plugins.jetbrains.com/plugin/23257-lsp4ij
  [3]: https://plugins.jetbrains.com/docs/intellij/language-server-protocol.html#supported-features
  [4]: https://github.com/redhat-developer/lsp4ij/blob/main/docs/LSPSupport.md
  [5]: https://docs.astral.sh/ruff/editors/features/
  [6]: https://github.com/redhat-developer/lsp4ij/issues/459
