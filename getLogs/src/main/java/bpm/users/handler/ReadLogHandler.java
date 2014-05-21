package bpm.users.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.util.Date;

import javax.naming.InitialContext;

import org.bouncycastle.asn1.ocsp.ServiceLocator;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import es.caib.bpm.attachment.AttachmentManager;
import es.caib.bpm.beans.remote.Document;
import es.caib.seycon.ng.comu.Grup;
import es.caib.seycon.ng.comu.Usuari;
import es.caib.seycon.ng.servei.GrupService;
import es.caib.seycon.ng.servei.UsuariService;
import es.caib.seycon.ng.servei.workflow.ejb.AltaBaixaUsuariService;
import es.caib.seycon.ng.servei.workflow.ejb.AltaBaixaUsuariServiceHome;


public class ReadLogHandler implements ActionHandler {

	public void execute(ExecutionContext ctx) throws Exception {
		
		System.out.println("Arrancado");
	    String dir = System.getProperty ("jboss.server.home.dir");
	    File f = new File (dir+"/log/server.log");
	    int max = 0;
	    int i = 0;
	    boolean loop = false;
	    String buffer [] = new String [1000];
	    BufferedReader reader = new BufferedReader(new FileReader(f));
	    String line = reader.readLine();
	    while (line != null)
	    {
	    	buffer[i] = line+"\n";
	    	if ( i > max ) max = i;
	    	i = i + 1;
	    	if ( i >= buffer.length) {
	    		loop = true;
	    		i = 0;
	    	}
	    	line = reader.readLine();
	    }
	    ContextInstance ctxInstance = ctx.getContextInstance();
        AttachmentManager am = new AttachmentManager (ctxInstance);
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.FULL);
        String name = "server-"+df.format(new Date())+".log";
		System.out.println("Generado documento"+name);
        Document d = am.createDocument("text/plain; charset=UTF-8", name);
        d.openUploadTransfer();
        if ( loop )
        {
        	int j = i;
        	do {
        		byte b[]  = buffer[j].getBytes("UTF-8");
                d.nextUploadPackage(b, b.length);
                j ++;
                if ( j >= buffer.length)
                	j = 0;
        	} while ( j != i);
        } else {
        	int j = 0;
        	do {
        		byte b[]  = buffer[j].getBytes("UTF-8");
                d.nextUploadPackage(b, b.length);
                j ++;
        	} while ( j < i);
        }
        d.endUploadTransfer();
		System.out.println("Subido documento"+name);
		String tag = name.replace('/', '-').replace(' ', '.');
        am.attach(tag, d);
		System.out.println("Atachado documento"+name);
        ctx.leaveNode();
	}

}
