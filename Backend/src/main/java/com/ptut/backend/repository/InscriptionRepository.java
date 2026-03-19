package com.ptut.backend.repository;

import com.ptut.backend.entity.Action;
import com.ptut.backend.entity.Inscription;
import com.ptut.backend.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InscriptionRepository extends JpaRepository<Inscription, Long> {

    boolean existsByActionAndUtilisateur(Action action, Utilisateur utilisateur);

    long countByAction(Action action);

    List<Inscription> findAllByAction_IdActionOrderByDateInscriptionDesc(Long idAction);
}