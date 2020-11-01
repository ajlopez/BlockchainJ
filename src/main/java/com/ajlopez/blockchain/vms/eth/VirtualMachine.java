package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.core.types.DataWord;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.utils.ByteUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by ajlopez on 10/12/2018.
 */
public class VirtualMachine {
    private final static FeeSchedule[] opCodeFees = new FeeSchedule[256];

    private final ProgramEnvironment programEnvironment;
    private final Storage storage;
    private final Memory memory = new Memory();
    private final Stack<DataWord> dataStack = new Stack<>();
    // TODO return stack max size
    private final Stack<Integer> returnStack = new Stack<>();

    static {
        opCodeFees[OpCodes.ADDRESS] = FeeSchedule.BASE;
        opCodeFees[OpCodes.BALANCE] = FeeSchedule.BALANCE;
        opCodeFees[OpCodes.ORIGIN] = FeeSchedule.BASE;
        opCodeFees[OpCodes.CALLER] = FeeSchedule.BASE;
        opCodeFees[OpCodes.CALLVALUE] = FeeSchedule.BASE;
        opCodeFees[OpCodes.CALLDATALOAD] = FeeSchedule.VERYLOW;
        opCodeFees[OpCodes.CALLDATASIZE] = FeeSchedule.BASE;
        opCodeFees[OpCodes.CODESIZE] = FeeSchedule.BASE;
        opCodeFees[OpCodes.PC] = FeeSchedule.BASE;

        opCodeFees[OpCodes.ADD] = FeeSchedule.VERYLOW;
        opCodeFees[OpCodes.SUB] = FeeSchedule.VERYLOW;
        opCodeFees[OpCodes.MUL] = FeeSchedule.LOW;
        opCodeFees[OpCodes.DIV] = FeeSchedule.LOW;
        opCodeFees[OpCodes.SDIV] = FeeSchedule.LOW;

        opCodeFees[OpCodes.NOT] = FeeSchedule.VERYLOW;
        opCodeFees[OpCodes.LT] = FeeSchedule.VERYLOW;
        opCodeFees[OpCodes.GT] = FeeSchedule.VERYLOW;
        opCodeFees[OpCodes.SLT] = FeeSchedule.VERYLOW;
        opCodeFees[OpCodes.SGT] = FeeSchedule.VERYLOW;
        opCodeFees[OpCodes.EQ] = FeeSchedule.VERYLOW;
        opCodeFees[OpCodes.ISZERO] = FeeSchedule.VERYLOW;

        opCodeFees[OpCodes.XOR] = FeeSchedule.VERYLOW;
        opCodeFees[OpCodes.AND] = FeeSchedule.VERYLOW;
        opCodeFees[OpCodes.OR] = FeeSchedule.VERYLOW;
        opCodeFees[OpCodes.BYTE] = FeeSchedule.VERYLOW;
        opCodeFees[OpCodes.SHL] = FeeSchedule.VERYLOW;
        opCodeFees[OpCodes.SHR] = FeeSchedule.VERYLOW;
        opCodeFees[OpCodes.SAR] = FeeSchedule.VERYLOW;

        opCodeFees[OpCodes.COINBASE] = FeeSchedule.BASE;
        opCodeFees[OpCodes.DIFFICULTY] = FeeSchedule.BASE;
        opCodeFees[OpCodes.GASLIMIT] = FeeSchedule.BASE;
        opCodeFees[OpCodes.CHAINID] = FeeSchedule.BASE;
        opCodeFees[OpCodes.SELFBALANCE] = FeeSchedule.LOW;
        opCodeFees[OpCodes.TIMESTAMP] = FeeSchedule.BASE;
        opCodeFees[OpCodes.NUMBER] = FeeSchedule.BASE;

        opCodeFees[OpCodes.MOD] = FeeSchedule.LOW;
        opCodeFees[OpCodes.SMOD] = FeeSchedule.LOW;

        opCodeFees[OpCodes.ADDMOD] = FeeSchedule.MID;
        opCodeFees[OpCodes.MULMOD] = FeeSchedule.MID;
        opCodeFees[OpCodes.JUMP] = FeeSchedule.MID;
        opCodeFees[OpCodes.JUMPDEST] = FeeSchedule.JUMPDEST;

        opCodeFees[OpCodes.JUMPI] = FeeSchedule.HIGH;

        opCodeFees[OpCodes.BEGINSUB] = FeeSchedule.BASE;
        opCodeFees[OpCodes.RETURNSUB] = FeeSchedule.LOW;
        opCodeFees[OpCodes.JUMPSUB] = FeeSchedule.HIGH;

        opCodeFees[OpCodes.POP] = FeeSchedule.BASE;
        opCodeFees[OpCodes.MLOAD] = FeeSchedule.VERYLOW;
        opCodeFees[OpCodes.MSTORE] = FeeSchedule.VERYLOW;
        opCodeFees[OpCodes.MSTORE8] = FeeSchedule.VERYLOW;
        opCodeFees[OpCodes.MSIZE] = FeeSchedule.BASE;
        opCodeFees[OpCodes.SLOAD] = FeeSchedule.SLOAD;

        opCodeFees[OpCodes.GASPRICE] = FeeSchedule.BASE;
        opCodeFees[OpCodes.EXTCODESIZE] = FeeSchedule.EXTCODESIZE;
        opCodeFees[OpCodes.EXTCODEHASH] = FeeSchedule.EXTCODEHASH;

        opCodeFees[OpCodes.GAS] = FeeSchedule.BASE;

        opCodeFees[OpCodes.RETURN & 0xff] = FeeSchedule.ZERO;

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

    public ExecutionResult execute(byte[] bytecodes) throws IOException {
        long gasUsed = 0;
        List<Log> logs = new ArrayList<>();
        int l = bytecodes.length;

        for (int pc = 0; pc < l; pc++) {
            byte bytecode = bytecodes[pc];

            FeeSchedule fee = opCodeFees[bytecode & 0xff];

            if (fee != null) {
                long gasCost = fee.getValue();

                if (gasUsed + gasCost > this.programEnvironment.getGas())
                    return ExecutionResult.ErrorException(this.programEnvironment.getGas(), new VirtualMachineException("Insufficient gas"));

                gasUsed += gasCost;
            }

            switch (bytecode) {
                case OpCodes.STOP:
                    return ExecutionResult.OkWithoutData(gasUsed, logs);

                case OpCodes.ADD:
                    DataWord word1 = this.dataStack.pop();
                    DataWord word2 = this.dataStack.pop();

                    this.dataStack.push(word1.add(word2));

                    break;

                case OpCodes.MUL:
                    word1 = this.dataStack.pop();
                    word2 = this.dataStack.pop();

                    this.dataStack.push(word1.mul(word2));

                    break;

                case OpCodes.SUB:
                    word1 = this.dataStack.pop();
                    word2 = this.dataStack.pop();

                    this.dataStack.push(word1.sub(word2));

                    break;

                case OpCodes.DIV:
                    word1 = this.dataStack.pop();
                    word2 = this.dataStack.pop();

                    if (word2.isZero())
                        this.dataStack.push(DataWord.ZERO);
                    else
                        this.dataStack.push(word1.div(word2));

                    break;

                case OpCodes.EXP:
                    word1 = this.dataStack.pop();
                    word2 = this.dataStack.pop();

                    this.dataStack.push(word1.exp(word2));

                    break;

                case OpCodes.SDIV:
                    word1 = this.dataStack.pop();
                    word2 = this.dataStack.pop();

                    if (word2.isZero())
                        this.dataStack.push(DataWord.ZERO);
                    else
                        this.dataStack.push(word1.sdiv(word2));

                    break;

                case OpCodes.MOD:
                    word1 = this.dataStack.pop();
                    word2 = this.dataStack.pop();

                    if (word2.isZero())
                        this.dataStack.push(DataWord.ZERO);
                    else
                        this.dataStack.push(word1.mod(word2));

                    break;

                case OpCodes.SMOD:
                    word1 = this.dataStack.pop();
                    word2 = this.dataStack.pop();

                    if (word2.isZero())
                        this.dataStack.push(DataWord.ZERO);
                    else
                        this.dataStack.push(word1.smod(word2));

                    break;

                case OpCodes.ADDMOD:
                    word1 = this.dataStack.pop();
                    word2 = this.dataStack.pop();
                    DataWord word3 = this.dataStack.pop();

                    if (word3.isZero())
                        this.dataStack.push(DataWord.ZERO);
                    else
                        this.dataStack.push(word1.add(word2).mod(word3));

                    break;

                case OpCodes.MULMOD:
                    word1 = this.dataStack.pop();
                    word2 = this.dataStack.pop();
                    word3 = this.dataStack.pop();

                    if (word3.isZero())
                        this.dataStack.push(DataWord.ZERO);
                    else
                        this.dataStack.push(word1.mul(word2).mod(word3));

                    break;

                case OpCodes.LT:
                    word1 = this.dataStack.pop();
                    word2 = this.dataStack.pop();

                    this.dataStack.push(word1.compareTo(word2) < 0 ? DataWord.ONE : DataWord.ZERO);

                    break;

                case OpCodes.GT:
                    word1 = this.dataStack.pop();
                    word2 = this.dataStack.pop();

                    this.dataStack.push(word1.compareTo(word2) > 0 ? DataWord.ONE : DataWord.ZERO);

                    break;

                case OpCodes.SLT:
                    word1 = this.dataStack.pop();
                    word2 = this.dataStack.pop();

                    this.dataStack.push(word1.compareToSigned(word2) < 0 ? DataWord.ONE : DataWord.ZERO);

                    break;

                case OpCodes.SGT:
                    word1 = this.dataStack.pop();
                    word2 = this.dataStack.pop();

                    this.dataStack.push(word1.compareToSigned(word2) > 0 ? DataWord.ONE : DataWord.ZERO);

                    break;

                case OpCodes.EQ:
                    word1 = this.dataStack.pop();
                    word2 = this.dataStack.pop();

                    this.dataStack.push(word1.compareTo(word2) == 0 ? DataWord.ONE : DataWord.ZERO);

                    break;

                case OpCodes.ISZERO:
                    DataWord word = this.dataStack.pop();

                    this.dataStack.push(word.isZero() ? DataWord.ONE : DataWord.ZERO);

                    break;

                case OpCodes.AND:
                    word1 = this.dataStack.pop();
                    word2 = this.dataStack.pop();

                    this.dataStack.push(word1.and(word2));

                    break;

                case OpCodes.OR:
                    word1 = this.dataStack.pop();
                    word2 = this.dataStack.pop();

                    this.dataStack.push(word1.or(word2));

                    break;

                case OpCodes.XOR:
                    word1 = this.dataStack.pop();
                    word2 = this.dataStack.pop();

                    this.dataStack.push(word1.xor(word2));

                    break;

                case OpCodes.NOT:
                    this.dataStack.push(this.dataStack.pop().not());

                    break;

                case OpCodes.BYTE:
                    word1 = this.dataStack.pop();
                    word2 = this.dataStack.pop();

                    int nbyte = word1.getBytes()[DataWord.DATAWORD_BYTES - 1] & 0xff;

                    if (word1.isUnsignedInteger() && nbyte < 32)
                        this.dataStack.push(DataWord.fromUnsignedInteger(word2.getBytes()[nbyte] & 0xff));
                    else
                        this.dataStack.push(DataWord.ZERO);

                    break;

                case OpCodes.SHL:
                    word1 = this.dataStack.pop();
                    word2 = this.dataStack.pop();

                    this.dataStack.push(word2.shiftLeft(word1));

                    break;

                case OpCodes.SHR:
                    word1 = this.dataStack.pop();
                    word2 = this.dataStack.pop();

                    this.dataStack.push(word2.shiftRight(word1));

                    break;

                case OpCodes.SAR:
                    word1 = this.dataStack.pop();
                    word2 = this.dataStack.pop();

                    this.dataStack.push(word2.shiftArithmeticRight(word1));

                    break;

                case OpCodes.ADDRESS:
                    this.dataStack.push(DataWord.fromAddress(this.programEnvironment.getAddress()));

                    break;

                case OpCodes.BALANCE:
                    this.dataStack.push(DataWord.fromCoin(this.programEnvironment.getBalance(this.dataStack.pop().toAddress())));

                    break;

                case OpCodes.CHAINID:
                    this.dataStack.push(DataWord.fromUnsignedInteger(this.programEnvironment.getChainId()));

                    break;

                case OpCodes.SELFBALANCE:
                    this.dataStack.push(DataWord.fromCoin(this.programEnvironment.getBalance(this.programEnvironment.getAddress())));

                    break;

                case OpCodes.ORIGIN:
                    this.dataStack.push(DataWord.fromAddress(this.programEnvironment.getOrigin()));

                    break;

                case OpCodes.CALLER:
                    this.dataStack.push(DataWord.fromAddress(this.programEnvironment.getCaller()));

                    break;

                case OpCodes.CALLVALUE:
                    this.dataStack.push(DataWord.fromCoin(this.programEnvironment.getValue()));

                    break;

                case OpCodes.CALLDATALOAD:
                    byte[] data = this.programEnvironment.getData();
                    int offset = this.dataStack.pop().asUnsignedInteger();

                    if (offset >= data.length)
                        this.dataStack.push(DataWord.ZERO);
                    else
                        this.dataStack.push(DataWord.fromBytesToLeft(data, offset, Math.min(DataWord.DATAWORD_BYTES, data.length - offset)));

                    break;

                case OpCodes.CALLDATASIZE:
                    this.dataStack.push(DataWord.fromUnsignedInteger(this.programEnvironment.getData().length));

                    break;

                case OpCodes.CALLDATACOPY:
                    data = this.programEnvironment.getData();
                    int targetOffset = this.dataStack.pop().asUnsignedInteger();
                    int sourceOffset = this.dataStack.pop().asUnsignedInteger();
                    int length = this.dataStack.pop().asUnsignedInteger();

                    this.memory.setBytes(targetOffset, data, sourceOffset, length);

                    break;

                case OpCodes.CODESIZE:
                    this.dataStack.push(DataWord.fromUnsignedInteger(bytecodes.length));

                    break;

                case OpCodes.CODECOPY:
                    targetOffset = this.dataStack.pop().asUnsignedInteger();
                    sourceOffset = this.dataStack.pop().asUnsignedInteger();
                    length = this.dataStack.pop().asUnsignedInteger();

                    this.memory.setBytes(targetOffset, bytecodes, sourceOffset, length);

                    break;

                case OpCodes.GASPRICE:
                    this.dataStack.push(DataWord.fromCoin(this.programEnvironment.getGasPrice()));

                    break;

                case OpCodes.EXTCODESIZE:
                    long codeLength = this.programEnvironment.getCodeLength(dataStack.pop().toAddress());
                    this.dataStack.push(DataWord.fromUnsignedLong(codeLength));

                    break;

                case OpCodes.EXTCODECOPY:
                    Address address = this.dataStack.pop().toAddress();
                    byte[] contractCode = this.programEnvironment.getCode(address);

                    // TODO check integer ranges
                    int to = this.dataStack.pop().asUnsignedInteger();
                    int from = this.dataStack.pop().asUnsignedInteger();
                    length = this.dataStack.pop().asUnsignedInteger();

                    if (contractCode == null)
                        contractCode = ByteUtils.EMPTY_BYTE_ARRAY;

                    this.memory.setBytes(to, contractCode, from, length);

                    break;

                case OpCodes.EXTCODEHASH:
                    Hash codeHash = this.programEnvironment.getCodeHash(dataStack.pop().toAddress());

                    if (codeHash == null)
                        codeHash = Hash.EMPTY_BYTES_HASH;

                    this.dataStack.push(DataWord.fromBytes(codeHash.getBytes()));

                    break;

                case OpCodes.COINBASE:
                    this.dataStack.push(DataWord.fromAddress(this.programEnvironment.getCoinbase()));

                    break;

                case OpCodes.TIMESTAMP:
                    this.dataStack.push(DataWord.fromUnsignedLong(this.programEnvironment.getTimestamp()));

                    break;

                case OpCodes.NUMBER:
                    this.dataStack.push(DataWord.fromUnsignedLong(this.programEnvironment.getNumber()));

                    break;

                case OpCodes.DIFFICULTY:
                    this.dataStack.push(this.programEnvironment.getDifficulty().toDataWord());

                    break;

                case OpCodes.GASLIMIT:
                    this.dataStack.push(DataWord.fromUnsignedLong(this.programEnvironment.getGasLimit()));

                    break;

                case OpCodes.POP:
                    this.dataStack.pop();

                    break;

                case OpCodes.MLOAD:
                    word1 = this.dataStack.pop();

                    this.dataStack.push(this.memory.getValue(word1.asUnsignedInteger()));

                    break;

                case OpCodes.MSTORE:
                    word1 = this.dataStack.pop();
                    word2 = this.dataStack.pop();

                    this.memory.setValue(word1.asUnsignedInteger(), word2);

                    break;

                case OpCodes.MSTORE8:
                    word1 = this.dataStack.pop();
                    word2 = this.dataStack.pop();

                    this.memory.setByte(word1.asUnsignedInteger(), word2.getBytes()[DataWord.DATAWORD_BYTES - 1]);

                    break;

                case OpCodes.SLOAD:
                    word1 = this.dataStack.pop();

                    this.dataStack.push(this.storage.getValue(word1));

                    break;

                case OpCodes.SSTORE:
                    if (this.programEnvironment.isReadOnly())
                        return ExecutionResult.ErrorException(this.programEnvironment.getGas(), new VirtualMachineException("Read-only message"));

                    word1 = this.dataStack.pop();
                    word2 = this.dataStack.pop();

                    DataWord originalValue = this.storage.getValue(word1);

                    long gasCost;

                    if (originalValue.isZero())
                        gasCost = FeeSchedule.SSET.getValue();
                    else
                        gasCost = FeeSchedule.SRESET.getValue();

                    if (gasUsed + gasCost > this.programEnvironment.getGas())
                        return ExecutionResult.ErrorException(this.programEnvironment.getGas(), new VirtualMachineException("Insufficient gas"));

                    gasUsed += gasCost;

                    // TODO Refund logic

                    this.storage.setValue(word1, word2);

                    break;

                case OpCodes.JUMP:
                    word = this.dataStack.pop();

                    try {
                        pc = getNewPc(bytecodes, word);
                    } catch (VirtualMachineException ex) {
                        return ExecutionResult.ErrorException(this.programEnvironment.getGas(), ex);
                    }

                    // TODO check JUMPDEST

                    break;

                case OpCodes.JUMPI:
                    word1 = this.dataStack.pop();
                    word2 = this.dataStack.pop();

                    if (word2.isZero())
                        break;

                    try {
                        pc = getNewPc(bytecodes, word1);
                    } catch (VirtualMachineException ex) {
                        return ExecutionResult.ErrorException(this.programEnvironment.getGas(), ex);
                    }

                    // TODO check JUMPDEST

                    break;

                case OpCodes.JUMPDEST:

                    break;

                case OpCodes.BEGINSUB:
                    return ExecutionResult.ErrorException(this.programEnvironment.getGas(), new VirtualMachineException("Invalid subroutine entry"));

                case OpCodes.RETURNSUB:
                    if (this.returnStack.isEmpty())
                        return ExecutionResult.ErrorException(this.programEnvironment.getGas(), new VirtualMachineException("Invalid retsub"));

                    // TODO check return stack is valid

                    pc = this.returnStack.pop();

                    break;

                case OpCodes.JUMPSUB:
                    this.returnStack.push(pc);

                    // TODO check stack top is a valid program counter
                    word = this.dataStack.pop();

                    if (!word.isUnsignedInteger())
                        return ExecutionResult.ErrorException(this.programEnvironment.getGas(), new VirtualMachineException("Invalid subroutine jump"));

                    pc = word.asUnsignedInteger();

                    if (pc >= bytecodes.length || bytecodes[pc] != OpCodes.BEGINSUB)
                        return ExecutionResult.ErrorException(this.programEnvironment.getGas(), new VirtualMachineException("Invalid subroutine jump"));

                    break;

                case OpCodes.PC:
                    this.dataStack.push(DataWord.fromUnsignedInteger(pc));

                    break;

                case OpCodes.MSIZE:
                    this.dataStack.push(DataWord.fromUnsignedInteger(this.memory.size()));

                    break;

                case OpCodes.GAS:
                    this.dataStack.push(DataWord.fromUnsignedLong(this.programEnvironment.getGas() - gasUsed));

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

                    this.dataStack.push(DataWord.fromBytes(bytecodes, pc + 1, lb));

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
                    this.dataStack.push(this.dataStack.get(this.dataStack.size() - 1 - (bytecode - OpCodes.DUP1)));

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
                    int size = this.dataStack.size();
                    offset = bytecode - OpCodes.SWAP1 + 1;

                    word1 = this.dataStack.get(size - 1);
                    word2 = this.dataStack.get(size - 1 - offset);

                    this.dataStack.set(size - 1, word2);
                    this.dataStack.set(size - 1 - offset, word1);

                    break;

                case OpCodes.LOG0:
                case OpCodes.LOG1:
                case OpCodes.LOG2:
                case OpCodes.LOG3:
                case OpCodes.LOG4:
                    offset = this.dataStack.pop().asUnsignedInteger();
                    length = this.dataStack.pop().asUnsignedInteger();

                    byte[] bytes = this.memory.getBytes(offset, length);
                    List<DataWord> topics = new ArrayList<>();

                    for (int k = 0; k < bytecode - OpCodes.LOG0; k++)
                        topics.add(this.dataStack.pop());

                    Log log = new Log(this.programEnvironment.getAddress(), bytes, topics);

                    logs.add(log);

                    break;

                case OpCodes.CALL:
                    // improve stack use

                    // TODO check gas as long
                    long gas = this.dataStack.pop().asUnsignedLong();
                    // TODO check is address
                    Address callee = this.dataStack.pop().toAddress();
                    Coin newValue = Coin.fromBytes(this.dataStack.pop().getBytes());

                    // TODO check they are an integer
                    int inputDataOffset = this.dataStack.pop().asUnsignedInteger();
                    int inputDataSize = this.dataStack.pop().asUnsignedInteger();
                    int outputDataOffset = this.dataStack.pop().asUnsignedInteger();
                    int outputDataSize = this.dataStack.pop().asUnsignedInteger();

                    ProgramEnvironment newProgramEnvironment = programEnvironment.createSubenvironment(
                            callee,
                            newValue,
                            gas,
                            null
                    );

                    byte[] newCode = programEnvironment.getCode(callee);

                    VirtualMachine newVirtualMachine = new VirtualMachine(newProgramEnvironment, null);

                    ExecutionResult executionResult = newVirtualMachine.execute(newCode);

                    this.memory.setBytes(outputDataOffset, executionResult.getReturnedData(), 0, outputDataSize);

                    continue;

                case OpCodes.RETURN:
                    offset = this.dataStack.pop().asUnsignedInteger();
                    length = this.dataStack.pop().asUnsignedInteger();

                    byte[] returnedData = this.memory.getBytes(offset, length);

                    return ExecutionResult.OkWithData(gasUsed, returnedData, logs);

                case OpCodes.REVERT:
                    offset = this.dataStack.pop().asUnsignedInteger();
                    length = this.dataStack.pop().asUnsignedInteger();

                    returnedData = this.memory.getBytes(offset, length);

                    return ExecutionResult.ErrorReverted(gasUsed, returnedData);

                default:
                    return ExecutionResult.ErrorException(this.programEnvironment.getGas(), new VirtualMachineException("Invalid opcode"));
            }
        }

        return ExecutionResult.OkWithoutData(gasUsed, logs);
    }

    private static int getNewPc(byte[] bytecodes, DataWord word1) throws VirtualMachineException {
        int newpc;

        if (!word1.isUnsignedInteger())
            throw new VirtualMachineException("Invalid jump");

        newpc = word1.asUnsignedInteger();

        if (newpc >= bytecodes.length || bytecodes[newpc] != OpCodes.JUMPDEST)
            throw new VirtualMachineException("Invalid jump");

        return newpc - 1;
    }

    public Stack<DataWord> getDataStack() {
        return this.dataStack;
    }

    public Memory getMemory() {
        return this.memory;
    }
}
