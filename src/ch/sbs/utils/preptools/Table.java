package ch.sbs.utils.preptools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
	* Copyright (C) 2010 Swiss Library for the Blind, Visually Impaired and Print Disabled
	*
	* This file is part of dtbook-preptools.
	* 	
	* dtbook-preptools is free software: you can redistribute it
	* and/or modify it under the terms of the GNU Lesser General Public
	* License as published by the Free Software Foundation, either
	* version 3 of the License, or (at your option) any later version.
	* 	
	* This program is distributed in the hope that it will be useful,
	* but WITHOUT ANY WARRANTY; without even the implied warranty of
	* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
	* Lesser General Public License for more details.
	* 	
	* You should have received a copy of the GNU Lesser General Public
	* License along with this program. If not, see
	* <http://www.gnu.org/licenses/>.
	*/

public class Table {

	interface Command {
		public void doit();
	}

	private final Map<Integer, Command> commands = new HashMap<Integer, Command>();

	private List<String> inputKeys;
	private List<String> outputKeys;
	private final Map<String, List<Integer>> inputs = new HashMap<String, List<Integer>>();
	private final Map<String, List<Integer>> outputs = new HashMap<String, List<Integer>>();

	public static Table parseTable(final String str) {
		final String[] data = str.split("_+");
		if (data.length != 2) {
			throw new RuntimeException(
					"wrong format, expected 2 lines, but were " + data.length
							+ " in " + str);
		}
		final Table table = new Table();
		table.inputKeys = storeLists(data[0].split("\n"), table.inputs, str);
		table.outputKeys = storeLists(data[1].split("\n"), table.outputs, str);
		return table;
	}

	private static List<String> storeLists(final String[] lines,
			final Map<String, List<Integer>> map, final String str) {
		final List<String> keys = new ArrayList<String>();
		final int INIT = -1;
		int previousCount = INIT;
		for (final String line : lines) {
			if (line.matches("^\\s*$")) {
				continue;
			}
			final String[] in = line.split(":");
			if (in.length != 2) {
				throw new RuntimeException("wrong format:" + in + " from: "
						+ str);
			}
			final List<Integer> nums = parseNums(in[1]);
			if (previousCount != INIT && previousCount != nums.size()) {
				throw new RuntimeException("previously had " + previousCount
						+ " now " + nums.size());
			}
			previousCount = nums.size();
			map.put(in[0], nums);
			keys.add(in[0]);
		}
		return keys;
	}

	private static List<Integer> parseNums(final String string) {
		final List<Integer> nums = new ArrayList<Integer>();
		final String[] numStrs = string.split("\\s+");
		for (final String numStr : numStrs) {
			if (numStr.matches("^\\s*$")) {
				continue;
			}
			nums.add(Integer.parseInt(numStr));
		}
		return nums;
	}

	/**
	 * Store a command for a key found in the outputs list
	 * 
	 * @param key
	 * @param theCommand
	 */
	public void storeCommand(int key, final Command theCommand) {
		commands.put(key, theCommand);
	}

	public void fire(int... input) {

	}

	public Map<String, List<Integer>> getInputs() {
		return inputs;
	}

	public Map<String, List<Integer>> getOutputs() {
		return outputs;
	}

	public List<String> getInputKeys() {
		return inputKeys;
	}

	public List<String> getOutputKeys() {
		return outputKeys;
	}
}
