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

import custom.exceptions.*;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static base.Main.createChoralFile;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestProcedure {
    @Test
    public void testManyStateful() throws Exception {
        Path correctFileX = Path.of( "src/test/resources/procedure_correct/multiProcedures/many_stateful/X.txt");
        Path correctFileY = Path.of( "src/test/resources/procedure_correct/multiProcedures/many_stateful/Y.txt");

        createChoralFile("./src/test/resources/files/Procedure/TestProcedure.txt","./src/test/resources/generated_results/");

        Path createdFileX = Path.of("src/test/resources/generated_results/X.txt");
        Path createdFileY = Path.of("src/test/resources/generated_results/Y.txt");

        List<String> correctList = new ArrayList<>();
        List<String> generatedList = new ArrayList<>();
        correctList.add(Files.readString(correctFileX).replaceAll("\\s",""));
        correctList.add(Files.readString(correctFileY).replaceAll("\\s",""));
        generatedList.add(Files.readString(createdFileX).replaceAll("\\s",""));
        generatedList.add(Files.readString(createdFileY).replaceAll("\\s",""));
        assertEquals(correctList,generatedList);
    }

    @Test
    public void testReturnNone() throws Exception {
        Path correctFileX = Path.of( "src/test/resources/procedure_correct/multiProcedures/return_empty/X.txt");
        Path correctFileY = Path.of( "src/test/resources/procedure_correct/multiProcedures/return_empty/Y.txt");

        createChoralFile("./src/test/resources/files/Procedure/TestReturnNone.txt","./src/test/resources/generated_results/");

        Path createdFileX = Path.of("src/test/resources/generated_results/X.txt");
        Path createdFileY = Path.of("src/test/resources/generated_results/Y.txt");

        List<String> correctList = new ArrayList<>();
        List<String> generatedList = new ArrayList<>();
        correctList.add(Files.readString(correctFileX).replaceAll("\\s",""));
        correctList.add(Files.readString(correctFileY).replaceAll("\\s",""));
        generatedList.add(Files.readString(createdFileX).replaceAll("\\s",""));
        generatedList.add(Files.readString(createdFileY).replaceAll("\\s",""));
        assertEquals(correctList,generatedList);
    }

    @Test
    public void testReturnOne() throws Exception {
        Path correctFileX = Path.of( "src/test/resources/procedure_correct/multiProcedures/return_one/X.txt");
        Path correctFileY = Path.of( "src/test/resources/procedure_correct/multiProcedures/return_one/Y.txt");

        createChoralFile("./src/test/resources/files/Procedure/TestReturnOne.txt","./src/test/resources/generated_results/");

        Path createdFileX = Path.of("src/test/resources/generated_results/X.txt");
        Path createdFileY = Path.of("src/test/resources/generated_results/Y.txt");

        List<String> correctList = new ArrayList<>();
        List<String> generatedList = new ArrayList<>();
        correctList.add(Files.readString(correctFileX).replaceAll("\\s",""));
        correctList.add(Files.readString(correctFileY).replaceAll("\\s",""));
        generatedList.add(Files.readString(createdFileX).replaceAll("\\s",""));
        generatedList.add(Files.readString(createdFileY).replaceAll("\\s",""));
        assertEquals(correctList,generatedList);
    }

    @Test
    public void testReturnOneTwo() throws Exception {
        Path correctFileX = Path.of( "src/test/resources/procedure_correct/multiProcedures/return_one_two/X.txt");
        Path correctFileY = Path.of( "src/test/resources/procedure_correct/multiProcedures/return_one_two/Y.txt");
        Path correctFileF3 = Path.of( "src/test/resources/procedure_correct/multiProcedures/return_one_two/Function3.txt");

        createChoralFile("./src/test/resources/files/Procedure/TestReturnOneTwo.txt","./src/test/resources/generated_results/");

        Path createdFileX = Path.of("src/test/resources/generated_results/X.txt");
        Path createdFileY = Path.of("src/test/resources/generated_results/Y.txt");
        Path createdFileF3 = Path.of("src/test/resources/generated_results/Function3.txt");

        List<String> correctList = new ArrayList<>();
        List<String> generatedList = new ArrayList<>();
        correctList.add(Files.readString(correctFileX).replaceAll("\\s",""));
        correctList.add(Files.readString(correctFileY).replaceAll("\\s",""));
        correctList.add(Files.readString(correctFileF3).replaceAll("\\s",""));
        generatedList.add(Files.readString(createdFileX).replaceAll("\\s",""));
        generatedList.add(Files.readString(createdFileY).replaceAll("\\s",""));
        generatedList.add(Files.readString(createdFileF3).replaceAll("\\s",""));
        assertEquals(correctList,generatedList);
    }

    @Test
    public void testReturnTwo() throws Exception {
        Path correctFileX = Path.of( "src/test/resources/procedure_correct/multiProcedures/return_two/X.txt");
        Path correctFileY = Path.of( "src/test/resources/procedure_correct/multiProcedures/return_two/Y.txt");
        Path correctFileT2 = Path.of( "src/test/resources/procedure_correct/multiProcedures/return_two/Tuple2.txt");
        Path correctFileF3 = Path.of( "src/test/resources/procedure_correct/multiProcedures/return_two/Function3.txt");

        createChoralFile("./src/test/resources/files/Procedure/TestReturnTwo.txt","./src/test/resources/generated_results/");

        Path createdFileX = Path.of("src/test/resources/generated_results/X.txt");
        Path createdFileY = Path.of("src/test/resources/generated_results/Y.txt");
        Path createdFileT2 = Path.of("src/test/resources/generated_results/Tuple2.txt");
        Path createdFileF3 = Path.of("src/test/resources/generated_results/Function3.txt");

        List<String> correctList = new ArrayList<>();
        List<String> generatedList = new ArrayList<>();
        correctList.add(Files.readString(correctFileX).replaceAll("\\s",""));
        correctList.add(Files.readString(correctFileY).replaceAll("\\s",""));
        correctList.add(Files.readString(correctFileT2).replaceAll("\\s",""));
        correctList.add(Files.readString(correctFileF3).replaceAll("\\s",""));
        generatedList.add(Files.readString(createdFileX).replaceAll("\\s",""));
        generatedList.add(Files.readString(createdFileY).replaceAll("\\s",""));
        generatedList.add(Files.readString(createdFileT2).replaceAll("\\s",""));
        generatedList.add(Files.readString(createdFileF3).replaceAll("\\s",""));
        assertEquals(correctList,generatedList);
    }

    @Test
    public void testOtherNames() throws Exception {
        Path correctFileX = Path.of( "src/test/resources/procedure_correct/multiProcedures/other_process_names/X.txt");
        Path correctFileY = Path.of( "src/test/resources/procedure_correct/multiProcedures/other_process_names/Y.txt");
        Path correctFileT2 = Path.of( "src/test/resources/procedure_correct/multiProcedures/other_process_names/Tuple2.txt");

        createChoralFile("./src/test/resources/files/Procedure/TestOtherNames.txt","./src/test/resources/generated_results/");

        Path createdFileX = Path.of("src/test/resources/generated_results/X.txt");
        Path createdFileY = Path.of("src/test/resources/generated_results/Y.txt");
        Path createdFileT2 = Path.of("src/test/resources/generated_results/Tuple2.txt");

        List<String> correctList = new ArrayList<>();
        List<String> generatedList = new ArrayList<>();
        correctList.add(Files.readString(correctFileX).replaceAll("\\s",""));
        correctList.add(Files.readString(correctFileY).replaceAll("\\s",""));
        correctList.add(Files.readString(correctFileT2).replaceAll("\\s",""));
        generatedList.add(Files.readString(createdFileX).replaceAll("\\s",""));
        generatedList.add(Files.readString(createdFileY).replaceAll("\\s",""));
        generatedList.add(Files.readString(createdFileT2).replaceAll("\\s",""));
        assertEquals(correctList,generatedList);
    }

    @Test
    public void testFunctionWith5() throws Exception {
        Path correctFileX = Path.of( "src/test/resources/procedure_correct/multiProcedures/function_with_5/X.txt");
        Path correctFileY = Path.of( "src/test/resources/procedure_correct/multiProcedures/function_with_5/Y.txt");
        Path correctFileT2 = Path.of( "src/test/resources/procedure_correct/multiProcedures/function_with_5/Tuple2.txt");
        Path correctFileF5 = Path.of( "src/test/resources/procedure_correct/multiProcedures/function_with_5/Function5.txt");

        createChoralFile("./src/test/resources/files/Procedure/TestFunctionWith5.txt","./src/test/resources/generated_results/");

        Path createdFileX = Path.of("src/test/resources/generated_results/X.txt");
        Path createdFileY = Path.of("src/test/resources/generated_results/Y.txt");
        Path createdFileT2 = Path.of("src/test/resources/generated_results/Tuple2.txt");
        Path createdFileF5 = Path.of("src/test/resources/generated_results/Function5.txt");

        List<String> correctList = new ArrayList<>();
        List<String> generatedList = new ArrayList<>();
        correctList.add(Files.readString(correctFileX).replaceAll("\\s",""));
        correctList.add(Files.readString(correctFileY).replaceAll("\\s",""));
        correctList.add(Files.readString(correctFileT2).replaceAll("\\s",""));
        correctList.add(Files.readString(correctFileF5).replaceAll("\\s",""));
        generatedList.add(Files.readString(createdFileX).replaceAll("\\s",""));
        generatedList.add(Files.readString(createdFileY).replaceAll("\\s",""));
        generatedList.add(Files.readString(createdFileT2).replaceAll("\\s",""));
        generatedList.add(Files.readString(createdFileF5).replaceAll("\\s",""));
        assertEquals(correctList,generatedList);
    }

    @Test
    public void testProcedureTypeError(){
        Assertions.assertThrows(UnexpectedTypeException.class, () -> {
            createChoralFile("./src/test/resources/files/Procedure/TestProcedureTypeError.txt", "./src/test/resources/generated_results/");
        });
    }

    @Test
    public void testNoReturn(){
        Assertions.assertThrows(ReturnException.class, () -> {
            createChoralFile("./src/test/resources/files/Procedure/testNoReturn.txt", "./src/test/resources/generated_results/");
        });
    }

    @Test
    public void testInvokeVoidAsNonVoid(){
        Assertions.assertThrows(UnexpectedInputException.class, () -> {
            createChoralFile("./src/test/resources/files/Procedure/testInvokeVoidAsNonVoid.txt", "./src/test/resources/generated_results/");
        });
    }

    @Test
    public void testInvokeNonVoidAsVoid(){
        Assertions.assertThrows(UnexpectedInputException.class, () -> {
            createChoralFile("./src/test/resources/files/Procedure/testInvokeNonVoidAsVoid.txt", "./src/test/resources/generated_results/");
        });
    }

    @Test
    public void testWrongFunctionType(){
        Assertions.assertThrows(UnexpectedTypeException.class, () -> {
            createChoralFile("./src/test/resources/files/Procedure/testWrongFunctionType.txt", "./src/test/resources/generated_results/");
        });
    }

    @Test
    public void testWrongFunctionReturnType(){
        Assertions.assertThrows(UnexpectedTypeException.class, () -> {
            createChoralFile("./src/test/resources/files/Procedure/testWrongFunctionReturnType.txt", "./src/test/resources/generated_results/");
        });
    }

    @Test
    public void testProcessesInWrongOrder(){
        Assertions.assertThrows(ProcessMismatchException.class, () -> {
            createChoralFile("./src/test/resources/files/Procedure/testProcessesInWrongOrder.txt", "./src/test/resources/generated_results/");
        });
    }

    @Test
    public void testProcessesNotDefined(){
        Assertions.assertThrows(NotDefinedException.class, () -> {
            createChoralFile("./src/test/resources/files/Procedure/testProcessesNotDefined.txt", "./src/test/resources/generated_results/");
        });
    }

    @AfterEach
    public void afterAllCleanUp() throws IOException {
        File directory = new File("./src/test/resources/generated_results");
        FileUtils.cleanDirectory(directory);
    }
}
