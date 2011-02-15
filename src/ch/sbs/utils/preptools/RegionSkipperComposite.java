package ch.sbs.utils.preptools;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * 
 * Maintains a list of skippers and provides skipper group functionality.
 * TODO: better have *one* skipper and collect all regions of its children
 * in it than keeping a list of self-contained skippers as children.
 * Challenge: Maintain a sorted list of regions.
 */
public class RegionSkipperComposite implements RegionSkipperComponent {

	private final List<RegionSkipperComponent> components = new ArrayList<RegionSkipperComponent>();

	public void addComponent(final RegionSkipperComponent component) {
		components.add(component);
	}

	@Override
	public void findRegionsToSkip(final String theText) {
		for (final RegionSkipperComponent component : components) {
			component.findRegionsToSkip(theText);
		}
	}

	@Override
	public boolean inSkipRegion(final Matcher matcher) {
		for (final RegionSkipperComponent component : components) {
			if (component.inSkipRegion(matcher)) {
				return true;
			}
		}
		return false;
	}

}
