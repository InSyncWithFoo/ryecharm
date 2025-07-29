# Frequently asked questions


## What does "RyeCharm" mean? What is the goal of this plugin?

The name is a portmanteau of "Rye" and "PyCharm".
Regardless of the name, this plugin only ever has
minimal support for Rye, since, as of the former's
creation, the latter is a tool being deprecated.

It was created to become the only PyCharm plugin
Python programmers will ever need when working with
the next-generation tools maintained by Astral,
much like how Rye was created to be an one-stop-shop.

Astral's tools are fast and robust,
while PyCharm's features are used (and perhaps loved) by many users.
RyeCharm aims to combine the best of both worlds.


## Is this an official Astral project?

No. However, it <em>is</em> meant to serve
as the first step towards an "official" plugin.


## How stable is this project?

As you might have inferred from the version, not at all.
<em>Expect things to be broken everytime you update</em>.

RyeCharm won't be stable in the foreseeable future,
at least until the tools it integrates are.

* Ruff is relatively stable in terms of CLI API, but it still isn't at 1.0.0.
* ty, which eventually will be merged with Ruff, is under development.
* uv's major features are mostly stable, but it also gets
  a new "major" release (0.x) with breaking changes every couple of months.
  Even "minor" releases (0.x.y) bring a lot of changes and improvements.
* PyCharm's native uv support and other packaging/language server features
  have also been constantly improving since the second half of 2024.

That said, RyeCharm tries not to compete with PyCharm and
will remove its own features if they collide with native features.

Consult the [changelog][1] of each release to know what has changed.


  [1]: https://github.com/InSyncWithFoo/ryecharm/blob/HEAD/CHANGELOG.md
