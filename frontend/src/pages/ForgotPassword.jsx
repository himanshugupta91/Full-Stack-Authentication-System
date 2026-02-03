import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const ForgotPassword = () => {
    const [email, setEmail] = useState('');
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [loading, setLoading] = useState(false);
    const { resetPassword } = useAuth();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');
        setLoading(true);

        try {
            const response = await resetPassword(email);
            setSuccess(response.message);
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to send reset link. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="auth-container">
            <div className="auth-card">
                <div className="auth-header">
                    <div className="auth-icon">
                        <i className="bi bi-key"></i>
                    </div>
                    <h2>Forgot Password</h2>
                    <p className="text-muted">Enter your email to receive a reset link</p>
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
                    <div className="mb-4">
                        <label className="form-label">Email Address</label>
                        <div className="input-group">
                            <span className="input-group-text">
                                <i className="bi bi-envelope"></i>
                            </span>
                            <input
                                type="email"
                                className="form-control"
                                placeholder="Enter your email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
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
                                Sending...
                            </>
                        ) : (
                            <>
                                <i className="bi bi-envelope me-2"></i>
                                Send Reset Link
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

export default ForgotPassword;
