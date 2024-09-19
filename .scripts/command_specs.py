# /// script
# requires-python = ">=3.12"
# dependencies = [
#     "more-itertools",
#     "nccsp",
#     "pydantic",
# ]
#
# [tool.uv.sources]
# nccsp = { git = "https://github.com/InSyncWithFoo/nccsp", rev = "master" }
# ///

from __future__ import annotations

import json
import subprocess
from itertools import groupby
from pathlib import Path
from typing import Literal, Self

import nccsp
from more_itertools import partition
from pydantic import BaseModel, Field

type _Executable = Literal['ruff', 'uv']


def _is_option(option_or_argument: nccsp.OptionOrArgument) -> bool:
	return option_or_argument.name.startswith('-')


class _Command(BaseModel):
	name: str
	path: list[str]
	description: str | None
	arguments: list[nccsp.OptionOrArgument]
	options: list[nccsp.OptionOrArgument]
	subcommands: dict[str, _Command] = Field(default_factory = dict)
	
	@classmethod
	def convert(cls, command: nccsp.Command) -> Self:
		fragments = command.fragments if len(command.fragments) == 1 else command.fragments[1:]
		
		*path, name = fragments
		description = command.description
		arguments, options = partition(_is_option, command.options_and_arguments)
		
		return cls(
			name = name, path = path,
			description = description,
			arguments = arguments,  # pyright: ignore [reportArgumentType]
			options = options  # pyright: ignore [reportArgumentType]
		)


def _get_version(executable: _Executable) -> str:
	return subprocess.check_output([executable, 'version']).decode('utf-8').strip()


def _get_data(executable: _Executable) -> list[nccsp.Command]:
	arguments = ['nccsp', 'executable', executable]
	output_stream = subprocess.check_output(arguments)
	output = output_stream.decode('utf-8')
	
	return [nccsp.Command(**element) for element in json.loads(output)]


def _add_to_tree(ancestor: _Command, descendant: _Command, path: list[str]) -> None:
	subtree = ancestor.subcommands
	
	if len(path) == 1:
		subtree[descendant.name] = descendant
		return
	
	_add_to_tree(subtree[path[0]], descendant, path[1:])


def _convert_flat_to_nested(commands: list[nccsp.Command]) -> _Command:
	root = _Command.convert(commands[0])
	
	for command in commands[1:]:
		_add_to_tree(root, _Command.convert(command), path = command.fragments[1:])
	
	return root


def _dump_data(version: str, tree: _Command, filename: str) -> None:
	json_resources_directory = Path(__file__).parent.parent / 'src' / 'main' / 'resources' / 'commandspecs'
	json_resources_directory.mkdir(parents = True, exist_ok = True)
	
	path = json_resources_directory / f'{filename}.json'
	
	with open(path.absolute(), 'w') as file:
		data = {
			'version': version,
			'tree': tree.model_dump()
		}
		json.dump(data, file)


def _get_and_dump_data(executable: _Executable) -> None:
	version = _get_version(executable)
	data_grouped_by_tool = groupby(_get_data(executable), lambda command: command.fragments[0])
	
	for tool_name, tool_data in data_grouped_by_tool:
		tree = _convert_flat_to_nested(list(tool_data))
		_dump_data(version, tree, filename = tool_name)


def main() -> None:  # noqa: D103
	_get_and_dump_data('ruff')
	_get_and_dump_data('uv')


if __name__ == '__main__':
	main()
