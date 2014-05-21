/**
 * 
 */
package com.soffid.iam.reconcile.bpm;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import com.soffid.iam.reconcile.service.ReconcileService;

import es.caib.seycon.ng.servei.SeyconServiceLocator;

/**
 * @author (C) Soffid 2013
 * 
 */
public class ExecutionHandler implements ActionHandler
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * @see
	 * org.jbpm.graph.def.ActionHandler#execute(org.jbpm.graph.exe.ExecutionContext
	 * )
	 */
	public void execute (ExecutionContext ctx) throws Exception
	{
		ReconcileService srv =
				SeyconServiceLocator.instance().getReconcileService();

		srv.createReconcileTask(new Long(ctx.getProcessInstance().getId()),
				(String) ctx.getVariable("dispatcher")); //$NON-NLS-1$

		ctx.getToken().signal();
	}
}
