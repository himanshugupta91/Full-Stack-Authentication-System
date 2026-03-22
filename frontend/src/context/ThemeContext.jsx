import { createContext, useCallback, useContext, useEffect, useMemo, useRef, useState } from 'react';

const ThemeContext = createContext(null);

const THEME_STORAGE_KEY = 'theme-preference';
const DARK_THEME = 'dark';
const LIGHT_THEME = 'light';
const THEME_SWITCH_CLASS = 'theme-switching';
const THEME_SWITCH_FALLBACK_DURATION_MS = 280;

const resolveInitialTheme = () => {
  const storedTheme = localStorage.getItem(THEME_STORAGE_KEY);
  if (storedTheme === DARK_THEME || storedTheme === LIGHT_THEME) {
    return storedTheme;
  }

  return window.matchMedia('(prefers-color-scheme: dark)').matches ? DARK_THEME : LIGHT_THEME;
};

const applyTheme = (theme) => {
  document.documentElement.setAttribute('data-theme', theme);
  document.documentElement.style.colorScheme = theme;
};

const shouldReduceMotion = () => window.matchMedia('(prefers-reduced-motion: reduce)').matches;

const getThemeSwitchDurationMs = () => {
  const durationValue = getComputedStyle(document.documentElement)
    .getPropertyValue('--theme-transition-duration')
    .trim();

  if (!durationValue) {
    return THEME_SWITCH_FALLBACK_DURATION_MS;
  }

  if (durationValue.endsWith('ms')) {
    const parsedDuration = Number.parseFloat(durationValue);
    return Number.isFinite(parsedDuration) ? parsedDuration : THEME_SWITCH_FALLBACK_DURATION_MS;
  }

  if (durationValue.endsWith('s')) {
    const parsedDuration = Number.parseFloat(durationValue) * 1000;
    return Number.isFinite(parsedDuration) ? parsedDuration : THEME_SWITCH_FALLBACK_DURATION_MS;
  }

  const parsedDuration = Number.parseFloat(durationValue);
  return Number.isFinite(parsedDuration) ? parsedDuration : THEME_SWITCH_FALLBACK_DURATION_MS;
};

export const ThemeProvider = ({ children }) => {
  const [theme, setTheme] = useState(resolveInitialTheme);
  const hasSyncedThemeOnce = useRef(false);

  useEffect(() => {
    const root = document.documentElement;
    const shouldAnimateThemeChange = hasSyncedThemeOnce.current && !shouldReduceMotion();

    if (shouldAnimateThemeChange) {
      root.classList.add(THEME_SWITCH_CLASS);
      // Ensure transition styles are applied before theme variables update.
      void root.offsetWidth;
    }

    applyTheme(theme);
    localStorage.setItem(THEME_STORAGE_KEY, theme);
    hasSyncedThemeOnce.current = true;

    if (!shouldAnimateThemeChange) {
      return undefined;
    }

    const timeoutId = window.setTimeout(() => {
      root.classList.remove(THEME_SWITCH_CLASS);
    }, getThemeSwitchDurationMs() + 40);

    return () => {
      window.clearTimeout(timeoutId);
      root.classList.remove(THEME_SWITCH_CLASS);
    };
  }, [theme]);

  const toggleTheme = useCallback(() => {
    setTheme((currentTheme) => (currentTheme === DARK_THEME ? LIGHT_THEME : DARK_THEME));
  }, []);

  const value = useMemo(
    () => ({
      theme,
      isDarkMode: theme === DARK_THEME,
      setTheme,
      toggleTheme,
    }),
    [theme, toggleTheme]
  );

  return <ThemeContext.Provider value={value}>{children}</ThemeContext.Provider>;
};

// eslint-disable-next-line react-refresh/only-export-components
export const useTheme = () => {
  const context = useContext(ThemeContext);
  if (!context) {
    throw new Error('useTheme must be used within a ThemeProvider');
  }
  return context;
};
