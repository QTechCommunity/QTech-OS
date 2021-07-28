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

import org.jnode.vm.classmgr.VmConstClass;
import org.jnode.vm.classmgr.VmConstFieldRef;
import org.jnode.vm.classmgr.VmConstIMethodRef;
import org.jnode.vm.classmgr.VmConstMethodRef;
import org.jnode.vm.classmgr.VmConstString;
import org.jnode.vm.classmgr.VmMethod;

/**
 * @author epr
 */
public abstract class BytecodeVisitorSupport extends BytecodeVisitor {

    private int instructionAddress = -1;
    private VmMethod method = null;
    private BytecodeParser parser;

    public void setParser(BytecodeParser parser) {
        this.parser = parser;
    }

    /**
     * @see BytecodeVisitor#endInstruction()
     */
    public void endInstruction() {
        this.instructionAddress = -1;
    }

    /**
     * @see BytecodeVisitor#endMethod()
     */
    public void endMethod() {
        this.method = null;
    }

    /**
     * @param address
     * @see BytecodeVisitor#startInstruction(int)
     */
    public void startInstruction(int address) {
        this.instructionAddress = address;
    }

    /**
     * @param method
     * @see BytecodeVisitor#startMethod(VmMethod)
     */
    public void startMethod(VmMethod method) {
        this.method = method;
    }

    /**
     * @see BytecodeVisitor#visit_aaload()
     */
    public void visit_aaload() {
    }

    /**
     * @see BytecodeVisitor#visit_aastore()
     */
    public void visit_aastore() {
    }

    /**
     * @see BytecodeVisitor#visit_aconst_null()
     */
    public void visit_aconst_null() {

    }

    /**
     * @param index
     * @see BytecodeVisitor#visit_aload(int)
     */
    public void visit_aload(int index) {

    }

    /**
     * @param clazz
     * @see BytecodeVisitor#visit_anewarray(VmConstClass)
     */
    public void visit_anewarray(VmConstClass clazz) {

    }

    /**
     * @see BytecodeVisitor#visit_areturn()
     */
    public void visit_areturn() {

    }

    /**
     * @see BytecodeVisitor#visit_arraylength()
     */
    public void visit_arraylength() {

    }

    /**
     * @param index
     * @see BytecodeVisitor#visit_astore(int)
     */
    public void visit_astore(int index) {

    }

    /**
     * @see BytecodeVisitor#visit_athrow()
     */
    public void visit_athrow() {

    }

    /**
     * @see BytecodeVisitor#visit_baload()
     */
    public void visit_baload() {

    }

    /**
     * @see BytecodeVisitor#visit_bastore()
     */
    public void visit_bastore() {

    }

    /**
     * @see BytecodeVisitor#visit_caload()
     */
    public void visit_caload() {

    }

    /**
     * @see BytecodeVisitor#visit_castore()
     */
    public void visit_castore() {

    }

    /**
     * @param clazz
     * @see BytecodeVisitor#visit_checkcast(VmConstClass)
     */
    public void visit_checkcast(VmConstClass clazz) {

    }

    /**
     * @see BytecodeVisitor#visit_d2f()
     */
    public void visit_d2f() {

    }

    /**
     * @see BytecodeVisitor#visit_d2i()
     */
    public void visit_d2i() {

    }

    /**
     * @see BytecodeVisitor#visit_d2l()
     */
    public void visit_d2l() {

    }

    /**
     * @see BytecodeVisitor#visit_dadd()
     */
    public void visit_dadd() {

    }

    /**
     * @see BytecodeVisitor#visit_daload()
     */
    public void visit_daload() {

    }

    /**
     * @see BytecodeVisitor#visit_dastore()
     */
    public void visit_dastore() {

    }

    /**
     * @see BytecodeVisitor#visit_dcmpg()
     */
    public void visit_dcmpg() {

    }

    /**
     * @see BytecodeVisitor#visit_dcmpl()
     */
    public void visit_dcmpl() {

    }

    /**
     * @param value
     * @see BytecodeVisitor#visit_dconst(double)
     */
    public void visit_dconst(double value) {

    }

    /**
     * @see BytecodeVisitor#visit_ddiv()
     */
    public void visit_ddiv() {

    }

    /**
     * @param index
     * @see BytecodeVisitor#visit_dload(int)
     */
    public void visit_dload(int index) {

    }

    /**
     * @see BytecodeVisitor#visit_dmul()
     */
    public void visit_dmul() {

    }

    /**
     * @see BytecodeVisitor#visit_dneg()
     */
    public void visit_dneg() {

    }

    /**
     * @see BytecodeVisitor#visit_drem()
     */
    public void visit_drem() {

    }

    /**
     * @see BytecodeVisitor#visit_dreturn()
     */
    public void visit_dreturn() {

    }

    /**
     * @param index
     * @see BytecodeVisitor#visit_dstore(int)
     */
    public void visit_dstore(int index) {

    }

    /**
     * @see BytecodeVisitor#visit_dsub()
     */
    public void visit_dsub() {

    }

    /**
     * @see BytecodeVisitor#visit_dup_x1()
     */
    public void visit_dup_x1() {

    }

    /**
     * @see BytecodeVisitor#visit_dup_x2()
     */
    public void visit_dup_x2() {

    }

    /**
     * @see BytecodeVisitor#visit_dup()
     */
    public void visit_dup() {

    }

    /**
     * @see BytecodeVisitor#visit_dup2_x1()
     */
    public void visit_dup2_x1() {

    }

    /**
     * @see BytecodeVisitor#visit_dup2_x2()
     */
    public void visit_dup2_x2() {

    }

    /**
     * @see BytecodeVisitor#visit_dup2()
     */
    public void visit_dup2() {

    }

    /**
     * @see BytecodeVisitor#visit_f2d()
     */
    public void visit_f2d() {

    }

    /**
     * @see BytecodeVisitor#visit_f2i()
     */
    public void visit_f2i() {

    }

    /**
     * @see BytecodeVisitor#visit_f2l()
     */
    public void visit_f2l() {

    }

    /**
     * @see BytecodeVisitor#visit_fadd()
     */
    public void visit_fadd() {

    }

    /**
     * @see BytecodeVisitor#visit_faload()
     */
    public void visit_faload() {

    }

    /**
     * @see BytecodeVisitor#visit_fastore()
     */
    public void visit_fastore() {

    }

    /**
     * @see BytecodeVisitor#visit_fcmpg()
     */
    public void visit_fcmpg() {

    }

    /**
     * @see BytecodeVisitor#visit_fcmpl()
     */
    public void visit_fcmpl() {

    }

    /**
     * @param value
     * @see BytecodeVisitor#visit_fconst(float)
     */
    public void visit_fconst(float value) {

    }

    /**
     * @see BytecodeVisitor#visit_fdiv()
     */
    public void visit_fdiv() {

    }

    /**
     * @param index
     * @see BytecodeVisitor#visit_fload(int)
     */
    public void visit_fload(int index) {

    }

    /**
     * @see BytecodeVisitor#visit_fmul()
     */
    public void visit_fmul() {

    }

    /**
     * @see BytecodeVisitor#visit_fneg()
     */
    public void visit_fneg() {

    }

    /**
     * @see BytecodeVisitor#visit_frem()
     */
    public void visit_frem() {

    }

    /**
     * @see BytecodeVisitor#visit_freturn()
     */
    public void visit_freturn() {

    }

    /**
     * @param index
     * @see BytecodeVisitor#visit_fstore(int)
     */
    public void visit_fstore(int index) {

    }

    /**
     * @see BytecodeVisitor#visit_fsub()
     */
    public void visit_fsub() {

    }

    /**
     * @param fieldRef
     * @see BytecodeVisitor#visit_getfield(VmConstFieldRef)
     */
    public void visit_getfield(VmConstFieldRef fieldRef) {

    }

    /**
     * @param fieldRef
     * @see BytecodeVisitor#visit_getstatic(VmConstFieldRef)
     */
    public void visit_getstatic(VmConstFieldRef fieldRef) {

    }

    /**
     * @param address
     * @see BytecodeVisitor#visit_goto(int)
     */
    public void visit_goto(int address) {

    }

    /**
     * @see BytecodeVisitor#visit_i2b()
     */
    public void visit_i2b() {

    }

    /**
     * @see BytecodeVisitor#visit_i2c()
     */
    public void visit_i2c() {

    }

    /**
     * @see BytecodeVisitor#visit_i2d()
     */
    public void visit_i2d() {

    }

    /**
     * @see BytecodeVisitor#visit_i2f()
     */
    public void visit_i2f() {

    }

    /**
     * @see BytecodeVisitor#visit_i2l()
     */
    public void visit_i2l() {

    }

    /**
     * @see BytecodeVisitor#visit_i2s()
     */
    public void visit_i2s() {

    }

    /**
     * @see BytecodeVisitor#visit_iadd()
     */
    public void visit_iadd() {

    }

    /**
     * @see BytecodeVisitor#visit_iaload()
     */
    public void visit_iaload() {

    }

    /**
     * @see BytecodeVisitor#visit_iand()
     */
    public void visit_iand() {

    }

    /**
     * @see BytecodeVisitor#visit_iastore()
     */
    public void visit_iastore() {

    }

    /**
     * @param value
     * @see BytecodeVisitor#visit_iconst(int)
     */
    public void visit_iconst(int value) {

    }

    /**
     * @see BytecodeVisitor#visit_idiv()
     */
    public void visit_idiv() {

    }

    /**
     * @param address
     * @see BytecodeVisitor#visit_if_acmpeq(int)
     */
    public void visit_if_acmpeq(int address) {

    }

    /**
     * @param address
     * @see BytecodeVisitor#visit_if_acmpne(int)
     */
    public void visit_if_acmpne(int address) {

    }

    /**
     * @param address
     * @see BytecodeVisitor#visit_if_icmpeq(int)
     */
    public void visit_if_icmpeq(int address) {

    }

    /**
     * @param address
     * @see BytecodeVisitor#visit_if_icmpge(int)
     */
    public void visit_if_icmpge(int address) {

    }

    /**
     * @param address
     * @see BytecodeVisitor#visit_if_icmpgt(int)
     */
    public void visit_if_icmpgt(int address) {

    }

    /**
     * @param address
     * @see BytecodeVisitor#visit_if_icmple(int)
     */
    public void visit_if_icmple(int address) {

    }

    /**
     * @param address
     * @see BytecodeVisitor#visit_if_icmplt(int)
     */
    public void visit_if_icmplt(int address) {

    }

    /**
     * @param address
     * @see BytecodeVisitor#visit_if_icmpne(int)
     */
    public void visit_if_icmpne(int address) {

    }

    /**
     * @param address
     * @see BytecodeVisitor#visit_ifeq(int)
     */
    public void visit_ifeq(int address) {

    }

    /**
     * @param address
     * @see BytecodeVisitor#visit_ifge(int)
     */
    public void visit_ifge(int address) {

    }

    /**
     * @param address
     * @see BytecodeVisitor#visit_ifgt(int)
     */
    public void visit_ifgt(int address) {

    }

    /**
     * @param address
     * @see BytecodeVisitor#visit_ifle(int)
     */
    public void visit_ifle(int address) {

    }

    /**
     * @param address
     * @see BytecodeVisitor#visit_iflt(int)
     */
    public void visit_iflt(int address) {

    }

    /**
     * @param address
     * @see BytecodeVisitor#visit_ifne(int)
     */
    public void visit_ifne(int address) {

    }

    /**
     * @param address
     * @see BytecodeVisitor#visit_ifnonnull(int)
     */
    public void visit_ifnonnull(int address) {

    }

    /**
     * @param address
     * @see BytecodeVisitor#visit_ifnull(int)
     */
    public void visit_ifnull(int address) {

    }

    /**
     * @param index
     * @param incValue
     * @see BytecodeVisitor#visit_iinc(int, int)
     */
    public void visit_iinc(int index, int incValue) {

    }

    /**
     * @param index
     * @see BytecodeVisitor#visit_iload(int)
     */
    public void visit_iload(int index) {

    }

    /**
     * @see BytecodeVisitor#visit_imul()
     */
    public void visit_imul() {

    }

    /**
     * @see BytecodeVisitor#visit_ineg()
     */
    public void visit_ineg() {

    }

    /**
     * @param clazz
     * @see BytecodeVisitor#visit_instanceof(VmConstClass)
     */
    public void visit_instanceof(VmConstClass clazz) {

    }

    /**
     * @param methodRef
     * @param count
     * @see BytecodeVisitor#visit_invokeinterface(VmConstIMethodRef, int)
     */
    public void visit_invokeinterface(VmConstIMethodRef methodRef, int count) {

    }

    /**
     * @param methodRef
     * @see BytecodeVisitor#visit_invokespecial(VmConstMethodRef)
     */
    public void visit_invokespecial(VmConstMethodRef methodRef) {

    }

    /**
     * @param methodRef
     * @see BytecodeVisitor#visit_invokestatic(VmConstMethodRef)
     */
    public void visit_invokestatic(VmConstMethodRef methodRef) {

    }

    /**
     * @param methodRef
     * @see BytecodeVisitor#visit_invokevirtual(VmConstMethodRef)
     */
    public void visit_invokevirtual(VmConstMethodRef methodRef) {

    }

    /**
     * @see BytecodeVisitor#visit_ior()
     */
    public void visit_ior() {

    }

    /**
     * @see BytecodeVisitor#visit_irem()
     */
    public void visit_irem() {

    }

    /**
     * @see BytecodeVisitor#visit_ireturn()
     */
    public void visit_ireturn() {

    }

    /**
     * @see BytecodeVisitor#visit_ishl()
     */
    public void visit_ishl() {

    }

    /**
     * @see BytecodeVisitor#visit_ishr()
     */
    public void visit_ishr() {

    }

    /**
     * @param index
     * @see BytecodeVisitor#visit_istore(int)
     */
    public void visit_istore(int index) {

    }

    /**
     * @see BytecodeVisitor#visit_isub()
     */
    public void visit_isub() {

    }

    /**
     * @see BytecodeVisitor#visit_iushr()
     */
    public void visit_iushr() {

    }

    /**
     * @see BytecodeVisitor#visit_ixor()
     */
    public void visit_ixor() {

    }

    /**
     * @param address
     * @see BytecodeVisitor#visit_jsr(int)
     */
    public void visit_jsr(int address) {

    }

    /**
     * @see BytecodeVisitor#visit_l2d()
     */
    public void visit_l2d() {

    }

    /**
     * @see BytecodeVisitor#visit_l2f()
     */
    public void visit_l2f() {

    }

    /**
     * @see BytecodeVisitor#visit_l2i()
     */
    public void visit_l2i() {

    }

    /**
     * @see BytecodeVisitor#visit_ladd()
     */
    public void visit_ladd() {

    }

    /**
     * @see BytecodeVisitor#visit_laload()
     */
    public void visit_laload() {

    }

    /**
     * @see BytecodeVisitor#visit_land()
     */
    public void visit_land() {

    }

    /**
     * @see BytecodeVisitor#visit_lastore()
     */
    public void visit_lastore() {

    }

    /**
     * @see BytecodeVisitor#visit_lcmp()
     */
    public void visit_lcmp() {

    }

    /**
     * @param value
     * @see BytecodeVisitor#visit_lconst(long)
     */
    public void visit_lconst(long value) {

    }

    /**
     * @param value
     * @see BytecodeVisitor#visit_ldc(VmConstString)
     */
    public void visit_ldc(VmConstString value) {

    }

    /**
     * @param value
     * @see BytecodeVisitor#visit_ldc(VmConstClass)
     */
    public void visit_ldc(VmConstClass value) {

    }

    /**
     * @see BytecodeVisitor#visit_ldiv()
     */
    public void visit_ldiv() {

    }

    /**
     * @param index
     * @see BytecodeVisitor#visit_lload(int)
     */
    public void visit_lload(int index) {

    }

    /**
     * @see BytecodeVisitor#visit_lmul()
     */
    public void visit_lmul() {

    }

    /**
     * @see BytecodeVisitor#visit_lneg()
     */
    public void visit_lneg() {

    }

    /**
     * @param defValue
     * @param matchValues
     * @param addresses
     * @see BytecodeVisitor#visit_lookupswitch(int, int[], int[])
     */
    public void visit_lookupswitch(int defValue, int[] matchValues, int[] addresses) {

    }

    /**
     * @see BytecodeVisitor#visit_lor()
     */
    public void visit_lor() {

    }

    /**
     * @see BytecodeVisitor#visit_lrem()
     */
    public void visit_lrem() {

    }

    /**
     * @see BytecodeVisitor#visit_lreturn()
     */
    public void visit_lreturn() {

    }

    /**
     * @see BytecodeVisitor#visit_lshl()
     */
    public void visit_lshl() {

    }

    /**
     * @see BytecodeVisitor#visit_lshr()
     */
    public void visit_lshr() {

    }

    /**
     * @param index
     * @see BytecodeVisitor#visit_lstore(int)
     */
    public void visit_lstore(int index) {

    }

    /**
     * @see BytecodeVisitor#visit_lsub()
     */
    public void visit_lsub() {

    }

    /**
     * @see BytecodeVisitor#visit_lushr()
     */
    public void visit_lushr() {

    }

    /**
     * @see BytecodeVisitor#visit_lxor()
     */
    public void visit_lxor() {

    }

    /**
     * @see BytecodeVisitor#visit_monitorenter()
     */
    public void visit_monitorenter() {

    }

    /**
     * @see BytecodeVisitor#visit_monitorexit()
     */
    public void visit_monitorexit() {

    }

    /**
     * @param clazz
     * @param dimensions
     * @see BytecodeVisitor#visit_multianewarray(VmConstClass, int)
     */
    public void visit_multianewarray(VmConstClass clazz, int dimensions) {

    }

    /**
     * @param clazz
     * @see BytecodeVisitor#visit_new(VmConstClass)
     */
    public void visit_new(VmConstClass clazz) {

    }

    /**
     * @param type
     * @see BytecodeVisitor#visit_newarray(int)
     */
    public void visit_newarray(int type) {

    }

    /**
     * @see BytecodeVisitor#visit_nop()
     */
    public void visit_nop() {

    }

    /**
     * @see BytecodeVisitor#visit_pop()
     */
    public void visit_pop() {

    }

    /**
     * @see BytecodeVisitor#visit_pop2()
     */
    public void visit_pop2() {

    }

    /**
     * @param fieldRef
     * @see BytecodeVisitor#visit_putfield(VmConstFieldRef)
     */
    public void visit_putfield(VmConstFieldRef fieldRef) {

    }

    /**
     * @param fieldRef
     * @see BytecodeVisitor#visit_putstatic(VmConstFieldRef)
     */
    public void visit_putstatic(VmConstFieldRef fieldRef) {

    }

    /**
     * @param index
     * @see BytecodeVisitor#visit_ret(int)
     */
    public void visit_ret(int index) {

    }

    /**
     * @see BytecodeVisitor#visit_return()
     */
    public void visit_return() {

    }

    /**
     * @see BytecodeVisitor#visit_saload()
     */
    public void visit_saload() {

    }

    /**
     * @see BytecodeVisitor#visit_sastore()
     */
    public void visit_sastore() {

    }

    /**
     * @see BytecodeVisitor#visit_swap()
     */
    public void visit_swap() {

    }

    /**
     * @param defValue
     * @param lowValue
     * @param highValue
     * @param addresses
     * @see BytecodeVisitor#visit_tableswitch(int, int, int, int[])
     */
    public void visit_tableswitch(int defValue, int lowValue, int highValue, int[] addresses) {

    }

    /**
     * Gets the address (PC) of the current instruction
     *
     * @return int
     */
    protected int getInstructionAddress() {
        return instructionAddress;
    }

    /**
     * Gets the currently visited method
     *
     * @return method
     */
    protected VmMethod getMethod() {
        return method;
    }

    /**
     * @return The parser
     */
    public final BytecodeParser getParser() {
        return this.parser;
    }

}
