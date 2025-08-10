# Other configurations


## Language injection


### PEP 723 inline script metadata blocks

Whether [PEP 723][1] inline script metadata blocks
should have TOML injected.

Default: `true`


### Requirements arrays in TOML files

Whether settings taking requirements arrays as values
should have <i>Requirements</i> injected.

Default: `true`


## Inspections


### Suppress `UnsatisfiedRequirementInspection` for `build-system.requires`

Whether `UnsatisfiedRequirementInspection` errors for dependencies defined
in `pyproject.toml`'s [`build-system.requires`][2] should be suppressed.

Such errors are considered false positives,
as build dependencies are normally installed into isolated environments;
plus, this only happens during build time, not development time.

Default: `true`


#### Even when the interpreter is not uv-based

Whether the errors should be suppressed even when
the project interpreter is not created by uv
or otherwise configured to be uv-managed.

Default: `false`


  [1]: https://peps.python.org/pep-0723/
  [2]: https://packaging.python.org/en/latest/specifications/pyproject-toml/#declaring-build-system-dependencies-the-build-system-table
