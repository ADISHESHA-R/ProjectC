package com.attendance.system.exception;

/**
 * Thrown when a requested resource (user, site, attendance, etc.) is not found.
 * Handled globally with HTTP 404.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Object identifier) {
        super(resourceName + " not found with identifier: " + identifier);
    }
}
