import { useCallback, useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { adminAPI } from '../services/api';
import LoadingSpinner from '../components/LoadingSpinner';
import { getApiErrorMessage } from '../utils/apiError';
import { getRoleBadgeClass } from '../utils/roles';

const resolveLoginSourceView = (source) => {
    const normalizedSource = (source || 'EMAIL_PASSWORD').toUpperCase();
    const sourceViews = {
        EMAIL_PASSWORD: { label: 'Email/Password', badgeClass: 'bg-secondary' },
        LOCAL: { label: 'Email/Password', badgeClass: 'bg-secondary' },
        GOOGLE: { label: 'Google', badgeClass: 'bg-danger' },
        GITHUB: { label: 'GitHub', badgeClass: 'bg-dark' },
        APPLE: { label: 'Apple', badgeClass: 'bg-dark' },
        LINKEDIN: { label: 'LinkedIn', badgeClass: 'bg-primary' },
    };

    return sourceViews[normalizedSource] || { label: normalizedSource, badgeClass: 'bg-info' };
};

const AdminDashboard = () => {
    const { user } = useAuth();
    const [dashboardData, setDashboardData] = useState(null);
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [searchInput, setSearchInput] = useState('');
    const [searchTerm, setSearchTerm] = useState('');
    const [statusFilter, setStatusFilter] = useState('all');
    const [roleFilter, setRoleFilter] = useState('all');
    const [page, setPage] = useState(0);
    const [size, setSize] = useState(10);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);

    const fetchData = useCallback(async () => {
        setLoading(true);
        setError('');

        const userQueryParams = { page, size };
        if (searchTerm) {
            userQueryParams.search = searchTerm;
        }
        if (statusFilter !== 'all') {
            userQueryParams.enabled = statusFilter === 'active';
        }
        if (roleFilter !== 'all') {
            userQueryParams.role = roleFilter;
        }

        try {
            const [dashboardRes, usersRes] = await Promise.all([
                adminAPI.getDashboard(),
                adminAPI.getUsers(userQueryParams),
            ]);

            const usersPage = usersRes.data || {};
            const usersPageMeta = usersPage.page || {};

            setDashboardData(dashboardRes.data);
            setUsers(usersPage.content || []);
            setTotalPages(usersPage.totalPages ?? usersPageMeta.totalPages ?? 0);
            setTotalElements(usersPage.totalElements ?? usersPageMeta.totalElements ?? 0);
        } catch (error) {
            setError(getApiErrorMessage(error, 'Failed to load admin data'));
        } finally {
            setLoading(false);
        }
    }, [page, size, searchTerm, statusFilter, roleFilter]);

    useEffect(() => {
        fetchData();
    }, [fetchData]);

    const handleApplyFilters = (event) => {
        event.preventDefault();
        setPage(0);
        setSearchTerm(searchInput.trim());
    };

    const handleClearFilters = () => {
        setSearchInput('');
        setSearchTerm('');
        setStatusFilter('all');
        setRoleFilter('all');
        setPage(0);
    };

    if (loading) {
        return <LoadingSpinner minHeight="60vh" size="2.5rem" />;
    }

    return (
        <div className="container py-5 admin-dashboard-page">
            <div className="row mb-4">
                <div className="col-12">
                    <div className="admin-header">
                        <div className="d-flex align-items-center">
                            <div className="avatar-circle admin me-3">
                                <i className="bi bi-gear-fill"></i>
                            </div>
                            <div>
                                <h2 className="mb-1">Admin Dashboard</h2>
                                <p className="text-muted mb-0">
                                    Welcome, {user?.name}
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {error && (
                <div className="alert alert-danger">
                    <i className="bi bi-exclamation-triangle me-2"></i>
                    {error}
                </div>
            )}

            <div className="row g-4 mb-4">
                <div className="col-md-6 col-lg-3">
                    <div className="stat-card bg-primary">
                        <div className="stat-icon">
                            <i className="bi bi-people"></i>
                        </div>
                        <div className="stat-content">
                            <h3>{dashboardData?.totalUsers || 0}</h3>
                            <p className="mb-0">Total Users</p>
                        </div>
                    </div>
                </div>

                <div className="col-md-6 col-lg-3">
                    <div className="stat-card bg-success">
                        <div className="stat-icon">
                            <i className="bi bi-person-check"></i>
                        </div>
                        <div className="stat-content">
                            <h3>{dashboardData?.activeUsers || 0}</h3>
                            <p className="mb-0">Active Users</p>
                        </div>
                    </div>
                </div>

                <div className="col-md-6 col-lg-3">
                    <div className="stat-card bg-warning">
                        <div className="stat-icon">
                            <i className="bi bi-person-x"></i>
                        </div>
                        <div className="stat-content">
                            <h3>{(dashboardData?.totalUsers || 0) - (dashboardData?.activeUsers || 0)}</h3>
                            <p className="mb-0">Pending</p>
                        </div>
                    </div>
                </div>

                <div className="col-md-6 col-lg-3">
                    <div className="stat-card bg-info">
                        <div className="stat-icon">
                            <i className="bi bi-clock-history"></i>
                        </div>
                        <div className="stat-content">
                            <h3>Today</h3>
                            <p className="mb-0">{new Date().toLocaleDateString()}</p>
                        </div>
                    </div>
                </div>
            </div>

            <div className="row">
                <div className="col-12">
                    <div className="users-table-card">
                        <div className="d-flex justify-content-between align-items-center mb-4">
                            <h4 className="mb-0">
                                <i className="bi bi-people me-2"></i>
                                Users ({totalElements})
                            </h4>
                            <button className="btn btn-outline-primary btn-sm" onClick={fetchData}>
                                <i className="bi bi-arrow-clockwise me-1"></i>
                                Refresh
                            </button>
                        </div>

                        <form className="row g-2 mb-3" onSubmit={handleApplyFilters}>
                            <div className="col-lg-5">
                                <input
                                    type="text"
                                    className="form-control"
                                    placeholder="Search by name or email"
                                    value={searchInput}
                                    onChange={(event) => setSearchInput(event.target.value)}
                                />
                            </div>
                            <div className="col-lg-2">
                                <select
                                    className="form-select"
                                    value={statusFilter}
                                    onChange={(event) => {
                                        setStatusFilter(event.target.value);
                                        setPage(0);
                                    }}
                                >
                                    <option value="all">All Status</option>
                                    <option value="active">Active</option>
                                    <option value="pending">Pending</option>
                                </select>
                            </div>
                            <div className="col-lg-2">
                                <select
                                    className="form-select"
                                    value={roleFilter}
                                    onChange={(event) => {
                                        setRoleFilter(event.target.value);
                                        setPage(0);
                                    }}
                                >
                                    <option value="all">All Roles</option>
                                    <option value="USER">User</option>
                                    <option value="ADMIN">Admin</option>
                                </select>
                            </div>
                            <div className="col-lg-1">
                                <select
                                    className="form-select"
                                    value={size}
                                    onChange={(event) => {
                                        setSize(Number(event.target.value));
                                        setPage(0);
                                    }}
                                >
                                    <option value={10}>10</option>
                                    <option value={20}>20</option>
                                    <option value={50}>50</option>
                                </select>
                            </div>
                            <div className="col-lg-2 d-flex gap-2">
                                <button type="submit" className="btn btn-primary w-100">Apply</button>
                                <button type="button" className="btn btn-outline-secondary w-100" onClick={handleClearFilters}>
                                    Clear
                                </button>
                            </div>
                        </form>

                        <div className="table-responsive">
                            <table className="table table-hover admin-users-table">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Name</th>
                                        <th>Email</th>
                                        <th>Login Source</th>
                                        <th>Status</th>
                                        <th>Roles</th>
                                        <th>Created At</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {users.map((u) => {
                                        const loginSource = resolveLoginSourceView(u.loginSource);
                                        return (
                                            <tr key={u.id}>
                                                <td>#{u.id}</td>
                                                <td>
                                                    <div className="d-flex align-items-center">
                                                        <div className="user-avatar me-2">
                                                            {u.name?.charAt(0).toUpperCase()}
                                                        </div>
                                                        {u.name}
                                                    </div>
                                                </td>
                                                <td>{u.email}</td>
                                                <td>
                                                    <span className={`badge ${loginSource.badgeClass}`}>{loginSource.label}</span>
                                                </td>
                                                <td>
                                                    <span className={`badge ${u.enabled ? 'bg-success' : 'bg-warning'}`}>
                                                        {u.enabled ? 'Active' : 'Pending'}
                                                    </span>
                                                </td>
                                                <td>
                                                    <div className="d-flex flex-wrap gap-1">
                                                        {u.roles?.map((role, idx) => (
                                                            <span
                                                                key={idx}
                                                                className={`badge ${getRoleBadgeClass(role)}`}
                                                            >
                                                                {role.replace('ROLE_', '')}
                                                            </span>
                                                        ))}
                                                    </div>
                                                </td>
                                                <td>{u.createdAt || 'N/A'}</td>
                                            </tr>
                                        );
                                    })}
                                </tbody>
                            </table>
                        </div>

                        {users.length === 0 && (
                            <div className="text-center py-4 text-muted">
                                <i className="bi bi-inbox empty-state-icon"></i>
                                <p className="mt-2">No users found</p>
                            </div>
                        )}

                        <div className="d-flex justify-content-between align-items-center mt-3">
                            <button
                                className="btn btn-outline-primary btn-sm"
                                onClick={() => setPage((currentPage) => Math.max(currentPage - 1, 0))}
                                disabled={page === 0}
                            >
                                Previous
                            </button>
                            <span className="small text-muted">
                                Page {totalPages === 0 ? 0 : page + 1} of {Math.max(totalPages, 1)}
                            </span>
                            <button
                                className="btn btn-outline-primary btn-sm"
                                onClick={() => setPage((currentPage) => currentPage + 1)}
                                disabled={totalPages === 0 || page + 1 >= totalPages}
                            >
                                Next
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default AdminDashboard;
