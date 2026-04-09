package tn.esprit.pi.enums;

public enum RecoveryStatus {
    ON_TRACK,       // deviation within acceptable range
    DELAYED,        // moderate deviation — monitor closely
    DETERIORATING   // high deviation — consider return to hospital
}
