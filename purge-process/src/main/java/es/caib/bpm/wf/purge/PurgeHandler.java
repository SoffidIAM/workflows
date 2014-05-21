package es.caib.bpm.wf.purge;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.jbpm.JbpmContext;
import org.jbpm.context.log.VariableLog;
import org.jbpm.db.GraphSession;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.logging.log.ProcessLog;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.jbpm.taskmgmt.log.TaskLog;

public class PurgeHandler implements ActionHandler {
	static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(PurgeHandler.class);

	public void execute(ExecutionContext ctx) throws Exception {
		Object end = ctx.getVariable("end");
		if (end != null) {
			ctx.getToken().signal("end");
		} else {
			Integer days = (Integer) ctx.getVariable("days");
			Date first = (Date) ctx.getVariable("lastExecution");
			Date last = new Date(System.currentTimeMillis() - (long) days.intValue()
					* 1000L * 60L * 60L * 24L);

			Criteria criteria = ctx.getJbpmContext().getSession()
					.createCriteria(ProcessInstance.class);
			criteria.add(Restrictions.le("end", last));
			if (first == null) {
				deleteOtherProcesses(ctx);
			} else {
				criteria.add(Restrictions.ge("end", first));
			}
			criteria.addOrder(Order.asc("end"));
			List l = criteria.list();
			int i = 0;
			String transition = "wait";
			for (Iterator it = l.iterator(); it.hasNext();) {
				ProcessInstance pi = (ProcessInstance) it.next();
				log.info("Purging process " + pi.getId());
				// Esborrar logs
				purgeLogs(ctx.getJbpmContext(), pi);
				// Esborrar taskInstances
				purgeTasks(ctx.getJbpmContext(), pi);
//				ctx.getJbpmContext().save(pi);
				i ++;
				if ( i >= 100 ) {
					last = pi.getEnd();
					transition = "continue";
					break;
				}
			}

			ctx.setVariable("lastExecution", last);
			ctx.getToken().signal(transition);
		}

	}

	private void deleteOtherProcesses(ExecutionContext ctx) {
		GraphSession gs = ctx.getJbpmContext().getGraphSession();
		List defs = gs.findAllProcessDefinitionVersions(ctx
				.getProcessDefinition().getName());
		for (Iterator it = defs.iterator(); it.hasNext();) {
			ProcessDefinition def = (ProcessDefinition) it.next();
			List procs = gs.findProcessInstances(def.getId());
			for (Iterator it2 = procs.iterator(); it2.hasNext();) {
				ProcessInstance pi = (ProcessInstance) it2.next();
				if (!pi.hasEnded()
						&& pi.getId() != ctx.getProcessInstance().getId()) {
					log.info("Stopping purge process " + pi.getId());
					pi.getContextInstance().setVariable("end", "end");
					pi.end();
					ctx.getJbpmContext().save(pi);
				}
			}
		}
	}

	private void purgeTasks(JbpmContext jbpmContext, ProcessInstance pi) {
		Collection tasks = pi.getTaskMgmtInstance().getTaskInstances();
		for (Iterator it = tasks.iterator(); it.hasNext();) {
			TaskInstance task = (TaskInstance) it.next();
			it.remove();
			jbpmContext.getSession().delete(task);
		}
	}

	private void purgeLogs(JbpmContext ctx, ProcessInstance pi) {
		purgeLogs(ctx, pi.getRootToken());
	}

	private void purgeLogs(JbpmContext ctx, Token t) {
		Criteria criteria= null;
		
		criteria= ctx.getSession().createCriteria(org.jbpm.logging.log.ProcessLog.class);
		criteria.add(Restrictions.eq("token", t));
		
		Iterator it = criteria.list().iterator();
		while (it.hasNext())
		{
			ProcessLog l = (ProcessLog) it.next();
			if (l instanceof VariableLog) {
				ctx.getSession().delete(l);
			}
			if (l instanceof TaskLog) {
				ctx.getSession().delete(l);
			}
		}
		
		for (Iterator it2 = t.getChildren().values().iterator(); it2.hasNext();) {
			Token childToken = (Token) it2.next();
			purgeLogs(ctx, childToken);
		}

	}

}
