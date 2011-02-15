package ch.sbs.utils.preptools;

import java.util.regex.Matcher;

public interface RegionSkipperComponent {
	public void findRegionsToSkip(final String theText);

	public boolean inSkipRegion(final Matcher matcher);
}
