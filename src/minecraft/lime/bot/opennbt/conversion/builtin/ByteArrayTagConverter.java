package lime.bot.opennbt.conversion.builtin;

import lime.bot.opennbt.conversion.TagConverter;
import lime.bot.opennbt.tag.builtin.ByteArrayTag;

/**
 * A converter that converts between ByteArrayTag and byte[].
 */
public class ByteArrayTagConverter implements TagConverter<ByteArrayTag, byte[]> {
	@Override
	public byte[] convert(ByteArrayTag tag) {
		return tag.getValue();
	}

	@Override
	public ByteArrayTag convert(String name, byte[] value) {
		return new ByteArrayTag(name, value);
	}
}
