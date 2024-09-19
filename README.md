# RyeCharm

> [!NOTE]
> Disclaimer: This is not an official Astral project.

> [!NOTE]
> This plugin is a work-in-progress.
> It may or may not work.
> Use it at your own risk.

[![Build](https://github.com/InSyncWithFoo/ryecharm/actions/workflows/build.yaml/badge.svg)][5]
[![Docs](https://github.com/InSyncWithFoo/ryecharm/actions/workflows/docs.yaml/badge.svg)][6]
[![Version](https://img.shields.io/jetbrains/plugin/v/25230)][7]
[![Rating](https://img.shields.io/jetbrains/plugin/r/rating/25230)][8]
[![Downloads](https://img.shields.io/jetbrains/plugin/d/25230)][9]

<!-- Plugin description -->
The all-in-one PyCharm plugin for Astral-backed Python tools:
[Ruff][1], [uv][2] and [Rye][3].


## Usage

If you already have Ruff, uv and/or Rye installed,
you can start using this plugin the moment you install it.
The executables will be automatically detected.

See [the documentation][4] for more information.


## Logging

If you use Ruff as a language server via LSP mode,
you are strongly encouraged to enable language server logging.
This will allow corresponding logs to be recorded in `idea.log`
for further analysis should a problem arises.

Add the following line to the <b>Help</b> |
<b>Diagnostic Tools</b> | <b>Debug Log Settings</b> panel:

```text
com.intellij.platform.lsp
```


  [1]: https://github.com/astral-sh/ruff
  [2]: https://github.com/astral-sh/uv
  [3]: https://github.com/astral-sh/rye
  [4]: https://insyncwithfoo.github.io/ryecharm
<!-- Plugin description end -->


## Installation

This plugin is [available on the Marketplace][7].
You can also download the ZIP files manually from [the <i>Releases</i> tab][10],
[the `build` branch][11] or [the <i>Actions</i> tab][12]
and follow the instructions described [here][13].

Currently supported versions:
2024.2 (build 242.20224.347) - 2024.2.* (build 242.*).


## Credits

Parts of this repository were taken or derived from:

* [@astral-sh/ruff][1]
* [@astral-sh/rye][3]
* [@astral-sh/uv][2]
* [@JetBrains/intellij-community][14]
* [@JetBrains/intellij-platform-plugin-template][15]
* [@koxudaxi/poetry-pycharm-plugin][16]
* [@koxudaxi/ruff-pycharm-plugin][17]
* [@lensvol/intellij-blackconnect][18]


  [5]: https://github.com/InSyncWithFoo/ryecharm/actions/workflows/build.yaml
  [6]: https://github.com/InSyncWithFoo/ryecharm/actions/workflows/docs.yaml
  [7]: https://plugins.jetbrains.com/plugin/25230/versions
  [8]: https://plugins.jetbrains.com/plugin/25230/reviews
  [9]: https://plugins.jetbrains.com/plugin/25230
  [10]: https://github.com/InSyncWithFoo/ryecharm/releases
  [11]: https://github.com/InSyncWithFoo/ryecharm/tree/build
  [12]: https://github.com/InSyncWithFoo/ryecharm/actions/workflows/build.yaml
  [13]: https://www.jetbrains.com/help/pycharm/managing-plugins.html#install_plugin_from_disk
  [14]: https://github.com/JetBrains/intellij-community
  [15]: https://github.com/JetBrains/intellij-platform-plugin-template
  [16]: https://github.com/koxudaxi/poetry-pycharm-plugin
  [17]: https://github.com/koxudaxi/ruff-pycharm-plugin
  [18]: https://github.com/lensvol/intellij-blackconnect
