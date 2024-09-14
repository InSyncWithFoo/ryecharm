## Executable

The uv executable to be used for all operations.

If it is not provided, RyeCharm will attempt
to find one in the following order:

* The `uv` executable found under Rye's `uv` subdirectory.
* Any `uv` executable found in PATH.
* Any `uv` executable found in the project's virtual environment.


## Use uv for package operations

If enabled, the <i>Python Packages</i> toolwindow's package operations
will be performed with `uv` instead of the default `pip`.

This setting only affects projects generated using [the <i>uv</i> panel][1].

Default: `true`


### Enforce for non-uv project

If enabled, uv will be used for the current project,
even if it is not a uv-generated project.

Default: `false`


  [1]: ../uv/generating.md
