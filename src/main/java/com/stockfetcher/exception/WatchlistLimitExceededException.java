package com.stockfetcher.exception;


public class WatchlistLimitExceededException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public WatchlistLimitExceededException(String message) {
        super(message);
    }
}
