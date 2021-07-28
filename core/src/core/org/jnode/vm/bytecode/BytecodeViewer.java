/*
 * $Id$
 *
 * Copyright (C) 2003-2015 QTech Community
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; If not, write to the Free Software Foundation, Inc., 
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
 
package org.jnode.vm.bytecode;

import java.io.PrintStream;
import org.jnode.vm.classmgr.VmConstClass;
import org.jnode.vm.classmgr.VmConstFieldRef;
import org.jnode.vm.classmgr.VmConstIMethodRef;
import org.jnode.vm.classmgr.VmConstMethodRef;
import org.jnode.vm.classmgr.VmConstString;
import org.jnode.vm.classmgr.VmMethod;
import org.jnode.vm.classmgr.VmType;

/**
 * <description>
 *
 * @author epr
 */
public class BytecodeViewer extends BytecodeVisitor {

    private int address;
    private ControlFlowGraph cfg;
    private String indent = "";
    private final PrintStream out;

    /**
     * @param parser
     * @see BytecodeVisitor#setParser(BytecodeParser)
     */
    public void setParser(BytecodeParser parser) {
    }

    /**
     * Constructor for BytecodeViewer.
     */
    public BytecodeViewer() {
        this(null, System.out);
    }

    /**
     * Constructor for BytecodeViewer.
     */
    public BytecodeViewer(PrintStream out) {
        this(null, out);
    }

    /**
     * Constructor for BytecodeViewer.
     *
     * @param cfg
     */
    public BytecodeViewer(ControlFlowGraph cfg) {
        this(cfg, System.out);
    }

    /**
     * Constructor for BytecodeViewer.
     *
     * @param cfg
     */
    public BytecodeViewer(ControlFlowGraph cfg, PrintStream out) {
        this.cfg = cfg;
        this.out = out;
    }

    /**
     * @param method
     * @see BytecodeVisitor#startMethod(VmMethod)
     */
    public void startMethod(VmMethod method) {
        out.println("Method: " + method.getName() + ", #locals " + method.getBytecode().getNoLocals());
    }

    /**
     * @see BytecodeVisitor#endMethod()
     */
    public void endMethod() {
        out.println("end\n");
    }

    /**
     * @param address
     * @see BytecodeVisitor#startInstruction(int)
     */
    public void startInstruction(int address) {
        this.address = address;
        if (cfg != null) {
            final BasicBlock bb = cfg.getBasicBlock(address);
            if (bb.getStartPC() == address) {
                out("-- Start of Basic Block " + bb + " --");
            }
        }
    }

    /**
     * @see BytecodeVisitor#endInstruction()
     */
    public void endInstruction() {
    }

    /**
     * @see BytecodeVisitor#visit_nop()
     */
    public void visit_nop() {
        out("nop");
    }

    /**
     * @see BytecodeVisitor#visit_aconst_null()
     */
    public void visit_aconst_null() {
        out("aconst_null");
    }

    /**
     * @param value
     * @see BytecodeVisitor#visit_iconst(int)
     */
    public void visit_iconst(int value) {
        out("iconst " + value);
    }

    /**
     * @param value
     * @see BytecodeVisitor#visit_lconst(long)
     */
    public void visit_lconst(long value) {
        out("lconst " + value);
    }

    /**
     * @param value
     * @see BytecodeVisitor#visit_fconst(float)
     */
    public void visit_fconst(float value) {
        out("fconst " + value);
    }

    /**
     * @param value
     * @see BytecodeVisitor#visit_dconst(double)
     */
    public void visit_dconst(double value) {
        out("dconst " + value);
    }

    /**
     * @param value
     * @see BytecodeVisitor#visit_ldc(VmConstString)
     */
    public void visit_ldc(VmConstString value) {
        out("ldc " + value);
    }

    /**
     * @param value
     * @see BytecodeVisitor#visit_ldc(VmConstClass)
     */
    public void visit_ldc(VmConstClass value) {
        out("ldc " + value);
    }

    /**
     * @param value
     * @see BytecodeVisitor#visit_ldc(VmConstClass)
     */
    public void visit_ldc(VmType<?> value) {
        out("ldc-type " + value.getName());
    }

    /**
     * @param index
     * @see BytecodeVisitor#visit_iload(int)
     */
    public void visit_iload(int index) {
        out("iload " + index);
    }

    /**
     * @param index
     * @see BytecodeVisitor#visit_lload(int)
     */
    public void visit_lload(int index) {
        out("lload " + index);
    }

    /**
     * @param index
     * @see BytecodeVisitor#visit_fload(int)
     */
    public void visit_fload(int index) {
        out("fload " + index);
    }

    /**
     * @param index
     * @see BytecodeVisitor#visit_dload(int)
     */
    public void visit_dload(int index) {
        out("dload " + index);
    }

    /**
     * @param index
     * @see BytecodeVisitor#visit_aload(int)
     */
    public void visit_aload(int index) {
        out("aload " + index);
    }

    /**
     * @see BytecodeVisitor#visit_iaload()
     */
    public void visit_iaload() {
        out("iaload");
    }

    /**
     * @see BytecodeVisitor#visit_laload()
     */
    public void visit_laload() {
        out("laload");
    }

    /**
     * @see BytecodeVisitor#visit_faload()
     */
    public void visit_faload() {
        out("faload");
    }

    /**
     * @see BytecodeVisitor#visit_daload()
     */
    public void visit_daload() {
        out("daload");
    }

    /**
     * @see BytecodeVisitor#visit_aaload()
     */
    public void visit_aaload() {
        out("aaload");
    }

    /**
     * @see BytecodeVisitor#visit_baload()
     */
    public void visit_baload() {
        out("baload");
    }

    /**
     * @see BytecodeVisitor#visit_caload()
     */
    public void visit_caload() {
        out("caload");
    }

    /**
     * @see BytecodeVisitor#visit_saload()
     */
    public void visit_saload() {
        out("saload");
    }

    /**
     * @param index
     * @see BytecodeVisitor#visit_istore(int)
     */
    public void visit_istore(int index) {
        out("istore " + index);
    }

    /**
     * @param index
     * @see BytecodeVisitor#visit_lstore(int)
     */
    public void visit_lstore(int index) {
        out("lstore " + index);
    }

    /**
     * @param index
     * @see BytecodeVisitor#visit_fstore(int)
     */
    public void visit_fstore(int index) {
        out("fstore " + index);
    }

    /**
     * @param index
     * @see BytecodeVisitor#visit_dstore(int)
     */
    public void visit_dstore(int index) {
        out("dstore " + index);
    }

    /**
     * @param index
     * @see BytecodeVisitor#visit_astore(int)
     */
    public void visit_astore(int index) {
        out("astore " + index);
    }

    /**
     * @see BytecodeVisitor#visit_iastore()
     */
    public void visit_iastore() {
        out("iastore");
    }

    /**
     * @see BytecodeVisitor#visit_lastore()
     */
    public void visit_lastore() {
        out("lastore");
    }

    /**
     * @see BytecodeVisitor#visit_fastore()
     */
    public void visit_fastore() {
        out("fastore");
    }

    /**
     * @see BytecodeVisitor#visit_dastore()
     */
    public void visit_dastore() {
        out("dastore");
    }

    /**
     * @see BytecodeVisitor#visit_aastore()
     */
    public void visit_aastore() {
        out("aastore");
    }

    /**
     * @see BytecodeVisitor#visit_bastore()
     */
    public void visit_bastore() {
        out("bastore");
    }

    /**
     * @see BytecodeVisitor#visit_castore()
     */
    public void visit_castore() {
        out("castore");
    }

    /**
     * @see BytecodeVisitor#visit_sastore()
     */
    public void visit_sastore() {
        out("sastore");
    }

    /**
     * @see BytecodeVisitor#visit_pop()
     */
    public void visit_pop() {
        out("pop");
    }

    /**
     * @see BytecodeVisitor#visit_pop2()
     */
    public void visit_pop2() {
        out("pop2");
    }

    /**
     * @see BytecodeVisitor#visit_dup()
     */
    public void visit_dup() {
        out("dup");
    }

    /**
     * @see BytecodeVisitor#visit_dup_x1()
     */
    public void visit_dup_x1() {
        out("dup_x1");
    }

    /**
     * @see BytecodeVisitor#visit_dup_x2()
     */
    public void visit_dup_x2() {
        out("dup_x2");
    }

    /**
     * @see BytecodeVisitor#visit_dup2()
     */
    public void visit_dup2() {
        out("dup2");
    }

    /**
     * @see BytecodeVisitor#visit_dup2_x1()
     */
    public void visit_dup2_x1() {
        out("dup2_x1");
    }

    /**
     * @see BytecodeVisitor#visit_dup2_x2()
     */
    public void visit_dup2_x2() {
        out("dup2_x2");
    }

    /**
     * @see BytecodeVisitor#visit_swap()
     */
    public void visit_swap() {
        out("swap");
    }

    /**
     * @see BytecodeVisitor#visit_iadd()
     */
    public void visit_iadd() {
        out("iadd");
    }

    /**
     * @see BytecodeVisitor#visit_ladd()
     */
    public void visit_ladd() {
        out("ladd");
    }

    /**
     * @see BytecodeVisitor#visit_fadd()
     */
    public void visit_fadd() {
        out("fadd");
    }

    /**
     * @see BytecodeVisitor#visit_dadd()
     */
    public void visit_dadd() {
        out("dadd");
    }

    /**
     * @see BytecodeVisitor#visit_isub()
     */
    public void visit_isub() {
        out("isub");
    }

    /**
     * @see BytecodeVisitor#visit_lsub()
     */
    public void visit_lsub() {
        out("lsub");
    }

    /**
     * @see BytecodeVisitor#visit_fsub()
     */
    public void visit_fsub() {
        out("fsub");
    }

    /**
     * @see BytecodeVisitor#visit_dsub()
     */
    public void visit_dsub() {
        out("dsub");
    }

    /**
     * @see BytecodeVisitor#visit_imul()
     */
    public void visit_imul() {
        out("imul");
    }

    /**
     * @see BytecodeVisitor#visit_lmul()
     */
    public void visit_lmul() {
        out("lmul");
    }

    /**
     * @see BytecodeVisitor#visit_fmul()
     */
    public void visit_fmul() {
        out("fmul");
    }

    /**
     * @see BytecodeVisitor#visit_dmul()
     */
    public void visit_dmul() {
        out("dmul");
    }

    /**
     * @see BytecodeVisitor#visit_idiv()
     */
    public void visit_idiv() {
        out("idiv");
    }

    /**
     * @see BytecodeVisitor#visit_ldiv()
     */
    public void visit_ldiv() {
        out("ldiv");
    }

    /**
     * @see BytecodeVisitor#visit_fdiv()
     */
    public void visit_fdiv() {
        out("fdiv");
    }

    /**
     * @see BytecodeVisitor#visit_ddiv()
     */
    public void visit_ddiv() {
        out("ddiv");
    }

    /**
     * @see BytecodeVisitor#visit_irem()
     */
    public void visit_irem() {
        out("irem");
    }

    /**
     * @see BytecodeVisitor#visit_lrem()
     */
    public void visit_lrem() {
        out("lrem");
    }

    /**
     * @see BytecodeVisitor#visit_frem()
     */
    public void visit_frem() {
        out("frem");
    }

    /**
     * @see BytecodeVisitor#visit_drem()
     */
    public void visit_drem() {
        out("drem");
    }

    /**
     * @see BytecodeVisitor#visit_ineg()
     */
    public void visit_ineg() {
        out("ineg");
    }

    /**
     * @see BytecodeVisitor#visit_lneg()
     */
    public void visit_lneg() {
        out("lneg");
    }

    /**
     * @see BytecodeVisitor#visit_fneg()
     */
    public void visit_fneg() {
        out("fneg");
    }

    /**
     * @see BytecodeVisitor#visit_dneg()
     */
    public void visit_dneg() {
        out("dneg");
    }

    /**
     * @see BytecodeVisitor#visit_ishl()
     */
    public void visit_ishl() {
        out("ishl");
    }

    /**
     * @see BytecodeVisitor#visit_lshl()
     */
    public void visit_lshl() {
        out("lshl");
    }

    /**
     * @see BytecodeVisitor#visit_ishr()
     */
    public void visit_ishr() {
        out("ishr");
    }

    /**
     * @see BytecodeVisitor#visit_lshr()
     */
    public void visit_lshr() {
        out("lshr");
    }

    /**
     * @see BytecodeVisitor#visit_iushr()
     */
    public void visit_iushr() {
        out("iushr");
    }

    /**
     * @see BytecodeVisitor#visit_lushr()
     */
    public void visit_lushr() {
        out("lushr");
    }

    /**
     * @see BytecodeVisitor#visit_iand()
     */
    public void visit_iand() {
        out("iand");
    }

    /**
     * @see BytecodeVisitor#visit_land()
     */
    public void visit_land() {
        out("land");
    }

    /**
     * @see BytecodeVisitor#visit_ior()
     */
    public void visit_ior() {
        out("ior");
    }

    /**
     * @see BytecodeVisitor#visit_lor()
     */
    public void visit_lor() {
        out("lor");
    }

    /**
     * @see BytecodeVisitor#visit_ixor()
     */
    public void visit_ixor() {
        out("ixor");
    }

    /**
     * @see BytecodeVisitor#visit_lxor()
     */
    public void visit_lxor() {
        out("lxor");
    }

    /**
     * @param index
     * @param incValue
     * @see BytecodeVisitor#visit_iinc(int, int)
     */
    public void visit_iinc(int index, int incValue) {
        out("iinc index=" + index + ", incr=" + incValue);
    }

    /**
     * @see BytecodeVisitor#visit_i2l()
     */
    public void visit_i2l() {
        out("i2l");
    }

    /**
     * @see BytecodeVisitor#visit_i2f()
     */
    public void visit_i2f() {
        out("i2f");
    }

    /**
     * @see BytecodeVisitor#visit_i2d()
     */
    public void visit_i2d() {
        out("i2d");
    }

    /**
     * @see BytecodeVisitor#visit_l2i()
     */
    public void visit_l2i() {
        out("l2i");
    }

    /**
     * @see BytecodeVisitor#visit_l2f()
     */
    public void visit_l2f() {
        out("l2f");
    }

    /**
     * @see BytecodeVisitor#visit_l2d()
     */
    public void visit_l2d() {
        out("l2d");
    }

    /**
     * @see BytecodeVisitor#visit_f2i()
     */
    public void visit_f2i() {
        out("f2i");
    }

    /**
     * @see BytecodeVisitor#visit_f2l()
     */
    public void visit_f2l() {
        out("f2l");
    }

    /**
     * @see BytecodeVisitor#visit_f2d()
     */
    public void visit_f2d() {
        out("f2d");
    }

    /**
     * @see BytecodeVisitor#visit_d2i()
     */
    public void visit_d2i() {
        out("d2i");
    }

    /**
     * @see BytecodeVisitor#visit_d2l()
     */
    public void visit_d2l() {
        out("d2l");
    }

    /**
     * @see BytecodeVisitor#visit_d2f()
     */
    public void visit_d2f() {
        out("d2f");
    }

    /**
     * @see BytecodeVisitor#visit_i2b()
     */
    public void visit_i2b() {
        out("i2b");
    }

    /**
     * @see BytecodeVisitor#visit_i2c()
     */
    public void visit_i2c() {
        out("i2c");
    }

    /**
     * @see BytecodeVisitor#visit_i2s()
     */
    public void visit_i2s() {
        out("i2s");
    }

    /**
     * @see BytecodeVisitor#visit_lcmp()
     */
    public void visit_lcmp() {
        out("lcmp");
    }

    /**
     * @see BytecodeVisitor#visit_fcmpl()
     */
    public void visit_fcmpl() {
        out("fcmpl");
    }

    /**
     * @see BytecodeVisitor#visit_fcmpg()
     */
    public void visit_fcmpg() {
        out("fcmpg");
    }

    /**
     * @see BytecodeVisitor#visit_dcmpl()
     */
    public void visit_dcmpl() {
        out("dcmpl");
    }

    /**
     * @see BytecodeVisitor#visit_dcmpg()
     */
    public void visit_dcmpg() {
        out("dcmpg");
    }

    /**
     * @param address
     * @see BytecodeVisitor#visit_ifeq(int)
     */
    public void visit_ifeq(int address) {
        out("ifeq " + address);
    }

    /**
     * @param address
     * @see BytecodeVisitor#visit_ifne(int)
     */
    public void visit_ifne(int address) {
        out("ifne " + address);
    }

    /**
     * @param address
     * @see BytecodeVisitor#visit_iflt(int)
     */
    public void visit_iflt(int address) {
        out("iflt " + address);
    }

    /**
     * @param address
     * @see BytecodeVisitor#visit_ifge(int)
     */
    public void visit_ifge(int address) {
        out("ifge " + address);
    }

    /**
     * @param address
     * @see BytecodeVisitor#visit_ifgt(int)
     */
    public void visit_ifgt(int address) {
        out("ifgt " + address);
    }

    /**
     * @param address
     * @see BytecodeVisitor#visit_ifle(int)
     */
    public void visit_ifle(int address) {
        out("ifle " + address);
    }

    /**
     * @param address
     * @see BytecodeVisitor#visit_if_icmpeq(int)
     */
    public void visit_if_icmpeq(int address) {
        out("if_icmpeq " + address);
    }

    /**
     * @param address
     * @see BytecodeVisitor#visit_if_icmpne(int)
     */
    public void visit_if_icmpne(int address) {
        out("if_icmpne " + address);
    }

    /**
     * @param address
     * @see BytecodeVisitor#visit_if_icmplt(int)
     */
    public void visit_if_icmplt(int address) {
        out("if_icmplt " + address);
    }

    /**
     * @param address
     * @see BytecodeVisitor#visit_if_icmpge(int)
     */
    public void visit_if_icmpge(int address) {
        out("if_icmpge " + address);
    }

    /**
     * @param address
     * @see BytecodeVisitor#visit_if_icmpgt(int)
     */
    public void visit_if_icmpgt(int address) {
        out("if_icmpgt " + address);
    }

    /**
     * @param address
     * @see BytecodeVisitor#visit_if_icmple(int)
     */
    public void visit_if_icmple(int address) {
        out("if_icmple " + address);
    }

    /**
     * @param address
     * @see BytecodeVisitor#visit_if_acmpeq(int)
     */
    public void visit_if_acmpeq(int address) {
        out("if_acmpeq " + address);
    }

    /**
     * @param address
     * @see BytecodeVisitor#visit_if_acmpne(int)
     */
    public void visit_if_acmpne(int address) {
        out("if_acmpne " + address);
    }

    /**
     * @param address
     * @see BytecodeVisitor#visit_goto(int)
     */
    public void visit_goto(int address) {
        out("goto " + address);
    }

    /**
     * @param address
     * @see BytecodeVisitor#visit_jsr(int)
     */
    public void visit_jsr(int address) {
        out("jsr " + address);
    }

    /**
     * @param index
     * @see BytecodeVisitor#visit_ret(int)
     */
    public void visit_ret(int index) {
        out("ret " + index);
    }

    /**
     * @param defValue
     * @param lowValue
     * @param highValue
     * @param addresses
     * @see BytecodeVisitor#visit_tableswitch(int, int, int, int[])
     */
    public void visit_tableswitch(int defValue, int lowValue, int highValue, int[] addresses) {
        out("tableswitch def=" + defValue);
        for (int i = 0; i < addresses.length; i++) {
            out("\t" + (lowValue + i) + "\t-> " + addresses[i]);
        }
    }

    /**
     * @param defValue
     * @param matchValues
     * @param addresses
     * @see BytecodeVisitor#visit_lookupswitch(int, int[], int[])
     */
    public void visit_lookupswitch(int defValue, int[] matchValues, int[] addresses) {
        out("lookupswitch def=" + defValue);
        for (int i = 0; i < addresses.length; i++) {
            out("\t" + matchValues[i] + "\t-> " + addresses[i]);
        }
    }

    /**
     * @see BytecodeVisitor#visit_ireturn()
     */
    public void visit_ireturn() {
        out("ireturn");
    }

    /**
     * @see BytecodeVisitor#visit_lreturn()
     */
    public void visit_lreturn() {
        out("lreturn");
    }

    /**
     * @see BytecodeVisitor#visit_freturn()
     */
    public void visit_freturn() {
        out("freturn");
    }

    /**
     * @see BytecodeVisitor#visit_dreturn()
     */
    public void visit_dreturn() {
        out("dreturn");
    }

    /**
     * @see BytecodeVisitor#visit_areturn()
     */
    public void visit_areturn() {
        out("areturn");
    }

    /**
     * @see BytecodeVisitor#visit_return()
     */
    public void visit_return() {
        out("return");
    }

    /**
     * @param fieldRef
     * @see BytecodeVisitor#visit_getstatic(VmConstFieldRef)
     */
    public void visit_getstatic(VmConstFieldRef fieldRef) {
        out("getstatic " + fieldRef);
    }

    /**
     * @param fieldRef
     * @see BytecodeVisitor#visit_putstatic(VmConstFieldRef)
     */
    public void visit_putstatic(VmConstFieldRef fieldRef) {
        out("putstatic " + fieldRef);
    }

    /**
     * @param fieldRef
     * @see BytecodeVisitor#visit_getfield(VmConstFieldRef)
     */
    public void visit_getfield(VmConstFieldRef fieldRef) {
        out("getfield " + fieldRef);
    }

    /**
     * @param fieldRef
     * @see BytecodeVisitor#visit_putfield(VmConstFieldRef)
     */
    public void visit_putfield(VmConstFieldRef fieldRef) {
        out("putfield " + fieldRef);
    }

    /**
     * @param methodRef
     * @see BytecodeVisitor#visit_invokevirtual(VmConstMethodRef)
     */
    public void visit_invokevirtual(VmConstMethodRef methodRef) {
        out("invokevirtual " + methodRef);
    }

    /**
     * @param methodRef
     * @see BytecodeVisitor#visit_invokespecial(VmConstMethodRef)
     */
    public void visit_invokespecial(VmConstMethodRef methodRef) {
        out("invokespecial " + methodRef);
    }

    /**
     * @param methodRef
     * @see BytecodeVisitor#visit_invokestatic(VmConstMethodRef)
     */
    public void visit_invokestatic(VmConstMethodRef methodRef) {
        out("invokestatic " + methodRef);
    }

    /**
     * @param methodRef
     * @param count
     * @see BytecodeVisitor#visit_invokeinterface(VmConstIMethodRef, int)
     */
    public void visit_invokeinterface(VmConstIMethodRef methodRef, int count) {
        out("invokeinterface " + methodRef + ", count=" + count);
    }

    /**
     * @param clazz
     * @see BytecodeVisitor#visit_new(VmConstClass)
     */
    public void visit_new(VmConstClass clazz) {
        out("new " + clazz);
    }

    /**
     * @param type
     * @see BytecodeVisitor#visit_newarray(int)
     */
    public void visit_newarray(int type) {
        out("newarray " + type);
    }

    /**
     * @param clazz
     * @see BytecodeVisitor#visit_anewarray(VmConstClass)
     */
    public void visit_anewarray(VmConstClass clazz) {
        out("anewarray " + clazz);
    }

    /**
     * @see BytecodeVisitor#visit_arraylength()
     */
    public void visit_arraylength() {
        out("arraylength");
    }

    /**
     * @see BytecodeVisitor#visit_athrow()
     */
    public void visit_athrow() {
        out("athrow");
    }

    /**
     * @param clazz
     * @see BytecodeVisitor#visit_checkcast(VmConstClass)
     */
    public void visit_checkcast(VmConstClass clazz) {
        out("checkcast " + clazz);
    }

    /**
     * @param clazz
     * @see BytecodeVisitor#visit_instanceof(VmConstClass)
     */
    public void visit_instanceof(VmConstClass clazz) {
        out("instanceof " + clazz);
    }

    /**
     * @see BytecodeVisitor#visit_monitorenter()
     */
    public void visit_monitorenter() {
        out("monitorenter");
    }

    /**
     * @see BytecodeVisitor#visit_monitorexit()
     */
    public void visit_monitorexit() {
        out("monitorexit");
    }

    /**
     * @param clazz
     * @param dimensions
     * @see BytecodeVisitor#visit_multianewarray(VmConstClass, int)
     */
    public void visit_multianewarray(VmConstClass clazz, int dimensions) {
        out("multianewarray " + clazz + ' ' + dimensions);
    }

    /**
     * @param address
     * @see BytecodeVisitor#visit_ifnull(int)
     */
    public void visit_ifnull(int address) {
        out("ifnull " + address);
    }

    /**
     * @param address
     * @see BytecodeVisitor#visit_ifnonnull(int)
     */
    public void visit_ifnonnull(int address) {
        out("ifnonnull " + address);
    }

    public void out(String line) {
        out.print(indent);
        out.print(address);
        out.print(":\t");
        out.println(line);
    }

    public void indent() {
        indent += "\t";
    }

    public void unindent() {
        indent = indent.substring(0, indent.length() - 1);
    }

    protected void out(Object obj) {
        String str = obj.toString();
        out(str);
    }
}
