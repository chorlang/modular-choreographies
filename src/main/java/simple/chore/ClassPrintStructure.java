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
import choral.ast.body.*;
import choral.ast.body.Class;
import choral.ast.type.FormalWorldParameter;
import choral.ast.type.TypeExpression;
import choral.ast.type.WorldArgument;
import choral.ast.visitors.PrettyPrinterVisitor;
import simple.chore.stuctures.*;
import stateful.chore.structures.L;
import stateful.chore.structures.ProcedureInfo;
import type.checking.structures.DataType;
import type.checking.structures.FuncType;

import java.util.*;

import static stateful.chore.FunctionPrintStructure.makeFunctionInterface;

public class ClassPrintStructure {

    /**
     *
     * @param procedureInfo ProcedureInfo, record containing all the information needed to create the class.
     * @param returnPath String, the path where all result files are created.
     * @return String, string of how the class should be printet.
     */
    public static String createClass(ProcedureInfo procedureInfo, String returnPath) {
        // Parameter: name
        Name name = new Name(procedureInfo.name());
        // Parameter: worldParameter
        List<FormalWorldParameter> worldParameters = createWorldParameters(procedureInfo);
        // Parameter: methods
        List<ClassMethodDefinition> methods = createMethods(procedureInfo, returnPath);
        // Create class
        choral.ast.body.Class choralClass = new Class(name,
                worldParameters,
                List.of(),
                null,
                List.of(),
                List.of(),
                methods,
                List.of(),
                List.of(),
                EnumSet.of(ClassModifier.PUBLIC),
                null);
        PrettyPrinterVisitor print = new PrettyPrinterVisitor();
        return print.visit(choralClass);

    }

    /**
     *
     * @param procedureInfo ProcedureInfo, record containing all the information needed to create the class.
     * @return List of FormalWorldParameters, a list of all the processes used in the class.
     */
    private static List<FormalWorldParameter> createWorldParameters(ProcedureInfo procedureInfo){
        List<FormalWorldParameter> worldParameters = new ArrayList<>();
        // Add all the processes used in the program
        for (Pid process : procedureInfo.processes()){
            FormalWorldParameter proc = new FormalWorldParameter(new Name(process.name()));
            worldParameters.add(proc);
        }
        return worldParameters;
    }


    /**
     *
     * @param procedureInfo ProcedureInfo, record containing all the information needed to create the class.
     * @param returnPath String, the path where all result files are created.
     * @return List of ClassMethodDefinitions, the methods in the class.
     */
    private static List<ClassMethodDefinition> createMethods(ProcedureInfo procedureInfo, String returnPath) {
        List<ClassMethodDefinition> methods = new ArrayList<>();
        List<WorldArgument> worlds = new ArrayList<>();
        List<TypeExpression> typeExpressions = new ArrayList<>();
        // Sets the return type
        TypeExpression returnType;
        if (procedureInfo.outputGamma().size() == 0) { // It does not return
            returnType = new TypeExpression(new Name("void"), List.of(), List.of());
        } else if (procedureInfo.outputGamma().size() == 1){ // It returns from one process
            // Add process
            worlds.add(new WorldArgument(new Name(procedureInfo.outputGamma().get(0).process().name())));
            if (procedureInfo.outputGamma().get(0).types().size() == 1){ // One value is returned
                returnType = new TypeExpression(new Name(procedureInfo.outputGamma().get(0).types().get(0).getStringType()), worlds, List.of());
            } else { // Multiple values are returned
                for (DataType dataType : procedureInfo.outputGamma().get(0).types()){
                    typeExpressions.add(new TypeExpression(new Name(dataType.getStringType()), List.of(), List.of()));
                }
                // Create tuple since multiple values are returned
                returnType = new TypeExpression(new Name("Tuple" + procedureInfo.outputGamma().size()), worlds, typeExpressions);
            }
        } else { // It returns from multiple processes
            // Iterate over all processes
            for (L output : procedureInfo.outputGamma()) {
                // Add the process
                worlds.add(new WorldArgument(new Name(output.process().name())));
                if (output.types().size() == 1){ // If one value from the process
                    typeExpressions.add(new TypeExpression(new Name(output.types().get(0).getStringType()), List.of(), List.of()));
                } else { // If multiple values from the process
                    typeExpressions.add(new TypeExpression(new Name("Tuple" + output.types().size()), List.of(), List.of()));
                }

            }
            // Create tuple since multiple values are returned
            returnType = new TypeExpression(new Name("Tuple" + procedureInfo.outputGamma().size()), worlds, typeExpressions);
        }
        List<FormalMethodParameter> parameters = new ArrayList<>();
        // Adding the possible channels to the parameters
        if (procedureInfo.processes().size() > 0) {
            for (int i = 0; i < procedureInfo.processes().size(); i++) {
                for (int j = i + 1; j < procedureInfo.processes().size(); j++) {
                    parameters.add(createFormalMethodParameter(procedureInfo,i,j));
                    parameters.add(createFormalMethodParameter(procedureInfo,j,i));
                }
            }
        }
        // Adding the arguments of the function to the parameters
        for (Map.Entry<Pid, Map<String,DataType>> entry : procedureInfo.inputGamma().entrySet()){
            for (Map.Entry<String,DataType> args : entry.getValue().entrySet()){
                parameters.add(new FormalMethodParameter(new Name(args.getKey().concat("_"+entry.getKey().name())), getTypeExpression(args.getValue(), entry.getKey(), returnPath),List.of(), null));
            }
        }
        // Create the method object
        MethodSignature signature = new MethodSignature(new Name("run"), List.of(), parameters, returnType, null);
        methods.add(new ClassMethodDefinition(signature, procedureInfo.toRun(), List.of(), EnumSet.of(ClassMethodModifier.PUBLIC, ClassMethodModifier.STATIC), null));
        return methods;
    }

    /**
     *
     * @param procedureInfo ProcedureInfo, record containing all the information needed to create the class.
     * @param i int, keeping track of the processes.
     * @param j int, keeping track of the processes.
     * @return FormalMethodParameter, is a channel from one process to another.
     */
    private static FormalMethodParameter createFormalMethodParameter(ProcedureInfo procedureInfo, int i, int j){
        List<WorldArgument> worldArgumentListDi = new ArrayList<>();
        List<TypeExpression> typeExpressionList = new ArrayList<>();
        // Add the processes
        worldArgumentListDi.add(new WorldArgument(new Name(procedureInfo.processes().get(i).name())));
        worldArgumentListDi.add(new WorldArgument(new Name(procedureInfo.processes().get(j).name())));
        // Specify what is sendt over the channel
        typeExpressionList.add(new TypeExpression(new Name("Serializable"),List.of(),List.of()));
        // Create the type of the channel
        TypeExpression typeExpression = new TypeExpression(new Name("DiChannel"), worldArgumentListDi, typeExpressionList);
        // Create the channel object
        return new FormalMethodParameter(new Name("ch".concat(procedureInfo.processes().get(i).name().substring(0, 1).toUpperCase().concat(procedureInfo.processes().get(j).name().substring(0, 1).toUpperCase()))), typeExpression,List.of(), null);
    }

    /**
     *
     * @param type Datatype, the datatype of the argument.
     * @param process Pid, the process where the argument is located.
     * @param returnPath String, the path where all result files are created.
     * @return TypeExpression, the arguments of the run method.
     */
    private static TypeExpression getTypeExpression(DataType type,Pid process, String returnPath) {
        List<WorldArgument> worldArgumentListDi = new ArrayList<>();
        // Add the process
        worldArgumentListDi.add(new WorldArgument(new Name(process.name())));
        Name name;
        if (type instanceof FuncType f) { // The argument is a function
            // Get the interface of the function
            switch (f.parameters().size()) {
                case 0 -> name = new Name("Supplier");
                case 1 -> name = new Name("Function");
                case 2 -> name = new Name("BiFunction");
                default -> {
                    // Is not defined by Java, own interface must be defined.
                    name = new Name("Function" + f.parameters().size());
                    makeFunctionInterface(f.parameters().size(), returnPath);
                }
            }
            List<TypeExpression> typeExpressionList = new ArrayList<>();
            // Add the parameters of the function.
            for (DataType dataType : f.parameters()){
                typeExpressionList.add( new TypeExpression(new Name(dataType.getStringType()),List.of(),List.of()));
            }
            // Create the type of the function
            typeExpressionList.add(new TypeExpression(new Name(f.returnType().getStringType()),List.of(),List.of()));
            // Create the argument
            return new TypeExpression(name, worldArgumentListDi, typeExpressionList);
        } else { // it is not a function but instead a primitive type
            // Get the type of the primitive and create the argument.
            name = new Name(type.getStringType());
            return new TypeExpression(name, worldArgumentListDi, List.of());
        }
    }

}
