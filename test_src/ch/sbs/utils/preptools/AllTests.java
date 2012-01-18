package ch.sbs.utils.preptools;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ch.sbs.plugin.preptools.AccentRegexTest;
import ch.sbs.plugin.preptools.RegexAbbrevTest;
import ch.sbs.plugin.preptools.RegexOrdinalTest;
import ch.sbs.utils.intervaltree.IntervalTreeTest;
import ch.sbs.utils.intervaltree.RbTreeTest;
import ch.sbs.utils.preptools.parens.ParensUtilTest;
import ch.sbs.utils.preptools.vform.VFormUtilTest;
import ch.sbs.utils.preptools.vform.WordHierarchyTest;
import ch.sbs.utils.string.StringUtilsTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ AccentRegexTest.class, DocumentUtilsTest.class,
		FileUtilsTest.class, ParensUtilTest.class, PropsUtilsTest.class,
		RegexAbbrevTest.class, RegexMeasureTest.class, RegexOrdinalTest.class,
		RegexPageBreakTest.class, RegexTest.class, RegionSkipperTest.class,
		StringUtilsTest.class, IntervalTreeTest.class, RbTreeTest.class,
		TextUtilsTest.class, VFormUtilTest.class, WordHierarchyTest.class, })
public class AllTests {
	// the class remains completely empty,
	// being used only as a holder for the above annotations
}
