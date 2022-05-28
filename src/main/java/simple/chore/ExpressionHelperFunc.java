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
import custom.exceptions.NotDeclaredException;
import custom.exceptions.NotDefinedException;
import parser.SimpleParser.SimpleParser;
import simple.chore.stuctures.Pid;
import type.checking.structures.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static simple.chore.MethodHelperFunc.getParameters;
import static simple.chore.MethodHelperFunc.getParametersExpr;

public class ExpressionHelperFunc {

    /**
     *
     * @param returnOptionsContext SimpleParser.ReturnOptionsContext, is part of the parser tree starting at rule returnOptions.
     * @param gamma Map<Pid, Map<String, DataType>>, the processes and the functions/variables defined in the scope.
     * @param list List<Expr>, a list of Expr.
     * @param sender Pid, the sender process.
     * @return List of the structure Expr.
     * @throws Exception is thrown when an error occurs.
     */
    public static List<Expr> returnOptions(SimpleParser.ReturnOptionsContext returnOptionsContext,Map<Pid, Map<String, DataType>> gamma, List<Expr> list, Pid sender) throws Exception {
        if (returnOptionsContext.eList() != null){
             return getEList(returnOptionsContext.eList(),gamma,list,sender);
        } else {
            list.add(getExprE(sender,returnOptionsContext.e(),gamma));
            return list;
        }
    }
    /**
     *
     * @param eListContext EListContext, is part of the parser tree starting at rule elist.
     * @param gamma Map, has all the procedures, and their input and output.
     * @param list List<Expr>, a list of Expr.
     * @param sender Pid, the sender process.
     * @return List of the structure Expr.
     * @throws Exception is thrown when an error occurred.
     */
    public static List<Expr> getEList(SimpleParser.EListContext eListContext, Map<Pid, Map<String, DataType>> gamma, List<Expr> list, Pid sender) throws Exception {
        if (eListContext == null || eListContext.e() == null){
            return list;
        }
        // Get the expression structure
        Expr expr = getExprE(sender, eListContext.e(),gamma);
        if (expr == null){ // Check if expression is null
            list.add(expr);
            return getEList(eListContext.eList(),gamma,list,sender);
        }
        list.add(expr);
        return getEList(eListContext.eList(),gamma,list,sender);
    }

    /**
     *
     * @param sender Pid, the sender process.
     * @param e EContext, is part of the parser tree starting at rule e.
     * @param gamma Pairs, the input and output of a specific procedure.
     * @return Expr, an expression.
     * @throws Exception is thrown when an error occurs.
     */
    public static Expr getExprE(Pid sender, SimpleParser.EContext e,  Map<Pid, Map<String, DataType>> gamma) throws Exception {
        // Checks which parser function is not null
        if (e.function() != null){
            List<Expr> paraExpr = new ArrayList<>();
            // Gets the parameters for the function
            if (e.function().parameters().e() != null){
                paraExpr = getParameters(sender, e.function().parameters(),gamma, paraExpr);
            }
            Map<String, DataType> input = gamma.get(sender);
            // Get the return type
            DataType returnType = input.get(e.function().functionName.getText());
            if (returnType == null){ // Checks if the function is defined.
                throw new NotDefinedException("Function ".concat(e.function().functionName.getText()).concat(" not defined in interface"));
            }
            return new FuncCall(e.function().functionName.getText(),paraExpr,getExprFromDataType(returnType));
        } else if (e.NUMBER() != null){
            return new IntExpr(Integer.parseInt(e.NUMBER().getText()));
        } else if (e.BOOL() != null){
            return new BoolExpr(Boolean.valueOf(e.BOOL().getText()));
        } else if (e.STRING() != null){
            return new StringExpr(e.STRING().getText());
        } else if (e.FLOAT() != null){
            return new DoubleExpr(Double.valueOf(e.FLOAT().getText()));
        } else if (e.WORD() != null){
            // Checks if the variable is defined.
            if (gamma.get(sender).get(e.WORD().getText()) == null){
                throw new NotDeclaredException("Variable "+e.WORD()+" not declared");
            }
            // Get the type of the variable
            Expr type = getExprFromDataType(gamma.get(sender).get(e.WORD().getText()));
            return new VariableExpr(e.WORD().getText(),type);
        }
        return null;
    }

    /**
     *
     * @param dataType A Datatype object.
     * @return The Expr that matches the given Datatype.
     */
    public static Expr getExprFromDataType(DataType dataType){
        switch (dataType){
            case BoolType b -> { return new BoolExpr(null);}
            case FuncType f -> { return getExprFromDataType(f.returnType());}
            case IntType i -> { return new IntExpr(0);}
            case StringType s -> { return new StringExpr(null);}
            case DoubleType d -> { return new DoubleExpr(1.0);}
            default -> { return null;}
        }
    }

    /**
     *
     * @param type Expr, an expression.
     * @param sender String, the process that the expression belongs to.
     * @return Expression, expression structure. Used be the pretty printer.
     */
    public static Expression getLiteralExpression(Expr type, String sender){
        switch (type) {
            case IntExpr i -> {
                return new LiteralExpression.IntegerLiteralExpression(i.value(), new WorldArgument(new Name(sender)));
            }
            case BoolExpr b -> {
                return new LiteralExpression.BooleanLiteralExpression(b.value(), new WorldArgument(new Name(sender)));
            }
            case StringExpr s -> {
                return new LiteralExpression.StringLiteralExpression(s.value(), new WorldArgument(new Name(sender)));
            }
            case DoubleExpr d -> {
                return new LiteralExpression.DoubleLiteralExpression(d.value(), new WorldArgument(new Name(sender)));
            }
            case FuncCall f -> {
                List<Expression> methodarglist = new ArrayList<>();
                // Get the parameters of the function
                methodarglist = getParametersExpr(f, methodarglist, sender);
                String name;
                // Method depends on if the functions have parameters or not
                if (f.parameter().size() == 0){
                    name = "get";
                } else {
                    name = "apply";
                }
                // Create the method
                Expression methodCom = new MethodCallExpression(new Name(name), methodarglist, List.of());
                return new ScopedExpression(new FieldAccessExpression(new Name(f.name().concat("_"+sender))), methodCom);
            }
            case VariableExpr v -> {
                return new FieldAccessExpression(new Name(v.name().concat("_"+sender)));
            }
        }
        return null;
    }
}
