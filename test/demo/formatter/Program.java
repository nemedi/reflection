package demo.formatter;

import java.util.Date;

import demo.formatter.CsvFormatter;
import demo.formatter.CsvFormatterException;
import demo.formatter.JsonFormatter;
import demo.formatter.JsonFormatterException;

public class Program {

	public static void main(String[] args) {
		try {
			testJson();
			testCsv();
		} catch (JsonFormatterException | CsvFormatterException e) {
			System.out.println(e.getLocalizedMessage());
		}
	}
	
	private static void testJson() throws JsonFormatterException {
		Order order = new Order();
		order.setId(1);
		order.setDate(new Date());
		order.setCustomer("Iulian Ilie-Nemedi");
		Item[] items = new Item[3];
		for(int i = 0; i < items.length; i++){
			items[i] = new Item();
			items[i].setPrice(i + 1);
			items[i].setUnits(i + 1);
			items[i].setProduct("Product " + (i + 1));
		}
		order.setItems(items);
		String text = JsonFormatter.serialize(order);
		System.out.println(text);
		order = JsonFormatter.deserialize(text, Order.class);
		text = JsonFormatter.serialize(order);
		System.out.println(text);		
	}
	
	private static void testCsv()
			throws CsvFormatterException {
		City[] cities = CsvFormatter.deserialize("resources/in/citiesInBulgaria.csv", City.class);
		for (City city : cities) { 
			System.out.println(city);
		}
		CsvFormatter.serialize("resources/out/citiesInBulgaria.csv", cities);
	}

}
