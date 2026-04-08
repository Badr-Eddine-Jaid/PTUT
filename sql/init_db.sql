--Projet ambassadeurs ISIS

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

-- Les comptes admin sont recrees automatiquement au demarrage (AdminAccountInitializer)
INSERT INTO utilisateur (id_utilisateur, nom, prenom, email, password, role) VALUES
(3, 'Salva', 'Clement', 'clement.salva@etud.univ-jfc.fr',
 '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS',
 'AMBASSADEUR')
ON CONFLICT (email) DO NOTHING;

INSERT INTO action (id_action, titre, description, lieu, type_action, type_etablissement, date_action, capacite_max, statut) VALUES
(1, 'Salon de l''Etudiant - InfoSup Toulouse',
 'Tenue du stand ISIS pour presenter l''ecole aux lyceens.',
 'Parc des Expositions, Toulouse', 'SALON ETUDIANT', NULL, '2026-01-25', 4, NULL),
(2, 'Intervention au Lycee de la Borde Basse',
 'Presentation de la classe preparatoire integree aux lyceens.',
 'Castres', 'LYCEE', 'Lycee', '2026-02-15', 2, 'OUVERT'),
(3, 'Formation des nouveaux Ambassadeurs',
 'Atelier pour apprendre a pitcher l''ecole.',
 'Campus ISIS, Salle 204', 'FORMATION', 'Ecole d''ingenieurs', '2026-03-10', 20, 'OUVERT'),
(4, 'Takeover Instagram - Journee Portes Ouvertes',
 'Prendre les commandes du compte Instagram ISIS.',
 'A distance / Campus ISIS', 'RESEAUX SOCIAUX', 'Digital', '2026-04-05', 1, 'OUVERT'),
(5, 'Formation Test', 'Test', 'ISIS', 'FORMATION', NULL, '2026-01-25', 5, NULL),
(6, 'Presentation de ISIS - Lycee Soult', 'Presentation de isis', 'Mazamet', 'LYCEE', NULL, '2026-04-01', 2, NULL),
(7, 'Salon de Perpignan', 'Tenue du salon', 'Perpignan', 'SALON ETUDIANT', NULL, '2026-02-01', 2, NULL),
(8, 'Story ISIS', 'faire une story', 'jsp', 'RESEAUX SOCIAUX', NULL, '2026-02-28', 1, NULL)
ON CONFLICT (id_action) DO NOTHING;

INSERT INTO inscription (id_inscription, date_inscription, statut_inscription, id_action, id_utilisateur, id_justificatif) VALUES
(23, '2026-04-05 13:43:06+00', 'VALIDE', 3, 3, NULL),
(24, '2026-04-05 14:01:49+00', 'DOSSIER_EN_COURS_DE_TRAITEMENT', 2, 3, NULL),
(25, '2026-04-05 14:02:07+00', 'INSCRIT', 4, 3, NULL)
ON CONFLICT DO NOTHING;

SELECT setval('utilisateur_id_utilisateur_seq', (SELECT MAX(id_utilisateur) FROM utilisateur));
SELECT setval('action_id_action_seq', (SELECT MAX(id_action) FROM action));
SELECT setval('inscription_id_inscription_seq', (SELECT MAX(id_inscription) FROM inscription));
