import re
import subprocess
from pathlib import Path
from typing import cast

_version = re.compile(r"^pluginVersion = (?P<version>.+)", re.M)


def get_head_commit_hash() -> str:
    output = subprocess.check_output(["git", "rev-parse", "--short", "HEAD"])
    return output.decode("ascii").strip()


def get_nightly_version(content: str) -> str:
    head_commit_hash = get_head_commit_hash()

    version_line = cast(re.Match[str], _version.search(content))
    current_version = version_line["version"]

    return f"{current_version}-nightly.{head_commit_hash}"


def modify_content(content: str) -> tuple[str, str]:
    nightly_version = get_nightly_version(content)
    new_content = _version.sub(f"pluginVersion = {nightly_version}", content)

    return nightly_version, new_content


def main():
    gradle_properties = Path("gradle.properties")

    with open(gradle_properties, "r") as file:
        content = file.read()

    with open(gradle_properties, "w") as file:
        nightly_version, new_content = modify_content(content)
        file.write(new_content)

    print(nightly_version)


if __name__ == "__main__":
    main()
