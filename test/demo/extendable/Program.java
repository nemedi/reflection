package demo.extendable;

import java.util.ArrayList;
import java.util.List;

import demo.extendable.Extension;

public class Program {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		
		List<String> list = new ArrayList<String>();
		
		list.add("a");
		list.add("b");
		list.add("c");
		
		ListExtension<String> listExtension = Extension.create(ListExtension.class, list);
		listExtension.foreach((String element) -> System.out.println(element));
		
		Text model = new Text("ion ara fara noi");
		String reversed = model.as(TextExtension.class).reverse();
		System.out.println(reversed);
	}
	
}
