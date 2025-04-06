const form = document.getElementById('addUserForm');
const usernameInput = document.getElementById('username');
const emailInput = document.getElementById('email');
const passwordInput = document.getElementById('password');

const usernameError = document.getElementById('usernameError');
const emailError = document.getElementById('emailError');
const passwordError = document.getElementById('passwordError');

const strengthContainer = document.getElementById('strengthContainer');
const strengthBar = document.getElementById('strengthBar');

let strengthTimeout;

// Realtime Password Strength Feedback
passwordInput.addEventListener('input', () => {
    const password = passwordInput.value.trim();
    const strength = getPasswordStrength(password);

    clearTimeout(strengthTimeout);

    if (password === '') {
        strengthContainer.classList.remove('active');
        strengthBar.className = 'strength-bar';
        return;
    }

    strengthContainer.classList.add('active');

    strengthBar.className = 'strength-bar'; // reset

    if (strength === 'Weak') {
        strengthBar.classList.add('strength-weak');
    } else if (strength === 'Medium') {
        strengthBar.classList.add('strength-medium');
    } else {
        strengthBar.classList.add('strength-strong');
    }

    // Auto-hide tray after delay
    strengthTimeout = setTimeout(() => {
        strengthContainer.classList.remove('active');
    }, 2500);
});

form.addEventListener('submit', function (event) {
    let isValid = true;

    usernameError.textContent = '';
    emailError.textContent = '';
    passwordError.textContent = '';
    [usernameInput, emailInput, passwordInput].forEach(el => {
        el.classList.remove('is-invalid', 'is-valid', 'shake');
    });

    // Username validation
    const username = usernameInput.value.trim();
    const usernameRegex = /^[A-Za-z]{3,20}$/;
    if (!usernameRegex.test(username)) {
        usernameError.textContent = 'Username must be 3–20 letters only.';
        usernameInput.classList.add('is-invalid', 'shake');
        isValid = false;
    } else {
        usernameInput.classList.add('is-valid');
    }

    // Email validation (gmail only)
    const email = emailInput.value.trim();
    const emailRegex = /^[a-zA-Z0-9._%+-]+@gmail\.com$/;
    if (email === '') {
        emailError.textContent = 'Email is required.';
        emailInput.classList.add('is-invalid', 'shake');
        isValid = false;
    } else if (!emailRegex.test(email)) {
        emailError.textContent = 'Only @gmail.com emails are accepted.';
        emailInput.classList.add('is-invalid', 'shake');
        isValid = false;
    } else {
        emailInput.classList.add('is-valid');
    }

    // Password validation
    const password = passwordInput.value.trim();
    const strength = getPasswordStrength(password);

    if (password === '') {
        passwordError.textContent = 'Password is required.';
        passwordInput.classList.add('is-invalid', 'shake');
        isValid = false;
    } else if (strength === 'Weak') {
        passwordError.textContent = 'Password is too weak. Use uppercase, lowercase, numbers, and symbols.';
        passwordInput.classList.add('is-invalid', 'shake');
        isValid = false;
    } else {
        passwordInput.classList.add('is-valid');
    }

    if (!isValid) {
        event.preventDefault();
    }
});

function getPasswordStrength(password) {
    let strength = 0;
    if (password.length >= 6) strength++;
    if (/[A-Z]/.test(password)) strength++;
    if (/[a-z]/.test(password)) strength++;
    if (/[0-9]/.test(password)) strength++;
    if (/[^A-Za-z0-9]/.test(password)) strength++;

    if (strength >= 4) return 'Strong';
    if (strength === 3) return 'Medium';
    return 'Weak';
}




// Auto-add shake class for server-side errors on page load
document.addEventListener('DOMContentLoaded', () => {
    if (usernameInput.classList.contains('is-invalid')) {
        usernameInput.classList.add('shake');
    }
    if (emailInput.classList.contains('is-invalid')) {
        emailInput.classList.add('shake');
    }
    if (passwordInput.classList.contains('is-invalid')) {
        passwordInput.classList.add('shake');
    }
});

form.addEventListener('submit', function (event) {
    let isValid = true;

    // Reset styles
    [usernameInput, emailInput, passwordInput].forEach(el => {
        el.classList.remove('is-invalid', 'is-valid', 'shake');
    });

    // Username validation
    const username = usernameInput.value.trim();
    const usernameRegex = /^[A-Za-z]{3,20}$/;
    if (!usernameRegex.test(username)) {
        usernameError.textContent = 'Username must be 3–20 letters only.';
        usernameInput.classList.add('is-invalid', 'shake');
        isValid = false;
    }

    // Email validation
    const email = emailInput.value.trim();
    const emailRegex = /^[a-zA-Z0-9._%+-]+@gmail\.com$/;
    if (!emailRegex.test(email)) {
        emailError.textContent = 'Only @gmail.com emails are accepted.';
        emailInput.classList.add('is-invalid', 'shake');
        isValid = false;
    }

    // Password validation
    const password = passwordInput.value.trim();
    const strength = getPasswordStrength(password);
    if (strength === 'Weak') {
        passwordError.textContent = 'Password is too weak. Use uppercase, lowercase, numbers, and symbols.';
        passwordInput.classList.add('is-invalid', 'shake');
        isValid = false;
    }

    if (!isValid) {
        event.preventDefault();
    }
});