import { useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { useAuth } from '../context/AuthContext';

const OAuthCallback = () => {
  const navigate = useNavigate();
  const { completeOAuthLogin } = useAuth();
  const isHandled = useRef(false);

  useEffect(() => {
    if (isHandled.current) {
      return;
    }
    isHandled.current = true;

    const finalizeOAuthLogin = async () => {
      try {
        const user = await completeOAuthLogin();
        toast.success('Signed in successfully.');

        if (user.roles?.includes('ROLE_ADMIN')) {
          navigate('/admin', { replace: true });
          return;
        }

        navigate('/dashboard', { replace: true });
      } catch (error) {
        toast.error(error.response?.data?.message || 'Could not complete OAuth login.');
        navigate('/login', { replace: true });
      }
    };

    finalizeOAuthLogin();
  }, [completeOAuthLogin, navigate]);

  return (
    <div className="auth-container">
      <div className="auth-card text-center">
        <div className="spinner-border text-primary mb-3" role="status">
          <span className="visually-hidden">Completing sign in...</span>
        </div>
        <h4>Completing sign in...</h4>
        <p className="text-muted mb-0">Please wait while we verify your account.</p>
      </div>
    </div>
  );
};

export default OAuthCallback;
