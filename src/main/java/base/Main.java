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

package base;
import parser.SimpleParser.*;
import simple.chore.ClassPrintStructure;
import simple.chore.stuctures.Pid;
import stateful.chore.structures.L;
import stateful.chore.structures.ProcedureInfo;
import type.checking.structures.ProcedureType;
import type.checking.structures.DataType;
import type.checking.structures.Pairs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static simple.chore.ParseTreeConversion.getTree;
import static base.ReturnFile.*;
import static simple.chore.ToProcedureInfo.getProcedures;
import static stateful.chore.ParseTreeConversion.*;
import static type.checking.ChoreTypingContent.createGlobalGamma;

public class Main {

    /**
     * @param filePath String, the path to the file containing the Mc program.
     * @param returnPath String, the path to where the resulting files should be outputted to.
     * @throws Exception thrown when an error occurs.
     */
    public static void createChoralFile(String filePath, String returnPath) throws Exception {
        try {
            // Read from a file
            Path file = Path.of(filePath);
            String example = Files.readString(file);
            // Get the parse tree, creates from the Mc program
            SimpleParser.ProgramContext start = getTree(example);
            // Get the name of the class
            String className = file.getFileName().toString().split("\\.")[0];
            // Checks if the choreography is empty
            if (start.procedure != null) {
                // Get the different procedures from the parse tree
                List<ProcedureType> interfaces = interfaceHandling(start, new ArrayList<>());
                // Get the gamma
                Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> gamma = createGlobalGamma(interfaces);
                // Original used in procedure invocation
                Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> original = createGlobalGamma(interfaces);
                // Create choral class from choreography
                List<ProcedureInfo> procedures = getProcedures(start, gamma, original, new ArrayList<>(), returnPath);
                // Create and write to the return file of each procedure
                for (ProcedureInfo procedureInfo : procedures) {
                    String resultText = ClassPrintStructure.createClass(procedureInfo, returnPath);
                    String returnFile = createFullReturnPath(returnPath, procedureInfo.name());
                    createReturnFile(returnFile);
                    writeToFile(returnFile, resultText);
                }

            } else {
                // Write nothing to a file because the input file is empty
                String returnFile = createFullReturnPath(returnPath, className);
                createReturnFile(returnFile);
                writeToFile(returnFile, "");

            }

        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

    }

    /**
     * @param path     String, the return path.
     * @param fileName String, the name of the file.
     * @return String, the full return path.
     */
    public static String createFullReturnPath(String path, String fileName) {
        if (path.substring(path.length() - 1).equals("/")) {
            return path.concat(fileName).concat(".txt");
        } else {
            return path.concat("/").concat(fileName).concat(".txt");
        }

    }

    /**
     * @param args String[], the arguments given in the commandline.
     * @throws Exception is thrown when an error occurs.
     */
    public static void main(String[] args) throws Exception {
        //createChoralFile(".\\src\\test\\resources\\files\\test.txt",".\\src\\test\\resources\\generated_results\\");
        createChoralFile(args[0], args[1]);
    }

}
