-- ============================================================
--  PTUT - Plateforme de gestion des ambassadeurs ISIS
--  Script d'initialisation de la base de données PostgreSQL
-- ============================================================

-- ── Tables ───────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS utilisateur (
    id_utilisateur BIGSERIAL PRIMARY KEY,
    nom            VARCHAR(255),
    prenom         VARCHAR(255),
    email          VARCHAR(255) UNIQUE NOT NULL,
    password       VARCHAR(255) NOT NULL,
    role           VARCHAR(50) CHECK (role IN ('ADMIN', 'AMBASSADEUR'))
);

CREATE TABLE IF NOT EXISTS action (
    id_action          BIGSERIAL PRIMARY KEY,
    titre              VARCHAR(255),
    description        TEXT,
    lieu               VARCHAR(255),
    type_etablissement VARCHAR(255),
    date_action        DATE,
    type_action        VARCHAR(100),
    capacite_max       INTEGER,
    statut             VARCHAR(100),
    date_creation      TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS document_resource (
    id           BIGSERIAL PRIMARY KEY,
    file_name    VARCHAR(255),
    content_type VARCHAR(255),
    size         BIGINT,
    uploaded_at  TIMESTAMP,
    uploaded_by  VARCHAR(255),
    content      BYTEA NOT NULL
);

CREATE TABLE IF NOT EXISTS inscription (
    id_inscription      BIGSERIAL PRIMARY KEY,
    date_inscription    TIMESTAMP,
    statut_inscription  VARCHAR(100),
    id_action           BIGINT NOT NULL REFERENCES action(id_action) ON DELETE CASCADE,
    id_utilisateur      BIGINT NOT NULL REFERENCES utilisateur(id_utilisateur) ON DELETE CASCADE,
    id_justificatif     BIGINT REFERENCES document_resource(id),
    CONSTRAINT uq_inscription UNIQUE (id_action, id_utilisateur)
);

-- ── Données de démonstration ──────────────────────────────────

-- Compte admin principal (mot de passe : admin0000)
-- Note : ce compte est aussi créé automatiquement au démarrage par AdminAccountInitializer
INSERT INTO utilisateur (nom, prenom, email, password, role) VALUES
('Admin', 'Manon', 'manon.fleuranceau@univ-jfc.fr',
 '$2a$10$7QxY5Z1KQJdV3e6nM2pLiO3RtWvXqA1bN4cD8eF9gH0iJ1kL2mN3o',
 'ADMIN')
ON CONFLICT (email) DO NOTHING;

-- Compte ambassadeur de test (mot de passe : test1234)
INSERT INTO utilisateur (nom, prenom, email, password, role) VALUES
('Dupont', 'Jean', 'jean.dupont@etudiant.fr',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPVyNFQBFwm',
 'AMBASSADEUR')
ON CONFLICT (email) DO NOTHING;

-- Actions de démonstration
INSERT INTO action (titre, description, lieu, type_action, type_etablissement, date_action, capacite_max, statut) VALUES
('Forum des métiers ISIS',
 'Présentation des formations ISIS lors du forum des métiers.',
 'ISIS - Castres',
 'SALON ÉTUDIANT',
 'Université',
 '2026-05-15',
 20,
 'OUVERT'),

('Intervention Lycée Bellevue',
 'Témoignage et présentation des études à ISIS auprès des terminales.',
 'Lycée Bellevue - Albi',
 'LYCÉE',
 'Lycée',
 '2026-04-22',
 5,
 'OUVERT'),

('Campagne Instagram ISIS',
 'Création de contenus pour les réseaux sociaux de l''école.',
 'En ligne',
 'RÉSEAUX SOCIAUX',
 NULL,
 '2026-05-01',
 NULL,
 'OUVERT'),

('Formation ambassadeurs',
 'Session de formation pour les nouveaux ambassadeurs.',
 'ISIS - Salle A12',
 'FORMATION',
 'Université',
 '2026-04-10',
 30,
 'OUVERT');
