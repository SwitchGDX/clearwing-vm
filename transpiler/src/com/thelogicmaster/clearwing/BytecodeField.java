package com.thelogicmaster.clearwing;

import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BytecodeField {

	private final BytecodeClass owner;
	private final String name;
	private final String originalName;
	private final int access;
	private final String desc;
	private final JavaType type;
	private final String signature;
	private final Object initialValue;
	private final ArrayList<BytecodeAnnotation> annotations = new ArrayList<>();

	public BytecodeField (BytecodeClass clazz, String name, int access, String desc, String signature, Object initialValue) {
		this.owner = clazz;
		this.originalName = name;
		this.access = access;
		this.name = Utils.sanitizeField(clazz.getName(), name, isStatic());
		this.desc = desc;
		this.signature = signature;
		this.initialValue = initialValue;
		type = new JavaType(desc);
	}

	public void collectDependencies(Set<String> dependencies, Map<String, BytecodeClass> classMap) {
		for (BytecodeAnnotation annotation: annotations)
			annotation.collectDependencies(dependencies, classMap);
		if (type.getComponentType() == TypeVariants.OBJECT)
			dependencies.add(type.getRegistryTypeName());
	}

	public void processHierarchy(HashMap<String, BytecodeClass> classMap) {
		for (BytecodeAnnotation annotation : annotations)
			annotation.mergeDefaults(classMap);
	}

	public boolean isStatic() {
		return (access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC;
	}

	public boolean isVolatile() {
		return (access & Opcodes.ACC_VOLATILE) == Opcodes.ACC_VOLATILE;
	}

	public boolean isFinal() {
		return (access & Opcodes.ACC_FINAL) == Opcodes.ACC_FINAL;
	}

	public BytecodeClass getOwner() {
		return owner;
	}

	public String getName () {
		return name;
	}

	public String getOriginalName () {
		return originalName;
	}

	public int getAccess () {
		return access;
	}

	public String getDesc () {
		return desc;
	}

	public String getSignature () {
		return signature;
	}

	public Object getInitialValue () {
		return initialValue;
	}

	public JavaType getType() {
		return type;
	}

	public void addAnnotation(BytecodeAnnotation annotation) {
		annotations.add(annotation);
	}

	public ArrayList<BytecodeAnnotation> getAnnotations() {
		return annotations;
	}
}
