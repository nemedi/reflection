package demo.clone;

import java.util.Random;
import java.util.function.Consumer;

public class Number {

	private Integer value;
	private Number next;
	
	private Number() {
	}
	
	public Number(Integer value) {
		this();
		this.value = value;
	}
	
	@Override
	public String toString() {
		return value.toString();
	}
		
	private Number append(Integer value) {
		if (this.value.compareTo(value) < 0) {
			if (next != null) {
				next = next.append(value);
			}
			else {
				next = new Number(value);
			}
			return this;
		}
		else {
			Number number = new Number(value);
			number.next = this;
			return number;
		}
	}
	
	public void adjust(Integer value) {
		this.value += value;
		if (next != null) {
			next.adjust(value);
		}
	}
	
	public void visit(Consumer<Number> consumer) {
		consumer.accept(this);
		if (next != null) {
			next.visit(consumer);
		}
	}
	
	public static Number create(int size, int maximum) {
		Random random = new Random();
		int value = random.nextInt(maximum);
		Number number = new Number(value);
		for (int i = 0; i < size - 1; i++) {
			value = random.nextInt(maximum);
			number = number.append(value);
		}
		return number;
	}
}
