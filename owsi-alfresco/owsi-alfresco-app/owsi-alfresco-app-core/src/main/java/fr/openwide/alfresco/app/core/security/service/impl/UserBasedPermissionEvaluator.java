package fr.openwide.alfresco.app.core.security.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.domain.PermissionFactory;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

import fr.openwide.alfresco.app.core.security.model.BusinessUser;
import fr.openwide.alfresco.app.core.security.model.PermissionObjectWrapper;
import fr.openwide.alfresco.app.core.security.service.UserService;
import fr.openwide.core.jpa.security.hierarchy.IPermissionHierarchy;

public abstract class UserBasedPermissionEvaluator implements PermissionEvaluator {

	@Autowired
	private UserService userService;
	@Autowired
	private PermissionFactory permissionFactory;
	@Autowired
	private IPermissionHierarchy permissionHierarchy;

	/**
	 * Vérifie qu'un utilisateur possède la permission souhaitée
	 * @param user peut être <code>null</code> dans le cas d'une authentification anonyme
	 */
	protected abstract boolean hasPermission(User user, Object targetDomainObject, Permission permission);

	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		BusinessUser user = userService.getUser(authentication); // user may be null
		if (user != null && user.isAdmin()) {
			return true;
		}
		List<Permission> permissions = resolvePermission(permission);
		if (targetDomainObject instanceof Collection<?>) {
			return checkObjectsPermissions(user, (Collection<?>) targetDomainObject, permissions);
		} else {
			return checkObjectsPermissions(user, Collections.singletonList(targetDomainObject), permissions);
		}
	}

	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
		throw new UnsupportedOperationException();
	}

	protected boolean checkObjectsPermissions(User user, Collection<?> targetDomainObject, List<Permission> permissions) {
		for (Object object : targetDomainObject) {
			// il faut que tous les objets possèdent les permissions requises
			boolean allowed = checkAcceptablePermissions(user, object, permissions);
			if (! allowed) {
				return false;
			}
		}
		return true;
	}

	protected boolean checkAcceptablePermissions(User user, Object targetDomainObject, List<Permission> permissions) {
		// gère les wrappers pour lesquels les permissions doivent être vérifiées sur l'objet wrappé
		Object permissionObject = targetDomainObject;
		if (permissionObject instanceof PermissionObjectWrapper<?>) {
			permissionObject = ((PermissionObjectWrapper<?>) permissionObject).getPermissionObject();
		}
		// les doublons de permissions sont retirés dans getAcceptablePermissions
		List<Permission> acceptablePermissions = permissionHierarchy.getAcceptablePermissions(permissions);
		for (Permission permission : acceptablePermissions) {
			// Il faut posséder au moins une des permissions acceptées
			boolean allowed = hasPermission(user, permissionObject, permission);
			if (allowed) {
				return true;
			}
		}
		return false;
	}

	/**
	 * {@see org.springframework.security.acls.AclPermissionEvaluator#resolvePermission(Object)}
	 */
	private List<Permission> resolvePermission(Object permission) {
		if (permission instanceof Permission) {
			return Arrays.asList((Permission) permission);
		} else if (permission instanceof Permission[]) {
			return Arrays.asList((Permission[]) permission);
		} else if (permission instanceof String) {
			String permString = (String) permission;
			String[] split = permString.split("\\|");
			List<Permission> result = new ArrayList<Permission>();
			for (String perm : split) {
				Permission resolvedPermission = resolvePermissionByName(perm);
				if (! result.contains(resolvedPermission)) {
					result.add(resolvedPermission);
				}
			}
			return result;
		}
		throw new IllegalStateException("Unsupported permission: " + permission);
	}

	private Permission resolvePermissionByName(String permission) {
		Permission p;
		try {
			p = permissionFactory.buildFromName(permission);
		} catch (IllegalArgumentException notfound) {
			p = permissionFactory.buildFromName(permission.toUpperCase());
		}
		if (p == null) {
			throw new IllegalStateException("Unsupported permission: " + permission);
		}
		return p;
	}

}
