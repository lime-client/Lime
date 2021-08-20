package lime.bot.opennbt.conversion.builtin;

import lime.bot.opennbt.conversion.TagConverter;
import lime.bot.opennbt.tag.builtin.FloatTag;

/**
 * A converter that converts between FloatTag and float.
 */
public class FloatTagConverter implements TagConverter<FloatTag, Float> {
	@Override
	public Float convert(FloatTag tag) {
		return tag.getValue();
	}

	@Override
	public FloatTag convert(String name, Float value) {
		return new FloatTag(name, value);
	}
}
