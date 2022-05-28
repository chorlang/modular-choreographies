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

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static base.Main.createChoralFile;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestExamples {

    @Test
    public void testDiffie() throws Exception{
        Path correctFileDH = Path.of( "src/test/resources/examples/diffie_hellman_correct/DiffieHellman.txt");
        Path correctFileF3 = Path.of( "src/test/resources/examples/diffie_hellman_correct/Function3.txt");
        Path correctFileT2 = Path.of( "src/test/resources/examples/diffie_hellman_correct/Tuple2.txt");

        createChoralFile("./src/test/resources/files/diffie_hellman.txt","./src/test/resources/generated_results/");

        Path createdFileDH = Path.of("src/test/resources/generated_results/DiffieHellman.txt");
        Path createdFileF3 = Path.of("src/test/resources/generated_results/Function3.txt");
        Path createdFileT2 = Path.of("src/test/resources/generated_results/Tuple2.txt");

        List<String> correctList = new ArrayList<>();
        List<String> generatedList = new ArrayList<>();
        correctList.add(Files.readString(correctFileDH).replaceAll("\\s",""));
        correctList.add(Files.readString(correctFileF3).replaceAll("\\s",""));
        correctList.add(Files.readString(correctFileT2).replaceAll("\\s",""));
        generatedList.add(Files.readString(createdFileDH).replaceAll("\\s",""));
        generatedList.add(Files.readString(createdFileF3).replaceAll("\\s",""));
        generatedList.add(Files.readString(createdFileT2).replaceAll("\\s",""));
        assertEquals(correctList,generatedList);
    }

    @Test
    public void testPP() throws Exception{
        Path correctFile = Path.of( "src/test/resources/examples/ping_pong_correct/PP.txt");
        createChoralFile("./src/test/resources/files/ping_pong.txt","./src/test/resources/generated_results/");
        Path createdFile = Path.of("src/test/resources/generated_results/PP.txt");
        List<String> correctList = new ArrayList<>();
        List<String> generatedList = new ArrayList<>();
        correctList.add(Files.readString(correctFile).replaceAll("\\s",""));
        generatedList.add(Files.readString(createdFile).replaceAll("\\s",""));
        assertEquals(correctList,generatedList);
    }

    @Test
    public void testSSO() throws Exception{
        Path correctFile = Path.of( "src/test/resources/examples/single_sign_on_correct/SSO.txt");
        createChoralFile("./src/test/resources/files/single_sign_on.txt","./src/test/resources/generated_results/");
        Path createdFile = Path.of("src/test/resources/generated_results/SSO.txt");
        List<String> correctList = new ArrayList<>();
        List<String> generatedList = new ArrayList<>();
        correctList.add(Files.readString(correctFile).replaceAll("\\s",""));
        generatedList.add(Files.readString(createdFile).replaceAll("\\s",""));
        assertEquals(correctList,generatedList);
    }

    @AfterAll
    static void cleanUp() throws IOException {
        File directory = new File("./src/test/resources/generated_results");
        FileUtils.cleanDirectory(directory);
    }
}
