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
import choral.ast.body.VariableDeclaration;
import choral.ast.expression.*;
import choral.ast.type.TypeExpression;
import choral.ast.type.WorldArgument;
import custom.exceptions.NotDefinedException;
import simple.chore.stuctures.Pid;
import simple.chore.stuctures.ProcInvoke;
import simple.chore.stuctures.StatefulCom;
import simple.chore.stuctures.StatefulComVar;
import stateful.chore.structures.L;
import stateful.chore.structures.N;
import type.checking.structures.DataType;
import type.checking.structures.Expr;
import type.checking.structures.FuncCall;
import type.checking.structures.VariableExpr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static simple.chore.ExpressionHelperFunc.getLiteralExpression;
import static stateful.chore.TuplePrintStructure.makeTuple;
import static type.checking.ChoreTypingContent.getDataType;

public class ChoralStatement {

    /**
     *
     * @param channelName String, the name of the channel
     * @return ScopedExpression, expression of the form "channelName.com(null)"
     */
    public static ScopedExpression createInteractionStatement(String channelName){
        List<Expression> comArgList = new ArrayList<>();
        List<WorldArgument> argList = new ArrayList<>();
        // This is for the simple communication so the argument of the com method is null.
        argList.add(new WorldArgument(new Name("Unit")));
        Expression comArg = new NullExpression(argList);
        comArgList.add(comArg);
        // Create the com method
        Expression methodCom = new MethodCallExpression(new Name("com"),comArgList,List.of());
        // Create the expression "channelName.com(null)"
        FieldAccessExpression fieldName = new FieldAccessExpression(new Name(channelName));
        return new ScopedExpression(fieldName,methodCom);

    }

    /**
     *
     * @param s StatefulComVar, contains the information of the communication between two processes, where the variable is declared.
     * @return VariableDeclaration, the declaration of the variable that contains the return of a function.
     * @throws Exception Is thrown if there is a reference to a function not defined in the interfaces.
     */
    public static VariableDeclaration createStatefulVarVariableDeclaration(StatefulComVar s) throws Exception {
        if (s.e() == null){ // Throw exception is expression is null
            throw new NotDefinedException("Wanted e() is not defined in the interfaces");
        }
        List<WorldArgument> worlds = new ArrayList<>();
        // Get the process of the variable
        worlds.add(new WorldArgument(new Name(s.receiver().name())));
        // Create the type of the variable
        TypeExpression type = new TypeExpression(new Name(s.type().getStringType()), worlds, List.of());
        // Create the variable declaration.
        return new VariableDeclaration(new Name(s.varName().concat("_"+s.receiver().name())), type,List.of(),createStatefulVarAssignExpression(s));
    }

    /**
     *
     * @param p ProvInvoke, contains the information of the procedure invocation.
     * @param returnPath String, the path where all result files are created.
     * @param nrProcInvoke int, the number of procedure invocation that have been made so far. Used to name the tuple variable.
     * @return VariableDeclaration, the declaration of the variable that contains the return of the invocation.
     */
    public static VariableDeclaration createProcInvokeVariableDeclaration(ProcInvoke p, String returnPath, int nrProcInvoke) {
        List<WorldArgument> worlds = new ArrayList<>();
        List<TypeExpression> typeExpressions = new ArrayList<>();
        Map<String, String> renaming = new HashMap<>();
        int index = 0;
        // Create a renaming map, is used when processes are given different names, but otherwise have the correct stucture
        for (Map.Entry<Pid, Map<String, DataType>> process : p.procedure().input().entrySet()){
            renaming.put(process.getKey().name(),p.arguments().get(index).process().name());
            index++;
        }
        if (p.procedure().output().size() == 1){ // If the procedure returns from one process, in this case we know multiple values are returned
            // Add the process name
            worlds.add(new WorldArgument(new Name(renaming.get(p.procedure().output().get(0).process().name()))));
            // Add the type of the values that are return from the process
            for (DataType types : p.procedure().output().get(0).types()){
                typeExpressions.add(new TypeExpression(new Name(types.getStringType()), List.of(), List.of()));
            }
            // Create the tuple object and class, the type of the tuple depends on how many values are returned
            makeTuple(p.procedure().output().size(), returnPath);
            TypeExpression type = new TypeExpression(new Name("Tuple"+p.procedure().output().size()), worlds, typeExpressions);
            // Create the declaration, the name of the variable depends on how many invocations have already been made.
            return new VariableDeclaration(new Name("tuple"+nrProcInvoke), type, List.of(),createProcInvokeAssignExpression(p, "tuple"+nrProcInvoke));
        } else { // If the procedure returns from multiple processes
            // Iterate over all processes that have return values
            for (L output : p.procedure().output()) {
                // Add the process
                worlds.add(new WorldArgument(new Name(renaming.get(output.process().name()))));
                // Create the inner tuple if multiple values are return from a process, otherwise get the one type.
                if (output.types().size() != 1) {
                    makeTuple(output.types().size(), returnPath); // Create the tuple class
                    typeExpressions.add(new TypeExpression(new Name("Tuple" + output.types().size()), List.of(), List.of()));
                } else {
                    typeExpressions.add(new TypeExpression(new Name(output.types().get(0).getStringType()), List.of(), List.of()));
                }
            }
            // Create the outer tuple object and class
            makeTuple(p.procedure().output().size(), returnPath);
            TypeExpression type = new TypeExpression(new Name("Tuple"+p.procedure().output().size()), worlds, typeExpressions);
            // Create the declaration, the name of the variable depends on how many invocations have already been made.
            return new VariableDeclaration(new Name("tuple"+nrProcInvoke), type, List.of(),createProcInvokeAssignExpression(p, "tuple"+nrProcInvoke));
        }
    }

    /**
     *
     * @param s StatefulCom object, contains the information of the stateful communication between two processes.
     * @return AssignExpression, expression of the form "variable = channelName.com(args...)"
     */
    public static AssignExpression createStatefulAssignExpression(StatefulCom s) {
        List<Expression> comArgList = new ArrayList<>();
        List<TypeExpression> typeExpressions = new ArrayList<>();
        // Get the channel name
        String channelName = s.sender().name().substring(0,1).toUpperCase().concat(s.receiver().name().substring(0,1).toUpperCase());
        // Get the variable name
        FieldAccessExpression variable = new FieldAccessExpression(new Name(s.varName().concat("_"+s.receiver().name())));
        if (s.e() != null) { // Make sure that the expression is not null
            // Add the argument to the method com
            comArgList.add(getLiteralExpression(s.e(), s.sender().name()));
            // Define the type that will be communicated over the channel.
            typeExpressions.add(new TypeExpression(new Name(getDataType(s.e()).getStringType()),List.of(),List.of()));
            // Create the com method
            Expression methodCallCom = new MethodCallExpression(new Name("com"), comArgList, typeExpressions);
            // Create the expression "channelName.com(args...)"
            FieldAccessExpression fieldName = new FieldAccessExpression(new Name("ch"+channelName));
            ScopedExpression scope = new ScopedExpression(fieldName, methodCallCom);
            return new AssignExpression(scope, variable, AssignExpression.Operator.ASSIGN);
        } else {
            return null;
        }
    }

    /**
     *
     * @param s StatefulComVar, contains the information of the stateful communication between two processes, where the variable is declared.
     * @return AssignExpression, expression of the form "variable = channelName.com(args...)"
     */
    public static AssignExpression createStatefulVarAssignExpression(StatefulComVar s) {
        List<Expression> comArgList = new ArrayList<>();
        List<TypeExpression> typeExpressions = new ArrayList<>();
        // Get the channel name
        String channelName = s.sender().name().substring(0,1).toUpperCase().concat(s.receiver().name().substring(0,1).toUpperCase());
        // Get the variable name
        FieldAccessExpression variable = new FieldAccessExpression(new Name(s.varName().concat("_"+s.receiver().name())));
        if (s.e() != null) { // Make sure that the expression is not null
            // Add the argument to the method com
            comArgList.add(getLiteralExpression(s.e(), s.sender().name()));
            // Define the type that will be communicated over the channel.
            typeExpressions.add(new TypeExpression(new Name(s.type().getStringType()),List.of(),List.of()));
            // Create the com method
            Expression methodCallCom = new MethodCallExpression(new Name("com"), comArgList, typeExpressions);
            // Create the expression "channelName.com(args...)"
            FieldAccessExpression fieldName = new FieldAccessExpression(new Name("ch"+channelName));
            ScopedExpression scope = new ScopedExpression(fieldName, methodCallCom);
            return new AssignExpression(scope, variable, AssignExpression.Operator.ASSIGN);
        } else {
            return null;
        }
    }

    /**
     *
     * @param p ProcInvoke, contains the information of the procedure invocation.
     * @param name String, name of the tuple created for the invocation.
     * @return AssignExpression, expression of the form "variable = "X @ ( p, q ).run( args... )""
     */
    public static AssignExpression createProcInvokeAssignExpression(ProcInvoke p, String name) {
        FieldAccessExpression variable = new FieldAccessExpression(new Name(name));
        ScopedExpression scope = procInvokeScope(p);
        return new AssignExpression(scope,variable, AssignExpression.Operator.ASSIGN);
    }

    /**
     *
     * @param p ProcInvoke, contains the information of the procedure invocation.
     * @return ScopedExpression, expression of the form "X @ ( p, q ).run( args... )"
     */
    public static ScopedExpression procInvokeScope(ProcInvoke p) {
        List<Expression> comArgList = new ArrayList<>();
        // Add the channels to the arguments of the run method
        for (int i = 0; i < p.arguments().size(); i++){
            for (int j = i+1; j < p.arguments().size(); j++){
                String sender = p.arguments().get(i).process().name();
                String receiver = p.arguments().get(j).process().name();
                FieldAccessExpression parameter = new FieldAccessExpression(new Name("ch".concat(sender.substring(0,1).toUpperCase()).concat(receiver.substring(0,1).toUpperCase())));
                comArgList.add(parameter);
                FieldAccessExpression parameterReverse = new FieldAccessExpression(new Name("ch".concat(receiver.substring(0,1).toUpperCase()).concat(sender.substring(0,1).toUpperCase())));
                comArgList.add(parameterReverse);
            }
        }
        // Add the other parameters defined for the procedure.
        for (N n : p.arguments()){
            if (n.value().size() == 0){
                continue;
            }
            for (Expr expr : n.value()){
                if (expr instanceof VariableExpr v){
                    comArgList.add(new FieldAccessExpression(new Name(v.name().concat("_"+n.process().name()))));
                } else if (expr instanceof FuncCall f) {
                    comArgList.add(new FieldAccessExpression(new Name(f.name().concat("_"+n.process().name()))));
                }
            }
        }
        // Create the run method
        Expression methodCallprocedure = new MethodCallExpression(new Name("run"),comArgList,List.of());
        // Add the processes
        List<WorldArgument> worldArguments = new ArrayList<>();
        for (N proc: p.arguments()){
            worldArguments.add(new WorldArgument(new Name(proc.process().name())));
        }
        // Create the expression "X @ ( p, q ).run( args... )"
        TypeExpression typeExpression = new TypeExpression(new Name(p.procedure().name()), worldArguments,List.of());
        StaticAccessExpression staticAccessExpression = new StaticAccessExpression(typeExpression);
        return new ScopedExpression(staticAccessExpression,methodCallprocedure);
    }

}
