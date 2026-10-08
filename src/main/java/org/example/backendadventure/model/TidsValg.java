package org.example.backendadventure.model;

import java.time.LocalTime;

// Ikke en entity: når manageren godkender en firmaforespørgsel,
// vælger manageren en starttid for hver aktivitet (US7)
public class TidsValg {
    private int activityId;
    private LocalTime tid;

    public TidsValg() {
    }

    public TidsValg(int activityId, LocalTime tid) {
        this.activityId = activityId;
        this.tid = tid;
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public LocalTime getTid() {
        return tid;
    }

    public void setTid(LocalTime tid) {
        this.tid = tid;
    }
}
