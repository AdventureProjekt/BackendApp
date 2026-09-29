package org.example.backendadventure.repos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.example.backendadventure.model.Activity;


public interface ActivityRepo extends JpaRepository<Activity, Integer> {



}
