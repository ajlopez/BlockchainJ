package com.ajlopez.blockchain.test.dsl;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.test.World;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by ajlopez on 16/12/2020.
 */
public class DslComparisonTest {
    @Test
    public void equalIntegerComparison() throws IOException {
        World world = new World();
        DslExpression one = new DslTerm("1");
        DslExpression two = new DslTerm("2");

        Assert.assertEquals(Boolean.TRUE, new DslComparison(one, "==", one).evaluate(world));
        Assert.assertEquals(Boolean.FALSE, new DslComparison(one, "==", two).evaluate(world));
    }

    @Test
    public void equalIntegerToLongComparison() throws IOException {
        World world = new World();
        Block block = FactoryHelper.createBlock(world.getBlock("genesis"), FactoryHelper.createRandomAddress(), 0);
        world.setBlock("b1", block);

        DslExpression expression = new DslDotExpression(new DslTerm("b1"), "number");
        DslExpression one = new DslTerm("1");

        Assert.assertEquals(Boolean.TRUE, new DslComparison(expression, "==", one).evaluate(world));
        Assert.assertEquals(Boolean.TRUE, new DslComparison(one, "==", expression).evaluate(world));
    }

    @Test
    public void notEqualIntegerComparison() throws IOException {
        World world = new World();
        DslExpression one = new DslTerm("1");
        DslExpression two = new DslTerm("2");

        Assert.assertEquals(Boolean.FALSE, new DslComparison(one, "!=", one).evaluate(world));
        Assert.assertEquals(Boolean.TRUE, new DslComparison(one, "!=", two).evaluate(world));
    }

    @Test
    public void lessGreaterLessEqualGreaterEqualIntegerComparison() throws IOException {
        World world = new World();
        DslExpression one = new DslTerm("1");
        DslExpression two = new DslTerm("2");

        Assert.assertEquals(Boolean.FALSE, new DslComparison(one, "<", one).evaluate(world));
        Assert.assertEquals(Boolean.TRUE, new DslComparison(one, "<", two).evaluate(world));

        Assert.assertEquals(Boolean.FALSE, new DslComparison(two, "<=", one).evaluate(world));
        Assert.assertEquals(Boolean.TRUE, new DslComparison(one, "<=", two).evaluate(world));
        Assert.assertEquals(Boolean.TRUE, new DslComparison(one, "<=", one).evaluate(world));

        Assert.assertEquals(Boolean.FALSE, new DslComparison(one, ">", one).evaluate(world));
        Assert.assertEquals(Boolean.TRUE, new DslComparison(two, ">", one).evaluate(world));

        Assert.assertEquals(Boolean.FALSE, new DslComparison(one, ">=", two).evaluate(world));
        Assert.assertEquals(Boolean.TRUE, new DslComparison(one, ">=", one).evaluate(world));
        Assert.assertEquals(Boolean.TRUE, new DslComparison(two, ">=", one).evaluate(world));
    }
}
