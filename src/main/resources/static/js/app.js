// Este archivo implementa un sistema de Single Page Application (SPA) para gestionar
// la autenticación y navegación en WatchList sin recargar la página.

// Referencias al DOM y almacenamiento del usuario actual
const app = document.getElementById('app');
const navbarItems = document.getElementById('navbarItems');
let currentUser = null;


async function checkAuth() {
    try {
        const response = await fetch('/api/auth/check');
        if (response.ok) {
            currentUser = await response.json();
            updateNavbar(true);
            return true;
        } else {
            updateNavbar(false);
            return false;
        }
    } catch (error) {
        console.error('Error al verificar autenticación:', error);
        updateNavbar(false);
        return false;
    }
}

function updateNavbar(isLoggedIn) {
    navbarItems.innerHTML = '';
    
    if (isLoggedIn) {
        // Navegación para usuarios autenticados
        navbarItems.innerHTML = `
            <li>
                <a href="#home">Inicio</a>
            </li>
            <li>
                <a href="#mylists">Mis Listas</a>
            </li>
            <li>
                <span>${currentUser.nombre}</span>
            </li>
            <li>
                <a href="#" id="logout">Cerrar Sesión</a>
            </li>
        `;
        
        document.getElementById('logout').addEventListener('click', logout);
    } else {
        // Navegación para usuarios no autenticados
        navbarItems.innerHTML = `
            <li>
                <a href="#login">Iniciar Sesión</a>
            </li>
            <li>
                <a href="#register">Registrarse</a>
            </li>
        `;
    }
}

function loadTemplate(templateId) {
    const template = document.getElementById(templateId);
    if (!template) {
        console.error(`Plantilla no encontrada: ${templateId}`);
        return;
    }
    
    const content = template.content.cloneNode(true);
    
    // Limpiar el contenedor y añadir el nuevo contenido
    app.innerHTML = '';
    app.appendChild(content);
}

async function handleRoute() {
    const isAuthenticated = await checkAuth();
    const hash = window.location.hash || '#home';
    
    if (hash === '#home' && !isAuthenticated) {
        window.location.hash = '#login';
        return;
    }
    
    switch (hash) {
        case '#login':
            loadTemplate('login-template');
            setupLoginForm();
            break;
        case '#register':
            loadTemplate('register-template');
            setupRegisterForm();
            break;
        case '#home':
            loadTemplate('home-template');
            document.getElementById('user-name').textContent = currentUser.nombre;
            break;
        default:
            loadTemplate('not-found-template');
    }
}

function setupLoginForm() {
    document.getElementById('login-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        const errorDiv = document.getElementById('login-error');
        
        try {
            const response = await fetch('/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ email, contrasenia: password })
            });
            
            if (response.ok) {
                currentUser = await response.json();
                window.location.hash = '#home';
            } else {
                const error = await response.json();
                errorDiv.textContent = error.mensaje || 'Error al iniciar sesión';
                errorDiv.style.display = 'block';
            }
        } catch (error) {
            console.error('Error en login:', error);
            errorDiv.textContent = 'Error de conexión';
            errorDiv.style.display = 'block';
        }
    });
}

function setupRegisterForm() {
    document.getElementById('register-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const email = document.getElementById('reg-email').value;
        const nombre = document.getElementById('reg-nombre').value;
        const password = document.getElementById('reg-password').value;
        const esPublico = document.getElementById('es-publico').checked;
        const errorDiv = document.getElementById('register-error');
        
        try {
            const response = await fetch('/api/auth/registro', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ email, nombre, contrasenia: password, esPublico })
            });
            
            if (response.ok) {
                window.location.hash = '#login';
            } else {
                const error = await response.json();
                errorDiv.textContent = error.mensaje || 'Error al registrarse';
                errorDiv.style.display = 'block';
            }
        } catch (error) {
            console.error('Error en registro:', error);
            errorDiv.textContent = 'Error de conexión';
            errorDiv.style.display = 'block';
        }
    });
}

async function logout() {
    try {
        await fetch('/api/auth/logout', { method: 'POST' });
        currentUser = null;
        window.location.hash = '#login';
    } catch (error) {
        console.error('Error en logout:', error);
    }
}

window.addEventListener('hashchange', handleRoute);
window.addEventListener('DOMContentLoaded', handleRoute);