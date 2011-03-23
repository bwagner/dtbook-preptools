package ch.sbs.utils.preptools;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ch.sbs.plugin.preptools.AccentRegexTest;
import ch.sbs.utils.intervaltree.TestIntervalTree;
import ch.sbs.utils.intervaltree.TestRbTree;
import ch.sbs.utils.preptools.parens.ParensUtilTest;
import ch.sbs.utils.preptools.vform.VFormUtilTest;
import ch.sbs.utils.preptools.vform.WordHierarchyTest;
import ch.sbs.utils.string.StringUtilsTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ FileUtilsTest.class, PropsUtilsTest.class,
		RegexTest.class, RegexMeasureTest.class, RegexOrdinalTest.class,
		ParensUtilTest.class, VFormUtilTest.class, WordHierarchyTest.class,
		TestRbTree.class, TestIntervalTree.class, AccentRegexTest.class,
		StringUtilsTest.class, DocumentUtilsTest.class })
public class AllTests {
	// the class remains completely empty,
	// being used only as a holder for the above annotations
}
