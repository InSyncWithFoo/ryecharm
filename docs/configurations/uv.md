## Executable

The uv executable to be used for all operations.

If it is not provided, RyeCharm will attempt
to find one in the following order:

* The `uv` executable found under Rye's `uv` subdirectory.
* Any `uv` executable found in PATH.
* Any `uv` executable found in the project's virtual environment.


## Configuration file

If specified, this file will be passed to uv on every invocation.

This corresponds to the `--config-file` command-line option.
