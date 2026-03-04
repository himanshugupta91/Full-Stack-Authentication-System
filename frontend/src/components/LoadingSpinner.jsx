import { memo } from 'react';

const LoadingSpinner = ({ minHeight = '100vh', size = '3rem' }) => {
    return (
        <div className="screen-center" style={{ minHeight }}>
            <div className="spinner-border text-primary" role="status" style={{ width: size, height: size }}>
                <span className="visually-hidden">Loading...</span>
            </div>
        </div>
    );
};

export default memo(LoadingSpinner);
