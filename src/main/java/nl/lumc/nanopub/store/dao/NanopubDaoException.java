package nl.lumc.nanopub.store.dao;

public class NanopubDaoException extends Exception {
	public NanopubDaoException(String message)
	{
		super(message);
	}
	
	public NanopubDaoException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
