import subprocess
import sys

from changelog import Changelogs


def main(latest: str) -> None:
	changelogs = Changelogs()
	sections = changelogs.user.sections
	
	for version in sections:
		if version in ('Unreleased', latest):
			continue
		
		tag = f'v{version}'
		
		subprocess.run([
			'gh', 'release', 'edit', tag,
			'--notes', changelogs.get_changelog_for(version)
		])


if __name__ == '__main__':
	main(sys.argv[1])
