package ch.sbs.utils.preptools;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class TableTest {

	/*
	 *  EBNF:
	 *  
	 *  table      = { inputline } inoutsep { outputline }.
	 *  inputline  = keynumline
	 *  outputline = keynumline
	 *  keynumline = key ":" number
	 *  key        = "^[A-Za-z]+"
	 *  number     = "^[0-9]+"
	 *  inoutsep   =  "^-+"
	 *  
	 */

	@Test
	public void testTableParse() {
		final String str = ""
				+ "hasStarted: 0 1 1 0 1 1                           \n"
				+ "isDone:     0 0 1 0 0 1                           \n"
				+ "isTextPage: 0 0 0 1 1 1                           \n"
				+ "_______________________                           \n"
				+ "traffic:    0 0 3 1 2 3                           \n"
				+ "start:      0 0 0 1 1 1                           \n"
				+ "find:       0 0 0 0 1 0                           \n"
				+ "accept:     0 0 0 0 1 0                           \n"
				+ "allforms:   0 0 0 1 1 1                           \n";
		final Table table = Table.parseTable(str);
		final Map<String, List<Integer>> inputs = table.getInputs();
		assertEquals(3, inputs.size());
		assertEquals(Arrays.asList(new Integer[] { 0, 1, 1, 0, 1, 1 }),
				inputs.get("hasStarted"));
		assertEquals(Arrays.asList(new Integer[] { 0, 0, 1, 0, 0, 1 }),
				inputs.get("isDone"));
		assertEquals(Arrays.asList(new Integer[] { 0, 0, 0, 1, 1, 1 }),
				inputs.get("isTextPage"));
		final Map<String, List<Integer>> outputs = table.getOutputs();
		assertEquals(5, outputs.size());
		assertEquals(Arrays.asList(new Integer[] { 0, 0, 3, 1, 2, 3 }),
				outputs.get("traffic"));
		assertEquals(Arrays.asList(new Integer[] { 0, 0, 0, 1, 1, 1 }),
				outputs.get("start"));
		assertEquals(Arrays.asList(new Integer[] { 0, 0, 0, 0, 1, 0 }),
				outputs.get("find"));
		assertEquals(Arrays.asList(new Integer[] { 0, 0, 0, 0, 1, 0 }),
				outputs.get("accept"));
		assertEquals(Arrays.asList(new Integer[] { 0, 0, 0, 1, 1, 1 }),
				outputs.get("allforms"));
	}

	@Test(expected = RuntimeException.class)
	public void testTableParseInconsistentCount() {
		final String str = ""
				+ "hasStarted: 0 1 1 0 1 1                           \n"
				+ "isDone:     0 0 1 0 0 1                           \n"
				+ "isTextPage: 0 0 0 1 1 1                           \n"
				+ "_______________________                           \n"
				+ "traffic:    0 0 3 1 2 3                           \n"
				+ "start:      0 0 0 1 1 1                           \n"
				+ "find:       0 0 0 0 1 0                           \n"
				+ "accept:     0 0 0 0 1 0                           \n"
				+ "allforms:   0 0 0 1 1                             \n";
		Table.parseTable(str);
	}

	@Test(expected = RuntimeException.class)
	public void testTableParseMissingSep() {
		final String str = ""
				+ "hasStarted: 0 1 1 0 1 1                           \n"
				+ "isDone:     0 0 1 0 0 1                           \n"
				+ "isTextPage: 0 0 0 1 1 1                           \n"
				+ "traffic:    0 0 3 1 2 3                           \n"
				+ "start:      0 0 0 1 1 1                           \n"
				+ "find:       0 0 0 0 1 0                           \n"
				+ "accept:     0 0 0 0 1 0                           \n";
		Table.parseTable(str);
	}
}
