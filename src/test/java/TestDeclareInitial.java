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

import custom.exceptions.NotDeclaredException;
import custom.exceptions.UnexpectedTypeException;
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

public class TestDeclareInitial {

    @Test
    public void testDeclareAlone() throws Exception{
        Path correctFileX = Path.of( "src/test/resources/declareInitial_correct/declareAlone/X.txt");

        createChoralFile("./src/test/resources/files/DeclareInitial/declareAlone.txt","./src/test/resources/generated_results/");

        Path createdFileX = Path.of("src/test/resources/generated_results/X.txt");

        List<String> correctList = new ArrayList<>();
        List<String> generatedList = new ArrayList<>();
        correctList.add(Files.readString(correctFileX).replaceAll("\\s",""));
        generatedList.add(Files.readString(createdFileX).replaceAll("\\s",""));
        assertEquals(correctList,generatedList);
    }

    @Test
    public void testDeclareThenInitial() throws Exception{
        Path correctFileX = Path.of( "src/test/resources/declareInitial_correct/declareThenInitial/X.txt");

        createChoralFile("./src/test/resources/files/DeclareInitial/declareThenInitial.txt","./src/test/resources/generated_results/");

        Path createdFileX = Path.of("src/test/resources/generated_results/X.txt");

        List<String> correctList = new ArrayList<>();
        List<String> generatedList = new ArrayList<>();
        correctList.add(Files.readString(correctFileX).replaceAll("\\s",""));
        generatedList.add(Files.readString(createdFileX).replaceAll("\\s",""));
        assertEquals(correctList,generatedList);
    }

    @Test
    public void testDeclareAndInitial() throws Exception{
        Path correctFileX = Path.of( "src/test/resources/declareInitial_correct/declareAndInitial/X.txt");

        createChoralFile("./src/test/resources/files/DeclareInitial/declareAndInitial.txt","./src/test/resources/generated_results/");

        Path createdFileX = Path.of("src/test/resources/generated_results/X.txt");

        List<String> correctList = new ArrayList<>();
        List<String> generatedList = new ArrayList<>();
        correctList.add(Files.readString(correctFileX).replaceAll("\\s",""));
        generatedList.add(Files.readString(createdFileX).replaceAll("\\s",""));
        assertEquals(correctList,generatedList);
    }

    @Test
    public void testDeclareWrongType1() {
        Assertions.assertThrows(UnexpectedTypeException.class, () -> {
            createChoralFile("./src/test/resources/files/DeclareInitial/declareWrongType1.txt", "./src/test/resources/generated_results/");
        });
    }

    @Test
    public void testDeclareWrongType2() {
        Assertions.assertThrows(UnexpectedTypeException.class, () -> {
            createChoralFile("./src/test/resources/files/DeclareInitial/declareWrongType2.txt", "./src/test/resources/generated_results/");
        });
    }

    @Test
    public void testInitialOnly() {
        Assertions.assertThrows(NotDeclaredException.class, () -> {
            createChoralFile("./src/test/resources/files/DeclareInitial/initialOnly.txt", "./src/test/resources/generated_results/");
        });
    }

    @Test
    public void testStatefulComNoVariable() {
        Assertions.assertThrows(NotDeclaredException.class, () -> {
            createChoralFile("./src/test/resources/files/DeclareInitial/statefulComNoVar.txt", "./src/test/resources/generated_results/");
        });
    }

    @Test
    public void testStatefulComVarCorrect() throws Exception{
        Path correctFileX = Path.of( "src/test/resources/declareInitial_correct/statefulComVarCorrect/X.txt");
        Path correctFileF3 = Path.of( "src/test/resources/declareInitial_correct/statefulComVarCorrect/Function3.txt");

        createChoralFile("./src/test/resources/files/DeclareInitial/statefulComVarCorrect.txt","./src/test/resources/generated_results/");

        Path createdFileX = Path.of("src/test/resources/generated_results/X.txt");
        Path createdFileF3 = Path.of("src/test/resources/generated_results/Function3.txt");

        List<String> correctList = new ArrayList<>();
        List<String> generatedList = new ArrayList<>();
        correctList.add(Files.readString(correctFileX).replaceAll("\\s",""));
        correctList.add(Files.readString(correctFileF3).replaceAll("\\s",""));
        generatedList.add(Files.readString(createdFileX).replaceAll("\\s",""));
        generatedList.add(Files.readString(createdFileF3).replaceAll("\\s",""));
        assertEquals(correctList,generatedList);
    }

    @Test
    public void testStatefulComVarWrongType() {
        Assertions.assertThrows(UnexpectedTypeException.class, () -> {
            createChoralFile("./src/test/resources/files/DeclareInitial/statefulComVarWrong.txt", "./src/test/resources/generated_results/");
        });
    }

    @AfterEach
    public void afterAllCleanUp() throws IOException {
        File directory = new File("./src/test/resources/generated_results");
        FileUtils.cleanDirectory(directory);
    }
}
