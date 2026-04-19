-- data.sql (charge automatiquement au demarrage Spring Boot)
INSERT INTO roles (nom) VALUES
                            ('ROLE_USER'), ('ROLE_ORG_ADMIN'), ('ROLE_SUPER_ADMIN')
    ON CONFLICT (nom) DO NOTHING;

INSERT INTO categories (nom) VALUES
                                 ('EDUCATION'), ('SANTE'), ('ENVIRONNEMENT'),
                                 ('LOGEMENT'), ('ALIMENTATION'), ('URGENCE'), ('AUTRE')
    ON CONFLICT (nom) DO NOTHING;
-- Dans application.properties, ajoute cette ligne : -- spring.sql.init.mode=always