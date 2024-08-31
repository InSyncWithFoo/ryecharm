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
