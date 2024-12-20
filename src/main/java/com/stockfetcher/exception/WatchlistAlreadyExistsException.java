package com.stockfetcher.exception;

public class WatchlistAlreadyExistsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public WatchlistAlreadyExistsException(String message) {
        super(message);
    }
}
