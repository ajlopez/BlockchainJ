package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.DataWord;

import java.util.Stack;

/**
 * Created by ajlopez on 10/12/2018.
 */
public class VirtualMachine {
    private final static FeeSchedule[] opCodeFees = new FeeSchedule[256];

    private final ProgramEnvironment programEnvironment;
    private final Storage storage;
    private final Memory memory = new Memory();
    private final Stack<DataWord> stack = new Stack<>();

    private int gasUsed;

    static {
        opCodeFees[OpCodes.ADDRESS] = FeeSchedule.BASE;
        opCodeFees[OpCodes.ORIGIN] = FeeSchedule.BASE;
        opCodeFees[OpCodes.CALLER] = FeeSchedule.BASE;
        opCodeFees[OpCodes.CALLVALUE] = FeeSchedule.BASE;
        opCodeFees[OpCodes.CALLDATALOAD] = FeeSchedule.VERYLOW;
        opCodeFees[OpCodes.CALLDATASIZE] = FeeSchedule.BASE;
        opCodeFees[OpCodes.PC] = FeeSchedule.BASE;

        opCodeFees[OpCodes.ADD] = FeeSchedule.VERYLOW;
        opCodeFees[OpCodes.SUB] = FeeSchedule.VERYLOW;
        opCodeFees[OpCodes.NOT] = FeeSchedule.VERYLOW;
        opCodeFees[OpCodes.LT] = FeeSchedule.VERYLOW;
        opCodeFees[OpCodes.GT] = FeeSchedule.VERYLOW;
        opCodeFees[OpCodes.SLT] = FeeSchedule.VERYLOW;
        opCodeFees[OpCodes.SGT] = FeeSchedule.VERYLOW;
        opCodeFees[OpCodes.EQ] = FeeSchedule.VERYLOW;
        opCodeFees[OpCodes.ISZERO] = FeeSchedule.VERYLOW;
        opCodeFees[OpCodes.ADDMOD] = FeeSchedule.MID;
        opCodeFees[OpCodes.MULMOD] = FeeSchedule.MID;

        opCodeFees[OpCodes.GASPRICE] = FeeSchedule.BASE;

        for (int k = 0; k < 32; k++)
            opCodeFees[OpCodes.PUSH1 + k] = FeeSchedule.VERYLOW;

        for (int k = 0; k < 16; k++)
            opCodeFees[(OpCodes.DUP1 & 0xff) + k] = FeeSchedule.VERYLOW;

        for (int k = 0; k < 16; k++)
            opCodeFees[(OpCodes.SWAP1 & 0xff) + k] = FeeSchedule.VERYLOW;
    }

    public VirtualMachine(ProgramEnvironment programEnvironment, Storage storage) {
        this.programEnvironment = programEnvironment;
        this.storage = storage;
    }

    public int getGasUsed() {
        return this.gasUsed;
    }

    public void execute(byte[] bytecodes) throws VirtualMachineException {
        int l = bytecodes.length;

        for (int pc = 0; pc < l; pc++) {
            byte bytecode = bytecodes[pc];

            FeeSchedule fee = opCodeFees[bytecode & 0xff];

            if (fee != null) {
                long gasCost = fee.getValue();

                if (this.gasUsed + gasCost > this.programEnvironment.getGas())
                    throw new VirtualMachineException("Insufficient gas");

                this.gasUsed += gasCost;
            }

            switch (bytecode) {
                case OpCodes.STOP:
                    return;

                case OpCodes.ADD:
                    DataWord word1 = this.stack.pop();
                    DataWord word2 = this.stack.pop();

                    this.stack.push(word1.add(word2));

                    break;

                case OpCodes.MUL:
                    word1 = this.stack.pop();
                    word2 = this.stack.pop();

                    this.stack.push(word1.mul(word2));

                    break;

                case OpCodes.SUB:
                    word1 = this.stack.pop();
                    word2 = this.stack.pop();

                    this.stack.push(word1.sub(word2));

                    break;

                case OpCodes.DIV:
                    word1 = this.stack.pop();
                    word2 = this.stack.pop();

                    if (word2.isZero())
                        this.stack.push(DataWord.ZERO);
                    else
                        this.stack.push(word1.div(word2));

                    break;

                case OpCodes.SDIV:
                    word1 = this.stack.pop();
                    word2 = this.stack.pop();

                    if (word2.isZero())
                        this.stack.push(DataWord.ZERO);
                    else
                        this.stack.push(word1.sdiv(word2));

                    break;

                case OpCodes.MOD:
                    word1 = this.stack.pop();
                    word2 = this.stack.pop();

                    if (word2.isZero())
                        this.stack.push(DataWord.ZERO);
                    else
                        this.stack.push(word1.mod(word2));

                    break;

                case OpCodes.SMOD:
                    word1 = this.stack.pop();
                    word2 = this.stack.pop();

                    if (word2.isZero())
                        this.stack.push(DataWord.ZERO);
                    else
                        this.stack.push(word1.smod(word2));

                    break;

                case OpCodes.ADDMOD:
                    word1 = this.stack.pop();
                    word2 = this.stack.pop();
                    DataWord word3 = this.stack.pop();

                    if (word3.isZero())
                        this.stack.push(DataWord.ZERO);
                    else
                        this.stack.push(word1.add(word2).mod(word3));

                    break;

                case OpCodes.MULMOD:
                    word1 = this.stack.pop();
                    word2 = this.stack.pop();
                    word3 = this.stack.pop();

                    if (word3.isZero())
                        this.stack.push(DataWord.ZERO);
                    else
                        this.stack.push(word1.mul(word2).mod(word3));

                    break;

                case OpCodes.LT:
                    word1 = this.stack.pop();
                    word2 = this.stack.pop();

                    this.stack.push(word1.compareTo(word2) < 0 ? DataWord.ONE : DataWord.ZERO);

                    break;

                case OpCodes.GT:
                    word1 = this.stack.pop();
                    word2 = this.stack.pop();

                    this.stack.push(word1.compareTo(word2) > 0 ? DataWord.ONE : DataWord.ZERO);

                    break;

                case OpCodes.SLT:
                    word1 = this.stack.pop();
                    word2 = this.stack.pop();

                    this.stack.push(word1.compareToSigned(word2) < 0 ? DataWord.ONE : DataWord.ZERO);

                    break;

                case OpCodes.SGT:
                    word1 = this.stack.pop();
                    word2 = this.stack.pop();

                    this.stack.push(word1.compareToSigned(word2) > 0 ? DataWord.ONE : DataWord.ZERO);

                    break;

                case OpCodes.EQ:
                    word1 = this.stack.pop();
                    word2 = this.stack.pop();

                    this.stack.push(word1.compareTo(word2) == 0 ? DataWord.ONE : DataWord.ZERO);

                    break;

                case OpCodes.ISZERO:
                    DataWord word = this.stack.pop();

                    this.stack.push(word.isZero() ? DataWord.ONE : DataWord.ZERO);

                    break;

                case OpCodes.AND:
                    word1 = this.stack.pop();
                    word2 = this.stack.pop();

                    this.stack.push(word1.and(word2));

                    break;

                case OpCodes.OR:
                    word1 = this.stack.pop();
                    word2 = this.stack.pop();

                    this.stack.push(word1.or(word2));

                    break;

                case OpCodes.XOR:
                    word1 = this.stack.pop();
                    word2 = this.stack.pop();

                    this.stack.push(word1.xor(word2));

                    break;

                case OpCodes.NOT:
                    this.stack.push(this.stack.pop().not());

                    break;

                case OpCodes.BYTE:
                    word1 = this.stack.pop();
                    word2 = this.stack.pop();

                    int nbyte = word1.getBytes()[DataWord.DATAWORD_BYTES - 1] & 0xff;

                    if (word1.isUnsignedInteger() && nbyte < 32)
                        this.stack.push(DataWord.fromUnsignedInteger(word2.getBytes()[nbyte] & 0xff));
                    else
                        this.stack.push(DataWord.ZERO);

                    break;

                case OpCodes.ADDRESS:
                    this.stack.push(DataWord.fromAddress(this.programEnvironment.getAddress()));

                    break;

                case OpCodes.ORIGIN:
                    this.stack.push(DataWord.fromAddress(this.programEnvironment.getOrigin()));

                    break;

                case OpCodes.CALLER:
                    this.stack.push(DataWord.fromAddress(this.programEnvironment.getCaller()));

                    break;

                case OpCodes.CALLVALUE:
                    this.stack.push(this.programEnvironment.getValue());

                    break;

                case OpCodes.CALLDATALOAD:
                    byte[] data = this.programEnvironment.getData();
                    int offset = this.stack.pop().asUnsignedInteger();

                    if (offset >= data.length)
                        this.stack.push(DataWord.ZERO);
                    else
                        this.stack.push(DataWord.fromBytesToLeft(data, offset, Math.min(DataWord.DATAWORD_BYTES, data.length - offset)));

                    break;

                case OpCodes.CALLDATASIZE:
                    this.stack.push(DataWord.fromUnsignedInteger(this.programEnvironment.getData().length));

                    break;

                case OpCodes.CALLDATACOPY:
                    data = this.programEnvironment.getData();
                    int targetOffset = this.stack.pop().asUnsignedInteger();
                    int sourceOffset = this.stack.pop().asUnsignedInteger();
                    int length = this.stack.pop().asUnsignedInteger();

                    this.memory.setBytes(targetOffset, data, sourceOffset, length);

                    break;

                case OpCodes.CODESIZE:
                    this.stack.push(DataWord.fromUnsignedInteger(bytecodes.length));

                    break;

                case OpCodes.CODECOPY:
                    targetOffset = this.stack.pop().asUnsignedInteger();
                    sourceOffset = this.stack.pop().asUnsignedInteger();
                    length = this.stack.pop().asUnsignedInteger();

                    this.memory.setBytes(targetOffset, bytecodes, sourceOffset, length);

                    break;

                case OpCodes.GASPRICE:
                    this.stack.push(this.programEnvironment.getGasPrice());

                    break;

                case OpCodes.COINBASE:
                    this.stack.push(DataWord.fromAddress(this.programEnvironment.getCoinbase()));

                    break;

                case OpCodes.TIMESTAMP:
                    this.stack.push(DataWord.fromUnsignedLong(this.programEnvironment.getTimestamp()));

                    break;

                case OpCodes.NUMBER:
                    this.stack.push(DataWord.fromUnsignedLong(this.programEnvironment.getNumber()));

                    break;

                case OpCodes.DIFFICULTY:
                    this.stack.push(this.programEnvironment.getDifficulty());

                    break;

                case OpCodes.POP:
                    this.stack.pop();

                    break;

                case OpCodes.MLOAD:
                    word1 = this.stack.pop();

                    this.stack.push(this.memory.getValue(word1.asUnsignedInteger()));

                    break;

                case OpCodes.MSTORE:
                    word1 = this.stack.pop();
                    word2 = this.stack.pop();

                    this.memory.setValue(word1.asUnsignedInteger(), word2);

                    break;

                case OpCodes.MSTORE8:
                    word1 = this.stack.pop();
                    word2 = this.stack.pop();

                    this.memory.setByte(word1.asUnsignedInteger(), word2.getBytes()[DataWord.DATAWORD_BYTES - 1]);

                    break;

                case OpCodes.SLOAD:
                    word1 = this.stack.pop();

                    this.stack.push(this.storage.getValue(word1));

                    break;

                case OpCodes.SSTORE:
                    word1 = this.stack.pop();
                    word2 = this.stack.pop();

                    this.storage.setValue(word1, word2);

                    break;

                case OpCodes.JUMP:
                    word = this.stack.pop();

                    pc = getNewPc(bytecodes, word);

                    break;

                case OpCodes.JUMPI:
                    word1 = this.stack.pop();
                    word2 = this.stack.pop();

                    if (word2.isZero())
                        break;

                    pc = getNewPc(bytecodes, word1);

                    break;

                case OpCodes.JUMPDEST:

                    break;

                case OpCodes.PC:
                    this.stack.push(DataWord.fromUnsignedInteger(pc));

                    break;

                case OpCodes.MSIZE:
                    this.stack.push(DataWord.fromUnsignedInteger(this.memory.size()));

                    break;

                case OpCodes.PUSH1:
                case OpCodes.PUSH2:
                case OpCodes.PUSH3:
                case OpCodes.PUSH4:
                case OpCodes.PUSH5:
                case OpCodes.PUSH6:
                case OpCodes.PUSH7:
                case OpCodes.PUSH8:
                case OpCodes.PUSH9:
                case OpCodes.PUSH10:
                case OpCodes.PUSH11:
                case OpCodes.PUSH12:
                case OpCodes.PUSH13:
                case OpCodes.PUSH14:
                case OpCodes.PUSH15:
                case OpCodes.PUSH16:
                case OpCodes.PUSH17:
                case OpCodes.PUSH18:
                case OpCodes.PUSH19:
                case OpCodes.PUSH20:
                case OpCodes.PUSH21:
                case OpCodes.PUSH22:
                case OpCodes.PUSH23:
                case OpCodes.PUSH24:
                case OpCodes.PUSH25:
                case OpCodes.PUSH26:
                case OpCodes.PUSH27:
                case OpCodes.PUSH28:
                case OpCodes.PUSH29:
                case OpCodes.PUSH30:
                case OpCodes.PUSH31:
                case OpCodes.PUSH32:
                    int lb = bytecode - OpCodes.PUSH1 + 1;

                    this.stack.push(DataWord.fromBytes(bytecodes, pc + 1, lb));

                    pc += lb;

                    break;

                case OpCodes.DUP1:
                case OpCodes.DUP2:
                case OpCodes.DUP3:
                case OpCodes.DUP4:
                case OpCodes.DUP5:
                case OpCodes.DUP6:
                case OpCodes.DUP7:
                case OpCodes.DUP8:
                case OpCodes.DUP9:
                case OpCodes.DUP10:
                case OpCodes.DUP11:
                case OpCodes.DUP12:
                case OpCodes.DUP13:
                case OpCodes.DUP14:
                case OpCodes.DUP15:
                case OpCodes.DUP16:
                    this.stack.push(this.stack.get(this.stack.size() - 1 - (bytecode - OpCodes.DUP1)));

                    break;

                case OpCodes.SWAP1:
                case OpCodes.SWAP2:
                case OpCodes.SWAP3:
                case OpCodes.SWAP4:
                case OpCodes.SWAP5:
                case OpCodes.SWAP6:
                case OpCodes.SWAP7:
                case OpCodes.SWAP8:
                case OpCodes.SWAP9:
                case OpCodes.SWAP10:
                case OpCodes.SWAP11:
                case OpCodes.SWAP12:
                case OpCodes.SWAP13:
                case OpCodes.SWAP14:
                case OpCodes.SWAP15:
                case OpCodes.SWAP16:
                    int size = this.stack.size();
                    offset = bytecode - OpCodes.SWAP1 + 1;

                    word1 = this.stack.get(size - 1);
                    word2 = this.stack.get(size - 1 - offset);

                    this.stack.set(size - 1, word2);
                    this.stack.set(size - 1 - offset, word1);

                    break;

                default:
                    throw new VirtualMachineException("Invalid opcode");
            }
        }
    }

    private int getNewPc(byte[] bytecodes, DataWord word1) throws VirtualMachineException {
        int newpc;

        if (!word1.isUnsignedInteger())
            throw new VirtualMachineException("Invalid jump");

        newpc = word1.asUnsignedInteger();

        if (newpc >= bytecodes.length || bytecodes[newpc] != OpCodes.JUMPDEST)
            throw new VirtualMachineException("Invalid jump");

        return newpc - 1;
    }

    public Stack<DataWord> getStack() {
        return this.stack;
    }

    public Memory getMemory() {
        return this.memory;
    }
}
