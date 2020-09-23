package generic;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;


public class Simulator {

	static FileInputStream inputcodeStream = null;
	public static Map<Instruction.OperationType, String> mapping = new HashMap<>() {{
		put(Instruction.OperationType.add, "00000");
		put(Instruction.OperationType.addi, "00001");
		put(Instruction.OperationType.sub, "00010");
		put(Instruction.OperationType.subi, "00011");
		put(Instruction.OperationType.mul, "00100");
		put(Instruction.OperationType.muli, "00101");
		put(Instruction.OperationType.div, "00110");
		put(Instruction.OperationType.divi, "00111");
		put(Instruction.OperationType.and, "01000");
		put(Instruction.OperationType.andi, "01001");
		put(Instruction.OperationType.or, "01010");
		put(Instruction.OperationType.ori, "01011");
		put(Instruction.OperationType.xor, "01100");
		put(Instruction.OperationType.xori, "01101");
		put(Instruction.OperationType.slt, "01110");
		put(Instruction.OperationType.slti, "01111");
		put(Instruction.OperationType.sll, "10000");
		put(Instruction.OperationType.slli, "10001");
		put(Instruction.OperationType.srl, "10010");
		put(Instruction.OperationType.srli, "10011");
		put(Instruction.OperationType.sra, "10100");
		put(Instruction.OperationType.srai, "10101");
		put(Instruction.OperationType.load, "10110");
		put(Instruction.OperationType.end, "11101");
	}};

	public static void setupSimulation(String assemblyProgramFile) {
		int firstCodeAddress = ParsedProgram.parseDataSection(assemblyProgramFile);
		ParsedProgram.parseCodeSection(assemblyProgramFile, firstCodeAddress);
		ParsedProgram.printState();
	}

	private static String toBinaryString(int n) {
		// Remove this conditional statement
		if (n >= 0) return String.valueOf(n);

		Stack<Integer> bits = new Stack<>();
		do {
			bits.push(n % 2);
			n /= 2;
		} while (n != 0);

		StringBuilder builder = new StringBuilder();
		while (!bits.isEmpty()) {
			builder.append(bits.pop());
		}
		return builder.toString();
	}

	private static String convert(Operand inst) {
		if (inst.getOperandType() == Operand.OperandType.Label)
			return toBinaryString(ParsedProgram.symtab.get(inst.getLabelValue()));

		// write logic for converting to binary/ hex
		return toBinaryString(inst.getValue());
		// check if inst is a label, in that case, use its value 
		// return String.valueOf(inst.getValue());
	}

	public static void assemble(String objectProgramFile) {
		FileWriter file;
		try {
			//1. open the objectProgramFile in binary mode
			file = new FileWriter(objectProgramFile);

			//2. write the firstCodeAddress to the file
			file.write(String.valueOf(ParsedProgram.firstCodeAddress));

			//3. write the data to the file
			for (var value: ParsedProgram.data)
				file.write(String.valueOf(value));

			//4. assemble one instruction at a time, and write to the file
			for (var inst: ParsedProgram.code) {
				/**
				 * inst.getSourceOperand().getValue() will be passed to a function as f()
				 * that will change decimal to binary and then will return the string
				 * form of the binary. It will also check if the value is a label,
				 * in case it is a label, it would call ParsedProgram.symtab.get()
				 * to get the address corresponding to the label
				 */
				// print operation type, use toBinaryString() instead of convert()
				file.write(mapping.get(inst.getOperationType()));
				// System.out.println(inst.getOperationType() + " " + mapping.get(inst.getOperationType()));
				// System.out.println(mapping);

				if (inst.getSourceOperand1() != null)
					file.write(convert(inst.getSourceOperand1()));
				if (inst.getSourceOperand2() != null)
					file.write(convert(inst.getSourceOperand2()));
				if (inst.getDestinationOperand() != null)
					file.write(convert(inst.getDestinationOperand()));
				file.write(inst.toString());
			}

			//5. close the file
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
}
