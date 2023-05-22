package demo.extendable;

import demo.extendable.Extension;

public interface TextExtension extends Extension<Text> {

	default String reverse() {
		String value = base().toString();
		StringBuilder builder = new StringBuilder();
		for (int i = value.length() - 1; i >= 0; i--) {
			builder.append(value.charAt(i));
		}
		return builder.toString();
	}
}
