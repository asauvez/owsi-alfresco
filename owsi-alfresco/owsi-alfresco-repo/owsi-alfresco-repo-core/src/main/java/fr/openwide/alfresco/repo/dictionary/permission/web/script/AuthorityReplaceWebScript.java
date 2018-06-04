package fr.openwide.alfresco.repo.dictionary.permission.web.script;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import org.alfresco.service.cmr.repository.DuplicateChildNodeNameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.extensions.webscripts.WebScriptRequest;

import fr.openwide.alfresco.api.core.authority.model.AuthorityReference;
import fr.openwide.alfresco.repo.dictionary.permission.service.PermissionRepositoryService;
import fr.openwide.alfresco.repo.remote.framework.web.script.AbstractMessageRemoteWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptAuthentication;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptTransactionAllow;

/**
 * This webscript take an input file from the classpath structured as:
 * 	<oldUserId1>,<newUserId1>
 *  <oldUserId2>,<newUserId2>
 *  <oldUserId3>,<newUserId3>
 *  with no header.
 *  For each couple, it replaces the old authority by the new one, in the same way as the webscript AuthorityReplaceWebScript.
 *  It then delete the old users.
 *  The maxItem parameter restrict the number of items replaced at each call to replace the user.
 *
 */
@GenerateWebScript(
		url={
			"/owsi/authorityReplace?old={old}&new={new}",
			"/owsi/authorityReplace?old={old}&new={new}&removeOldInSite={removeOldInSite}",
			"/owsi/authorityReplace?old={old}&new={new}&removeOldInSite={removeOldInSite}&deactivateOldUser={deactivateOldUser}",
			"/owsi/authorityReplace?old={old}&new={new}&maxItem={maxItem}",
			"/owsi/authorityReplace?old={old}&new={new}&maxItem={maxItem}&removeOldInSite={removeOldInSite}",
			"/owsi/authorityReplace?old={old}&new={new}&maxItem={maxItem}&removeOldInSite={removeOldInSite}&deactivateOldUser={deactivateOldUser}",
			"/owsi/authorityReplace?inputFile={inputFile}",
			"/owsi/authorityReplace?inputFile={inputFile}&removeOldInSite={removeOldInSite}",
			"/owsi/authorityReplace?inputFile={inputFile}&removeOldInSite={removeOldInSite}&deactivateOldUser={deactivateOldUser}",
			"/owsi/authorityReplace?inputFile={inputFile}&maxItem={maxItem}",
			"/owsi/authorityReplace?inputFile={inputFile}&maxItem={maxItem}&removeOldInSite={removeOldInSite}",
			"/owsi/authorityReplace?inputFile={inputFile}&maxItem={maxItem}&removeOldInSite={removeOldInSite}&deactivateOldUser={deactivateOldUser}"
		},
		authentication = GenerateWebScriptAuthentication.ADMIN,
		shortName="Replace une authority par une autre",
		transactionAllow=GenerateWebScriptTransactionAllow.READWRITE,
		family="OWSI",
		beanParent="webscript.owsi.remote")
public class AuthorityReplaceWebScript extends AbstractMessageRemoteWebScript<List<String>, WebScriptRequest> {


	private static final String PARAM_REMOVE_OLD_IN_SITE = "removeOldInSite";
	
	private static final String PARAM_DEACTIVATE_OLD_USER = "deactivateOldUser";

	private static final String PARAM_FOR_LIST_EXECUTION = "inputFile";

	private final Logger LOGGER = LoggerFactory.getLogger(AuthorityReplaceWebScript.class);
	
	@Autowired
	private ResourceLoader resourceLoader;
	
	@Autowired
	private PermissionRepositoryService permissionRepositoryService;
	
	@Override
	protected WebScriptRequest extractPayload(WebScriptRequest req) {
		return req;
	}
	
	private Integer executeSingle(WebScriptRequest req) {
		AuthorityReference oldAuthority = AuthorityReference.authority(req.getParameter("old"));
		AuthorityReference newAuthority = AuthorityReference.authority(req.getParameter("new"));
		String maxItem = req.getParameter("maxItem");
		boolean removeOldInSite = getBooleanParameter(req, PARAM_REMOVE_OLD_IN_SITE, true);
		boolean deactivateOldUser = getBooleanParameter(req, PARAM_DEACTIVATE_OLD_USER, true);
		return permissionRepositoryService.replaceAuthority(oldAuthority, newAuthority, 
				Optional.ofNullable(maxItem).map(Integer::parseInt), removeOldInSite, deactivateOldUser);
	}
	
	
	private List<String> executeList(WebScriptRequest req){
		List<String> results = new ArrayList<>();
		String parameter = req.getParameter(PARAM_FOR_LIST_EXECUTION);
		
		boolean removeOldInSite = getBooleanParameter(req, PARAM_REMOVE_OLD_IN_SITE, true);
		boolean deactivateOldUser = getBooleanParameter(req, PARAM_DEACTIVATE_OLD_USER, true);
		LOGGER.info(String.format("--> UserDuplicateWebScript execute with inputFile: %s ; removeOldInSite:%s ; deactivateOldUser: %s", parameter, removeOldInSite, deactivateOldUser));
		if (parameter == null) {
			results.add("The webscript should be called with an argument inputFile, with inputFile a file in the classpath");
			return results;
		}
		int count = 0;
		try {
			Resource resource = resourceLoader.getResource(parameter);
			if (!resource.exists()){
				results.add(String.format("The resource %s doesn't exist", parameter));
				return results;
			}
			
			String maxItem = req.getParameter("maxItem");
			// The warning about closing the resource is a bug from the eclipse checker, it is safe to ignore it
			try(@SuppressWarnings("resource") Scanner scanner = new Scanner(resource.getInputStream()).useDelimiter(",|\\n")) {
				while (scanner.hasNext()) {
					String oldUserId = scanner.next();
					if (!scanner.hasNext()) {
						// empty line, skip without error
						if ("".equals(oldUserId)) {
							continue;
						}
						String malformedFileMessage = String.format("The input file is malformed at line starting with %s, aborting execution",oldUserId);
						LOGGER.error(malformedFileMessage);
						results.add(malformedFileMessage);
						return results;
					}
					String newUserId = scanner.next();
					LOGGER.info(String.format("UserDuplicateWebScript replacingUser %s by %s", oldUserId, newUserId));
					try{
						int userCount = permissionRepositoryService.replaceAuthority(
								AuthorityReference.authority(oldUserId), 
								AuthorityReference.authority(newUserId), 
								Optional.ofNullable(maxItem).map(Integer::parseInt), removeOldInSite, deactivateOldUser);
						count += userCount;
						results.add(String.format("content from user %s moved to user %s -> items count : %s", oldUserId, newUserId, userCount));
					} catch (DuplicateChildNodeNameException| IllegalArgumentException e) {
						String expectedErrorMessage = String.format("Can't move content from user %s to user %s -> %s", oldUserId, newUserId, e.getMessage());
						LOGGER.warn(expectedErrorMessage);
						results.add(expectedErrorMessage);
					} catch (Exception e) {
						String unexpectedErrorMessage = String.format("Error in moving content from user %s to user %s -> %s", oldUserId, newUserId, e.getMessage());
						LOGGER.error(unexpectedErrorMessage, e);
						results.add(unexpectedErrorMessage);
					}
				}
			} catch (FileNotFoundException e) {
				LOGGER.error("File resource not found", e);
				results.add(String.format("The input file is not found", e.getMessage()));
				return results;
			}
		} catch (IOException e) {
			LOGGER.error("UserDuplicateWebScript: io exception thrown", e);
			results.add(String.format("The file %s could not be read : %s", parameter, e.getMessage()));
			return results;
		}
		LOGGER.info(String.format("<-- UserDuplicateWebScript execute exit %s item modified", count));
		return results;
	}

	private boolean getBooleanParameter(WebScriptRequest req, String param, boolean defaultValue) {
		String valueStr = req.getParameter(param);
		boolean value = defaultValue;
		if (valueStr != null) {
			value = Boolean.parseBoolean(valueStr);
		}
		return value;
	}
	
	@Override
	protected List<String> execute(WebScriptRequest req) {
		if (Arrays.asList(req.getParameterNames()).contains(PARAM_FOR_LIST_EXECUTION)) {
			return executeList(req);
		} else {
			Integer singleResult = executeSingle(req);
			ArrayList<String> results = new ArrayList<String>();
			results.add(String.valueOf(singleResult));
			return results;
		}
		
	}
	
}
