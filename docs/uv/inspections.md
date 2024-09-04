## `uv.lock` should not be edited manually

When `uv.lock` is edited, an editor-level notice is shown.

`uv.lock` is meant to be generated by `uv`;
manualy changes will be overwritten in subsequent runs.
The file therefore should not be edited manually.

![](../assets/uv-inspections-uv-lock-editing-notice.png)