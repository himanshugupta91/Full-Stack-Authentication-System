import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import toast from 'react-hot-toast';
import { useAuth } from '../context/AuthContext';
import { authAPI, userAPI } from '../services/api';

const UserDashboard = () => {
    const { user, isAdmin } = useAuth();
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [resendingOtp, setResendingOtp] = useState(false);
    const isEmailVerified = user?.enabled === true;

    useEffect(() => {
        fetchDashboardData();
    }, []);

    const fetchDashboardData = async () => {
        try {
            await userAPI.getDashboard();
            // Data is not currently used in UI, but call is made to verify token/access
        } catch {
            setError('Failed to load dashboard data');
        } finally {
            setLoading(false);
        }
    };

    const handleResendOtp = async () => {
        if (!user?.email || resendingOtp) {
            return;
        }

        setResendingOtp(true);
        try {
            const response = await authAPI.resendOtp(user.email);
            toast.success(response.data?.message || 'OTP sent successfully! Check your email.');
        } catch (err) {
            toast.error(err.response?.data?.message || 'Failed to resend OTP.');
        } finally {
            setResendingOtp(false);
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
            <div className="row">
                <div className="col-12">
                    <div className="welcome-card mb-4">
                        <div className="d-flex align-items-center">
                            <div className="avatar-circle me-3">
                                <i className="bi bi-person-fill"></i>
                            </div>
                            <div>
                                <h2 className="mb-1" style={{ color: '#1f1f1f' }}>Welcome back, {user?.name}!</h2>
                                <p className="text-muted mb-0">
                                    <i className="bi bi-envelope me-2"></i>
                                    {user?.email}
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {
                error && (
                    <div className="alert alert-danger">
                        <i className="bi bi-exclamation-triangle me-2"></i>
                        {error}
                    </div>
                )
            }

            {user?.enabled === false && (
                <div className="alert alert-warning d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3">
                    <div>
                        <strong className="d-block mb-1">Email verification pending</strong>
                        <span>Please verify your email to fully activate your account.</span>
                    </div>
                    <div className="d-flex gap-2">
                        <Link
                            to={`/verify-otp?email=${encodeURIComponent(user.email)}`}
                            state={{ email: user.email }}
                            className="btn btn-warning btn-sm"
                        >
                            <i className="bi bi-shield-check me-1"></i>
                            Verify Now
                        </Link>
                        <button
                            type="button"
                            className="btn btn-outline-secondary btn-sm"
                            onClick={handleResendOtp}
                            disabled={resendingOtp}
                        >
                            {resendingOtp ? 'Sending...' : 'Resend OTP'}
                        </button>
                    </div>
                </div>
            )}

            <div className="row g-4">
                <div className="col-md-6 col-lg-4">
                    <div className="dashboard-card">
                        <div className="card-icon bg-primary">
                            <i className="bi bi-person-badge"></i>
                        </div>
                        <h5 style={{ color: '#1f1f1f' }}>Account Status</h5>
                        <p className={`${isEmailVerified ? 'text-success' : 'text-warning'} mb-0`}>
                            <i className={`bi ${isEmailVerified ? 'bi-check-circle' : 'bi-hourglass-split'} me-1`}></i>
                            {isEmailVerified ? 'Verified & Active' : 'Pending Verification'}
                        </p>
                    </div>
                </div>

                <div className="col-md-6 col-lg-4">
                    <div className="dashboard-card">
                        <div className="card-icon bg-success">
                            <i className="bi bi-shield-check"></i>
                        </div>
                        <h5>Role</h5>
                        <div className="d-flex flex-wrap gap-2">
                            {user?.roles?.map((role, index) => (
                                <span key={index} className={`badge ${role.includes('ADMIN') ? 'bg-danger' : 'bg-primary'}`}>
                                    {role.replace('ROLE_', '')}
                                </span>
                            ))}
                        </div>
                    </div>
                </div>

                <div className="col-md-6 col-lg-4">
                    <div className="dashboard-card">
                        <div className="card-icon bg-info">
                            <i className="bi bi-clock"></i>
                        </div>
                        <h5>Last Active</h5>
                        <p className="mb-0">{new Date().toLocaleString()}</p>
                    </div>
                </div>
            </div>

            {
                isAdmin() && (
                    <div className="row mt-4">
                        <div className="col-12">
                            <div className="admin-notice">
                                <div className="d-flex align-items-center">
                                    <i className="bi bi-gear-fill me-3 text-warning" style={{ fontSize: '2rem' }}></i>
                                    <div>
                                        <h5 className="mb-1">Admin Access Available</h5>
                                        <p className="mb-0 text-muted">You have administrator privileges.</p>
                                    </div>
                                    <Link to="/admin" className="btn btn-warning ms-auto">
                                        <i className="bi bi-arrow-right me-2"></i>
                                        Go to Admin Panel
                                    </Link>
                                </div>
                            </div>
                        </div>
                    </div>
                )
            }


        </div >
    );
};

export default UserDashboard;
