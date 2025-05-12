document.addEventListener('DOMContentLoaded', function () {
    const container = document.getElementById('persona-roles-container');
    const template = document.getElementById('persona-role-template');
    const addButton = document.getElementById('add-persona-role-button');

    if (addButton) {
        addButton.addEventListener('click', function () {
            if (template) { 
                const newEntry = template.cloneNode(true);
                newEntry.removeAttribute('id');
                newEntry.style.display = 'flex'; 

                container.appendChild(newEntry);
            } else {
                console.error("Persona role template not found!");
            }
        });
    }

    const existingRemoveButtons = document.querySelectorAll('#persona-roles-container .remove-persona-role-btn');
    existingRemoveButtons.forEach(button => {

        if (!button.closest('#persona-role-template')) {
            button.addEventListener('click', function() {
                removePersonaRoleEntry(this);
            });
        }
    });


});

function removePersonaRoleEntry(button) {
    let entryToRemove = button.closest('.persona-rol-fila'); 
    if (entryToRemove) {
        entryToRemove.remove();
    }
}

