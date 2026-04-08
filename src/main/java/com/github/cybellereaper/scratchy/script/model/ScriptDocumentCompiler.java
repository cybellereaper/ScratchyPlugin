package com.github.cybellereaper.scratchy.script.model;

import com.github.cybellereaper.scratchy.domain.*;
import com.github.cybellereaper.scratchy.script.blocks.ScriptBlock;
import com.github.cybellereaper.scratchy.script.blocks.ScriptSequence;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ScriptDocumentCompiler {

    public ScriptDefinition compile(ScriptDocument document) {
        return new ScriptDefinition(document.id(), document.name(), document.trigger(), compileSequence(document.root()));
    }

    private ScriptStep compileSequence(ScriptSequence sequence) {
        return new SequenceStep(sequence.blocks().stream().map(this::compileBlock).collect(Collectors.toList()));
    }

    private ScriptStep compileBlock(ScriptBlock block) {
        return switch (block.blockType()) {
            case "if" -> compileIf(block);
            case "repeat_n" -> new RepeatNStep(intArg(block, "times", 1), compileBranchOrStop(block, "body"));
            case "repeat_while" -> new RepeatWhileStep(
                    new ConditionSpec(strArg(block, "condition", "random_chance"), Map.of()),
                    compileBranchOrStop(block, "body"),
                    intArg(block, "maxIterations", 100)
            );
            case "stop_script" -> new StopStep();
            default -> new ActionStep(block.blockType(), ValueRefs.toLegacyArgs(block.args()));
        };
    }

    private ScriptStep compileIf(ScriptBlock block) {
        ScriptStep thenStep = compileBranchOrStop(block, "then");
        ScriptSequence elseBranch = block.branches().get("else");
        ScriptStep elseStep = elseBranch == null ? null : compileSequence(elseBranch);
        return new IfStep(new ConditionSpec(strArg(block, "condition", "random_chance"), Map.of()), thenStep, elseStep);
    }

    private ScriptStep compileBranchOrStop(ScriptBlock block, String branchName) {
        ScriptSequence branch = block.branches().get(branchName);
        if (branch == null || branch.blocks().isEmpty()) {
            return new StopStep();
        }
        return compileSequence(branch);
    }

    private int intArg(ScriptBlock block, String key, int fallback) {
        Object value = ValueRefs.toLiteral(block.args().get(key));
        if (value instanceof Number number) {
            return number.intValue();
        }
        return fallback;
    }

    private String strArg(ScriptBlock block, String key, String fallback) {
        Object value = ValueRefs.toLiteral(block.args().get(key));
        return value == null ? fallback : String.valueOf(value);
    }
}
