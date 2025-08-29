# /// script
# requires-python = ">=3.13"
# dependencies = [
#     "pydantic"
# ]
# ///

import json
import re
import subprocess  # noqa: S404
from collections.abc import Iterable
from pathlib import Path
from typing import Final

from pydantic import BaseModel, Field, TypeAdapter


_PROJECT_ROOT: Final = Path(__file__).parent.parent
_LIBS_VERSION_TOML: Final = _PROJECT_ROOT / 'gradle' / 'libs.versions.toml'

_IPGP_VERSION_IN_SPECIFIER: Final = re.compile(r'(?m)(?<=^intelliJPlatform = ").+(?="$)')


class Release(BaseModel):
	is_latest: bool = Field(alias = 'isLatest')
	tag_name: str = Field(alias = 'tagName')
	
	@staticmethod
	def list_from(raw: Iterable[object]) -> 'list[Release]':
		adapter = TypeAdapter(list[Release])
		return adapter.validate_python(raw)


def _get_latest_version() -> str:
	arguments = [
		'gh', 'release', 'list',
		'--json', 'isLatest,tagName',
		'--limit', '10',
		'--repo', 'JetBrains/intellij-platform-gradle-plugin'
	]
	output = subprocess.run(arguments, capture_output = True, check = True)
	
	for release in Release.list_from(json.loads(output.stdout)):
		if release.is_latest:
			return release.tag_name.removeprefix('v')
	
	raise RuntimeError


def _replace_version(new_version: str) -> None:
	contents = _LIBS_VERSION_TOML.read_text()
	new_contents = _IPGP_VERSION_IN_SPECIFIER.sub(new_version, contents)
	
	_LIBS_VERSION_TOML.write_text(new_contents)


def main() -> None:
	latest_version = _get_latest_version()
	_replace_version(latest_version)


if __name__ == '__main__':
	main()
