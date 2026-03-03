const GradientBackground = () => {
    return (
        <div
            style={{
                position: 'fixed',
                top: 0,
                left: 0,
                width: '100%',
                height: '100%',
                zIndex: -1,
                pointerEvents: 'none',
                background: `
                    radial-gradient(ellipse 80% 60% at 50% 0%, rgba(199, 175, 255, 0.25) 0%, transparent 60%),
                    radial-gradient(ellipse 60% 50% at 80% 20%, rgba(255, 197, 168, 0.2) 0%, transparent 50%),
                    radial-gradient(ellipse 50% 40% at 20% 30%, rgba(168, 210, 255, 0.2) 0%, transparent 50%),
                    linear-gradient(180deg, #faf9f7 0%, #ffffff 40%, #faf9f7 100%)
                `,
            }}
        />
    );
};

export default GradientBackground;
