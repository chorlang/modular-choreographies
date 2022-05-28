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

import choral.ast.expression.*;
import choral.ast.statement.NilStatement;
import parser.DescriptiveErrorListener;
import parser.SimpleParser.SimpleLexer;
import parser.SimpleParser.SimpleParser;
import parser.SimpleParser.SimpleParser.CContext;
import parser.SimpleParser.SimpleParser.InteractionContext;
import parser.SimpleParser.SimpleParser.StatefulInteractionContext;
import org.antlr.v4.runtime.*;
import simple.chore.stuctures.*;
import stateful.chore.structures.L;
import stateful.chore.structures.N;
import type.checking.structures.ProcedureType;
import type.checking.structures.*;

import static type.checking.ChoreTypingContent.copyGamma;
import static type.checking.ChoreTypingContent.getDataType;

import java.util.*;

import static simple.chore.ChoralStatement.createInteractionStatement;
import static simple.chore.MethodHelperFunc.getArguments;
import static simple.chore.MethodHelperFunc.getVariables;
import static simple.chore.ExpressionHelperFunc.getExprE;
import static stateful.chore.ParseTreeConversion.getDatatypeFromString;


public class ParseTreeConversion {

    /**
     *
     * @param source The text to be parsed.
     * @return ProgramContext is the start rule of the generated parse tree.
     */
    public static SimpleParser.ProgramContext getTree(String source) {
        CodePointCharStream input = CharStreams.fromString(source);
        Lexer lexer = new SimpleLexer(input);
        lexer.removeErrorListeners();
        lexer.addErrorListener(DescriptiveErrorListener.INSTANCE);
        TokenStream tokenStream = new CommonTokenStream(lexer);
        SimpleParser parser = new SimpleParser(tokenStream);
        parser.removeErrorListeners();
        parser.addErrorListener(DescriptiveErrorListener.INSTANCE);
        return parser.start().program();
    }

    /**
     *
     * @param procedure String, the procedure the choreography is for.
     * @param cnode CContext, is part of the parser tree starting at rule C.
     * @param gamma Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>>, has all the procedures, and their input and output. Is added to.
     * @param original Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>>, has all the procedures, and their input and output. Is not changed at any point.
     * @return Choreography, recursive structure of the different instructions.
     * @throws Exception is thrown when an error occurred.
     */
    public static Choreography getObject(String procedure, CContext cnode,
                                         Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> gamma,
                                         Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> original) throws Exception
    {
        Empty e = new Empty(new NilStatement());
        if (cnode == null){ // Checks if choreography is null
            return e;
        }
        // Find the correct instruction and create the corresponding record.
        if(cnode.instruction() != null){
            if(cnode.instruction().interaction() != null){
                return com(procedure,cnode,gamma,original);
            } else if (cnode.instruction().statefulInteraction() != null) {
                return statefulCom(procedure, cnode, gamma,original);
            }else if (cnode.instruction().variableInteraction() != null){
                return statefulComVar(procedure,cnode,gamma,original);
            } else if (cnode.instruction().returnState() != null){
                return return_(procedure, cnode, gamma);
            } else if (cnode.instruction().procInvoke() != null){
                return procInvoke(procedure, cnode, gamma,original);
            } else if (cnode.instruction().ifState() != null){
                return ifState(procedure,cnode,gamma,original);
            } else if (cnode.instruction().declaration() != null){
                return declaration(procedure,cnode,gamma,original);
            } else if (cnode.instruction().initialization() != null){
                return initialization(procedure,cnode,gamma,original);
            } else if (cnode.instruction().initialDeclara() != null){
                return initialDeclare(procedure,cnode,gamma,original);
            } else if (cnode.instruction().selection() != null){
                return selection(procedure,cnode,gamma,original);
            }
            //Other possibilities can be added here.
        }

        return e;
    }

    /**
     *
     * @param procedure String, the procedure the choreography is for.
     * @param cnode CContext, is part of the parser tree starting at rule C.
     * @param gamma Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>>, has all the procedures, and their input and output. Is added to.
     * @param original Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>>, has all the procedures, and their input and output. Is not changed at any point.
     * @return Com, recursive structure of the instruction Com.
     * @throws Exception is thrown when an error occurred.
     */
    private static Com com(String procedure, CContext cnode,
                           Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> gamma,
                           Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> original) throws Exception {
        InteractionContext interact = cnode.instruction().interaction();
        // Get the channel name
        String channelName = "ch".concat(interact.sender.getText().toUpperCase()).concat(interact.receiver.getText().toUpperCase());
        // Create the statement for the stateless communication.
        ScopedExpression scopedExpression = createInteractionStatement(channelName);
        // Create the Com record.
        return new Com(channelName, scopedExpression,getObject(procedure,cnode.c(),gamma,original));
    }

    /**
     *
     * @param procedure String, the procedure the choreography is for.
     * @param cnode CContext, is part of the parser tree starting at rule C.
     * @param gamma Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>>, has all the procedures, and their input and output. Is added to.
     * @param original Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>>, has all the procedures, and their input and output. Is not changed at any point.
     * @return StatefulCom, recursive structure of the instruction StatefulCom.
     * @throws Exception is thrown when an error occurred.
     */
    private static StatefulCom statefulCom(String procedure, CContext cnode,
                                           Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> gamma,
                                           Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> original) throws Exception {
        StatefulInteractionContext interact = cnode.instruction().statefulInteraction();
        // Get the expression type
        Expr eExpr = getExprE(new Pid(interact.sender.getText()), interact.e(),gamma.get(procedure).first());
        // Create the StatefulCom record.
        return new StatefulCom(new Pid(interact.sender.getText()), new Pid(interact.receiver.getText()),
                eExpr,interact.variable.getText(),getObject(procedure, cnode.c(),gamma,original));
    }

    /**
     *
     * @param procedure String, the procedure the choreography is for.
     * @param cnode CContext, is part of the parser tree starting at rule C.
     * @param gamma Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>>, has all the procedures, and their input and output. Is added to.
     * @param original Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>>, has all the procedures, and their input and output. Is not changed at any point.
     * @return StatefulComVar, recursive structure of the instruction StatefulCom where the variable is declared in the instruction.
     * @throws Exception is thrown when an error occurred.
     */
    private static StatefulComVar statefulComVar(String procedure, CContext cnode,
                                                 Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> gamma,
                                                 Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> original) throws Exception {
        SimpleParser.VariableInteractionContext varContext = cnode.instruction().variableInteraction();
        // Get the expression type
        Expr eExpr = getExprE(new Pid(varContext.sender.getText()), varContext.e(),gamma.get(procedure).first());
        // Add the variable declared to the gamma.
        Map<String, Pairs<Map<Pid, Map  <String, DataType>>, List<L>>> copy = copyGamma(new Pid(varContext.receiver.getText()),
                varContext.variable.getText(), getDataType(eExpr), procedure,  gamma);
        // Create the StatefulComVar record.
        return new StatefulComVar(new Pid(varContext.sender.getText()), new Pid(varContext.receiver.getText()),
                eExpr,varContext.variable.getText(), getDatatypeFromString(varContext.type.getText())
                ,getObject(procedure, cnode.c(),copy,original));
    }

    /**
     *
     * @param procedure String, the procedure the choreography is for.
     * @param cnode CContext, is part of the parser tree starting at rule C.
     * @param gamma Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>>, has all the procedures, and their input and output.
     * @return Return, recursive structure of the instruction Return.
     * @throws Exception is thrown when an error occurred.
     */
    private static Return return_(String procedure, CContext cnode,
                                  Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> gamma) throws Exception {
        SimpleParser.ReturnStateContext returnStateContext = cnode.instruction().returnState();
        // Get the return values
        List<N> resultValues = getArguments(returnStateContext.returnValue(), gamma.get(procedure).first(), new ArrayList<>());
        // Create the Return record.
        return new Return(resultValues);
    }

    /**
     *
     * @param procedure String, the procedure the choreography is for.
     * @param cnode CContext, is part of the parser tree starting at rule C.
     * @param gamma Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>>, has all the procedures, and their input and output. Is added to.
     * @param original Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>>, has all the procedures, and their input and output. Is not changed at any point.
     * @return ProcInvoke, recursive structure of the instruction Procedure Invocation.
     * @throws Exception is thrown when an error occurred.
     */
    private static ProcInvoke procInvoke(String procedure, CContext cnode,
                                         Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> gamma,
                                         Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> original) throws Exception {
        SimpleParser.ProcInvokeContext procInvokeContext = cnode.instruction().procInvoke();
        // Get the variables used in the invocation.
        List<N> variables = getVariables(procInvokeContext.invokeargs(),gamma.get(procedure).first(), new ArrayList<>());
        List<N> arguments;
        if (procInvokeContext.returnValue() != null){ // The procedure returns.
            // Get the arguments used in the invocation.
            arguments = getArguments(procInvokeContext.returnValue(),original.get(procedure).first(),new ArrayList<>());
        } else { // The procedure does not return.
            arguments = null;
        }
        // Get the information of the procedure that is invoked.
        ProcedureType procedureType = new ProcedureType(procInvokeContext.procedure.getText(), original.get(procInvokeContext.procedure.getText()).first(),original.get(procInvokeContext.procedure.getText()).second());
        // Create the ProcInvoke record.
        return new ProcInvoke(arguments, variables, procedureType, getObject(procedure,cnode.c(),gamma,original) );
    }

    /**
     *
     * @param procedure String, the procedure the choreography is for.
     * @param cnode CContext, is part of the parser tree starting at rule C.
     * @param gamma Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>>, has all the procedures, and their input and output. Is added to.
     * @param original Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>>, has all the procedures, and their input and output. Is not changed at any point.
     * @return IfState, recursive structure of the if-statement instruction
     * @throws Exception is thrown when an error occurred.
     */
    private static IfState ifState(String procedure, CContext cnode,
                                   Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> gamma,
                                   Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> original
                                   ) throws Exception {
        SimpleParser.IfStateContext ifStateContext = cnode.instruction().ifState();
        // Get the process that evaluates the conditional.
        Pid process = new Pid(ifStateContext.process.getText());
        // Get the conditional expression.
        Expr eExpr = getExprE(process, ifStateContext.e(),gamma.get(procedure).first());
        // Create the IfState record.
        return new IfState(process, eExpr,
                getObject(procedure,ifStateContext.c(0),gamma,original),
                getObject(procedure,ifStateContext.c(1),gamma,original),
                getObject(procedure,cnode.c(),gamma,original));

    }

    /**
     *
     * @param procedure String, the procedure the choreography is for.
     * @param cnode CContext, is part of the parser tree starting at rule C.
     * @param gamma Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>>, has all the procedures, and their input and output. Is added to.
     * @param original Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>>, has all the procedures, and their input and output. Is not changed at any point.
     * @return Declaration, recursive structure of the declaration instruction
     * @throws Exception is thrown when an error occurred.
     */
    private static Declaration declaration(String procedure, CContext cnode,
                                           Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> gamma,
                                           Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> original) throws Exception {
        SimpleParser.DeclarationContext declarationContext = cnode.instruction().declaration();
        // Add the declared variable to the gamma.
        Map<String, Pairs<Map<Pid, Map  <String, DataType>>, List<L>>> copy = copyGamma(new Pid(declarationContext.process.getText()),
                declarationContext.varName.getText(), getDatatypeFromString(declarationContext.type.getText()), procedure,  gamma);
        // Create the Declaration record.
        return new Declaration(new Pid(declarationContext.process.getText()),
                declarationContext.varName.getText(),
                getDatatypeFromString(declarationContext.type.getText()),
                getObject(procedure,cnode.c(),copy,original));
    }

    /**
     *
     * @param procedure String, the procedure the choreography is for.
     * @param cnode CContext, is part of the parser tree starting at rule C.
     * @param gamma Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>>, has all the procedures, and their input and output. Is added to.
     * @param original Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>>, has all the procedures, and their input and output. Is not changed at any point.
     * @return Initialization, recursive structure of the assignment instruction
     * @throws Exception is thrown when an error occurred.
     */
    private static Initialization initialization(String procedure, CContext cnode,
                                                 Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> gamma,
                                                 Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> original) throws Exception {
        SimpleParser.InitializationContext initializationContext = cnode.instruction().initialization();
        // Get the process the variable is located at.
        Pid process = new Pid(initializationContext.process.getText());
        // Create the Initialization record.
        return new Initialization(process,
                initializationContext.var.getText(),
                getExprE(process, initializationContext.e(), gamma.get(procedure).first()),
                getObject(procedure,cnode.c(),gamma,original));
    }

    /**
     *
     * @param procedure String, the procedure the choreography is for.
     * @param cnode CContext, is part of the parser tree starting at rule C.
     * @param gamma Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>>, has all the procedures, and their input and output. Is added to.
     * @param original Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>>, has all the procedures, and their input and output. Is not changed at any point.
     * @return InitialDeclare, recursive structure of the declaration and initialization instruction
     * @throws Exception is thrown when an error occurred.
     */
    private static InitialDeclare initialDeclare(String procedure, CContext cnode,
                                                 Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> gamma,
                                                 Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> original) throws Exception {
        SimpleParser.InitialDeclaraContext initialDeclaraContext = cnode.instruction().initialDeclara();
        // Get the process the variable is located at.
        Pid process = new Pid(initialDeclaraContext.process.getText());
        // Add the variable to the gamma.
        Map<String, Pairs<Map<Pid, Map  <String, DataType>>, List<L>>> copy = copyGamma(process,
                initialDeclaraContext.var.getText(), getDatatypeFromString(initialDeclaraContext.type.getText()), procedure,  gamma);
        // Create the InitialDeclare record.
        return new InitialDeclare(process,
                initialDeclaraContext.var.getText(),
                getDatatypeFromString(initialDeclaraContext.type.getText()),
                getExprE(process,initialDeclaraContext.e(),gamma.get(procedure).first()),
                getObject(procedure,cnode.c(),copy,original));
    }

    /**
     *
     * @param procedure String, the procedure the choreography is for.
     * @param cnode CContext, is part of the parser tree starting at rule C.
     * @param gamma Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>>, has all the procedures, and their input and output. Is added to.
     * @param original Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>>, has all the procedures, and their input and output. Is not changed at any point.
     * @return Selection, recursive structure of the selection instruction
     * @throws Exception is thrown when an error occurred.
     */
    private static Selection selection(String procedure, CContext cnode,
                                       Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> gamma,
                                       Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> original) throws Exception {
        SimpleParser.SelectionContext selectionContext = cnode.instruction().selection();
        // Create the Selection record.
        return new Selection(new Pid(selectionContext.sender.getText()),
                new Pid(selectionContext.receiver.getText()),
                selectionContext.label.getText(),
                getObject(procedure,cnode.c(),gamma,original));
    }

}
