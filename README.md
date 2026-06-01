# 🌟 CharityHub - Plateforme de Solidarité Connectée

**CharityHub** est une application web moderne conçue pour simplifier et sécuriser la mise en relation entre les donateurs et les organisations caritatives. La plateforme permet de créer, gérer et financer des campagnes d'impact social avec une transparence totale.

---

## 🚀 Fonctionnalités Clés

- **🏠 Portail Donateurs** : Explorez des campagnes vérifiées, filtrez par catégories et faites des dons sécurisés.
- **🏢 Console Organisation** : Tableau de bord complet pour gérer les campagnes, suivre les dons et soumettre des rapports d'impact.
- **🛡️ Administration Centrale** : Système de vérification et d'approbation des organisations pour garantir la confiance.
- **💳 Paiements Sécurisés** : Intégration fluide avec **Stripe** pour les transactions financières.
- **🔑 Authentification Multi-mode** : Connexion classique ou via **Google OAuth 2.0**.
- **🌍 Multilingue** : Support complet du Français, Anglais et Arabe (RTL).
- **📧 Notifications Automatisées** : Confirmation de dons et mises à jour de statut par e-mail via SMTP Gmail.

---

## 🛠️ Stack Technique

- **Backend** : Java 21, Spring Boot 3, Spring Security.
- **Bases de données** :
  - **PostgreSQL** (Données relationnelles, utilisateurs, dons).
  - **MongoDB** (Gestion performante des fichiers médias et images).
- **Frontend** : Thymeleaf, Tailwind CSS, JavaScript.
- **APIs Tierces** : Stripe (Paiements), Google Cloud (Auth), Gmail SMTP.
- **DevOps** : Docker, Docker Compose.

---

## 🖼️ Démonstration Vidéo

https://github.com/user-attachments/assets/e779f6ef-5e71-4079-82b2-2db3ad4d9577



---

## 📦 Installation et Lancement

### Prérequis

- [Docker](https://www.docker.com/products/docker-desktop/) et [Docker Compose](https://docs.docker.com/compose/install/) installés.

### Étape 1 : Configuration

Clonez le dépôt et créez un fichier `.env` à la racine en vous basant sur `.env.example` :

```properties
DB_PROD_USERNAME=votre_utilisateur
DB_PROD_PASSWORD=votre_mot_de_passe
GOOGLE_CLIENT_ID=votre_id_google
GOOGLE_CLIENT_SECRET=votre_secret_google
STRIPE_TEST_SK=votre_clé_stripe_sk
STRIPE_TEST_PK=votre_clé_stripe_pk
GMAIL_USERNAME=votre_email_gmail
GMAIL_PASSWORD=votre_mot_de_passe_application
```

### Étape 2 : Lancement avec Docker

Exécutez la commande suivante pour compiler et lancer tous les services :

```bash
docker-compose up --build
```

L'application sera accessible sur : **http://localhost:8081**

---

## 🤝 Contribution

Les contributions sont les bienvenues ! N'hésitez pas à ouvrir une *Issue* ou à soumettre une *Pull Request*.

---

*Développé avec ❤️ pour un impact social positif.*
