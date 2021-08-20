package lime.bot.opennbt.conversion.builtin;

import lime.bot.opennbt.conversion.TagConverter;
import lime.bot.opennbt.tag.builtin.ByteTag;

/**
 * A converter that converts between ByteTag and byte.
 */
public class ByteTagConverter implements TagConverter<ByteTag, Byte> {
	@Override
	public Byte convert(ByteTag tag) {
		return tag.getValue();
	}

	@Override
	public ByteTag convert(String name, Byte value) {
		return new ByteTag(name, value);
	}
}
