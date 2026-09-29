package org.example.backendadventure.services;

import org.example.backendadventure.model.Activity;
import org.example.backendadventure.repos.ActivityRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityService {
    private final ActivityRepo activityRepo;

    public ActivityService(ActivityRepo activityRepo) {
        this.activityRepo = activityRepo;
    }

    public List<Activity> findAll() {
        return activityRepo.findAll();
    }

    public Activity findById(int id) {
        return activityRepo.findById(id).orElse(null);
    }

    public Activity save(Activity activity) {
        valider(activity);
        return activityRepo.save(activity);
    }

    public Activity opdater(int id, Activity activity) {
        Activity eksisterende = findById(id);
        if (eksisterende == null) {
            return null;
        }
        valider(activity);
        eksisterende.setNavn(activity.getNavn());
        eksisterende.setPris(activity.getPris());
        eksisterende.setBeskrivelse(activity.getBeskrivelse());
        eksisterende.setAldersgrænse(activity.getAldersgrænse());
        eksisterende.setVarighed(activity.getVarighed());
        eksisterende.setKapacitet(activity.getKapacitet());
        return activityRepo.save(eksisterende);
    }

    public boolean deleteById(int id) {
        if (!activityRepo.existsById(id)) {
            return false;
        }
        activityRepo.deleteById(id);
        return true;
    }

    private void valider(Activity activity) {
        if (activity.getNavn() == null || activity.getNavn().isBlank()) {
            throw new IllegalArgumentException("Navn skal udfyldes");
        }
        if (activity.getPris() < 0) {
            throw new IllegalArgumentException("Pris kan ikke være negativ");
        }
        if (activity.getAldersgrænse() < 0) {
            throw new IllegalArgumentException("Aldersgrænse kan ikke være negativ");
        }
        if (activity.getVarighed() <= 0) {
            throw new IllegalArgumentException("Varighed skal være over 0 minutter");
        }
        if (activity.getKapacitet() <= 0) {
            throw new IllegalArgumentException("Kapacitet skal være over 0");
        }
    }
}
