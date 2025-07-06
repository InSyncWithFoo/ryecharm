# RyeCharm

> [!NOTE]
> Disclaimer: This is not an official Astral project.

> [!NOTE]
> This plugin is a work-in-progress.
> It may or may not work.
> Use it at your own risk.

[![Build](https://github.com/InSyncWithFoo/ryecharm/actions/workflows/build.yaml/badge.svg)][6]
[![Docs](https://github.com/InSyncWithFoo/ryecharm/actions/workflows/docs.yaml/badge.svg)][7]
[![Version](https://img.shields.io/jetbrains/plugin/v/25230)][8]
[![Rating](https://img.shields.io/jetbrains/plugin/r/rating/25230)][9]
[![Downloads](https://img.shields.io/jetbrains/plugin/d/25230)][10]

<!-- Plugin description -->
The all-in-one PyCharm plugin for Astral-backed Python tools:
[Ruff][1], [uv][2], [ty][3] and [Rye][4].


## Usage

If you already have Ruff, uv and/or Rye installed,
you can start using this plugin the moment you install it.
The executables will be automatically detected.

See [the documentation][5] for more information.


## Logging

If you use Ruff as a language server via LSP mode,
you are strongly encouraged to enable language server logging.
This will allow corresponding logs to be recorded in log files
for further analysis should a problem arises.

Add the following line to the <b>Help</b> |
<b>Diagnostic Tools</b> | <b>Debug Log Settings</b> panel:

```text
com.intellij.platform.lsp
```


  [1]: https://github.com/astral-sh/ruff
  [2]: https://github.com/astral-sh/uv
  [3]: https://github.com/astral-sh/ty
  [4]: https://github.com/astral-sh/rye
  [5]: https://insyncwithfoo.github.io/ryecharm
<!-- Plugin description end -->


## Installation

This plugin is [available on the Marketplace][8].
You can also download the ZIP files manually from [the <i>Releases</i> tab][11],
[the `build` branch][12] or [the <i>Actions</i> tab][13]
and follow the instructions described [here][14].

Currently supported versions:
2025.1 (build 251.23774.444) and later.


## Credits

Parts of this repository were taken or derived from:

* [@alexander-doroshko/intellij-lsp-plugin-example][15]
* [@astral-sh/ruff][1]
* [@astral-sh/rye][4]
* [@astral-sh/ty][3]
* [@astral-sh/uv][2]
* [@JetBrains/intellij-community][16]
* [@JetBrains/intellij-platform-plugin-template][17]
* [@koxudaxi/poetry-pycharm-plugin][18]
* [@koxudaxi/ruff-pycharm-plugin][19]
* [@lensvol/intellij-blackconnect][20]


  [6]: https://github.com/InSyncWithFoo/ryecharm/actions/workflows/build.yaml
  [7]: https://github.com/InSyncWithFoo/ryecharm/actions/workflows/docs.yaml
  [8]: https://plugins.jetbrains.com/plugin/25230/versions
  [9]: https://plugins.jetbrains.com/plugin/25230/reviews
  [10]: https://plugins.jetbrains.com/plugin/25230
  [11]: https://github.com/InSyncWithFoo/ryecharm/releases
  [12]: https://github.com/InSyncWithFoo/ryecharm/tree/build
  [13]: https://github.com/InSyncWithFoo/ryecharm/actions/workflows/build.yaml
  [14]: https://www.jetbrains.com/help/pycharm/managing-plugins.html#install_plugin_from_disk
  [15]: https://github.com/alexander-doroshko/intellij-lsp-plugin-example
  [16]: https://github.com/JetBrains/intellij-community
  [17]: https://github.com/JetBrains/intellij-platform-plugin-template
  [18]: https://github.com/koxudaxi/poetry-pycharm-plugin
  [19]: https://github.com/koxudaxi/ruff-pycharm-plugin
  [20]: https://github.com/lensvol/intellij-blackconnect
