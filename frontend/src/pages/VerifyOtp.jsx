import { useState, useEffect, useRef } from 'react';
import { useNavigate, useLocation, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { authAPI } from '../services/api';
import toast from 'react-hot-toast';

const VerifyOtp = () => {
    const [otp, setOtp] = useState(['', '', '', '', '', '']);
    const [loading, setLoading] = useState(false);
    const [resending, setResending] = useState(false);
    const [countdown, setCountdown] = useState(0);
    const inputRefs = useRef([]);
    const navigate = useNavigate();
    const location = useLocation();
    const email = location.state?.email || '';

    useEffect(() => {
        if (!email) {
            navigate('/register');
        }
    }, [email, navigate]);

    useEffect(() => {
        if (countdown > 0) {
            const timer = setTimeout(() => setCountdown(countdown - 1), 1000);
            return () => clearTimeout(timer);
        }
    }, [countdown]);

    const handleChange = (index, value) => {
        if (value.length > 1) return;

        const newOtp = [...otp];
        newOtp[index] = value;
        setOtp(newOtp);

        // Auto-focus next input
        if (value && index < 5) {
            inputRefs.current[index + 1]?.focus();
        }
    };

    const handleKeyDown = (index, e) => {
        if (e.key === 'Backspace' && !otp[index] && index > 0) {
            inputRefs.current[index - 1]?.focus();
        }
    };

    const handlePaste = (e) => {
        e.preventDefault();
        const pastedData = e.clipboardData.getData('text').slice(0, 6);
        const newOtp = [...otp];
        pastedData.split('').forEach((char, index) => {
            if (index < 6) {
                newOtp[index] = char;
            }
        });
        setOtp(newOtp);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        const otpString = otp.join('');
        if (otpString.length !== 6) {
            toast.error('Please enter complete OTP');
            return;
        }

        setLoading(true);

        try {
            const response = await authAPI.verifyOtp({ email, otp: otpString });
            if (response.data.success) {
                toast.success('Email verified successfully! Redirecting to login...');
                setTimeout(() => navigate('/login'), 2000);
            } else {
                toast.error(response.data.message);
            }
        } catch (err) {
            toast.error(err.response?.data?.message || 'Verification failed. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    const handleResendOtp = async () => {
        if (countdown > 0) return;

        setResending(true);

        try {
            const response = await authAPI.resendOtp(email);
            if (response.data.success) {
                toast.success('OTP sent successfully! Check your email.');
                setCountdown(60);
                setOtp(['', '', '', '', '', '']);
            } else {
                toast.error(response.data.message);
            }
        } catch (err) {
            toast.error(err.response?.data?.message || 'Failed to resend OTP.');
        } finally {
            setResending(false);
        }
    };

    return (
        <div className="auth-container">
            <div className="auth-card">
                <div className="auth-header">
                    <div className="auth-icon">
                        <i className="bi bi-envelope-check"></i>
                    </div>
                    <h2>Verify Email</h2>
                    <p className="text-muted">
                        Enter the 6-digit code sent to<br />
                        <strong>{email}</strong>
                    </p>
                </div>

                <form onSubmit={handleSubmit}>
                    <div className="otp-container mb-4">
                        {otp.map((digit, index) => (
                            <input
                                key={index}
                                ref={(el) => (inputRefs.current[index] = el)}
                                type="text"
                                className="otp-input"
                                maxLength={1}
                                value={digit}
                                onChange={(e) => handleChange(index, e.target.value)}
                                onKeyDown={(e) => handleKeyDown(index, e)}
                                onPaste={handlePaste}
                            />
                        ))}
                    </div>

                    <button
                        type="submit"
                        className="btn btn-primary w-100 btn-lg"
                        disabled={loading}
                    >
                        {loading ? (
                            <>
                                <span className="spinner-border spinner-border-sm me-2"></span>
                                Verifying...
                            </>
                        ) : (
                            <>
                                <i className="bi bi-check-circle me-2"></i>
                                Verify Email
                            </>
                        )}
                    </button>
                </form>

                <div className="text-center mt-4">
                    <p className="text-muted mb-2">Didn't receive the code?</p>
                    <button
                        className="btn btn-link text-decoration-none"
                        onClick={handleResendOtp}
                        disabled={resending || countdown > 0}
                    >
                        {resending ? (
                            'Sending...'
                        ) : countdown > 0 ? (
                            `Resend in ${countdown}s`
                        ) : (
                            'Resend OTP'
                        )}
                    </button>
                </div>

                <div className="text-center mt-3">
                    <Link to="/register" className="text-decoration-none">
                        <i className="bi bi-arrow-left me-1"></i>
                        Back to Registration
                    </Link>
                </div>
            </div>
        </div>
    );
};

export default VerifyOtp;
