package fr.openwide.alfresco.app.core.security.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.acls.domain.PermissionFactory;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import fr.openwide.alfresco.api.core.authority.model.RepositoryAuthority;
import fr.openwide.alfresco.app.core.security.model.PermissionObjectWrapper;
import fr.openwide.alfresco.app.core.security.service.UserService;
import fr.openwide.core.jpa.security.business.authority.util.CoreAuthorityConstants;
import fr.openwide.core.jpa.security.hierarchy.IPermissionHierarchy;

public abstract class UserAwarePermissionEvaluator implements PermissionEvaluator {

	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private UserService userService;
	@Autowired
	private PermissionFactory permissionFactory;
	@Autowired
	private IPermissionHierarchy permissionHierarchy;
	@Autowired
	private RoleHierarchy dynamicRoleHierarchy;

	protected abstract boolean hasPermission(UserDetails user, Object targetDomainObject, Permission permission);

	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		Optional<UserDetails> user = userService.getUserDetails(authentication);
		if (! user.isPresent()) {
			return false;
		}
		if (user.get().getAuthorities().contains(CoreAuthorityConstants.AUTHORITY_ADMIN)) {
			return true;
		}
		List<Permission> permissions = resolvePermission(permission);
		if (targetDomainObject instanceof Collection<?>) {
			return checkObjectsPermissions(user.get(), (Collection<?>) targetDomainObject, permissions);
		} else {
			return checkObjectsPermissions(user.get(), Collections.singletonList(targetDomainObject), permissions);
		}
	}

	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
		throw new UnsupportedOperationException();
	}

	protected boolean checkObjectsPermissions(UserDetails user, Collection<?> targetDomainObject, List<Permission> permissions) {
		for (Object object : targetDomainObject) {
			// il faut que tous les objets possèdent les permissions requises
			boolean allowed = checkAcceptablePermissions(user, object, permissions);
			if (! allowed) {
				return false;
			}
		}
		return true;
	}

	protected boolean checkAcceptablePermissions(UserDetails user, Object targetDomainObject, List<Permission> permissions) {
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
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Permission check : {} - {} - {} ==> {}", 
						(user != null ? user.getUsername() : null), targetDomainObject, permission, allowed);
			}
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
			List<Permission> result = new ArrayList<>();
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

	public boolean hasRole(UserDetails user, RepositoryAuthority authority) {
		return hasRole(user, authority.getName());
	}
	public boolean hasRole(UserDetails user, String role) {
		for(GrantedAuthority auth : getAuthorities(user)) {
			if(auth.getAuthority().equals(role)) {
				return true;
			}
		}
		return false;
	}

	protected Collection<? extends GrantedAuthority> getAuthorities(UserDetails user) {
		return dynamicRoleHierarchy.getReachableGrantedAuthorities(user.getAuthorities());
	}

}
