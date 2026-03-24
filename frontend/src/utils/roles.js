export const ROLE_ADMIN = 'ROLE_ADMIN';

export const hasRole = (roles, roleName) => {
  if (!Array.isArray(roles) || !roleName) {
    return false;
  }
  return roles.includes(roleName);
};

export const hasAdminRole = (roles) => hasRole(roles, ROLE_ADMIN);

export const getRoleBadgeClass = (role) => {
  if (typeof role !== 'string') {
    return 'bg-primary';
  }
  return role.includes('ADMIN') ? 'bg-danger' : 'bg-primary';
};
