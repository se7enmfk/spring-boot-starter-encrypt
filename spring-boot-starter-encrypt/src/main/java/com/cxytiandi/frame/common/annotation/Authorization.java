/*
 * Copyright (c) 2017- Ftecx Corp.
 * All rights reserved.
 */

package com.cxytiandi.frame.common.annotation;

import java.lang.annotation.*;

/**
 * Determines whether a Class's access is limited by the AuthorizationLevel
 * given.<br>
 * Default is AuthorizationLevel.REQUIRED
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target( {ElementType.TYPE, ElementType.METHOD})
public @interface Authorization {

    boolean value() default true;
}
