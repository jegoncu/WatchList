function updateSliderValue(slider, outputId) {
    const outputElement = document.getElementById(outputId);
    if (outputElement) {
        outputElement.textContent = slider.value;
    }
}

function validateAnioRange(minId, maxId) {
    const minSlider = document.getElementById(minId);
    const maxSlider = document.getElementById(maxId);
    if (!minSlider || !maxSlider) return;

    const minValSpan = document.getElementById(minId + 'Val');
    const maxValSpan = document.getElementById(maxId + 'Val');

    if (parseInt(minSlider.value) > parseInt(maxSlider.value)) {
        if (event.target.id === minId) {
            maxSlider.value = minSlider.value;
            if (maxValSpan) maxValSpan.textContent = minSlider.value;
        } else {
            minSlider.value = maxSlider.value;
            if (minValSpan) minValSpan.textContent = maxSlider.value;
        }
    }
}

function validateNTemporadasRange(minId, maxId) {
    const minSlider = document.getElementById(minId);
    const maxSlider = document.getElementById(maxId);
    if (!minSlider || !maxSlider) return;

    const minValSpan = document.getElementById(minId + 'Val');
    const maxValSpan = document.getElementById(maxId + 'Val');

    if (parseInt(minSlider.value) > parseInt(maxSlider.value)) {
        if (event.target.id === minId) {
            maxSlider.value = minSlider.value;
            if (maxValSpan) maxValSpan.textContent = minSlider.value;
        } else {
            minSlider.value = maxSlider.value;
            if (minValSpan) minValSpan.textContent = maxSlider.value;
        }
    }
}

function resetPeliculasFiltersAndSort() {
    const form = document.getElementById('filter-sort-form-peliculas');
    if (form) {
        form.querySelector('input[name="sortBy"][value="puntuacion"]').checked = true;
        form.querySelector('input[name="sortDir"][value="desc"]').checked = true;

        form.querySelectorAll('input[name="generoIds"]').forEach(cb => cb.checked = false);
        form.querySelectorAll('input[name="plataformaIds"]').forEach(cb => cb.checked = false);

        const anioMinSlider = form.querySelector('#peliculaAnioMin');
        if (anioMinSlider) {
            const defaultMinAnio = anioMinSlider.getAttribute('min');
            anioMinSlider.value = defaultMinAnio;
            updateSliderValue(anioMinSlider, 'peliculaAnioMinVal');
        }

        const anioMaxSlider = form.querySelector('#peliculaAnioMax');
        if (anioMaxSlider) {
            const defaultMaxAnio = anioMaxSlider.getAttribute('max');
            anioMaxSlider.value = defaultMaxAnio;
            updateSliderValue(anioMaxSlider, 'peliculaAnioMaxVal');
        }

        const puntuacionMinSlider = form.querySelector('#peliculaPuntuacionMin');
        if (puntuacionMinSlider) {
            puntuacionMinSlider.value = 0.0;
            updateSliderValue(puntuacionMinSlider, 'peliculaPuntuacionMinVal');
        }

        const applyButton = form.querySelector('button[hx-get]');
        if (applyButton) {
            htmx.trigger(applyButton, "click");
        }
    }
}

function resetSeriesFiltersAndSort() {
    const form = document.getElementById('filter-sort-form-series');
    if (form) {
        form.querySelector('input[name="sortBy"][value="puntuacion"]').checked = true;
        form.querySelector('input[name="sortDir"][value="desc"]').checked = true;

        form.querySelectorAll('input[name="generoIds"]').forEach(cb => cb.checked = false);
        form.querySelectorAll('input[name="plataformaIds"]').forEach(cb => cb.checked = false);

        const anioEstrenoMinSlider = form.querySelector('#serieAnioEstrenoMin');
        if (anioEstrenoMinSlider) {
            const defaultMinAnio = anioEstrenoMinSlider.getAttribute('min');
            anioEstrenoMinSlider.value = defaultMinAnio;
            updateSliderValue(anioEstrenoMinSlider, 'serieAnioEstrenoMinVal');
        }

        const anioEstrenoMaxSlider = form.querySelector('#serieAnioEstrenoMax');
        if (anioEstrenoMaxSlider) {
            const defaultMaxAnio = anioEstrenoMaxSlider.getAttribute('max');
            anioEstrenoMaxSlider.value = defaultMaxAnio;
            updateSliderValue(anioEstrenoMaxSlider, 'serieAnioEstrenoMaxVal');
        }

        const puntuacionMinSlider = form.querySelector('#seriePuntuacionMin');
        if (puntuacionMinSlider) {
            puntuacionMinSlider.value = 0.0;
            updateSliderValue(puntuacionMinSlider, 'seriePuntuacionMinVal');
        }

        const nTemporadasMinSlider = form.querySelector('#serieNTemporadasMin');
        if (nTemporadasMinSlider) {
            const defaultMinTemp = nTemporadasMinSlider.getAttribute('min');
            nTemporadasMinSlider.value = defaultMinTemp;
            updateSliderValue(nTemporadasMinSlider, 'serieNTemporadasMinVal');
        }

        const nTemporadasMaxSlider = form.querySelector('#serieNTemporadasMax');
        if (nTemporadasMaxSlider) {
            const defaultMaxTemp = nTemporadasMaxSlider.getAttribute('max');
            nTemporadasMaxSlider.value = defaultMaxTemp;
            updateSliderValue(nTemporadasMaxSlider, 'serieNTemporadasMaxVal');
        }

        form.querySelector('input[name="estadoSerie"][value="todos"]').checked = true;

        const applyButton = form.querySelector('button[hx-get]');
        if (applyButton) {
            htmx.trigger(applyButton, "click");
        }
    }
}

function initSidebarDropdowns() {
    const dropdownToggles = document.querySelectorAll('.dropdown-toggle');

    dropdownToggles.forEach(toggle => {
        toggle.addEventListener('click', function() {
            const targetId = this.getAttribute('data-target');

            if (targetId === 'sidebar-dropdown-1-child') {
                const sortCriteriaElement = document.querySelector('.sidebar-dropdown-1-child');
                const sortDirectionElement = document.querySelector('.sort-direction-container');

                this.classList.toggle('collapsed');

                if (sortCriteriaElement) {
                    sortCriteriaElement.classList.toggle('collapsed');
                }
                if (sortDirectionElement) {
                    sortDirectionElement.classList.toggle('collapsed');
                }
            } else {
                const targetElement = document.querySelector('.' + targetId);
                if (targetElement) {
                    this.classList.toggle('collapsed');
                    targetElement.classList.toggle('collapsed');
                }
            }
        });
    });
}

function initSidebarToggle() {
    const toggleButton = document.getElementById('sidebar-toggle');
    const sidebar = document.getElementById('sidebar');
    const mainContent = document.getElementById('main-content');

    if (toggleButton && sidebar && mainContent) {
        toggleButton.addEventListener('click', function() {
            sidebar.classList.toggle('collapsed');
            mainContent.classList.toggle('expanded');
            this.classList.toggle('sidebar-collapsed');
            const icon = this.querySelector('.sidebar-toggle-icon');
            if (sidebar.classList.contains('collapsed')) {
                icon.textContent = '▶';
            } else {
                icon.textContent = '◀';
            }
        });
    }
}

document.addEventListener('DOMContentLoaded', function () {
    ['peliculaAnioMin', 'peliculaAnioMax', 'peliculaPuntuacionMin'].forEach(id => {
        const slider = document.getElementById(id);
        if (slider) updateSliderValue(slider, id + 'Val');
    });

    ['serieAnioEstrenoMin', 'serieAnioEstrenoMax', 'seriePuntuacionMin', 'serieNTemporadasMin',
        'serieNTemporadasMax'
    ].forEach(id => {
        const slider = document.getElementById(id);
        if (slider) updateSliderValue(slider, id + 'Val');
    });

    initSidebarDropdowns();

    initSidebarToggle();
});
