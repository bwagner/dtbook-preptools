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
