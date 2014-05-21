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
public class ReconcileData implements ActionHandler
{

	/*
	 * (non-Javadoc)
	 * @see
	 * org.jbpm.graph.def.ActionHandler#execute(org.jbpm.graph.exe.ExecutionContext
	 * )
	 */
	public void execute (ExecutionContext executionContext) throws Exception
	{
		ReconcileService reconcileSrv =
				SeyconServiceLocator.instance().getReconcileService();

		reconcileSrv.reconcileData(new Long(executionContext.getProcessInstance()
				.getId()));

		executionContext.getToken().signal();
	}
}
