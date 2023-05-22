package demo.extendable;

import demo.extendable.Extendable;

public class Text implements Extendable<Text> {

	private String name;

	public Text(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
