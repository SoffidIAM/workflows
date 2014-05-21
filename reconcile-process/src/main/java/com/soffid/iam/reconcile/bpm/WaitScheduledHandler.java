/**
 * 
 */
package com.soffid.iam.reconcile.bpm;

import java.util.Date;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.job.Timer;

/**
 * @author (C) Soffid 2013
 * 
 */
public class WaitScheduledHandler implements ActionHandler
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String TRANSITION_NAME = "Create task"; //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * @see
	 * org.jbpm.graph.def.ActionHandler#execute(org.jbpm.graph.exe.ExecutionContext
	 * )
	 */
	public void execute (ExecutionContext ctx) throws Exception
	{
		Date date = (Date) ctx.getVariable("startDate"); //$NON-NLS-1$
		Timer time = new Timer(ctx.getToken());

		time.setTransitionName(TRANSITION_NAME);
		time.setDueDate(date);
		time.setProcessInstance(ctx.getProcessInstance());
		time.setToken(ctx.getToken());

		ctx.getJbpmContext().getSession().save(time);
	}
}
