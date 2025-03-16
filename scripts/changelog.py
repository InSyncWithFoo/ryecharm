import re
import sys
from functools import cached_property
from pathlib import Path
from typing import ClassVar, cast


class Changelog:
	_pattern: ClassVar[re.Pattern[str]] = re.compile(
		r'''(?mix)
		^\#\#\x20\[(?P<section_header>[^\[\]]+)].*\n
		(?P<section_body>(?s:.)+?)
		(?=\n\#\#\x20|\n\x20\x20\[Unreleased])
		'''
	)
	
	_text: str
	
	def __init__(self, path: Path, /) -> None:
		self._text = path.read_text(encoding = 'utf-8')
	
	def __getitem__(self, heading: str) -> str | None:
		return self.sections.get(heading)
	
	@cached_property
	def sections(self) -> dict[str, str]:
		matches = self._pattern.finditer(self._text)
		
		return {
			match['section_header']: match['section_body'].strip()
			for match in matches
		}
	
	def get_section_or_unreleased(self, version: str) -> str:
		return cast('str', self[version] or self['Unreleased'])


class Changelogs:
	user: Changelog
	code: Changelog
	
	def __init__(self) -> None:
		root = Path(__file__).parent.parent
		
		self.user = Changelog(root / 'CHANGELOG.md')
		self.code = Changelog(root / 'CHANGELOG_CODE.md')
	
	def get_changelog_for(self, version: str) -> str:
		return '\n'.join(
			[
				'## For users',
				'',
				self.user.get_section_or_unreleased(version),
				'\n',
				'## For contributors',
				'',
				self.code.get_section_or_unreleased(version)
			]
		)


def main(version: str) -> None:
	print(Changelogs().get_changelog_for(version))


if __name__ == '__main__':
	main(sys.argv[1])
