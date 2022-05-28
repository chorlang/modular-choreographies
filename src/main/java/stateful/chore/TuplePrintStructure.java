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

package stateful.chore;

import choral.ast.Name;
import choral.ast.body.*;
import choral.ast.body.Class;
import choral.ast.expression.AssignExpression;
import choral.ast.expression.FieldAccessExpression;
import choral.ast.expression.ScopedExpression;
import choral.ast.expression.ThisExpression;
import choral.ast.statement.ExpressionStatement;
import choral.ast.statement.NilStatement;
import choral.ast.statement.ReturnStatement;
import choral.ast.statement.Statement;
import choral.ast.type.FormalTypeParameter;
import choral.ast.type.FormalWorldParameter;
import choral.ast.type.TypeExpression;
import choral.ast.type.WorldArgument;
import choral.ast.visitors.PrettyPrinterVisitor;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static base.Main.createFullReturnPath;
import static base.ReturnFile.createReturnFile;
import static base.ReturnFile.writeToFile;

public class TuplePrintStructure {

    /**
     *
     * @param size int, number of elements in the tuple.
     * @param returnPath String, the path where all result files are created.
     */
    public static void makeTuple(int size, String returnPath) {
        if (size != 1){ // Checks that the size is not equal to one.
            String returnFileTuple = createFullReturnPath(returnPath,"Tuple"+size);
            Boolean notAlreadyCreated = createReturnFile(returnFileTuple);
            // If a tuple class of the same size have already been created then it will not be created again
            if (notAlreadyCreated){
                String tupleClassText = createClass(size);
                writeToFile(returnFileTuple,tupleClassText);
            }
        }
    }

    /**
     *
     * @param size int, number of elements in the tuple.
     * @return String, the string representation of the class.
     */
    public static String createClass(int size) {
        // Parameter: name
        Name name = new Name("Tuple"+size);
        // Parameter: worldParameter
        List<FormalWorldParameter> worldParameters = createWorldParameters(size);
        // Parameter: typeParameter
        List<FormalTypeParameter> typeParameters = createTypeParameters(size);
        // Parameter: fields
        List<Field> fields = createFields(size);
        // Parameter: methods
        List<ClassMethodDefinition> methods = createMethods(size);
        // Parameter: constructors
        List<ConstructorDefinition> constructorList = createConstructors(size);
        PrettyPrinterVisitor print = new PrettyPrinterVisitor();
        choral.ast.body.Class tupleClass = new Class(name,
                worldParameters,
                typeParameters,
                null,
                List.of(),
                fields,
                methods,
                constructorList,
                List.of(),
                EnumSet.of(ClassModifier.PUBLIC),
                null);
        return print.visit(tupleClass);

    }

    /**
     *
     * @param size int, number of elements in the tuple.
     * @return List of FormalWorldParameter, the processes used in the class.
     */
    private static List<FormalWorldParameter> createWorldParameters(int size){
        List<FormalWorldParameter> worldParameters = new ArrayList<>();
        // Go through the number of elements and add a pid for each one.
        for (int i = 0; i < size; i++){
            Name pidName = new Name("R"+(i+1));
            FormalWorldParameter proc = new FormalWorldParameter(pidName);
            worldParameters.add(proc);
        }
        return worldParameters;
    }

    /**
     *
     * @param size int, number of elements in the tuple.
     * @return List of FormalTypeParameters, the types used in the class.
     */
    private static List<FormalTypeParameter> createTypeParameters(int size){
        List<FormalTypeParameter> typeParameters = new ArrayList<>();
        // Go through the number of elements and add a type for each one.
        for (int i = 0; i < size; i++){
            List<FormalWorldParameter> formalWorldParameters = new ArrayList<>();
            formalWorldParameters.add(new FormalWorldParameter(new Name("R"+(i + 1))));
            Name name = new Name("T"+ (i + 1));
            FormalTypeParameter type = new FormalTypeParameter(name,formalWorldParameters,List.of(), List.of());
            typeParameters.add(type);
        }
        return typeParameters;
    }

    /**
     *
     * @param size int, number of elements in the tuple.
     * @return List of Fields, the different fields used in the class.
     */
    private static List<Field> createFields(int size){
        List<Field> fields = new ArrayList<>();
        // Go through the number of elements
        for (int i = 0; i < size; i++){
            // Get the channel name
            Name chName = new Name("_"+(i+1));
            List<WorldArgument> worldArgumentList = new ArrayList<>();
            // Add a pid
            worldArgumentList.add(new WorldArgument(new Name("R"+(i+1))));
            // Add a type
            TypeExpression type = new TypeExpression(new Name("T"+(i+1)),worldArgumentList,List.of());
            // Add the modifiers private and final
            EnumSet<FieldModifier> fieldModifiers = EnumSet.of(FieldModifier.PRIVATE,FieldModifier.FINAL);
            // Create the field
            Field chField = new Field(chName,type,List.of(),fieldModifiers,null);
            fields.add(chField);
        }
        return fields;
    }

    /**
     *
     * @param size int, number of elements in the tuple.
     * @return List of ClassMethodDefinition, the different methods in the class.
     */
    private static List<ClassMethodDefinition> createMethods(int size) {
        List<ClassMethodDefinition> methods = new ArrayList<>();
        // Go through the number of elements
        for (int i = 0; i < size; i++){
            List<WorldArgument> worlds = new ArrayList<>();
            // Add a pid
            worlds.add(new WorldArgument(new Name("R"+(i+1))));
            // Add the return type
            TypeExpression returnTypeExp = new TypeExpression(new Name("T"+(i+1)), worlds,List.of());
            // Create the method signature
            MethodSignature signature = new MethodSignature(new Name("_"+(i+1)),List.of(),List.of(),returnTypeExp,null);
            // Create the field to return
            FieldAccessExpression fieldAccessExpression = new FieldAccessExpression(new Name("_"+(i+1)));
            // Create the return statement
            Statement returnStatemnt = new ReturnStatement(fieldAccessExpression,new NilStatement());
            // Create the method definition
            ClassMethodDefinition procedureMethod = new ClassMethodDefinition(signature, returnStatemnt,List.of(),EnumSet.of(ClassMethodModifier.PUBLIC),null);
            methods.add(procedureMethod);
        }
        return methods;
    }

    /**
     *
     * @param size int, number of elements in the tuple.
     * @return List of ConstructorDefinition, the constructor of the class.
     */
    private static List<ConstructorDefinition> createConstructors(int size){
        List<FormalMethodParameter> constructorParameters = new ArrayList<>();
        // Go through the number of elements
        for (int i = 0; i < size; i++){
            // The name of the parameter of the constructor
            Name paraName = new Name("_"+(i+1));
            List<WorldArgument> worldArgumentList = new ArrayList<>();
            // The process for the parameter
            worldArgumentList.add(new WorldArgument(new Name("R"+(i+1))));
            // The type of the parameter
            TypeExpression paraType = new TypeExpression(new Name("T"+(i+1)),worldArgumentList,List.of());
            // Create the parameter
            FormalMethodParameter parameter = new FormalMethodParameter(paraName,paraType,List.of(),null);
            constructorParameters.add(parameter);
        }
        // Create the signature
        ConstructorSignature constructorSignature = new ConstructorSignature(new Name("Tuple"+size), List.of(),constructorParameters,null);
        List<ConstructorDefinition> constructorList = new ArrayList<>();
        // Create the body
        Statement blockStatement = createConstructorBody(size, 0);
        // Create the constructor
        ConstructorDefinition constructor = new ConstructorDefinition(constructorSignature,null,blockStatement,List.of(),EnumSet.of(ConstructorModifier.PUBLIC),null);
        constructorList.add(constructor);
        return constructorList;
    }


    /**
     *
     * @param size int, number of elements in the tuple.
     * @param index int, what number variable is worked on.
     * @return Statement, recursive structure of the choreography. Used by the pretty printer.
     */
    private static Statement createConstructorBody(int size, int index){
        if (index == size){ // Base case
            return new NilStatement();
        } else {
            // Creates expression of the following form "this._1 = _1;"
            Name paraName = new Name("_"+(index+1));
            ThisExpression thisEx = new ThisExpression();
            FieldAccessExpression fieldAcc = new FieldAccessExpression(paraName);
            AssignExpression assign = new AssignExpression(fieldAcc,fieldAcc, AssignExpression.Operator.ASSIGN);
            ScopedExpression scopedExpression = new ScopedExpression(thisEx,assign);
            return new ExpressionStatement(scopedExpression,createConstructorBody(size,++index));
        }

    }

}
