import { useState, useEffect } from 'react';
import { useNavigate, useSearchParams, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const ResetPassword = () => {
    const [searchParams] = useSearchParams();
    const token = searchParams.get('token');
    const [formData, setFormData] = useState({
        newPassword: '',
        confirmPassword: '',
    });
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [loading, setLoading] = useState(false);
    const { updatePassword } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        if (!token) {
            navigate('/forgot-password');
        }
    }, [token, navigate]);

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value,
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        if (formData.newPassword !== formData.confirmPassword) {
            setError('Passwords do not match!');
            return;
        }

        if (formData.newPassword.length < 6) {
            setError('Password must be at least 6 characters!');
            return;
        }

        setLoading(true);

        try {
            const response = await updatePassword(token, formData.newPassword);
            if (response.success) {
                setSuccess('Password updated successfully! Redirecting to login...');
                setTimeout(() => navigate('/login'), 2000);
            } else {
                setError(response.message);
            }
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to reset password. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="auth-container">
            <div className="auth-card">
                <div className="auth-header">
                    <div className="auth-icon">
                        <i className="bi bi-shield-lock"></i>
                    </div>
                    <h2>Reset Password</h2>
                    <p className="text-muted">Enter your new password</p>
                </div>

                {error && (
                    <div className="alert alert-danger alert-dismissible fade show" role="alert">
                        <i className="bi bi-exclamation-triangle me-2"></i>
                        {error}
                        <button type="button" className="btn-close" onClick={() => setError('')}></button>
                    </div>
                )}

                {success && (
                    <div className="alert alert-success" role="alert">
                        <i className="bi bi-check-circle me-2"></i>
                        {success}
                    </div>
                )}

                <form onSubmit={handleSubmit}>
                    <div className="mb-3">
                        <label className="form-label">New Password</label>
                        <div className="input-group">
                            <span className="input-group-text">
                                <i className="bi bi-lock"></i>
                            </span>
                            <input
                                type="password"
                                className="form-control"
                                name="newPassword"
                                placeholder="Enter new password"
                                value={formData.newPassword}
                                onChange={handleChange}
                                required
                                minLength={6}
                            />
                        </div>
                        <small className="text-muted">Must be at least 6 characters</small>
                    </div>

                    <div className="mb-4">
                        <label className="form-label">Confirm Password</label>
                        <div className="input-group">
                            <span className="input-group-text">
                                <i className="bi bi-lock-fill"></i>
                            </span>
                            <input
                                type="password"
                                className="form-control"
                                name="confirmPassword"
                                placeholder="Confirm new password"
                                value={formData.confirmPassword}
                                onChange={handleChange}
                                required
                            />
                        </div>
                    </div>

                    <button
                        type="submit"
                        className="btn btn-primary w-100 btn-lg"
                        disabled={loading}
                    >
                        {loading ? (
                            <>
                                <span className="spinner-border spinner-border-sm me-2"></span>
                                Updating...
                            </>
                        ) : (
                            <>
                                <i className="bi bi-check-circle me-2"></i>
                                Update Password
                            </>
                        )}
                    </button>
                </form>

                <div className="text-center mt-4">
                    <Link to="/login" className="text-decoration-none">
                        <i className="bi bi-arrow-left me-1"></i>
                        Back to Login
                    </Link>
                </div>
            </div>
        </div>
    );
};

export default ResetPassword;
