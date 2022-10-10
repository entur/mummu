package no.entur.mummu.repositories;

public class StopPlaceFetchException extends RuntimeException{
    public StopPlaceFetchException(Exception exception) {
        super(exception);
    }
}
