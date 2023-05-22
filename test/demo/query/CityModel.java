package demo.query;

import demo.query.QueryException;
import demo.query.QueryField;
import demo.query.QueryModel;

public class CityModel extends QueryModel<City> {

	public CityModel() {
		super(City.class);
	}

	public QueryField<City, Integer> id() throws QueryException {
		return thisField();
	}

	public QueryField<City, String> name() throws QueryException {
		return thisField();
	}

	public QueryField<City, String> district() throws QueryException {
		return thisField();
	}

	public QueryField<City, Integer> inhabitants() throws QueryException {
		return thisField();
	}

}
