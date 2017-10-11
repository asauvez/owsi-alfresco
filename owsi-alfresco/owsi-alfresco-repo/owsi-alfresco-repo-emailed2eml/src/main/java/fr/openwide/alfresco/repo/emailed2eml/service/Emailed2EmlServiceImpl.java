package fr.openwide.alfresco.repo.emailed2eml.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies.OnAddAspectPolicy;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * Convertis les emails reçus en fichier .eml, visible par le plugin Outlook.
 * 
 * @author asauvez
 * 
 * Pour tester :
 * telnet localhost 8025
helo me
mail from:admin@alfresco.com
rcpt to:a123@alfresco.com
Data
Subject: Test mail adrien
Date: Thu, 24 Mar 2011 15:15:29 +0900
MIME-Version: 1.0
Content-Type: multipart/mixed;
boundary="----=_NextPart_000_001A_01CBEA36.4FC7F870"

This is a multi-part message in MIME format.

------=_NextPart_000_001A_01CBEA36.4FC7F870
Content-Type: text/plain;
 charset="utf-8"
Content-Transfer-Encoding: 7bit

Ceci est mon test.

------=_NextPart_000_001A_01CBEA36.4FC7F870
Content-Type: text/html;
 charset="utf-8"
Content-Transfer-Encoding: 7bit

Ceci <b>est</b> mon test.

------=_NextPart_000_001A_01CBEA36.4FC7F870
Content-Type: image/x-png;
 name="Image1.png"
Content-Transfer-Encoding: base64
Content-Disposition: inline;
 filename="Image1.png"

iVBORw0KGgoAAAANSUhEUgAAACgAAAAoCAIAAAADnC86AAAAFXRFWHRDcmVhdGlv
biBUaW1lAAfb
AxgPCwvMW+fiAAAAB3RJTUUH2wMYDwsbBO0U9gAAAAlwSFlzAAALEgAACxIB0t1+
/AAAAC1JREFU
eNrtzQENAAAIAyC1f2eN8c1BAXorY0KvWCwWi8VisVgsFovFYrH4ZXzcfAFPgCy5
CQAAAABJRU5E
rkJggg==

------=_NextPart_000_001A_01CBEA36.4FC7F870--

.

quit
 * 
 *
 */
public class Emailed2EmlServiceImpl implements InitializingBean, OnAddAspectPolicy {

	private static final String EML_MIME_TYPE = "message/rfc822";

	private static final Logger LOGGER = LoggerFactory.getLogger(Emailed2EmlServiceImpl.class);
	
	private PolicyComponent policyComponent;
	private NodeService nodeService;
	private ContentService contentService;
	private TransactionService transactionService;
	
	private boolean convertToEml = true;
	private boolean useHtmlAsBody = true;
	private long deleteWaitMs = 5000;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		policyComponent.bindClassBehaviour(OnAddAspectPolicy.QNAME, 
				ContentModel.ASPECT_EMAILED, 
				new JavaBehaviour(this, OnAddAspectPolicy.QNAME.getLocalName(), NotificationFrequency.TRANSACTION_COMMIT));
	}
	
	@Override
	public void onAddAspect(final NodeRef emailNodeRef, QName aspect) {

		LOGGER.debug("cm:emailed reçu " + emailNodeRef);
		if (! convertToEml) {
			LOGGER.warn(emailNodeRef + " : owsi.emailed2eml.convertToEml is set to false.");
			return;
		}
		
		// Fait dans un autre thread pour éviter qu'un plantage fasse relancer indéfiniment SMTP
		Thread threadAction = new Thread("owsi.emailed2eml") {
			@Override
			public void run() {
				try {
					if (deleteWaitMs > 0) {
						Thread.sleep(deleteWaitMs);
					}
					LOGGER.debug("Traitement démarré " + emailNodeRef);
					String creator = AuthenticationUtil.runAs(new RunAsWork<String>() {
						@Override
						public String doWork() throws Exception {
							if (! nodeService.exists(emailNodeRef)) {
								LOGGER.warn(emailNodeRef + " : node doesn't exist anymore.");
								return null;
							}
							return (String) nodeService.getProperty(emailNodeRef, ContentModel.PROP_CREATOR);
						}
					}, AuthenticationUtil.getSystemUserName());

					if (creator != null) {
						AuthenticationUtil.runAs(new RunAsWork<Void>() {
							@Override
							public Void doWork() throws Exception {
								return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Void>() {
									@Override
									public Void execute() throws Exception {
										transformInEml(emailNodeRef);
										return null;
									}
								}, false, false);
							}
						}, creator);
	
						LOGGER.debug("Traitement terminé " + emailNodeRef);
					}
				} catch (Exception ex) {
					// On log, mais en cas d'erreur, on laisse tomber. 
					// On ne veut pas empecher le mail d'être stocké, même au mauvais format.
					LOGGER.error(emailNodeRef.toString(), ex);
				}
			};
		};
		if (deleteWaitMs != 0) {
			threadAction.start();
		} else {
			threadAction.run();
		}
	}
	
	private void transformInEml(NodeRef emailNodeRef) throws Exception {
		ContentData contentData = (ContentData) nodeService.getProperty(emailNodeRef, ContentModel.PROP_CONTENT);
		if (contentData == null || EML_MIME_TYPE.equalsIgnoreCase(contentData.getMimetype())) {
			LOGGER.warn(emailNodeRef + " : Wrong mime type : " + ((contentData != null) ? contentData.getMimetype() : "no content"));
			return;
		}
		
		String originator = (String) nodeService.getProperty(emailNodeRef, ContentModel.PROP_ORIGINATOR);
		String addressee = (String) nodeService.getProperty(emailNodeRef, ContentModel.PROP_ADDRESSEE);
		@SuppressWarnings("unchecked") List<String> addressees = (List<String>) nodeService.getProperty(emailNodeRef, ContentModel.PROP_ADDRESSEES);
		String subjectLine = (String) nodeService.getProperty(emailNodeRef, ContentModel.PROP_SUBJECT);
		Date sentDate = (Date) nodeService.getProperty(emailNodeRef, ContentModel.PROP_SENTDATE);

		List<Address> recipients = new ArrayList<Address>();
		if (addressee != null) {
			recipients.addAll(Arrays.asList(InternetAddress.parse(addressee)));
		}
		if (addressees != null) {
			for (String line : addressees) {
				recipients.addAll(Arrays.asList(InternetAddress.parse(line)));
			}
		}
		final Message message = new MimeMessage(Session.getInstance(System.getProperties()));
		if (originator != null) {
			message.setFrom(new InternetAddress(originator));
		}
		message.setRecipients(Message.RecipientType.TO, recipients.toArray(new Address[recipients.size()]));
		message.setSubject(subjectLine);
		message.setSentDate(sentDate);
		Multipart multipart = new MimeMultipart();
		
		// Construit le message
		List<AssociationRef> attachments = nodeService.getTargetAssocs(emailNodeRef, ContentModel.ASSOC_ATTACHMENTS);
		
		if (useHtmlAsBody && ! attachments.isEmpty()) {
			NodeRef attachmentNode = attachments.get(0).getTargetRef();
			ContentData attachmentContentData = (ContentData) nodeService.getProperty(attachmentNode, ContentModel.PROP_CONTENT);
			if ("text/html".equals(attachmentContentData.getMimetype())) {
				String html = contentService.getReader(attachmentNode, ContentModel.PROP_CONTENT).getContentString();

				MimeBodyPart contentHtml = new MimeBodyPart();
				contentHtml.setText(html, "UTF-8", "html");
				multipart.addBodyPart(contentHtml);
			}
		}
		
		MimeBodyPart contentTxt = new MimeBodyPart();
		String bodyTxt = contentService.getReader(emailNodeRef, ContentModel.PROP_CONTENT).getContentString();
		contentTxt.setText(bodyTxt);
		multipart.addBodyPart(contentTxt);
		
		for (AssociationRef attachmentNode : attachments) {
			String fileName = (String) nodeService.getProperty(attachmentNode.getTargetRef(), ContentModel.PROP_NAME);
			ContentData attachmentContentData = (ContentData) nodeService.getProperty(attachmentNode.getTargetRef(), ContentModel.PROP_CONTENT);
			String mimetype = attachmentContentData.getMimetype();
			
			InputStream in = contentService.getReader(emailNodeRef, ContentModel.PROP_CONTENT).getContentInputStream();
			try {
				MimeBodyPart attachment = new MimeBodyPart();
				DataSource source = new ByteArrayDataSource(in, mimetype);
				attachment.setDataHandler(new DataHandler(source));
				attachment.setFileName(fileName);
				multipart.addBodyPart(attachment);
			} finally {
				in.close();
			}
		}
		message.setContent(multipart);
		
		// Créer la node .eml
		NodeRef parentRef = nodeService.getPrimaryParent(emailNodeRef).getParentRef();
		String fileName = nodeService.getProperty(emailNodeRef, ContentModel.PROP_NAME) + ".eml";
		fileName = getUniqueName(parentRef, fileName);
		
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
		properties.put(ContentModel.PROP_NAME, fileName);
		QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(fileName));
		NodeRef newNode = nodeService.createNode(parentRef, ContentModel.ASSOC_CONTAINS, assocQName, ContentModel.TYPE_CONTENT, properties).getChildRef();
		
		OutputStream out = contentService.getWriter(newNode, ContentModel.PROP_CONTENT, true).getContentOutputStream();
		try {
			message.writeTo(out);
			out.flush();
		} finally {
			out.close();
		}

		// Nettoyer anciennes nodes
		for (AssociationRef attachmentNode : attachments) {
			if (nodeService.exists(attachmentNode.getTargetRef())) {
				nodeService.deleteNode(attachmentNode.getTargetRef());
			}
		}
		if (nodeService.exists(emailNodeRef)) {
			nodeService.deleteNode(emailNodeRef);
		}
	}
		
	private String getUniqueName(NodeRef folder, String originalName) {
		String extension = FilenameUtils.getExtension(originalName);
		if (! extension.isEmpty()) {
			extension = "." + extension;
		}
		String baseName = FilenameUtils.removeExtension(originalName);
		String name = originalName;
		int index = 1;
		while (nodeService.getChildByName(folder, ContentModel.ASSOC_CONTAINS, name) != null) {
			name = baseName + "-" + (index ++) + extension;
		}
		return name;
	}
		
	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}
	public void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}
	public void setConvertToEml(boolean convertToEml) {
		this.convertToEml = convertToEml;
	}
	public void setUseHtmlAsBody(boolean useHtmlAsBody) {
		this.useHtmlAsBody = useHtmlAsBody;
	}
	public void setDeleteWaitMs(long deleteWaitMs) {
		this.deleteWaitMs = deleteWaitMs;
	}
}