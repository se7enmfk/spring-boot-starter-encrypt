/*
 * Copyright (c) 2017- Ftecx Corp.
 * All rights reserved.
 */
package com.cxytiandi.frame.common.annotation;

import java.lang.annotation.*;

/**
 *  External Interface
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target( {ElementType.TYPE, ElementType.METHOD})
public @interface NotAuthorization {

    boolean value() default true;
}
