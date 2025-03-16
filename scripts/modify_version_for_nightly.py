import re
import subprocess  # noqa: S404
from pathlib import Path
from typing import cast


_version = re.compile(r'(?m)^pluginVersion = (?P<version>.+)')


def _get_head_commit_hash() -> str:
	output = subprocess.check_output(['git', 'rev-parse', '--short', 'HEAD'])  # noqa: S607
	return output.decode('ascii').strip()


def _get_nightly_version(content: str) -> str:
	head_commit_hash = _get_head_commit_hash()
	
	version_line = cast('re.Match[str]', _version.search(content))
	current_version = version_line['version']
	
	return f'{current_version}-nightly.{head_commit_hash}'


def _modify_content(content: str) -> tuple[str, str]:
	nightly_version = _get_nightly_version(content)
	new_content = _version.sub(f'pluginVersion = {nightly_version}', content)
	
	return nightly_version, new_content


def main() -> None:
	gradle_properties = Path('gradle.properties')
	
	content = gradle_properties.read_text()
	nightly_version, new_content = _modify_content(content)
	
	gradle_properties.write_text(new_content)
	
	print(nightly_version)


if __name__ == '__main__':
	main()
