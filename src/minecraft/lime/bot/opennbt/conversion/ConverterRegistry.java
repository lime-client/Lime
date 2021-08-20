package lime.bot.opennbt.conversion;

import lime.bot.opennbt.conversion.builtin.ByteArrayTagConverter;
import lime.bot.opennbt.conversion.builtin.ByteTagConverter;
import lime.bot.opennbt.conversion.builtin.CompoundTagConverter;
import lime.bot.opennbt.conversion.builtin.DoubleTagConverter;
import lime.bot.opennbt.conversion.builtin.FloatTagConverter;
import lime.bot.opennbt.conversion.builtin.IntArrayTagConverter;
import lime.bot.opennbt.conversion.builtin.IntTagConverter;
import lime.bot.opennbt.conversion.builtin.ListTagConverter;
import lime.bot.opennbt.conversion.builtin.LongTagConverter;
import lime.bot.opennbt.conversion.builtin.ShortTagConverter;
import lime.bot.opennbt.conversion.builtin.StringTagConverter;
import lime.bot.opennbt.conversion.builtin.custom.DoubleArrayTagConverter;
import lime.bot.opennbt.conversion.builtin.custom.FloatArrayTagConverter;
import lime.bot.opennbt.conversion.builtin.custom.LongArrayTagConverter;
import lime.bot.opennbt.conversion.builtin.custom.SerializableArrayTagConverter;
import lime.bot.opennbt.conversion.builtin.custom.SerializableTagConverter;
import lime.bot.opennbt.conversion.builtin.custom.ShortArrayTagConverter;
import lime.bot.opennbt.conversion.builtin.custom.StringArrayTagConverter;
import lime.bot.opennbt.tag.builtin.ByteArrayTag;
import lime.bot.opennbt.tag.builtin.ByteTag;
import lime.bot.opennbt.tag.builtin.CompoundTag;
import lime.bot.opennbt.tag.builtin.DoubleTag;
import lime.bot.opennbt.tag.builtin.FloatTag;
import lime.bot.opennbt.tag.builtin.IntArrayTag;
import lime.bot.opennbt.tag.builtin.IntTag;
import lime.bot.opennbt.tag.builtin.ListTag;
import lime.bot.opennbt.tag.builtin.LongTag;
import lime.bot.opennbt.tag.builtin.ShortTag;
import lime.bot.opennbt.tag.builtin.StringTag;
import lime.bot.opennbt.tag.builtin.Tag;
import lime.bot.opennbt.tag.builtin.custom.DoubleArrayTag;
import lime.bot.opennbt.tag.builtin.custom.FloatArrayTag;
import lime.bot.opennbt.tag.builtin.custom.LongArrayTag;
import lime.bot.opennbt.tag.builtin.custom.SerializableArrayTag;
import lime.bot.opennbt.tag.builtin.custom.SerializableTag;
import lime.bot.opennbt.tag.builtin.custom.ShortArrayTag;
import lime.bot.opennbt.tag.builtin.custom.StringArrayTag;
import lime.bot.opennbt.tag.TagRegisterException;

import java.io.Serializable;
import java.util.*;

/**
 * A registry mapping tags and value types to converters.
 */
public class ConverterRegistry {
	private static final Map<Class<? extends Tag>, TagConverter<? extends Tag, ?>> tagToConverter = new HashMap<Class<? extends Tag>, TagConverter<? extends Tag, ?>>();
	private static final Map<Class<?>, TagConverter<? extends Tag, ?>> typeToConverter = new HashMap<Class<?>, TagConverter<? extends Tag, ?>>();

	static {
		register(ByteTag.class, Byte.class, new ByteTagConverter());
		register(ShortTag.class, Short.class, new ShortTagConverter());
		register(IntTag.class, Integer.class, new IntTagConverter());
		register(LongTag.class, Long.class, new LongTagConverter());
		register(FloatTag.class, Float.class, new FloatTagConverter());
		register(DoubleTag.class, Double.class, new DoubleTagConverter());
		register(ByteArrayTag.class, byte[].class, new ByteArrayTagConverter());
		register(StringTag.class, String.class, new StringTagConverter());
		register(ListTag.class, List.class, new ListTagConverter());
		register(CompoundTag.class, Map.class, new CompoundTagConverter());
		register(IntArrayTag.class, int[].class, new IntArrayTagConverter());

		register(DoubleArrayTag.class, double[].class, new DoubleArrayTagConverter());
		register(FloatArrayTag.class, float[].class, new FloatArrayTagConverter());
		register(LongArrayTag.class, long[].class, new LongArrayTagConverter());
		register(SerializableArrayTag.class, Serializable[].class, new SerializableArrayTagConverter());
		register(SerializableTag.class, Serializable.class, new SerializableTagConverter());
		register(ShortArrayTag.class, short[].class, new ShortArrayTagConverter());
		register(StringArrayTag.class, String[].class, new StringArrayTagConverter());
	}

	/**
	 * Registers a converter.
	 *
	 * @param <T>       Tag type to convert from.
	 * @param <V>       Value type to convert to.
	 * @param tag       Tag type class to register the converter to.
	 * @param type      Value type class to register the converter to.
	 * @param converter Converter to register.
	 * @throws ConverterRegisterException If an error occurs while registering the converter.
	 */
	public static <T extends Tag, V> void register(Class<T> tag, Class<V> type, TagConverter<T, V> converter) throws ConverterRegisterException {
		if(tagToConverter.containsKey(tag)) {
			throw new TagRegisterException("Type conversion to tag " + tag.getName() + " is already registered.");
		}

		if(typeToConverter.containsKey(type)) {
			throw new TagRegisterException("Tag conversion to type " + type.getName() + " is already registered.");
		}

		tagToConverter.put(tag, converter);
		typeToConverter.put(type, converter);
	}

	/**
	 * Converts the given tag to a value.
	 *
	 * @param <T> Tag type to convert from.
	 * @param <V> Value type to convert to.
	 * @param tag Tag to convert.
	 * @return The converted value.
	 * @throws ConversionException If a suitable converter could not be found.
	 */
	public static <T extends Tag, V> V convertToValue(T tag) throws ConversionException {
		if(tag == null || tag.getValue() == null) {
			return null;
		}

		if(!tagToConverter.containsKey(tag.getClass())) {
			throw new ConversionException("Tag type " + tag.getClass().getName() + " has no converter.");
		}

		TagConverter<T, ?> converter = (TagConverter<T, ?>) tagToConverter.get(tag.getClass());
		return (V) converter.convert(tag);
	}

	/**
	 * Converts the given value to a tag.
	 *
	 * @param <V>   Value type to convert from.
	 * @param <T>   Tag type to convert to.
	 * @param name  Name of the resulting tag.
	 * @param value Value to convert.
	 * @return The converted tag.
	 * @throws ConversionException If a suitable converter could not be found.
	 */
	public static <V, T extends Tag> T convertToTag(String name, V value) throws ConversionException {
		if(value == null) {
			return null;
		}

		TagConverter<T, V> converter = (TagConverter<T, V>) typeToConverter.get(value.getClass());
		if(converter == null) {
			for(Class<?> clazz : getAllClasses(value.getClass())) {
				if(typeToConverter.containsKey(clazz)) {
					try {
						converter = (TagConverter<T, V>) typeToConverter.get(clazz);
						break;
					} catch(ClassCastException e) {
					}
				}
			}
		}

		if(converter == null) {
			throw new ConversionException("Value type " + value.getClass().getName() + " has no converter.");
		}

		return converter.convert(name, value);
	}

	private static Set<Class<?>> getAllClasses(Class<?> clazz) {
		Set<Class<?>> ret = new LinkedHashSet<Class<?>>();
		Class<?> c = clazz;
		while(c != null) {
			ret.add(c);
			ret.addAll(getAllSuperInterfaces(c));
			c = c.getSuperclass();
		}

		// Make sure Serializable is at the end to avoid mix-ups.
		if(ret.contains(Serializable.class)) {
			ret.remove(Serializable.class);
			ret.add(Serializable.class);
		}

		return ret;
	}

	private static Set<Class<?>> getAllSuperInterfaces(Class<?> clazz) {
		Set<Class<?>> ret = new HashSet<Class<?>>();
		for(Class<?> c : clazz.getInterfaces()) {
			ret.add(c);
			ret.addAll(getAllSuperInterfaces(c));
		}

		return ret;
	}
}
