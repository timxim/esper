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
package com.espertech.esper.compiler.internal.util;

import com.espertech.esper.common.internal.compile.stage1.Compilable;

public class CompilableEPL implements Compilable {
    private final String epl;

    public CompilableEPL(String epl) {
        this.epl = epl;
    }

    public String getEpl() {
        return epl;
    }

    public String toEPL() {
        return epl;
    }

    public String toString() {
        return "CompilableEPL{" +
                "epl='" + epl + '\'' +
                '}';
    }
}
