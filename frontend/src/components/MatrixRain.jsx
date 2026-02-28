import React, { useEffect, useRef } from 'react';

const MatrixRain = () => {
    const canvasRef = useRef(null);

    useEffect(() => {
        const canvas = canvasRef.current;
        if (!canvas) {
            return;
        }

        const ctx = canvas.getContext('2d');
        if (!ctx) {
            return;
        }

        const reducedMotionQuery = window.matchMedia('(prefers-reduced-motion: reduce)');
        const reducedMotion = reducedMotionQuery.matches;

        // Keep a static background when reduced motion is requested.
        if (reducedMotion) {
            const setStaticCanvas = () => {
                canvas.width = window.innerWidth;
                canvas.height = window.innerHeight;
                ctx.clearRect(0, 0, canvas.width, canvas.height);
                ctx.fillStyle = 'rgba(0, 0, 0, 0.88)';
                ctx.fillRect(0, 0, canvas.width, canvas.height);
            };

            setStaticCanvas();
            window.addEventListener('resize', setStaticCanvas);
            return () => window.removeEventListener('resize', setStaticCanvas);
        }

        const deviceMemory = navigator.deviceMemory || 4;
        const cpuCores = navigator.hardwareConcurrency || 4;
        const lowPowerDevice = deviceMemory <= 2 || cpuCores <= 4;
        const fontSize = lowPowerDevice ? 18 : 16;
        const targetFps = lowPowerDevice ? 16 : 30;
        const resetChance = lowPowerDevice ? 0.99 : 0.975;

        const katakana = 'アァカサタナハマヤャラワガザダバパイィキシチニヒミリヰギジヂビピウゥクスツヌフムユュルグズブヅプエェケセテネヘメレヱゲゼデベペオォコソトノホモヨョロヲゴゾドボポヴッン';
        const latin = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
        const nums = '0123456789';
        const alphabet = katakana + latin + nums;

        let drops = [];
        let animationFrameId = null;
        let lastFrameTime = 0;

        const initializeDrops = () => {
            canvas.width = window.innerWidth;
            canvas.height = window.innerHeight;
            const columns = Math.floor(canvas.width / fontSize);
            drops = Array.from({ length: columns }, () => Math.random() * canvas.height / fontSize);
        };

        const draw = (timestamp) => {
            const minimumFrameTime = 1000 / targetFps;
            if (timestamp - lastFrameTime < minimumFrameTime) {
                animationFrameId = window.requestAnimationFrame(draw);
                return;
            }
            lastFrameTime = timestamp;

            ctx.fillStyle = 'rgba(0, 0, 0, 0.05)';
            ctx.fillRect(0, 0, canvas.width, canvas.height);

            ctx.font = fontSize + 'px monospace';

            for (let i = 0; i < drops.length; i += 1) {
                const text = alphabet.charAt(Math.floor(Math.random() * alphabet.length));
                if (Math.random() > 0.95) {
                    ctx.fillStyle = '#FFF';
                } else {
                    ctx.fillStyle = '#0F0';
                }

                ctx.fillText(text, i * fontSize, drops[i] * fontSize);

                if (drops[i] * fontSize > canvas.height && Math.random() > resetChance) {
                    drops[i] = 0;
                }

                drops[i] += 1;
            }

            animationFrameId = window.requestAnimationFrame(draw);
        };

        initializeDrops();
        animationFrameId = window.requestAnimationFrame(draw);

        const handleResize = () => {
            initializeDrops();
        };

        const handleVisibilityChange = () => {
            if (document.hidden && animationFrameId) {
                window.cancelAnimationFrame(animationFrameId);
                animationFrameId = null;
                return;
            }

            if (!document.hidden && !animationFrameId) {
                animationFrameId = window.requestAnimationFrame(draw);
            }
        };

        window.addEventListener('resize', handleResize);
        document.addEventListener('visibilitychange', handleVisibilityChange);

        return () => {
            if (animationFrameId) {
                window.cancelAnimationFrame(animationFrameId);
            }
            window.removeEventListener('resize', handleResize);
            document.removeEventListener('visibilitychange', handleVisibilityChange);
        };
    }, []);

    return (
        <canvas
            ref={canvasRef}
            style={{
                position: 'fixed',
                top: 0,
                left: 0,
                zIndex: -1,
                width: '100%',
                height: '100%',
                background: 'transparent',
                pointerEvents: 'none'
            }}
        />
    );
};

export default MatrixRain;
