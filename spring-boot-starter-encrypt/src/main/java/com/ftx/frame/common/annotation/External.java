/*
 * Copyright (c) 2017- Ftecx Corp.
 * All rights reserved.
 */

package com.ftx.frame.common.annotation;

import java.lang.annotation.*;

/**
 *  External Interface
 * 
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target( {ElementType.TYPE, ElementType.METHOD})
public @interface External {

    boolean value() default true;
}
