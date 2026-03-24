import { createContext, useCallback, useContext, useEffect, useMemo, useRef, useState } from 'react';

const ThemeContext = createContext(null);

const THEME_STORAGE_KEY = 'theme-preference';
const DARK_THEME = 'dark';
const LIGHT_THEME = 'light';
const THEME_SWITCH_CLASS = 'theme-switching';
const THEME_VALUES = new Set([DARK_THEME, LIGHT_THEME]);
const IS_BROWSER = typeof window !== 'undefined';

const isSupportedTheme = (value) => THEME_VALUES.has(value);

const readStoredTheme = () => {
  if (!IS_BROWSER) {
    return null;
  }

  try {
    const storedTheme = window.localStorage.getItem(THEME_STORAGE_KEY);
    return isSupportedTheme(storedTheme) ? storedTheme : null;
  } catch {
    return null;
  }
};

const writeStoredTheme = (theme) => {
  if (!IS_BROWSER) {
    return;
  }

  try {
    window.localStorage.setItem(THEME_STORAGE_KEY, theme);
  } catch {
    // Ignore storage write issues (private mode/quota restrictions).
  }
};

const resolveInitialTheme = () => {
  if (!IS_BROWSER) {
    return LIGHT_THEME;
  }

  const preloadedTheme = document.documentElement.getAttribute('data-theme');
  if (isSupportedTheme(preloadedTheme)) {
    return preloadedTheme;
  }

  const storedTheme = readStoredTheme();
  if (storedTheme) {
    return storedTheme;
  }

  return window.matchMedia('(prefers-color-scheme: dark)').matches ? DARK_THEME : LIGHT_THEME;
};

const applyTheme = (theme) => {
  document.documentElement.setAttribute('data-theme', theme);
  document.documentElement.style.colorScheme = theme;
};

export const ThemeProvider = ({ children }) => {
  const [theme, setThemeState] = useState(resolveInitialTheme);
  const shouldAnimateThemeChange = useRef(false);

  useEffect(() => {
    const root = document.documentElement;
    const shouldAnimate = shouldAnimateThemeChange.current;
    shouldAnimateThemeChange.current = false;

    if (shouldAnimate) {
      root.classList.add(THEME_SWITCH_CLASS);
    }

    applyTheme(theme);
    writeStoredTheme(theme);

    if (!shouldAnimate) {
      return undefined;
    }

    let secondRafId = 0;
    const firstRafId = window.requestAnimationFrame(() => {
      secondRafId = window.requestAnimationFrame(() => {
        root.classList.remove(THEME_SWITCH_CLASS);
      });
    });

    return () => {
      window.cancelAnimationFrame(firstRafId);
      if (secondRafId) {
        window.cancelAnimationFrame(secondRafId);
      }
      root.classList.remove(THEME_SWITCH_CLASS);
    };
  }, [theme]);

  useEffect(() => {
    if (!IS_BROWSER) {
      return undefined;
    }

    const handleStorage = (event) => {
      if (event.key !== THEME_STORAGE_KEY || !isSupportedTheme(event.newValue)) {
        return;
      }
      setThemeState(event.newValue);
    };

    window.addEventListener('storage', handleStorage);
    return () => {
      window.removeEventListener('storage', handleStorage);
    };
  }, []);

  const setTheme = useCallback((nextThemeOrUpdater) => {
    setThemeState((currentTheme) => {
      const resolvedTheme = typeof nextThemeOrUpdater === 'function'
        ? nextThemeOrUpdater(currentTheme)
        : nextThemeOrUpdater;

      if (!isSupportedTheme(resolvedTheme) || resolvedTheme === currentTheme) {
        return currentTheme;
      }

      shouldAnimateThemeChange.current = true;
      return resolvedTheme;
    });
  }, []);

  const toggleTheme = useCallback(() => {
    setTheme((currentTheme) => (currentTheme === DARK_THEME ? LIGHT_THEME : DARK_THEME));
  }, [setTheme]);

  const value = useMemo(
    () => ({
      theme,
      isDarkMode: theme === DARK_THEME,
      setTheme,
      toggleTheme,
    }),
    [theme, setTheme, toggleTheme]
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
