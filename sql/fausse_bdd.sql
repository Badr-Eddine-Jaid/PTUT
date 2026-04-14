-- Donnees de demonstration - 50 ambassadeurs fictifs
-- Mot de passe pour tous : test
-- IDs 100-149 pour eviter les conflits avec les comptes auto-generes (admins)

INSERT INTO utilisateur (id_utilisateur, nom, prenom, email, password, role) VALUES
(100, 'Martin',     'Emma',       'emma.martin@etud.univ-jfc.fr',        '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(101, 'Bernard',    'Lucas',      'lucas.bernard@etud.univ-jfc.fr',      '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(102, 'Dubois',     'Lea',        'lea.dubois@etud.univ-jfc.fr',         '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(103, 'Thomas',     'Hugo',       'hugo.thomas@etud.univ-jfc.fr',        '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(104, 'Petit',      'Chloe',      'chloe.petit@etud.univ-jfc.fr',        '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(105, 'Robert',     'Nathan',     'nathan.robert@etud.univ-jfc.fr',      '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(106, 'Durand',     'Manon',      'manon.durand@etud.univ-jfc.fr',       '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(107, 'Leroy',      'Theo',       'theo.leroy@etud.univ-jfc.fr',         '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(108, 'Moreau',     'Camille',    'camille.moreau@etud.univ-jfc.fr',     '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(109, 'Simon',      'Enzo',       'enzo.simon@etud.univ-jfc.fr',         '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(110, 'Michel',     'Ines',       'ines.michel@etud.univ-jfc.fr',        '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(111, 'Laurent',    'Louis',      'louis.laurent@etud.univ-jfc.fr',      '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(112, 'Lefebvre',   'Zoe',        'zoe.lefebvre@etud.univ-jfc.fr',       '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(113, 'Marchand',   'Arthur',     'arthur.marchand@etud.univ-jfc.fr',    '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(114, 'Garcia',     'Jade',       'jade.garcia@etud.univ-jfc.fr',        '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(115, 'Dupont',     'Raphael',    'raphael.dupont@etud.univ-jfc.fr',     '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(116, 'Martinez',   'Clara',      'clara.martinez@etud.univ-jfc.fr',     '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(117, 'Morel',      'Tom',        'tom.morel@etud.univ-jfc.fr',          '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(118, 'David',      'Lucie',      'lucie.david@etud.univ-jfc.fr',        '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(119, 'Fontaine',   'Maxime',     'maxime.fontaine@etud.univ-jfc.fr',    '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(120, 'Bertrand',   'Elisa',      'elisa.bertrand@etud.univ-jfc.fr',     '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(121, 'Girard',     'Antoine',    'antoine.girard@etud.univ-jfc.fr',     '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(122, 'Bonnet',     'Alice',      'alice.bonnet@etud.univ-jfc.fr',       '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(123, 'Lambert',    'Julien',     'julien.lambert@etud.univ-jfc.fr',     '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(124, 'Denis',      'Oceane',     'oceane.denis@etud.univ-jfc.fr',       '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(125, 'Chevallier', 'Baptiste',   'baptiste.chevallier@etud.univ-jfc.fr','$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(126, 'Colin',      'Sarah',      'sarah.colin@etud.univ-jfc.fr',        '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(127, 'Masson',     'Pierre',     'pierre.masson@etud.univ-jfc.fr',      '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(128, 'Noel',       'Eva',        'eva.noel@etud.univ-jfc.fr',           '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(129, 'Renard',     'Florian',    'florian.renard@etud.univ-jfc.fr',     '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(130, 'Garnier',    'Pauline',    'pauline.garnier@etud.univ-jfc.fr',    '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(131, 'Arnaud',     'Romain',     'romain.arnaud@etud.univ-jfc.fr',      '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(132, 'Guerin',     'Marine',     'marine.guerin@etud.univ-jfc.fr',      '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(133, 'Faure',      'Alexandre',  'alexandre.faure@etud.univ-jfc.fr',    '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(134, 'Perrin',     'Juliette',   'juliette.perrin@etud.univ-jfc.fr',    '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(135, 'Gauthier',   'Quentin',    'quentin.gauthier@etud.univ-jfc.fr',   '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(136, 'Blanc',      'Margaux',    'margaux.blanc@etud.univ-jfc.fr',      '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(137, 'Caron',      'Nicolas',    'nicolas.caron@etud.univ-jfc.fr',      '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(138, 'Briand',     'Charlotte',  'charlotte.briand@etud.univ-jfc.fr',   '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(139, 'Meyer',      'Thomas',     'thomas.meyer@etud.univ-jfc.fr',       '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(140, 'Marchal',    'Laura',      'laura.marchal@etud.univ-jfc.fr',      '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(141, 'Dumont',     'Gabriel',    'gabriel.dumont@etud.univ-jfc.fr',     '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(142, 'Leroux',     'Elisa',      'elisa.leroux@etud.univ-jfc.fr',       '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(143, 'Giraud',     'Paul',       'paul.giraud@etud.univ-jfc.fr',        '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(144, 'Blanchard',  'Amelia',     'amelia.blanchard@etud.univ-jfc.fr',   '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(145, 'Perrot',     'Mathieu',    'mathieu.perrot@etud.univ-jfc.fr',     '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(146, 'Roussel',    'Victoire',   'victoire.roussel@etud.univ-jfc.fr',   '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(147, 'Picard',     'Adrien',     'adrien.picard@etud.univ-jfc.fr',      '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(148, 'Gaillard',   'Lola',       'lola.gaillard@etud.univ-jfc.fr',      '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR'),
(149, 'Bourgeois',  'Kevin',      'kevin.bourgeois@etud.univ-jfc.fr',    '$2a$10$rrnFDDAjdAxJOvK5UVYYV.uOdIllrCh/IW.Vpfvxsf46s5GVJemRS', 'AMBASSADEUR')
ON CONFLICT DO NOTHING;

-- Inscriptions reparties sur les 8 actions / 4 types / 4 mois
-- Action 1  - SALON ETUDIANT  - Jan 2026
-- Action 2  - LYCEE           - Feb 2026
-- Action 3  - FORMATION       - Mar 2026
-- Action 4  - RESEAUX SOCIAUX - Avr 2026  (cap 1, Clement deja inscrit)
-- Action 5  - FORMATION       - Jan 2026
-- Action 6  - LYCEE           - Avr 2026
-- Action 7  - SALON ETUDIANT  - Feb 2026
-- Action 8  - RESEAUX SOCIAUX - Feb 2026

INSERT INTO inscription (id_inscription, date_inscription, statut_inscription, id_action, id_utilisateur) VALUES
-- Action 1 (SALON ETUDIANT, Jan) - 12 inscrits
(100, '2026-01-10 09:00:00+00', 'VALIDE',                        1, 100),
(101, '2026-01-10 09:05:00+00', 'VALIDE',                        1, 101),
(102, '2026-01-11 10:00:00+00', 'VALIDE',                        1, 102),
(103, '2026-01-12 11:00:00+00', 'DOSSIER_EN_COURS_DE_TRAITEMENT',1, 103),
(104, '2026-01-13 14:00:00+00', 'DOSSIER_EN_COURS_DE_TRAITEMENT',1, 104),
(105, '2026-01-14 08:30:00+00', 'INSCRIT',                       1, 105),
(106, '2026-01-14 09:00:00+00', 'INSCRIT',                       1, 106),
(107, '2026-01-15 10:00:00+00', 'INSCRIT',                       1, 107),
(108, '2026-01-15 11:00:00+00', 'VALIDE',                        1, 108),
(109, '2026-01-16 14:00:00+00', 'VALIDE',                        1, 109),
(110, '2026-01-17 09:00:00+00', 'INSCRIT',                       1, 110),
(111, '2026-01-18 10:00:00+00', 'INSCRIT',                       1, 111),

-- Action 5 (FORMATION, Jan) - 8 inscrits
(112, '2026-01-20 09:00:00+00', 'VALIDE',                        5, 112),
(113, '2026-01-20 10:00:00+00', 'VALIDE',                        5, 113),
(114, '2026-01-21 09:00:00+00', 'VALIDE',                        5, 114),
(115, '2026-01-22 11:00:00+00', 'DOSSIER_EN_COURS_DE_TRAITEMENT',5, 115),
(116, '2026-01-23 14:00:00+00', 'INSCRIT',                       5, 116),
(117, '2026-01-23 15:00:00+00', 'INSCRIT',                       5, 117),
(118, '2026-01-24 09:00:00+00', 'INSCRIT',                       5, 118),
(119, '2026-01-24 10:00:00+00', 'INSCRIT',                       5, 119),

-- Action 2 (LYCEE, Feb) - 8 inscrits
(120, '2026-02-01 09:00:00+00', 'VALIDE',                        2, 120),
(121, '2026-02-02 10:00:00+00', 'VALIDE',                        2, 121),
(122, '2026-02-03 09:00:00+00', 'VALIDE',                        2, 122),
(123, '2026-02-04 11:00:00+00', 'DOSSIER_EN_COURS_DE_TRAITEMENT',2, 123),
(124, '2026-02-05 14:00:00+00', 'DOSSIER_EN_COURS_DE_TRAITEMENT',2, 124),
(125, '2026-02-06 09:00:00+00', 'INSCRIT',                       2, 125),
(126, '2026-02-07 10:00:00+00', 'INSCRIT',                       2, 126),
(127, '2026-02-08 11:00:00+00', 'INSCRIT',                       2, 127),

-- Action 7 (SALON ETUDIANT, Feb) - 7 inscrits
(128, '2026-02-05 09:00:00+00', 'VALIDE',                        7, 128),
(129, '2026-02-06 10:00:00+00', 'VALIDE',                        7, 129),
(130, '2026-02-07 09:00:00+00', 'DOSSIER_EN_COURS_DE_TRAITEMENT',7, 130),
(131, '2026-02-08 11:00:00+00', 'INSCRIT',                       7, 131),
(132, '2026-02-09 14:00:00+00', 'INSCRIT',                       7, 132),
(133, '2026-02-10 09:00:00+00', 'INSCRIT',                       7, 133),
(134, '2026-02-11 10:00:00+00', 'INSCRIT',                       7, 134),

-- Action 8 (RESEAUX SOCIAUX, Feb) - 5 inscrits
(135, '2026-02-15 09:00:00+00', 'VALIDE',                        8, 135),
(136, '2026-02-16 10:00:00+00', 'VALIDE',                        8, 136),
(137, '2026-02-17 09:00:00+00', 'DOSSIER_EN_COURS_DE_TRAITEMENT',8, 137),
(138, '2026-02-18 11:00:00+00', 'INSCRIT',                       8, 138),
(139, '2026-02-19 14:00:00+00', 'INSCRIT',                       8, 139),

-- Action 3 (FORMATION, Mar) - 10 inscrits
(140, '2026-03-01 09:00:00+00', 'VALIDE',                        3, 140),
(141, '2026-03-02 10:00:00+00', 'VALIDE',                        3, 141),
(142, '2026-03-03 09:00:00+00', 'VALIDE',                        3, 142),
(143, '2026-03-04 11:00:00+00', 'VALIDE',                        3, 143),
(144, '2026-03-05 14:00:00+00', 'DOSSIER_EN_COURS_DE_TRAITEMENT',3, 144),
(145, '2026-03-06 09:00:00+00', 'DOSSIER_EN_COURS_DE_TRAITEMENT',3, 145),
(146, '2026-03-07 10:00:00+00', 'INSCRIT',                       3, 146),
(147, '2026-03-08 11:00:00+00', 'INSCRIT',                       3, 147),
(148, '2026-03-09 14:00:00+00', 'INSCRIT',                       3, 148),
(149, '2026-03-10 09:00:00+00', 'INSCRIT',                       3, 149),

-- Action 6 (LYCEE, Avr) - 5 inscrits
(150, '2026-04-01 09:00:00+00', 'VALIDE',                        6, 100),
(151, '2026-04-01 10:00:00+00', 'VALIDE',                        6, 101),
(152, '2026-04-02 09:00:00+00', 'DOSSIER_EN_COURS_DE_TRAITEMENT',6, 102),
(153, '2026-04-02 11:00:00+00', 'INSCRIT',                       6, 103),
(154, '2026-04-03 14:00:00+00', 'INSCRIT',                       6, 104)
ON CONFLICT DO NOTHING;

SELECT setval('utilisateur_id_utilisateur_seq', 149);
SELECT setval('inscription_id_inscription_seq', 154);
