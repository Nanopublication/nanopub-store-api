package nl.lumc.nanopub.store.dao;

/**
 * 
 * @author Eelke van der Horst
 * @author Mark Thompson 
 * @author Kees Burger
 * @author Rajaram Kaliyaperumal
 * @author Reinout van Schouwen
 * 
 * @since 31-10-2013
 * @version 0.1
 */

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
