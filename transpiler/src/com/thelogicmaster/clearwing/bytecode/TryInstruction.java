package com.thelogicmaster.clearwing.bytecode;

import com.thelogicmaster.clearwing.BytecodeMethod;
import com.thelogicmaster.clearwing.StackEntry;
import com.thelogicmaster.clearwing.TranspilerConfig;
import com.thelogicmaster.clearwing.Utils;
import org.objectweb.asm.Label;

import java.util.*;

/**
 * A pseudo-instruction try-catch that gets inserted in parts after the start and end labels
 */
public class TryInstruction extends Instruction implements JumpingInstruction {

    private final BytecodeMethod.ExceptionFrame frame;
    private final CatchInstruction catchInstruction;

    public TryInstruction(BytecodeMethod method, BytecodeMethod.ExceptionFrame frame) {
        super(method, -1);
        this.frame = frame;
        catchInstruction = new CatchInstruction();
    }

    @Override
    public void appendUnoptimized(StringBuilder builder, TranspilerConfig config) {
        builder.append("\t// Begin try-").append(frame.getIndex()).append("\n");
    }

    @Override
    public void appendOptimized(StringBuilder builder, TranspilerConfig config) {
        appendUnoptimized(builder, config);
    }

    @Override
    public void collectDependencies(Set<String> dependencies) {
        if (frame.getType() != null)
            dependencies.add(Utils.sanitizeName(frame.getType()));
        else
            dependencies.add("java/lang/Throwable");
    }

    @Override
    public List<Integer> getJumpLabels() {
        return List.of(frame.getHandlerLabel());
    }

    @Override
    public void resolveIO(List<StackEntry> stack) {
        setInputs();
        setBasicOutputs();
    }

    public BytecodeMethod.ExceptionFrame getFrame() {
        return frame;
    }

    public CatchInstruction getCatchInstruction() {
        return catchInstruction;
    }

    public class CatchInstruction extends Instruction {
        
        private CatchInstruction() {
            super(TryInstruction.this.getMethod(), -1);
        }

        @Override
        public void appendUnoptimized(StringBuilder builder, TranspilerConfig config) {
            builder.append("\t// End try-").append(frame.getIndex()).append("\n");
        }

        @Override
        public void appendOptimized(StringBuilder builder, TranspilerConfig config) {
            appendUnoptimized(builder, config);
        }

        public TryInstruction getTry() {
            return TryInstruction.this;
        }

        @Override
        public void resolveIO(List<StackEntry> stack) {
            setInputs();
            setBasicOutputs();
        }
    }
}
