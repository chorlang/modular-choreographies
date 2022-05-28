/*
 * Copyright 2022 Anne Møller Madsen <annemadsen05@gmail.com>
 * Copyright 2022 Fabrizio Montesi <famontesi@gmail.com>
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
import choral.ast.statement.*;
import choral.ast.type.TypeExpression;
import choral.ast.type.WorldArgument;
import custom.exceptions.VariableExprException;
import parser.SimpleParser.SimpleParser;
import simple.chore.stuctures.*;
import stateful.chore.structures.L;
import stateful.chore.structures.N;
import stateful.chore.structures.ProcedureInfo;
import type.checking.structures.*;

import java.util.*;

import static simple.chore.ChoralStatement.*;
import static simple.chore.ExpressionHelperFunc.getLiteralExpression;
import static simple.chore.ParseTreeConversion.getObject;
import static stateful.chore.TuplePrintStructure.makeTuple;
import static type.checking.ChoreTypingContent.getDataType;
import static type.checking.ChoreTypingContent.typeCheck;
import static type.checking.InitializationCheck.createGamma;
import static type.checking.InitializationCheck.initializingCheck;
import static type.checking.ReturnCheck.returnCheck;

public class ToProcedureInfo {

    /**
     *
     * @param start SimpleParser.ProgramContext, is part of the parser tree starting at rule program.
     * @param gamma Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>>, has all the procedures, and their input and output. Is added to.
     * @param original Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>>, has all the procedures, and their input and output. Is not changed at any point.
     * @param list List<ProcedureInfo>, the list of the procedures.
     * @param returnPath String, the path to where the files should be outputted.
     * @return List<ProcedureInfo>, list of procedures.
     * @throws Exception is thrown when an error occurred.
     */
    public static List<ProcedureInfo> getProcedures(SimpleParser.ProgramContext start,
                                                    Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> gamma,
                                                    Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> original,
                                                    List<ProcedureInfo> list, String returnPath) throws Exception {
        if ( start == null || start.procedure == null){ // Base case
            return list;
        }
        // Create the choreography
        Choreography choreography = getObject(start.procedure.getText(), start.c(),gamma, original);
        Map<Pairs<Pid,String>,Boolean> initialGamma = createGamma(gamma);
        // Make the return check, initialization check and type checking
        Boolean shouldReturn = gamma.get(start.procedure.getText()).second().size() > 0;
        returnCheck(choreography,shouldReturn,false);
        initializingCheck(choreography,initialGamma);
        typeCheck(start.procedure.getText(),gamma,choreography);
        // Get the info needed for the procedure class
        List<Pid> proc = pn(gamma.get(start.procedure.getText()).first());
        Statement methodBody = toRun(choreography, returnPath, proc, 0);
        // Create the ProcedureInfo record
        ProcedureInfo procedure = new ProcedureInfo(start.procedure.getText(),proc,methodBody,gamma.get(start.procedure.getText()).second(),gamma.get(start.procedure.getText()).first());
        // Add the procedure to the list.
        list.add(procedure);
        return getProcedures(start.program(),gamma, original,list, returnPath);
    }

    /**
     *
     * @param gamma Map<Pid, Map<String, DataType>>, has all the processes in a procedure and the functions/variables in their scope.
     * @return List<Pid>, list of all the processes in a procedure.
     */
    static List<Pid> pn(Map<Pid, Map<String, DataType>> gamma){
        return new ArrayList<>(gamma.keySet());
    }



    /**
     *
     * @param c Choreography, recursive structure of the choreography.
     * @param returnPath String, the path where all result files are created.
     * @param processes List<Pid>, list of the processes.
     * @param nrProcInvoke int, the number of procedure invocation that have been made so far.
     * @return Statement, recursive structure of the choreography. Used by the pretty printer.
     * @throws Exception is thrown when an error occurred.
     */
    static Statement toRun(simple.chore.stuctures.Choreography c, String returnPath, List<Pid> processes, int nrProcInvoke) throws Exception {
        // Creates a statement based on the instruction record.
        switch (c) {
            case Com p -> {
                return new ExpressionStatement(p.expression(),toRun(p.continuation(),returnPath, processes, nrProcInvoke));
            }
            case StatefulCom s -> {
                return statefulCom(s,returnPath,processes, nrProcInvoke);
            }
            case StatefulComVar sv -> {
                return statefulComVar(sv, returnPath,processes, nrProcInvoke);
            }
            case Return r -> {
                return return_(r, returnPath);
            }
            case ProcInvoke p -> {
                return procInvoke(p, returnPath, processes, nrProcInvoke);
            }
            case IfState i -> {
                return ifState(i,returnPath,processes, nrProcInvoke);
            }
            case Declaration d -> {
                return declaration(d,returnPath,processes, nrProcInvoke);
            }
            case Initialization in -> {
                return initialization(in,returnPath,processes, nrProcInvoke);
            }
            case InitialDeclare inDe -> {
                return initialDeclare(inDe,returnPath,processes, nrProcInvoke);
            }
            case Selection sel -> {
                return selection(sel,returnPath,processes, nrProcInvoke);
            }
            case Empty e -> {return e.statement();}
        }
        return new NilStatement();
    }

    /**
     *
     * @param s StatefulCom, recursive structure for the instruction statefulCom.
     * @param returnPath String, the path where all result files are created.
     * @param processes List<Pid>, list of the processes.
     * @param nrProcInvoke int, the number of procedure invocation that have been made so far.
     * @return Statement, recursive structure of the choreography. Used by the pretty printer.
     * @throws Exception is thrown when an error occurred.
     */
    private static Statement statefulCom(StatefulCom s, String returnPath, List<Pid> processes, int nrProcInvoke) throws Exception {
        // Create the assign expression
        AssignExpression assignExpression = createStatefulAssignExpression(s);
        // Create the expression statement
        return new ExpressionStatement(assignExpression,toRun(s.continuation(),returnPath, processes, nrProcInvoke));
    }

    /**
     *
     * @param s StatefulComVar, contains the information of the communication between two processes, where the variable is declared.
     * @param returnPath String, the path where all result files are created.
     * @param processes List<Pid>, list of the processes.
     * @param nrProcInvoke int, the number of procedure invocation that have been made so far.
     * @return Statement, recursive structure of the choreography. Used by the pretty printer.
     * @throws Exception is thrown when an error occurred.
     */
    private static Statement statefulComVar(StatefulComVar s, String returnPath, List<Pid> processes, int nrProcInvoke) throws Exception {
        List<VariableDeclaration> variList = new ArrayList<>();
        // Create the variable declaration.
        VariableDeclaration variableDeclaration = createStatefulVarVariableDeclaration(s);
        variList.add(variableDeclaration);
        // Create the variable delcaration statement.
        return new VariableDeclarationStatement(variList,toRun(s.continuation(),returnPath, processes, nrProcInvoke));
    }

    /**
     *
     * @param r Return, recursive structure for the return instruction.
     * @param returnPath String, the path where all result files are created.
     * @return Statement, recursive structure of the choreography. Used by the pretty printer.
     */
    private static Statement return_(Return r, String returnPath){
        // Create the tuple class
        makeTuple(r.returnValues().size(),returnPath);
        TypeExpression typeExpression;
        List<Expression>  expressionList = new ArrayList<>();
        List<TypeExpression> typeExpressions = new ArrayList<>();
        List<WorldArgument> worldArgumentList = new ArrayList<>();
        if (r.returnValues().size() == 1 ){ // The number of processes that have return values is one.
            // Add the process
            worldArgumentList.add(new WorldArgument(new Name(r.returnValues().get(0).process().name())));
            if (r.returnValues().get(0).value().size() > 1){ // The process have more than one return value
                // Go through each value and get the literal expression
                for (Expr expr: r.returnValues().get(0).value()){
                    expressionList.add(getLiteralExpression(expr, r.returnValues().get(0).process().name()));
                    // Create the type for the literal
                    typeExpressions.add(new TypeExpression(new Name(getDataType(expr).getStringType()),List.of(),List.of()));
                }
                // Create the tuple.
                typeExpression = new TypeExpression(new Name("Tuple"+ r.returnValues().size()),worldArgumentList,typeExpressions);
            } else { // The process have one return value
                // Get the literal expression.
                expressionList.add(getLiteralExpression(r.returnValues().get(0).value().get(0), r.returnValues().get(0).process().name()));
                if (r.returnValues().get(0).value().get(0) instanceof VariableExpr v){ // If the expression is a variable
                    // Return the variable name
                    return new ReturnStatement(new FieldAccessExpression(new Name(v.name().concat("_"+r.returnValues().get(0).process().name()))),new NilStatement());
                } else { // Not a variable
                    // Create the type of the literal.
                    typeExpression = new TypeExpression(new Name(r.returnValues().get(0).value().get(0).getStringType()),worldArgumentList,typeExpressions);
                }
            }
        } else { // More than one process have return values.
            // Go through the processes.
            for (N returnValue : r.returnValues()){
                if (returnValue.value().size() > 1){ // The process have more than one return value.
                    makeTuple(returnValue.value().size(),returnPath); // Create the tuple class
                    List<Expression> expressions = new ArrayList<>();
                    List<WorldArgument> world = new ArrayList<>();
                    List<TypeExpression> innerTupleType = new ArrayList<>();
                    // Add the process, used in outer tuple
                    worldArgumentList.add(new WorldArgument(new Name(returnValue.process().name())));
                    // Go through the return values of the process
                    for (Expr expr : returnValue.value()){
                        // Get the literal expression
                        expressions.add(getLiteralExpression(expr, returnValue.process().name()));
                        // Create the type of the literal and add it to the inner tuple.
                        innerTupleType.add(new TypeExpression(new Name(expr.getStringType()),List.of(),List.of()));
                    }
                    // Add process, used in inner tuple.
                    world.add(new WorldArgument(new Name(returnValue.process().name())));
                    // Create the inner tuple.
                    TypeExpression name = new TypeExpression(new Name("Tuple"+returnValue.value().size()),world,innerTupleType);
                    expressionList.add(new ClassInstantiationExpression(name,expressions,List.of()));
                    // Add to the outer tuple
                    typeExpressions.add(new TypeExpression(new Name("Tuple"+returnValue.value().size()),List.of(),List.of()));
                } else {
                    // Add the process, used in outer tuple
                    worldArgumentList.add(new WorldArgument(new Name(returnValue.process().name())));
                    // Go through the return values of the process
                    for (Expr expr : returnValue.value()){
                        // Get the literal expression
                        expressionList.add(getLiteralExpression(expr, returnValue.process().name()));
                        // Create the type of the literal and add it to the outer tuple.
                        typeExpressions.add(new TypeExpression(new Name(expr.getStringType()),List.of(),List.of()));
                    }
                }
            }
            // Create the type of the outer tuple
            typeExpression = new TypeExpression(new Name("Tuple"+ r.returnValues().size()),worldArgumentList,typeExpressions);
        }
        // Create the outer tuple.
        ClassInstantiationExpression newTuple = new ClassInstantiationExpression(typeExpression,expressionList,List.of());
        // Create the return statement.
        return new ReturnStatement(newTuple,new NilStatement());
    }

    /**
     *
     * @param p ProvInvoke, contains the information of the procedure invocation.
     * @param returnPath String, the path where all result files are created.
     * @param processes List<Pid>, list of processes.
     * @param nrProcInvoke int, the number of procedure invocation that have been made so far.
     * @return Statement, recursive structure of the choreography. Used by the pretty printer.
     * @throws Exception is thrown when an error occurred.
     */
    private static Statement procInvoke(ProcInvoke p, String returnPath, List<Pid> processes, int nrProcInvoke) throws Exception {
        if (p.procedure().output().size() == 0){ // The procedure does not return.
            ScopedExpression scope = procInvokeScope(p);
            return new ExpressionStatement(scope,toRun(p.continuation(),returnPath, processes, nrProcInvoke));
        } else if (p.procedure().output().size() == 1 && p.procedure().output().get(0).types().size() == 1) {
            // The procedure have one process with return values and it is one value.
            String name;
            if (p.variables().get(0).value().get(0) instanceof VariableExpr v){
                name = v.name()+"_"+p.procedure().output().get(0).process().name();
            } else { // Variables that store the return values need to be VariableExpr, otherwise they are not variables
                throw new VariableExprException("Variable used in procedure invocation is not a VariableExpr");
            }
            // Create the assign expression
            AssignExpression assignExpression = createProcInvokeAssignExpression(p,name);
            return new ExpressionStatement(assignExpression,toRun(p.continuation(),returnPath, processes, nrProcInvoke));
        } else { // The procedure have multiple or one process which have multiple return values.
            nrProcInvoke++; // Increment the number of procedure invocations that have been done.
            List<VariableDeclaration> variList = new ArrayList<>();
            VariableDeclaration variableDeclaration = createProcInvokeVariableDeclaration(p, returnPath, nrProcInvoke);
            variList.add(variableDeclaration);
            if (p.variables().size() == 1){ // Checks if there is one process that have variables
                if (p.variables().get(0).value().size() == 1){ // Checks if the one process have one variable
                    return new VariableDeclarationStatement(variList,toRun(p.continuation(),returnPath, processes, nrProcInvoke));
                }
            } // If multiple processes or one process have multiple variables the unpackingTuple need to be called.
            return new VariableDeclarationStatement(variList,unpackingTuple(p.variables(),0,0,p,returnPath,processes, nrProcInvoke));

        }
    }

    /**
     *
     * @param i IfState, contains the information of the if-statement.
     * @param returnPath String, the path where all result files are created.
     * @param processes List<Pid>, list of processes.
     * @param nrProcInvoke int, the number of procedure invocation that have been made so far.
     * @return Statement, recursive structure of the choreography. Used by the pretty printer.
     * @throws Exception is thrown when an error occurred.
     */
    private static Statement ifState(IfState i, String returnPath, List<Pid> processes, int nrProcInvoke) throws Exception {
        // Get the conditional expression.
        Expression condition = getLiteralExpression(i.e(), i.process().name());
        // Run on each of the branches.
        Statement ifBranch = toRun(i.then(),returnPath,processes, nrProcInvoke);
        Statement elseBranch = toRun(i.else_(),returnPath,processes, nrProcInvoke);
        return new IfStatement(condition,ifBranch,elseBranch, toRun(i.continuation(),returnPath,processes, nrProcInvoke));
    }

    /**
     *
     * @param d Declaration, contains the information of the declaration instruction.
     * @param returnPath String, the path where all result files are created.
     * @param processes List<Pid>, list of processes.
     * @param nrProcInvoke int, the number of procedure invocation that have been made so far.
     * @return Statement, recursive structure of the choreography. Used by the pretty printer.
     * @throws Exception is thrown when an error occurred.
     */
    private static Statement declaration(Declaration d, String returnPath, List<Pid> processes, int nrProcInvoke) throws Exception {
        List<VariableDeclaration> variList = new ArrayList<>();
        List<WorldArgument> worlds = new ArrayList<>();
        // Add the process
        worlds.add(new WorldArgument(new Name(d.process().name())));
        // Create the type
        TypeExpression type = new TypeExpression(new Name(d.type().getStringType()), worlds, List.of());
        variList.add(new VariableDeclaration(new Name(d.variableName()+"_"+d.process().name()), type,List.of(),null));
        // Create the variable declaration statement.
        return new VariableDeclarationStatement(variList,toRun(d.continuation(),returnPath,processes, nrProcInvoke));
    }

    /**
     *
     * @param i Initialization, contains the information of the assignment instruction.
     * @param returnPath String, the path where all result files are created.
     * @param processes List<Pid>, list of processes.
     * @param nrProcInvoke int, the number of procedure invocation that have been made so far.
     * @return Statement, recursive structure of the choreography. Used by the pretty printer.
     * @throws Exception is thrown when an error occurred.
     */
    private static Statement initialization(Initialization i, String returnPath, List<Pid> processes, int nrProcInvoke) throws Exception {
        // Get the expression it is assigned to
        Expression value = getLiteralExpression(i.e(),i.process().name());
        // Get the variable name
        FieldAccessExpression fieldName = new FieldAccessExpression(new Name(i.varName()+"_"+i.process().name()));
        // Create the assign expression
        AssignExpression assignExpression = new AssignExpression(value,fieldName, AssignExpression.Operator.ASSIGN);
        // Create the expression statement
        return new ExpressionStatement(assignExpression,toRun(i.continuation(),returnPath, processes, nrProcInvoke));
    }

    /**
     *
     * @param inDe InitialDeclare, contains the information of the instruction where the declaration and initialization is combined.
     * @param returnPath String, the path where all result files are created.
     * @param processes List<Pid>, list of processes.
     * @param nrProcInvoke int, the number of procedure invocation that have been made so far.
     * @return Statement, recursive structure of the choreography. Used by the pretty printer.
     * @throws Exception is thrown when an error occurred.
     */
    private static Statement initialDeclare(InitialDeclare inDe, String returnPath, List<Pid> processes, int nrProcInvoke) throws Exception {
        List<VariableDeclaration> variList = new ArrayList<>();
        List<WorldArgument> worlds = new ArrayList<>();
        // Get the expression of the initialization
        Expression value = getLiteralExpression(inDe.e(),inDe.process().name());
        // Get the variable name
        FieldAccessExpression fieldName = new FieldAccessExpression(new Name(inDe.varName()+"_"+inDe.process().name()));
        // Create the assign expression
        AssignExpression assignExpression = new AssignExpression(value,fieldName, AssignExpression.Operator.ASSIGN);
        // Add the process
        worlds.add(new WorldArgument(new Name(inDe.process().name())));
        // Get the type of the expression type
        TypeExpression type = new TypeExpression(new Name(inDe.type().getStringType()), worlds, List.of());
        variList.add(new VariableDeclaration(new Name(inDe.varName()+"_"+inDe.process().name()), type, List.of(),assignExpression));
        // Create the variable declaration statement
        return new VariableDeclarationStatement(variList,toRun(inDe.continuation(),returnPath, processes, nrProcInvoke));
    }

    /**
     *
     * @param sel Selection, contains the information of the selection statement.
     * @param returnPath String, the path where all result files are created.
     * @param processes List<Pid>, list of processes.
     * @param nrProcInvoke int, the number of procedure invocation that have been made so far.
     * @return Statement, recursive structure of the choreography. Used by the pretty printer.
     * @throws Exception is thrown when an error occurred.
     */
    private static Statement selection(Selection sel, String returnPath, List<Pid> processes, int nrProcInvoke) throws Exception {
        List<Expression> comArgList = new ArrayList<>();
        List<TypeExpression> typeExpressions = new ArrayList<>();
        // Get the channel name
        String channelName = sel.sender().name().substring(0,1).toUpperCase().concat(sel.receiver().name().substring(0,1).toUpperCase());
        // Get the label
        EnumCaseInstantiationExpression enumInstan = new EnumCaseInstantiationExpression(new Name("Choice"), new Name(sel.label()), new WorldArgument(new Name(sel.sender().name())));
        comArgList.add(enumInstan);
        // Get the type of the label, hardcoded to the type "Choice"
        typeExpressions.add(new TypeExpression(new Name("Choice"),List.of(),List.of()));
        FieldAccessExpression fieldName = new FieldAccessExpression(new Name("ch"+channelName));
        // Create the method "select"
        Expression methodCallCom = new MethodCallExpression(new Name("select"), comArgList, typeExpressions);
        ScopedExpression scope = new ScopedExpression(fieldName, methodCallCom);
        return new ExpressionStatement(scope,toRun(sel.continuation(),returnPath,processes,nrProcInvoke));
    }

    /**
     *
     * @param varList List<N>, list of the structure N, is the variables that need to be unpacked.
     * @param listIndex int, is the index of the varlist that are worked on.
     * @param index int, is the index of the value of the structure N that are worked on.
     * @param p ProcInvoke, contains the information of the procedure invocation.
     * @param returnPath String, the path where all result files are created.
     * @param processes List<Pid>, list of processes.
     * @param nrProcInvoke int, the number of procedure invocation that have been made so far.
     * @return VariableDeclaration, the declaration of the variable that contains the value of the tuple.
     * @throws Exception is thrown when an error occurred.
     */
    public static VariableDeclarationStatement unpackingTuple(List<N> varList, int listIndex, int index, ProcInvoke p, String returnPath, List<Pid> processes, int nrProcInvoke) throws Exception {
        List<WorldArgument> worlds = new ArrayList<>();
        // Get the process worked on
        String process = varList.get(listIndex).process().name();
        List<TypeExpression> typeExpressions = new ArrayList<>();
        // Add the process
        worlds.add(new WorldArgument(new Name(process)));
        // Create the type of the element of the tuple that is being unpacked.
        TypeExpression type = new TypeExpression(new Name(p.procedure().output().get(listIndex).types().get(index).getStringType()), worlds, typeExpressions);
        List<VariableDeclaration> variList = new ArrayList<>();
        if (varList.get(listIndex).value().get(index) instanceof VariableExpr v){ // The element unpacked is a variable
            // Get the name of the variable
            FieldAccessExpression variable = new FieldAccessExpression(new Name(v.name()+"_"+process));
            Expression expression;
            if((varList.get(listIndex).value().size() != 1) && varList.size() == 1){
                // Number of processes with values is one, but have more than one value. Therefore e.g. varname._1
                Expression methodCom = new MethodCallExpression(new Name("_"+(index+1)),List.of(),List.of());
                expression = new ScopedExpression(new FieldAccessExpression(new Name("tuple"+nrProcInvoke)),methodCom);
            } else if (varList.get(listIndex).value().size() != 1) {
                // Multiple processes with more than one value. Therefore e.g. varname._1._2
                Expression methodCom = new MethodCallExpression(new Name("_"+(listIndex+1)),List.of(),List.of());
                Expression scopedExpression = new ScopedExpression(new FieldAccessExpression(new Name("tuple"+nrProcInvoke)),methodCom);
                Expression varMethodCom = new MethodCallExpression(new Name("_"+(index+1)),List.of(),List.of());
                expression = new ScopedExpression(scopedExpression,varMethodCom);
            } else {
                // One process that have one value. Therefore e.g. varname._1, but other index than other case.
                Expression methodCom = new MethodCallExpression(new Name("_"+(listIndex+1)),List.of(),List.of());
                expression = new ScopedExpression(new FieldAccessExpression(new Name("tuple"+nrProcInvoke)),methodCom);
            }
            // Create variable declaration.
            VariableDeclaration variableDeclaration = new VariableDeclaration(new Name(v.name()+"_"+process), type, List.of(),new AssignExpression(expression,variable, AssignExpression.Operator.ASSIGN));
            variList.add(variableDeclaration);
            // How the recursive call should be called.
            if (index+1 == varList.get(listIndex).value().size()){
                // There are no more elements in a process
                if (listIndex+1 == varList.size()){
                    // There are no more processes
                    return new VariableDeclarationStatement(variList,toRun(p.continuation(),returnPath,processes,nrProcInvoke));
                }
                // There are still processes
                return new VariableDeclarationStatement(variList,unpackingTuple(varList, ++listIndex, 0,p,returnPath, processes,nrProcInvoke));
            } else {
                // There are still elements in a process
                return new VariableDeclarationStatement(variList,unpackingTuple(varList, listIndex, ++index,p,returnPath,processes,nrProcInvoke));
            }
        } else { // The element need to be a variable so if not the case throw an exception
            throw new VariableExprException("Variable not VariableExpr");
        }
    }



}
