## Clean cache

Remove all `.ruff_cache` directories in the project recursively.

This is equivalent to running `ruff clean` at the project's path.


## Clear plugin cache

[Documentation popups][1] are prerendered and cached.
While the cache is automatically invalidated whenever the executable changes,
manually clearing it might sometimes be useful.


## Open configuration file

Open the global Ruff configuration file (`ruff.toml`/`.ruff.toml`)
in the editor.


## Show executable

Show the Ruff executable that would be used for the project,
or, when the action is not triggered in a project context,
the global Ruff executable.

![](../assets/ruff-actions-show-executable-demo.png)


## Show linters

Show a table of upstream linters that Ruff supports.

This is equivalent to running `ruff linter`.

![](../assets/ruff-actions-show-linters-demo.png)


## Show version

Display a message showing the version of the current Ruff executable.

This is equivalent to running `ruff version`.

![](../assets/ruff-actions-show-version-demo.png)


## Suggest project executable

Find a potential executable and suggest
setting it as the Ruff executable for the current project,
even if one is already specified.

In most cases, this is not necessary, since the plugin
will detect such executables automatically.

![](../assets/ruff-actions-suggest-project-executable-demo.png)


  [1]: documentation.md
