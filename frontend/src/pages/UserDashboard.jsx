import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import toast from 'react-hot-toast';
import { useAuth } from '../context/AuthContext';
import { authAPI, userAPI } from '../services/api';
import LoadingSpinner from '../components/LoadingSpinner';
import { getApiErrorMessage } from '../utils/apiError';
import { getRoleBadgeClass } from '../utils/roles';

const UserDashboard = () => {
    const { user, isAdmin } = useAuth();
    const [dashboardData, setDashboardData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [resendingOtp, setResendingOtp] = useState(false);
    const isEmailVerified = user?.enabled === true;
    const displayName = dashboardData?.user || user?.name || 'User';

    const fetchDashboardData = useCallback(async () => {
        try {
            const response = await userAPI.getDashboard();
            setDashboardData(response.data ?? null);
        } catch {
            setError('Failed to load dashboard data');
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        fetchDashboardData();
    }, [fetchDashboardData]);

    const handleResendOtp = async () => {
        if (!user?.email || resendingOtp) {
            return;
        }

        setResendingOtp(true);
        try {
            const response = await authAPI.resendOtp(user.email);
            toast.success(response.data?.message || 'OTP sent successfully! Check your email.');
        } catch (err) {
            toast.error(getApiErrorMessage(err, 'Failed to resend OTP.'));
        } finally {
            setResendingOtp(false);
        }
    };

    if (loading) {
        return <LoadingSpinner minHeight="60vh" size="2.5rem" />;
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
                                <h2 className="mb-1 dashboard-title">
                                    {`Welcome back, ${displayName}!`}
                                </h2>
                                <p className="text-muted mb-0">
                                    <i className="bi bi-envelope me-2"></i>
                                    {dashboardData?.email || user?.email}
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
                        <h5>Account Status</h5>
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
                            {(dashboardData?.roles || user?.roles || []).map((role, index) => (
                                <span key={index} className={`badge ${getRoleBadgeClass(role)}`}>
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
                        <p className="mb-0">{dashboardData?.timestamp || new Date().toLocaleString()}</p>
                    </div>
                </div>
            </div>

            {
                isAdmin() && (
                    <div className="row mt-4">
                        <div className="col-12">
                            <div className="admin-notice">
                                <div className="d-flex align-items-center">
                                    <i className="bi bi-gear-fill me-3 text-warning admin-access-icon"></i>
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
