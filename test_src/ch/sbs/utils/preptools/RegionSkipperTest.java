package ch.sbs.utils.preptools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import ch.sbs.plugin.preptools.VFormActionHelper;
import ch.sbs.utils.preptools.vform.MarkupUtil;
import ch.sbs.utils.preptools.vform.VFormUtil;

public class RegionSkipperTest {

	@Test
	public void testSkip() {
		final String tag = "brl:v-form";
		RegionSkipperComponent theRegionSkipperComponent = RegionSkipperLeaf
				.makeMarkupRegionSkipper(tag);
		final MarkupUtil mu = new MarkupUtil(theRegionSkipperComponent);
		final Match m = mu.find("Sieben können " + MarkupUtil.wrap("Sie", tag)
				+ " haben.", 0, VFormUtil.get3rdPPPattern());
		assertEquals(Match.NULL_MATCH, m);
	}

	@Test
	public void testSkipLiteral() {
		final RegionSkipperComposite rs = RegionSkipperLeaf
				.getLiteralAndCommentSkipper();
		rs.addComponent(RegionSkipperLeaf
				.makeMarkupRegionSkipper(VFormActionHelper.VFORM_TAG));
		final MarkupUtil mu = new MarkupUtil(rs);
		final Match m = mu.find(
				"Sieben können " + MarkupUtil.wrap("Sie", "brl:literal")
						+ " haben.", 0, VFormUtil.get3rdPPPattern());
		assertEquals(Match.NULL_MATCH, m);
	}

	@Test
	public void testLiteralSkipper() {
		final RegionSkipperComponent literalSkipper = RegionSkipperLeaf
				.getLiteralSkipper();
		final String theText = "\nhallo\n<brl:literal>\ndu\n</brl:literal>\nhier\n";
		literalSkipper.findRegionsToSkip(theText);
		assertFalse(literalSkipper.inSkipRegion(makeMatcher("hallo", theText)));
		assertTrue(literalSkipper.inSkipRegion(makeMatcher("du", theText)));
	}

	@Test
	public void tesCommentSkipper() {
		final RegionSkipperComponent commentSkipper = RegionSkipperLeaf
				.getCommentSkipper();
		final String theText = "\nhallo\n<!--\ndu\n-->\nhier\n";
		commentSkipper.findRegionsToSkip(theText);
		assertFalse(commentSkipper.inSkipRegion(makeMatcher("hallo", theText)));
		assertTrue(commentSkipper.inSkipRegion(makeMatcher("du", theText)));
	}

	@Test
	public void testLiteralSkipper1() {
		final RegionSkipperComponent literalSkipper = RegionSkipperLeaf
				.getLiteralAndCommentSkipper();
		final String theText = "\nhallo\n<brl:literal>\ndu\n</brl:literal>\nhier\n";
		literalSkipper.findRegionsToSkip(theText);
		assertFalse(literalSkipper.inSkipRegion(makeMatcher("hallo", theText)));
		assertTrue(literalSkipper.inSkipRegion(makeMatcher("du", theText)));
	}

	@Test
	public void tesCommentSkipper1() {
		final RegionSkipperComponent commentSkipper = RegionSkipperLeaf
				.getLiteralAndCommentSkipper();
		final String theText = "\nhallo\n<!--\ndu\n-->\nhier\n";
		commentSkipper.findRegionsToSkip(theText);
		assertFalse(commentSkipper.inSkipRegion(makeMatcher("hallo", theText)));
		assertTrue(commentSkipper.inSkipRegion(makeMatcher("du", theText)));
	}

	private static Matcher makeMatcher(final String thePattern,
			final String theText) {
		final Matcher matcher = Pattern.compile(thePattern).matcher(theText);
		matcher.find();
		return matcher;
	}
}
