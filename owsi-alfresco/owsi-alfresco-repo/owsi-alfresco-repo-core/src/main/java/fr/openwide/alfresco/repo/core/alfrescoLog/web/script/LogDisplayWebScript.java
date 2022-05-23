package fr.openwide.alfresco.repo.core.alfrescoLog.web.script;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

public abstract class LogDisplayWebScript extends AbstractWebScript {

	protected abstract String getLogFile();
	
	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		String[] grep = req.getParameterValues("grep");
		String[] grepv = req.getParameterValues("grepv");
		
		String linesS = req.getParameter("lines");
		int nbLines = (linesS != null) ? Integer.parseInt(linesS) : 50;

		res.setContentType("text/plain");
		
		File file = new File(getLogFile());
		if (nbLines == -1) {
			FileUtils.copyFile(file, res.getOutputStream());
		} else {
			List<String> lines = new ArrayList<>();
			try (ReversedLinesFileReader reader = new ReversedLinesFileReader(file)) {
				while (lines.size() <= nbLines) {
					String line = reader.readLine();
					if (line == null) {
						break;
					}
					
					if (filterLine(line, grep, grepv)) {
						lines.add(line);
					}
				}
			}
			
			Collections.reverse(lines);
			
			try (PrintWriter out = new PrintWriter(res.getWriter())) {
				for (String line : lines) {
					out.println(line);
				}
			}
		}
	}
	
	private boolean filterLine(String line, String[] grep, String[] grepv) {
		if (grep != null) {
			for (String grepItem : grep) {
				if (! line.toLowerCase().contains(grepItem.toLowerCase())) {
					return false;
				}
			}
		}
		if (grepv != null) {
			for (String grepItem : grepv) {
				if (line.toLowerCase().contains(grepItem.toLowerCase())) {
					return false;
				}
			}
		}
		return true;
	}
}
