package demo.rest;

public class RestResponse {

	private int status;
	private Object entity;
	
	private RestResponse(int status, Object entity) {
		this.status = status;
		this.entity = entity;
	}
	
	public static RestResponse ok(Object entity) {
		return new RestResponse(200, entity);
	}
	
	public static RestResponse notFound() {
		return new RestResponse(200, null);
	}
	
	public static RestResponse created(Object entity) {
		return new RestResponse(201, entity);
	}
	
	public static RestResponse accepted() {
		return new RestResponse(202, null);
	}
	
	public static RestResponse noContent() {
		return new RestResponse(204, null);
	}
	
	public static RestResponse badRequest() {
		return new RestResponse(400, null);
	}
	
	public static RestResponse unauthorized() {
		return new RestResponse(401, null);
	}
	
	public static RestResponse forbidden() {
		return new RestResponse(403, null);
	}
	
	public static RestResponse methodNoAllow() {
		return new RestResponse(405, null);
	}
	
	public static RestResponse notAcceptable() {
		return new RestResponse(406, null);
	}

	public static RestResponse internalServerError(String message) {
		return new RestResponse(500, message);
	}
	
	public static RestResponse serviceUnavailable() {
		return new RestResponse(503, null);
	}
	
	public int getStatus() {
		return status;
	}
	
	public Object getEntity() {
		return entity;
	}
}
