package com.charite.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;

    public void envoyerEmailValidation(String destinataire, String nomOrg, boolean approuver) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinataire);
        message.setSubject("Mise a jour du statut de votre organisation");

        if (approuver) {
            message.setText("Bonjour,\n\nVotre organisation '" + nomOrg + "' a ete approuvee avec succes sur notre plateforme !");
        } else {
            message.setText("Bonjour,\n\nMalheureusement, votre demande d'inscription pour l'organisation '" + nomOrg + "' a ete rejetee.");
        }

        try {
            emailSender.send(message);
        } catch (Exception e) {
            System.err.println("Erreur (Simulee) d'envoi d'email: " + e.getMessage());
        }
    }

    public void envoyerConfirmationDon(String to, String titreAction, java.math.BigDecimal montant) {
        SimpleMailMessage m = new SimpleMailMessage();
        m.setTo(to);
        m.setSubject("Confirmation de votre don");
        m.setText("Merci pour votre don de " + montant + " MAD pour : " + titreAction);
        try {
            emailSender.send(m);
        } catch (Exception e) {
            System.err.println("Erreur (Simulee) d'envoi d'email: " + e.getMessage());
        }
    }

    public void envoyerNotification(java.util.List<String> destinataires, String sujet, String texte) {
        SimpleMailMessage m = new SimpleMailMessage();
        m.setTo(destinataires.toArray(new String[0]));
        m.setSubject(sujet);
        m.setText(texte);
        try {
            emailSender.send(m);
        } catch (Exception e) {
            System.err.println("Erreur (Simulee) d'envoi d'email: " + e.getMessage());
        }
    }
}
