/*
 ***************************************************************************************
 *  Copyright (C) 2006 EsperTech, Inc. All rights reserved.                            *
 *  http://www.espertech.com/esper                                                     *
 *  http://www.espertech.com                                                           *
 *  ---------------------------------------------------------------------------------- *
 *  The software in this package is published under the terms of the GPL license       *
 *  a copy of which has been included with this distribution in the license.txt file.  *
 ***************************************************************************************
 */
package com.espertech.esperio.http;

import com.espertech.esper.common.internal.event.core.EventBeanManufacturer;
import com.espertech.esper.common.internal.event.core.WriteablePropertyDescriptor;
import com.espertech.esper.common.internal.util.SimpleTypeParser;

public class EsperHttpRequestCacheEntry {

    private final EventBeanManufacturer eventBeanManufacturer;
    private final WriteablePropertyDescriptor[] writableProperties;
    private final SimpleTypeParser[] parsers;

    public EsperHttpRequestCacheEntry(EventBeanManufacturer eventBeanManufacturer, WriteablePropertyDescriptor[] writableProperties, SimpleTypeParser[] parsers) {
        this.eventBeanManufacturer = eventBeanManufacturer;
        this.writableProperties = writableProperties;
        this.parsers = parsers;
    }

    public EventBeanManufacturer getEventBeanManufacturer() {
        return eventBeanManufacturer;
    }

    public WriteablePropertyDescriptor[] getWritableProperties() {
        return writableProperties;
    }

    public SimpleTypeParser[] getParsers() {
        return parsers;
    }
}