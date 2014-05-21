/**
 * 
 */
package com.soffid.iam.expirationpassreport.bpm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.CreateException;
import javax.naming.NamingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.zkoss.text.DateFormats;

import es.caib.bpm.attachment.AttachmentManager;
import es.caib.bpm.beans.exception.DocumentBeanException;
import es.caib.bpm.exception.BPMException;
import es.caib.seycon.ng.comu.Account;
import es.caib.seycon.ng.comu.AccountType;
import es.caib.seycon.ng.comu.TipusUsuari;
import es.caib.seycon.ng.servei.AccountService;
import es.caib.seycon.ng.servei.SeyconServiceLocator;

/**
 * @author (C) Soffid 2013
 * 
 */
public class ObtainPasswordsHandler implements ActionHandler
{

	private static final long serialVersionUID = 1L;
	private static final String summaryFileName =
			"report_pass_near_to_expire-(%1$s).html";
	private static final String charEncondig = "UTF-8";
	private static final String summaryContentType = "text/html; charset="
			+ charEncondig;
	private static final String identationReference =
			"{http://xml.apache.org/xslt}indent-amount";
	private static final String errorHeader = "Write xml error: %1$s";

	private static String xsltPath = "template.xsl";
	private File xmlFile;
	private File htmlFile;

	/*
	 * (non-Javadoc)
	 * @see
	 * org.jbpm.graph.def.ActionHandler#execute(org.jbpm.graph.exe.ExecutionContext
	 * )
	 */
	public void execute (ExecutionContext ctx) throws Exception
	{
		int numDays = Integer.parseInt(ctx.getVariable("daysRemaining").toString());	// Number
																																									// of
																																									// days
																																									// left
																																									// expiration
		List<AccountType> accounts =
				(List<AccountType>) ctx.getVariable("accountsTypes");	// Account types
		List<TipusUsuari> userTypes =
				(List<TipusUsuari>) ctx.getVariable("userTypes");	// User types

		Calendar limitDate = Calendar.getInstance();
		limitDate.add(Calendar.DATE, numDays);

		generateXMLSummaryFile(limitDate.getTime(), accounts, userTypes);

		generateHTMLSummaryFile();

		uploadHTMLFile(ctx, limitDate.getTime());

		deleteTemporaryFiles();

		ctx.getToken().signal();
	}

	/**
	 * Method that implements the functionality to delete temporary files
	 * generated during the process.
	 */
	private void deleteTemporaryFiles ()
	{
		xmlFile.delete();
		htmlFile.delete();
	}

	/**
	 * Method that implements the functionality to generate a XML file with the
	 * account that password is near to expire.
	 * 
	 * @param limitDate
	 *          Limit date of expiration password to search.
	 * @param accounts
	 *          Accounts types to search.
	 * @param userTypes
	 *          User types to search
	 * @throws Exception
	 */
	private void generateXMLSummaryFile (Date limitDate,
			List<AccountType> accounts, List<TipusUsuari> userTypes) throws Exception
	{
		AccountService service =
				SeyconServiceLocator.instance().getAccountService();

		List<Account> accountsList =
				service.findAccountsNearToExpire(new Date(), limitDate, accounts,
						userTypes);

		WriteXMLFile(accountsList, limitDate);
	}

	/**
	 * Method that implements the functionality to write accounts XML file.
	 * 
	 * @param accountsList
	 *          Accounts to write in file.
	 * @param limit
	 *          Limit date to search accounts passwords left to expire.
	 * @param path
	 *          Path to store the XML file.
	 * @throws Exception
	 */
	private void WriteXMLFile (List<Account> accountsList, Date limit)
			throws Exception
	{
		try
		{
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// Root elements
			org.w3c.dom.Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("accountssummary");
			doc.appendChild(rootElement);

			Element accountRoot = null;				// Account root element
			Element element = null;						// Account element
			Element grantedUserElem = null;		// Granted user element
			Element grantedGroupElem = null;	// Granted group element
			Element grantedRoleElem = null;		// Granted roles element

			// Append limit date
			element = doc.createElement("expirationdate");
			element.appendChild(doc.createTextNode(DateFormats.format(limit, false)));

			rootElement.appendChild(element);

			for (Account account : accountsList)
			{
				// Account elements
				accountRoot = doc.createElement("account");
				rootElement.appendChild(accountRoot);

				// Append attribute to account elements
				Attr attr = doc.createAttribute("name");
				attr.setValue(account.getName());
				accountRoot.setAttributeNode(attr);

				// Check account name
				if (account.getName() != null)
				{
					// Append account name
					element = doc.createElement("name");
					element.appendChild(doc.createTextNode(account.getName()));

					accountRoot.appendChild(element);
				}

				// Check account description
				if (account.getDescription() != null)
				{
					// Append account description
					element = doc.createElement("description");
					element.appendChild(doc.createTextNode(account.getDescription()));

					accountRoot.appendChild(element);
				}

				// Append account password policy
				element = doc.createElement("policy");
				element.appendChild(doc.createTextNode(account.getPasswordPolicy()));

				accountRoot.appendChild(element);

				// Check account password last set
				if (account.getLastPasswordSet() != null)
				{
					// Append account password last set
					element = doc.createElement("lastpasswordset");
					element.appendChild(doc.createTextNode(DateFormats.format(account
							.getLastPasswordSet().getTime(), false)));

					accountRoot.appendChild(element);
				}

				// Check account password expiration date
				if (account.getPasswordExpiration() != null)
				{
					// Append account password expiration date
					element = doc.createElement("passwordexpiration");
					element.appendChild(doc.createTextNode(DateFormats.format(account
							.getPasswordExpiration().getTime(), false)));

					accountRoot.appendChild(element);
				}

				// Append account type
				element = doc.createElement("accountype");
				element.appendChild(doc.createTextNode(org.zkoss.util.resource.Labels
						.getLabel("accountType." + account.getType().toString())));

				accountRoot.appendChild(element);

				// Append account type
				element = doc.createElement("system");
				element.appendChild(doc.createTextNode(account.getDispatcher()));

				accountRoot.appendChild(element);
			}

			// Write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(identationReference, "2");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			DOMSource source = new DOMSource(doc);
			xmlFile = File.createTempFile("xmlfile", ".xml");
			StreamResult result = new StreamResult(xmlFile.getPath());

			transformer.transform(source, result);
		}
		catch (Exception e)
		{
			throw new Exception(String.format(errorHeader, e.getMessage()));
		}
	}

	/**
	 * Method that implements the functionality to obtain HTML file from
	 * previously XML file generated.
	 * 
	 * @throws Exception
	 */
	private void generateHTMLSummaryFile () throws Exception
	{
		try
		{
			TransformerFactory tFactory = TransformerFactory.newInstance();
			InputStream in = getClass().getResourceAsStream(xsltPath);
			Source xslDoc = new StreamSource(in);
			Source xmlDoc = new StreamSource(xmlFile);

			htmlFile = File.createTempFile("htmlfile", ".html");
			OutputStream htmlPage = new FileOutputStream(htmlFile.getPath());

			Transformer transformer = tFactory.newTransformer(xslDoc);
			transformer.transform(xmlDoc, new StreamResult(htmlPage));
		}
		catch (Exception e)
		{
			throw new Exception(String.format(errorHeader, e.getMessage()));
		}
	}

	/**
	 * Functionality to upload HTML summary file
	 * 
	 * @param ctx
	 *          Context of process.
	 * 
	 * @param date
	 *          Date of process.
	 * @throws BPMException
	 * @throws InterruptedException
	 * @throws DocumentBeanException
	 * @throws CreateException
	 * @throws NamingException
	 * @throws IOException
	 */
	private void uploadHTMLFile (ExecutionContext ctx, Date date)
			throws IOException, NamingException, CreateException,
			DocumentBeanException, InterruptedException, BPMException
	{
		int max = 0;
		int i = 0;
		boolean loop = false;
		String buffer[] = new String[1000];
		BufferedReader reader = new BufferedReader(new FileReader(htmlFile));
		String line = reader.readLine();
		while (line != null)
		{
			buffer[i] = line + "\n";
			if (i > max)
				max = i;
			i = i + 1;
			if (i >= buffer.length)
			{
				loop = true;
				i = 0;
			}
			line = reader.readLine();
		}

		AttachmentManager am = new AttachmentManager(ctx.getContextInstance());
		DateFormat df =
				DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.FULL);
		String name = String.format(summaryFileName, df.format(date));
		es.caib.bpm.beans.remote.Document d =
				am.createDocument(summaryContentType, name);
		d.openUploadTransfer();
		if (loop)
		{
			int j = i;
			do
			{
				byte b[] = buffer[j].getBytes(charEncondig);
				d.nextUploadPackage(b, b.length);
				j++;
				if (j >= buffer.length)
					j = 0;
			} while (j != i);
		}
		else
		{
			int j = 0;
			do
			{
				byte b[] = buffer[j].getBytes(charEncondig);
				d.nextUploadPackage(b, b.length);
				j++;
			} while (j < i);
		}
		d.endUploadTransfer();
		reader.close();
		String tag = name.replace('/', '-').replace(' ', '.');
		am.attach(tag, d);
	}
}
