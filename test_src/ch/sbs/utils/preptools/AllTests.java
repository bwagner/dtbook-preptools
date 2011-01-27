package ch.sbs.utils.preptools;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ch.sbs.utils.preptools.parens.ParensUtilTest;
import ch.sbs.utils.preptools.vform.VFormUtilTest;
import ch.sbs.utils.preptools.vform.WordHierarchyTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ FileUtilsTest.class, PropsUtilsTest.class,
		RegexTest.class, ParensUtilTest.class, VFormUtilTest.class,
		WordHierarchyTest.class })
public class AllTests {
	// the class remains completely empty,
	// being used only as a holder for the above annotations
}
