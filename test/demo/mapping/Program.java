package demo.mapping;

import demo.mapping.OrmConnection;

public class Program {

	public static void main(String[] args) {
		try (OrmConnection connection = new OrmConnection()) {
			City[] cities = connection.select(City.class, "name like 'B%'");
			for (City city : cities) {
				System.out.println(city);
			}
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
	}

}
