document.addEventListener('DOMContentLoaded', function () {
    const personaRolesContainer = document.getElementById('persona-roles-container');
    const personaRolTemplate = document.getElementById('persona-rol-template');
    const addPersonaRoleBtn = document.getElementById('add-persona-role-button');
    
    // Función para añadir una nueva fila de persona-rol
    function addPersonaRolRow() {
        const template = personaRolTemplate.querySelector('.persona-rol-fila').cloneNode(true);
        personaRolesContainer.appendChild(template);
        
        // Añadir event listener al botón eliminar
        const eliminarBtn = template.querySelector('.eliminar-fila-btn');
        eliminarBtn.addEventListener('click', function() {
            personaRolesContainer.removeChild(template);
        });
    }
    
    // Añadir event listener a los botones de eliminar existentes
    document.querySelectorAll('.eliminar-fila-btn').forEach(function(btn) {
        btn.addEventListener('click', function() {
            const fila = btn.closest('.persona-rol-fila');
            personaRolesContainer.removeChild(fila);
        });
    });
    
    // Añadir event listener para añadir nueva fila
    if (addPersonaRoleBtn) {
        addPersonaRoleBtn.addEventListener('click', addPersonaRolRow);
    }
});