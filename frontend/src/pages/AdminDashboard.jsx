import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { adminAPI } from '../services/api';

const AdminDashboard = () => {
    const { user } = useAuth();
    const [dashboardData, setDashboardData] = useState(null);
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            const [dashboardRes, usersRes] = await Promise.all([
                adminAPI.getDashboard(),
                adminAPI.getUsers(),
            ]);
            setDashboardData(dashboardRes.data);
            setUsers(usersRes.data);
        } catch (err) {
            setError('Failed to load admin data');
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return (
            <div className="d-flex justify-content-center align-items-center" style={{ minHeight: '60vh' }}>
                <div className="spinner-border text-primary" role="status">
                    <span className="visually-hidden">Loading...</span>
                </div>
            </div>
        );
    }

    return (
        <div className="container py-5">
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
                                All Users
                            </h4>
                            <button className="btn btn-outline-primary btn-sm" onClick={fetchData}>
                                <i className="bi bi-arrow-clockwise me-1"></i>
                                Refresh
                            </button>
                        </div>

                        <div className="table-responsive">
                            <table className="table table-hover">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Name</th>
                                        <th>Email</th>
                                        <th>Status</th>
                                        <th>Roles</th>
                                        <th>Created At</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {users.map((u) => (
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
                                                <span className={`badge ${u.enabled ? 'bg-success' : 'bg-warning'}`}>
                                                    {u.enabled ? 'Active' : 'Pending'}
                                                </span>
                                            </td>
                                            <td>
                                                <div className="d-flex flex-wrap gap-1">
                                                    {u.roles?.map((role, idx) => (
                                                        <span
                                                            key={idx}
                                                            className={`badge ${role.includes('ADMIN') ? 'bg-danger' : 'bg-primary'}`}
                                                        >
                                                            {role.replace('ROLE_', '')}
                                                        </span>
                                                    ))}
                                                </div>
                                            </td>
                                            <td>{u.createdAt ? new Date(u.createdAt).toLocaleDateString() : 'N/A'}</td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>

                        {users.length === 0 && (
                            <div className="text-center py-4 text-muted">
                                <i className="bi bi-inbox" style={{ fontSize: '3rem' }}></i>
                                <p className="mt-2">No users found</p>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default AdminDashboard;
