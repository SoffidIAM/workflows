package com.soffid.iam.reconcile.bpm;

import java.util.Collection;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;

/**
 * @author (C) Soffid 2013
 * 
 */
public class ReconcileMessages implements Map<String, String>
{
	private static final String BUNDLE_NAME =
			"com.soffid.iam.reconcile.bpm.messages"; //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * @see java.util.Map#size()
	 */
	public int size ()
	{
		return Integer.MAX_VALUE;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Map#isEmpty()
	 */
	public boolean isEmpty ()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public boolean containsKey (Object key)
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue (Object value)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public String get (Object key)
	{
		try
		{
			return es.caib.seycon.ng.comu.lang.MessageFactory.getString(BUNDLE_NAME,
					key.toString());
		}
		catch (MissingResourceException e)
		{
			return '!' + key.toString() + '!';
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public String put (String key, String value)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public String remove (Object key)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	public void putAll (Map<? extends String, ? extends String> m)
	{
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	public void clear ()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Map#keySet()
	 */
	public Set<String> keySet ()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Map#values()
	 */
	public Collection<String> values ()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Map#entrySet()
	 */
	public Set<java.util.Map.Entry<String, String>> entrySet ()
	{
		return null;
	}

}
