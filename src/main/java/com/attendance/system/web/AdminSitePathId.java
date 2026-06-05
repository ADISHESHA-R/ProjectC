package com.attendance.system.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Resolves a path segment to a site PK via {@link com.attendance.system.service.SiteService#resolveSiteIdFromClientKey(String)}.
 * Uses URI variable {@code id} or {@code siteId}. Accepts numeric id, job code, or slug {@code name-jobCode}.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface AdminSitePathId {
}
