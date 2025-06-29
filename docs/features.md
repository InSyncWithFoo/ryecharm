Aside from the specialized APIs used for "core" operations,
RyeCharm provides integration via the following IDE features:
actions, intentions and inspections.


## Actions

[IDE actions][1] are the main entrypoint for many operations
that are otherwise unfit to be used as intentions.

Examples: [Open configuration file][2] and [Cleaning cache][3].


## Intentions

[Intentions][4] are contextually relevant actions
that may only be triggered in an editor.

Examples: [Bump project version][5] and [Synchronize project][6].


## Inspections

[Inspections][7] find certain problems with your project.
Those implemented by RyeCharm are mostly about
the use of the tools rather than code smells.

Examples: [`uv.lock` should not be edited manually][8].


  [1]: https://www.jetbrains.com/help/idea/discover-intellij-idea.html#find-action
  [2]: rye/actions.md#open-configuration-file
  [3]: ruff/actions.md#clean-cache
  [4]: https://www.jetbrains.com/help/pycharm/intention-actions.html
  [5]: uv/intentions.md#bump-project-version
  [6]: uv/intentions.md#synchronize-project
  [7]: https://www.jetbrains.com/help/pycharm/code-inspection.html
  [8]: uv/inspections.md#editing-uvlock
