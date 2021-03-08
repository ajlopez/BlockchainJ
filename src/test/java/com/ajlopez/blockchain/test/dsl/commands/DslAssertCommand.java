package com.ajlopez.blockchain.test.dsl.commands;

import com.ajlopez.blockchain.test.World;
import com.ajlopez.blockchain.test.dsl.*;

import java.io.IOException;
import java.util.List;

/**
 * Created by ajlopez on 07/03/2021.
 */
public class DslAssertCommand extends DslCommand {
    public DslAssertCommand(List<String> arguments) {
        super("account", arguments);
    }

    @Override
    public void execute(World world) throws IOException, DslException {
        List<String> arguments  = this.getArguments();

        DslExpression expression;

        if (arguments.size() == 1)
            expression = toDslExpression(arguments.get(0));
        else
            expression = new DslComparison(toDslExpression(arguments.get(0)), arguments.get(1), toDslExpression(arguments.get(2)));

        if (Boolean.FALSE.equals(expression.evaluate(world)))
            throw new DslException(String.format("unsatisfied assertion '%s'", this.argumentsToString(arguments)));
    }

    private static DslExpression toDslExpression(String text) {
        int p = text.lastIndexOf('.');

        if (p > 0)
            return new DslDotExpression(toDslExpression(text.substring(0, p)), text.substring(p + 1));

        return new DslTerm(text);
    }

    private String argumentsToString(List<String> arguments) {
        String result = "";

        for (String argument : arguments)
            if (result.length() > 0)
                result += " " + argument;
            else
                result = argument;

        return result;
    }
}
