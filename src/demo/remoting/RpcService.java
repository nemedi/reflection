package demo.remoting;

import java.util.HashMap;
import java.util.Map;

public class RpcService {

	private Class<?> type;
	private Map<String, RpcSkeleton> skeletons;
	private RpcSkeleton skeleton;

	public RpcService(Class<?> type) {
		this.type = type;
		this.skeletons = new HashMap<String, RpcSkeleton>();
	}
	
	public RpcService(Object instance) {
		this.skeleton = new RpcSkeleton(instance);
	}
	
	public void process(RpcRequest request, RpcResponse response) {
		try {
			if (RpcRequest.DESTROY_METHOD.equals(request.getMethod())) {
				String session = request.getSession();
				if (session != null
						&& skeletons.containsKey(session)) {
					skeletons.remove(session);
				}
				return;
			}
			RpcSkeleton skeleton = null;
			if (this.skeleton != null) {
				skeleton = this.skeleton;
			}
			else {
				String session = request.getSession();
				if (session == null || !skeletons.containsKey(session)) {
					RpcSkeleton newSkeleton = new RpcSkeleton(type);
					session = newSkeleton.getSession();
					skeletons.put(session, newSkeleton);
				}
				skeleton = skeletons.get(session); 
			}
			skeleton.process(request, response);
		} catch (Exception e) {
			response.setFault(e.getLocalizedMessage());
		}
	}
}
