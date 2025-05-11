-- Usuarios (no sirve, las contraseñas hay que encriptarlas)
-- INSERT INTO
--     usuario (email, nombre, contrasenia, es_publico, es_admin)
-- VALUES
--     (
--         'admin@watchlist.com',
--         'Administrador',
--         'admin123',
--         true,
--         true
--     ),
--     (
--         'juan.perez@mail.es',
--         'Juan Pérez',
--         'password123',
--         true,
--         false
--     ),
--     (
--         'maria.gonzalez@mail.es',
--         'María González',
--         'password123',
--         true,
--         false
--     ),
--     (
--         'carlos.rodriguez@mail.es',
--         'Carlos Rodríguez',
--         'password123',
--         true,
--         false
--     ),
--     (
--         'laura.fernandez@mail.es',
--         'Laura Fernández',
--         'password123',
--         false,
--         false
--     ),
--     (
--         'pedro.lopez@mail.es',
--         'Pedro López',
--         'password123',
--         true,
--         false
--     ),
--     (
--         'ana.martinez@mail.es',
--         'Ana Martínez',
--         'password123',
--         true,
--         false
--     ),
--     (
--         'david.sanchez@mail.es',
--         'David Sánchez',
--         'password123',
--         true,
--         false
--     ),
--     (
--         'sofia.gomez@mail.es',
--         'Sofía Gómez',
--         'password123',
--         false,
--         false
--     ),
--     (
--         'javier.diaz@mail.es',
--         'Javier Díaz',
--         'password123',
--         true,
--         false
--     ),
--     (
--         'elena.moreno@mail.es',
--         'Elena Moreno',
--         'password123',
--         true,
--         false
--     );

-- Tabla Maestra de Generos
INSERT INTO
    genero (nombre)
VALUES
    ('Acción'),
    ('Animación'),
    ('Anime'),
    ('Aventura'),
    ('Biografía'),
    ('Ciencia ficción'),
    ('Cine negro'),
    ('Comedia'),
    ('Comedia romántica'),
    ('Crimen'),
    ('Deporte'),
    ('Distópico'),
    ('Documental'),
    ('Drama'),
    ('Drama histórico'),
    ('Educativo'),
    ('Entrevistas'),
    ('Experimental'),
    ('Familiar'),
    ('Fantasía'),
    ('Falso documental'),
    ('Historia'),
    ('Infantil'),
    ('Misterio'),
    ('Música'),
    ('Musical'),
    ('Noticias'),
    ('Policíaco'),
    ('Posapocalíptico'),
    ('Reality'),
    ('Religiosa'),
    ('Romance'),
    ('Slapstick'),
    ('Superhéroes'),
    ('Surrealismo'),
    ('Suspenso'),
    ('Talk Show'),
    ('Terror'),
    ('Terror sobrenatural'),
    ('Thriller'),
    ('Thriller psicológico'),
    ('Western'),
    ('Zombies');

-- Plataformas de Streaming
INSERT INTO
    plataforma (nombre, logo)
VALUES
    ('Netflix', '/img/logos/netflix.png'),
    ('Amazon Prime Video', '/img/logos/prime.png'),
    ('Disney+', '/img/logos/disney.png'),
    ('HBO Max', '/img/logos/HBO.png'),
    ('Apple TV+', '/img/logos/apple.png'),
    ('Movistar Plus+', '/img/logos/movistar.png'),
    ('Filmin', '/img/logos/filmin.png'),
    ('Sky Showtime', '/img/logos/skyshowtime.png'),
    ('YouTube Premium', '/img/logos/youtube.png'),
    ('Crunchyroll', '/img/logos/crunchyroll.png');