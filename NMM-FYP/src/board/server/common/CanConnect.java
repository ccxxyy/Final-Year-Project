package board.server.common;

public interface CanConnect {
	public boolean connect(ConnectInfo cif) throws Exception ;
	public boolean disconnect() throws Exception ;
}
