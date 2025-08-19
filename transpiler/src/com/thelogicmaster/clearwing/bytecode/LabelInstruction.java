package com.thelogicmaster.clearwing.bytecode;

import com.thelogicmaster.clearwing.BytecodeMethod;
import com.thelogicmaster.clearwing.StackEntry;
import com.thelogicmaster.clearwing.TranspilerConfig;
import org.objectweb.asm.Label;

import java.util.List;

/**
 * A pseudo-instruction representing a label
 */
public class LabelInstruction extends Instruction {

	private final int label;
	private int location = -1;

	public LabelInstruction (BytecodeMethod method, int label) {
		super(method, -1);
		this.label = label;
	}
	
	public LabelInstruction (BytecodeMethod method, Label label) {
		this(method, method.getLabelId(label));
	}

	@Override
	public void appendUnoptimized (StringBuilder builder, TranspilerConfig config) {
		builder.append(LABEL_PREFIX).append(label).append(":;\n");
		if (location >= 0 && !method.getExceptionFrames().isEmpty())
			builder.append("\tFRAME_LOCATION(").append(location).append(");\n");
	}

	@Override
	public void appendOptimized(StringBuilder builder, TranspilerConfig config) {
		appendUnoptimized(builder, config);
	}

	public int getLabel () {
		return label;
	}

	public int getLocation() {
		return location;
	}

	public void setLocation(int location) {
		this.location = location;
	}

	@Override
	public void resolveIO(List<StackEntry> stack) {
		setInputs();
		setBasicOutputs();
	}
}
