## Bump project version

The three intentions of this type
(for major, minor and patch bumping types)
are available in a `pyproject.toml` file.

This is equivalent to running `uv version --bump ...` at the project's path.

![](../assets/uv-intentions-bump-project-version-demo.png)


## Synchronize project

This intention is available in a `pyproject.toml` file.
It does not modify the file, but trigger a subprocess
that updates the project's environment.

This is equivalent to running `uv sync` at the project's path.
