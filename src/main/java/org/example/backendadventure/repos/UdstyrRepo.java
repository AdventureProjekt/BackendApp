package org.example.backendadventure.repos;

import org.example.backendadventure.model.Udstyr;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UdstyrRepo extends JpaRepository<Udstyr, Integer> {
    List<Udstyr> findByActivityId(int activityId);
}
