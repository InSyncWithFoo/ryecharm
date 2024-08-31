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

This setting has no effect on an `uv`-generated project,
as `pip` is not installed by default for such projects.

Default: `true`
