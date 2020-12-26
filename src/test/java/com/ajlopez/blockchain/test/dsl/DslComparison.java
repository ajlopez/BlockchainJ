package com.ajlopez.blockchain.test.dsl;

import com.ajlopez.blockchain.core.types.Coin;
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

        leftValue = adjustSecondValue(rightValue, leftValue);
        rightValue = adjustSecondValue(leftValue, rightValue);

        if ("==".equals(this.operator))
            return leftValue.equals(rightValue);

        if ("!=".equals(this.operator))
            return !leftValue.equals(rightValue);

        int compare = ((Comparable)leftValue).compareTo(rightValue);

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

    private static Object adjustSecondValue(Object firstValue, Object secondValue) {
        if (firstValue instanceof Long && secondValue instanceof Integer)
            return ((Integer)secondValue).longValue();

        if (firstValue instanceof Coin && secondValue instanceof Integer)
            return Coin.fromUnsignedLong((Integer) secondValue);

        return secondValue;
    }
}
