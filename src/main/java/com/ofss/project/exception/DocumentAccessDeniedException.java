package com.ofss.project.exception;

public class DocumentAccessDeniedException extends RuntimeException {

	public DocumentAccessDeniedException(String message) {
		super(message);
	}
}