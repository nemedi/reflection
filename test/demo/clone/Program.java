package demo.clone;

import java.util.function.Consumer;

import demo.clone.CloneException;
import demo.clone.Cloner;


public class Program {

	public static void main(String[] args)  {
		try {
			Consumer<Number> consumer = item -> System.out.println(item); 
			Number original = Number.create(3, 10);
			System.out.println("Original:");
			original.visit(consumer);
			Number clone = Cloner.clone(original);
			clone.adjust(10);
			System.out.println("Clone:");
			clone.visit(consumer);
			System.out.println("Original:");
			original.visit(consumer);
		} catch (CloneException e) {
			System.out.println(e.getLocalizedMessage());
		}
		
	}

}
