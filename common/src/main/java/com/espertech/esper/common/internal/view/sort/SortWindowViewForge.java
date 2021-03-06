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
package com.espertech.esper.common.internal.view.sort;

import com.espertech.esper.common.client.EventType;
import com.espertech.esper.common.internal.bytecodemodel.base.CodegenClassScope;
import com.espertech.esper.common.internal.bytecodemodel.base.CodegenMethod;
import com.espertech.esper.common.internal.bytecodemodel.model.expression.CodegenExpressionRef;
import com.espertech.esper.common.internal.context.aifactory.core.SAIFFInitializeSymbol;
import com.espertech.esper.common.internal.epl.expression.core.ExprForge;
import com.espertech.esper.common.internal.epl.expression.core.ExprNode;
import com.espertech.esper.common.internal.epl.expression.core.ExprNodeUtilityQuery;
import com.espertech.esper.common.internal.epl.expression.core.ExprOrderedExpr;
import com.espertech.esper.common.internal.view.core.*;
import com.espertech.esper.common.internal.view.util.ViewForgeSupport;

import java.util.List;

import static com.espertech.esper.common.internal.bytecodemodel.model.expression.CodegenExpressionBuilder.constant;
import static com.espertech.esper.common.internal.epl.expression.core.ExprNodeUtilityCodegen.codegenEvaluator;
import static com.espertech.esper.common.internal.epl.expression.core.ExprNodeUtilityCodegen.codegenEvaluators;

/**
 * Factory for sort window views.
 */
public class SortWindowViewForge extends ViewFactoryForgeBase implements DataWindowViewForge, DataWindowViewForgeWithPrevious {
    private final static String NAME = "Sort";

    private List<ExprNode> viewParameters;
    private ExprForge sizeForge;
    protected ExprNode[] sortCriteriaExpressions;
    protected boolean[] isDescendingValues;
    private boolean useCollatorSort = false;

    public void setViewParameters(List<ExprNode> parameters, ViewForgeEnv viewForgeEnv, int streamNumber) throws ViewParameterException {
        this.viewParameters = parameters;
        this.useCollatorSort = viewForgeEnv.getConfiguration().getCompiler().getLanguage().isSortUsingCollator();
    }

    public void attach(EventType parentEventType, int streamNumber, ViewForgeEnv viewForgeEnv) throws ViewParameterException {
        eventType = parentEventType;
        String message = NAME + " window requires a numeric size parameter and a list of expressions providing sort keys";
        if (viewParameters.size() < 2) {
            throw new ViewParameterException(message);
        }

        ExprNode[] validated = ViewForgeSupport.validate(NAME + " window", parentEventType, viewParameters, true, viewForgeEnv, streamNumber);
        for (int i = 1; i < validated.length; i++) {
            ViewForgeSupport.assertReturnsNonConstant(NAME + " window", validated[i], i);
        }

        ViewForgeSupport.validateNoProperties(getViewName(), validated[0], 0);
        sizeForge = ViewForgeSupport.validateSizeParam(getViewName(), validated[0], 0);

        sortCriteriaExpressions = new ExprNode[validated.length - 1];
        isDescendingValues = new boolean[sortCriteriaExpressions.length];

        for (int i = 1; i < validated.length; i++) {
            if (validated[i] instanceof ExprOrderedExpr) {
                isDescendingValues[i - 1] = ((ExprOrderedExpr) validated[i]).isDescending();
                sortCriteriaExpressions[i - 1] = validated[i].getChildNodes()[0];
            } else {
                sortCriteriaExpressions[i - 1] = validated[i];
            }
        }
    }

    protected Class typeOfFactory() {
        return SortWindowViewFactory.class;
    }

    protected String factoryMethod() {
        return "sort";
    }

    protected void assign(CodegenMethod method, CodegenExpressionRef factory, SAIFFInitializeSymbol symbols, CodegenClassScope classScope) {
        method.getBlock()
                .exprDotMethod(factory, "setSize", codegenEvaluator(sizeForge, method, this.getClass(), classScope))
                .exprDotMethod(factory, "setSortCriteriaEvaluators", codegenEvaluators(sortCriteriaExpressions, method, this.getClass(), classScope))
                .exprDotMethod(factory, "setSortCriteriaTypes", constant(ExprNodeUtilityQuery.getExprResultTypes(sortCriteriaExpressions)))
                .exprDotMethod(factory, "setIsDescendingValues", constant(isDescendingValues))
                .exprDotMethod(factory, "setUseCollatorSort", constant(useCollatorSort));
    }

    public String getViewName() {
        return NAME;
    }
}
