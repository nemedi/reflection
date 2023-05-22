package demo.loader;

import demo.remoting.RpcException;

public interface IClassService {

	String getBytecode(String name) throws RpcException;

}
