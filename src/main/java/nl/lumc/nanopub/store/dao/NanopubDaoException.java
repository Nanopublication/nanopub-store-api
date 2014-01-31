package nl.lumc.nanopub.store.dao;

public class NanopubDaoException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1032968090316310549L;

	public NanopubDaoException(String message)
	{
		super(message);
	}
	
	public NanopubDaoException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
