package fr.openwide.alfresco.demo;

import fr.openwide.alfresco.component.model.repository.model.cm.CmEmailed;
import fr.openwide.alfresco.repo.module.classification.model.ClassificationEvent;
import fr.openwide.alfresco.repo.module.classification.model.builder.ClassificationBuilder;
import fr.openwide.alfresco.repo.module.classification.model.builder.SubFolderBuilder;
import fr.openwide.alfresco.repo.module.classification.model.policy.ClassificationPolicy;

/**
 * Pour tester :
 * 
 * cat > /tmp/mail
helo me
mail from:admin@alfresco.com
rcpt to:a123@alfresco.com
Data
From: "Moi" <moi@moi.net>
To: "Un to" <un_to@test.net>
Cc: "Un CC" <un_cc@test.net>
Bcc: "Un BCC" <un_bcc@test.net>
Subject: [ABC][DEF] Test mail adrien
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
 * cat /tmp/mail | telnet localhost 8025
 * 
 * @author asauvez
 *
 */
public class EmailDemoClassificationPolicy implements ClassificationPolicy<CmEmailed> {

	@Override
	public void classify(ClassificationBuilder builder, CmEmailed model, ClassificationEvent event) {
		builder.rootCompanyHome()
			.subFolder("Email")
			.subFolder("Par mois")
			.subFolder(new SubFolderBuilder(model.sentDate)
				.formatDate("yyyy.MM"))
			.moveWithUniqueName();
	
		builder.rootCompanyHome()
			.subFolder("Email")
			.subFolder("Par contact")
			.subFolder(new SubFolderBuilder(
					model.addressee, 
					model.addressees, 
					model.originator))
			.deletePrevious()
			.createSecondaryParent();
		
		builder.rootCompanyHome()
			.subFolder("Email")
			.subFolder("Par tag")
			.subFolder(new SubFolderBuilder(model.subjectLine)
					.regex("\\[([^\\]]*)\\]", 1))
			.deletePrevious()
			.createSecondaryParent();
		
		// TODO par société : toto@smile.fr --> smile.fr
	}
}