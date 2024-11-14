RyeCharm allows configurations at two levels: IDE and project.
Project-level configurations only override the IDE-level counterparts
when the corresponding "Override" checkboxes are selected.

Project-level settings are stored in the `ryecharm.xml` file
under the `.idea` directory and can be shared with other people
(for example, via version-control systems)
to serve as the default settings for the project.

Override settings are stored in the `ryecharm-overrides.xml` file
in the same directory. This file should <em>not</em> be committed,
as its purpose is to allow overriding project defaults.


## Advanced settings

Settings in this group are not expected to be used
by the vast majority of users and might be changed at any time.
They are thus deliberately undocumented.


## Registry keys

RyeCharm manages a few registry keys
which enables development-specific features.
They are useful if you want to contribute to RyeCharm
and need to debug your code.
