package stateful.chore.structures;

import choral.ast.statement.Statement;
import simple.chore.stuctures.Pid;
import type.checking.structures.DataType;

import java.util.List;
import java.util.Map;

public record ProcedureInfo(String name,
                            List<Pid> processes,
                            Statement toRun,
                            List<L> outputGamma,
                            Map<Pid, Map<String, DataType>> inputGamma) {
}
