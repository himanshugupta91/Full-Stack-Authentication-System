export const PASSWORD_MIN_LENGTH = 6;

const LETTER_PATTERN = /[A-Za-z]/;
const DIGIT_PATTERN = /\d/;
const WHITESPACE_PATTERN = /\s/;

const COMMON_PASSWORD_BLOCKLIST = new Set([
  'password',
  'password123',
  'password1',
  '123456',
  '12345678',
  '123456789',
  '1234567890',
  'qwerty',
  'qwerty123',
  'letmein',
  'welcome',
  'admin',
  'admin123',
  'iloveyou',
  'abc123',
  '111111',
  '123123',
]);

export const PASSWORD_POLICY_HINT = 'Minimum 6 chars with at least one letter and one number (no spaces).';

export const validatePassword = (password, emailHint) => {
  if (!password || !password.trim()) {
    return 'Password is required.';
  }

  if (password.length < PASSWORD_MIN_LENGTH) {
    return 'Password must be at least 6 characters long.';
  }

  if (WHITESPACE_PATTERN.test(password)) {
    return 'Password must not contain spaces.';
  }

  if (!LETTER_PATTERN.test(password)) {
    return 'Password must contain at least one letter.';
  }

  if (!DIGIT_PATTERN.test(password)) {
    return 'Password must contain at least one number.';
  }

  const normalizedPassword = password.toLowerCase();
  if (COMMON_PASSWORD_BLOCKLIST.has(normalizedPassword)) {
    return 'Password is too common. Choose a less predictable password.';
  }

  if (emailHint && emailHint.trim()) {
    const localPart = emailHint.toLowerCase().split('@')[0];
    if (localPart.length >= 3 && normalizedPassword.includes(localPart)) {
      return 'Password must not include your email username.';
    }
  }

  return null;
};
