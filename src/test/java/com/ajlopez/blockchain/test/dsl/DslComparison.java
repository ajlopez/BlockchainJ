package com.ajlopez.blockchain.test.dsl;

import com.ajlopez.blockchain.test.World;

import java.io.IOException;

/**
 * Created by ajlopez on 16/12/2020.
 */
public class DslComparison implements DslExpression {
    private final DslExpression leftExpression;
    private final DslExpression rightExpression;
    private final String operator;

    public DslComparison(DslExpression leftExpression, String operator, DslExpression rightExpression) {
        this.leftExpression = leftExpression;
        this.operator = operator;
        this.rightExpression = rightExpression;
    }

    public Object evaluate(World world) throws IOException {
        Object leftValue = this.leftExpression.evaluate(world);
        Object rightValue = this.rightExpression.evaluate(world);

        if (leftValue instanceof Long && rightValue instanceof Integer)
            rightValue = ((Integer)rightValue).longValue();
        else if (leftValue instanceof Integer && rightValue instanceof Long)
            leftValue = ((Integer)leftValue).longValue();

        int compare = ((Comparable)leftValue).compareTo(rightValue);

        if ("==".equals(this.operator))
            return compare == 0;

        if ("!=".equals(this.operator))
            return compare != 0;

        if ("<".equals(this.operator))
            return compare < 0;

        if ("<=".equals(this.operator))
            return compare <= 0;

        if (">".equals(this.operator))
            return compare > 0;

        if (">=".equals(this.operator))
            return compare >= 0;

        // TODO unknown operator
        return null;
    }
}
