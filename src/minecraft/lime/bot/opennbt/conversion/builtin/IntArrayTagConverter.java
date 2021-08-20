package lime.bot.opennbt.conversion.builtin;

import lime.bot.opennbt.conversion.TagConverter;
import lime.bot.opennbt.tag.builtin.IntArrayTag;

/**
 * A converter that converts between IntArrayTag and int[].
 */
public class IntArrayTagConverter implements TagConverter<IntArrayTag, int[]> {
	@Override
	public int[] convert(IntArrayTag tag) {
		return tag.getValue();
	}

	@Override
	public IntArrayTag convert(String name, int[] value) {
		return new IntArrayTag(name, value);
	}
}
