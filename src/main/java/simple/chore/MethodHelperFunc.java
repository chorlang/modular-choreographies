/*
 * Copyright 2022 Anne Møller Madsen <annemadsen05@gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package simple.chore;

import choral.ast.Name;
import choral.ast.expression.*;
import choral.ast.type.WorldArgument;
import custom.exceptions.NotDefinedException;
import custom.exceptions.NotRecognizedException;
import parser.SimpleParser.SimpleParser;
import simple.chore.stuctures.Pid;
import stateful.chore.structures.L;
import stateful.chore.structures.N;
import type.checking.structures.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static simple.chore.ExpressionHelperFunc.*;

public class MethodHelperFunc {

    /**
     *
     * @param returnValueContext ReturnValueContext, is part of the parser tree starting at rule returnValue.
     * @param gamma Map<Pid, Map<String, DataType>>, has all the processes and the functions/variables in its scope.
     * @param list List<N>, list of the record N.
     * @return List of the structure N.
     * @throws Exception is thrown when an error occurred.
     */
    public static List<N> getArguments(SimpleParser.ReturnValueContext returnValueContext, Map<Pid, Map<String, DataType>> gamma, List<N> list) throws Exception {
        if (returnValueContext == null || returnValueContext.process == null){
            return list;
        }
        list.add(new N(new Pid(returnValueContext.process.getText()),returnOptions(returnValueContext.returnOptions(),gamma,new ArrayList<>(),new Pid(returnValueContext.process.getText()))));
        return getArguments(returnValueContext.returnValue(),gamma,list);
    }

    /**
     *
     * @param invokeargsContext InvokeargsContext, is part of the parser tree starting at rule invokeargs.
     * @param gamma Map<Pid, Map<String, DataType>>, has all the processes and the functions/variables in its scope.
     * @param list List<N>, list of the record N.
     * @return List of the structure N.
     * @throws Exception is thrown when an error occurred.
     */
    public static List<N> getVariables(SimpleParser.InvokeargsContext invokeargsContext, Map<Pid, Map<String, DataType>> gamma, List<N> list) throws Exception {
        if (invokeargsContext == null || invokeargsContext.process == null){
            return list; // Base case
        }
        List<Expr> nList = new ArrayList<>();
        // Go through all the arguments
        for (SimpleParser.EContext eContext : invokeargsContext.argsvalues().e()){
            if (invokeargsContext.process == null){ // Checks if the process have been written.
                throw new NotDefinedException("Process not defined for variable in invoke");
            }
            // Get the expression of the arguments
            Expr type = getExprE(new Pid(invokeargsContext.process.getText()),eContext, gamma);
            if (type == null){ // Check if the expression is null.
                throw new NotRecognizedException("Expr for return of procedure "+invokeargsContext.process.getText()+ "not recognized");
            }
            nList.add(type);
        }
        // Add the process with the arguments for that process, models; "p.(args)"
        list.add(new N(new Pid(invokeargsContext.process.getText()),nList));
        return getVariables(invokeargsContext.invokeargs(),gamma,list);
    }

    /**
     *
     * @param sender Pid, the sender process.
     * @param parameter ParametersContext, is part of the parser tree starting at rule parameters.
     * @param gamma Pairs, the input and output of a specific procedure.
     * @param list List<Expr>, list to return.
     * @return List of Expr, a list of expressions.
     * @throws Exception is thrown when an error occurred.
     */
    public static List<Expr> getParameters(Pid sender, SimpleParser.ParametersContext parameter, Map<Pid, Map<String, DataType>> gamma, List<Expr> list) throws Exception {
        if (parameter == null ){ // Base case
            return list;
        }
        if (parameter.e() == null){ // Check if the parameter is defined.
            throw new NotDefinedException("Parameter not defined");
        }
        // Get the expression
        Expr expr = getExprE(sender, parameter.e(), gamma);
        // In the parameters the process needs to be defined, unless the expression is a variable or function.
        if (parameter.process == null & !(expr instanceof VariableExpr || expr instanceof FuncCall)){
            throw new NotDefinedException("Process not defined for Expr");
        }
        if (expr == null){ // Check if the expression is null.
            throw new NotRecognizedException("Expr for parameter of function for process "+sender.name()+" not recognized");
        }
        list.add(expr); // Add the expression to the list.
        return getParameters(sender,parameter.parameters(),gamma,list);
    }

    /**
     *
     * @param funcCall FuncCall object, which is the structure of a function expression.
     * @param arglist List of Expressions, is empty when first called.
     * @param sender String, is the process name of the sender. Is the one the parameters resides at.
     * @return List of Expressions, is a list that contains all the parameters which are LiteralExpressions.
     */
    public static List<Expression> getParametersExpr(FuncCall funcCall, List<Expression> arglist, String sender){
        // Go through all the parameters and get the expression object which fits.
        for ( Expr para : funcCall.parameter()) {
            switch (para) {
                case IntExpr i -> {
                    LiteralExpression.IntegerLiteralExpression lit = new LiteralExpression.IntegerLiteralExpression(i.value(), new WorldArgument(new Name(sender)));
                    arglist.add(lit);
                }
                case BoolExpr b -> {
                    LiteralExpression.BooleanLiteralExpression bool = new LiteralExpression.BooleanLiteralExpression(b.value(), new WorldArgument(new Name(sender)));
                    arglist.add(bool);
                }
                case StringExpr s -> {
                    LiteralExpression.StringLiteralExpression string = new LiteralExpression.StringLiteralExpression(s.value(),new WorldArgument(new Name(sender)));
                    arglist.add(string);
                }
                case DoubleExpr d -> {
                    LiteralExpression.DoubleLiteralExpression doubleExpr = new LiteralExpression.DoubleLiteralExpression(d.value(),new WorldArgument(new Name(sender)));
                    arglist.add(doubleExpr);
                }
                case FuncCall f -> {
                    List<Expression> methodarglist = new ArrayList<>();
                    // Get the parameters of the function.
                    methodarglist = getParametersExpr(f,methodarglist,sender);
                    String name;
                    // The method to invoke the function depends on the number of parameters.
                    if (f.parameter().size() == 0){
                        name = "get";
                    } else {
                        name = "apply";
                    }
                    // Create the method
                    Expression methodCom = new MethodCallExpression(new Name(name),methodarglist,List.of());
                    ScopedExpression scopedExpression = new ScopedExpression(new FieldAccessExpression(new Name(f.name().concat("_"+sender))),methodCom);
                    arglist.add(scopedExpression);
                }
                case VariableExpr v -> {
                    Expression acessExpr = new FieldAccessExpression(new Name(v.name().concat("_"+sender)));
                    arglist.add(acessExpr);
                }
            }
        }
        return arglist;
    }
}
