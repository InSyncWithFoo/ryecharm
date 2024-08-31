# RyeCharm

> [!NOTE]
> Disclaimer: This is not an official Astral project.

> [!NOTE]
> This plugin is a work-in-progress.
> It may or may not work.
> Use it at your own risk.

[![Build](https://github.com/InSyncWithFoo/ryecharm/actions/workflows/build.yaml/badge.svg)][4]
[![Docs](https://github.com/InSyncWithFoo/ryecharm/actions/workflows/docs.yaml/badge.svg)][5]

<!-- Plugin description -->
The all-in-one PyCharm plugin for Astral-backed Python tools.

This plugin provides [Ruff][1], [uv][2] and [Rye][3] integration for PyCharm.


## Usage

If you already have Ruff, uv and/or Rye installed,
you can start using this plugin the moment you install it.
The executables will be automatically detected.


## Logging

If you use Ruff as a language server via LSP mode,
you are strongly encouraged to enable language server logging.
This will allow corresponding logs to be recorded in `idea.log`
for further analysis should a problem arises.

Add the following line to the <b>Help</b> |
<b>Diagnostic Tools</b> | <b>Debug Log Settings</b> panel:

```text
#com.intellij.platform.lsp
```


  [1]: https://github.com/astral-sh/ruff
  [2]: https://github.com/astral-sh/uv
  [3]: https://github.com/astral-sh/rye
<!-- Plugin description end -->


## Installation

This plugin is not yet available on the Marketplace.
You can download the ZIP files manually from [the <i>Releases</i> tab][6],
[the `build` branch][7] or [the <i>Actions</i> tab][8]
and follow the instructions described [here][9].

Currently supported versions:
2024.2 (build 242.20224.347) - 2024.3.* (build 243.*).


## Credits

Parts of this repository were taken or derived from:

* [@astral-sh/ruff][1]
* [@astral-sh/rye][3]
* [@astral-sh/uv][2]
* [@JetBrains/intellij-community][10]
* [@JetBrains/intellij-platform-plugin-template][11]
* [@koxudaxi/poetry-pycharm-plugin][12]
* [@koxudaxi/ruff-pycharm-plugin][13]
* [@lensvol/intellij-blackconnect][14]


  [4]: https://github.com/InSyncWithFoo/ryecharm/actions/workflows/build.yaml
  [5]: https://github.com/InSyncWithFoo/ryecharm/actions/workflows/docs.yaml
  [6]: https://github.com/InSyncWithFoo/ryecharm/releases
  [7]: https://github.com/InSyncWithFoo/ryecharm/tree/build
  [8]: https://github.com/InSyncWithFoo/ryecharm/actions/workflows/build.yaml
  [9]: https://www.jetbrains.com/help/pycharm/managing-plugins.html#install_plugin_from_disk
  [10]: https://github.com/JetBrains/intellij-community
  [11]: https://github.com/JetBrains/intellij-platform-plugin-template
  [12]: https://github.com/koxudaxi/poetry-pycharm-plugin
  [13]: https://github.com/koxudaxi/ruff-pycharm-plugin
  [14]: https://github.com/lensvol/intellij-blackconnect
