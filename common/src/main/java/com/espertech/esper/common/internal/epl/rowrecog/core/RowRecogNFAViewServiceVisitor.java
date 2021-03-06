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
package com.espertech.esper.common.internal.epl.rowrecog.core;

import com.espertech.esper.common.internal.epl.rowrecog.state.RowRecogPartitionState;

import java.util.Map;

public interface RowRecogNFAViewServiceVisitor {
    public void visitUnpartitioned(RowRecogPartitionState state);

    public void visitPartitioned(Map<Object, RowRecogPartitionState> states);
}
