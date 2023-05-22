package demo.browser;

import demo.browser.ClassBrowser;
import demo.mapping.City;

public class Program {

	public static void main(String[] args) {
		System.out.println(ClassBrowser.getClassDescription(City.class));
	}

}
