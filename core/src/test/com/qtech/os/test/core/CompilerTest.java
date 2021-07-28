/*
 * $Id$
 *
 * Copyright (C) 2003-2014 QTech Community
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
 
package com.qtech.os.test.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import com.qtech.os.assembler.ObjectResolver;
import com.qtech.os.assembler.x86.X86TextAssembler;
import org.jnode.vm.VmImpl;
import org.jnode.vm.BaseVmArchitecture;
import org.jnode.vm.VmSystemClassLoader;
import org.jnode.vm.classmgr.VmMethod;
import org.jnode.vm.classmgr.VmType;
import org.jnode.vm.compiler.NativeCodeCompiler;
import org.jnode.vm.x86.VmX86Architecture;
import org.jnode.vm.x86.VmX86Architecture32;
import org.jnode.vm.x86.VmX86Architecture64;
import org.jnode.vm.x86.X86CpuID;
import org.jnode.vm.x86.compiler.l1a.X86Level1ACompiler;
import org.jnode.vm.x86.compiler.l2.X86Level2Compiler;

/**
 * Bytecode to native compiler test.
 * Runt this class in the root directory of the JNode source tree.
 *
 * @author epr
 */
public class CompilerTest {

    public static final String[] clsNames = {
//            "java.util.zip.ZipFile$PartialInputStream",
//            "java.security.SecureRandom",
//            "java.lang.Boolean",
//            "java.lang.Integer",
//            "java.lang.String",
        //"java.lang.StrictMath",
//            "java.lang.VMDouble",
//            "java.io.StringBufferInputStream",
//            "java.net.MimeTypeMapper",
//            "gnu.java.io.ObjectIdentityWrapper",
//            "gnu.java.io.encode.EncoderEightBitLookup",
//            "java.io.ObjectInputStream$3",
//            "java.io.PrintWriter",
//            "java.nio.ByteOrder",
//            "com.qtech.os.assembler.x86.X86BinaryAssembler",
//            "com.qtech.os.assembler.x86.X86BinaryAssembler$X86ObjectInfo",
//            "com.qtech.os.fs.ext2.Ext2FileSystem",
//            "com.qtech.os.vm.Unsafe$UnsafeObjectResolver",
//            "com.qtech.os.vm.MemoryBlockManager",
//            "com.qtech.os.vm.SoftByteCodes",
//            "com.qtech.os.vm.VmSystem",
//      "com.qtech.os.vm.VmStacReader",
//        "com.qtech.os.vm.classmgr.VmType",
//            "com.qtech.os.test.ArrayLongTest",
//        "com.qtech.os.test.ArrayTest",
        "org.jnode.vm.compiler.ir.PrimitiveTest",
//        "com.qtech.os.vm.compiler.ir.CompilerTest",
//        "com.qtech.os.games.tetris.Tetris",
//            "com.qtech.os.test.Linpack",
//            "com.qtech.os.test.MultiANewArrayTest",
//            "com.qtech.os.test.Sieve",
//            "com.qtech.os.test.PrimitiveIntTest",
//            "com.qtech.os.test.PrimitiveLongTest",
//            "com.qtech.os.test.PrimitiveFloatTest",
//            "com.qtech.os.test.PrimitiveDoubleTest", "com.qtech.os.test.InvokeTest",
//            "com.qtech.os.test.InvokeInterfaceTest",
//            "com.qtech.os.test.InvokeStaticTest",
//            "com.qtech.os.plugin.model.PluginJar",
//            "com.qtech.os.test.ArithOpt",
//            "com.qtech.os.test.InlineTestClass",
//            "com.qtech.os.test.CastTest",
//            "com.qtech.os.test.InstanceOfTest",
        //"com.qtech.os.test.ConvertTest",
//            "com.qtech.os.vm.MonitorManager",
//            "com.qtech.os.vm.memmgr.def.VmBootHeap",
//            "com.qtech.os.vm.classmgr.VmCP",
//            "java.util.zip.ZipInputStream",
//            "com.qtech.os.test.MagicWordTest",
//        "gnu.java.awt.color.SrgbConverter",
//           "gnu.classpath.SystemProperties",
//            "com.qtech.os.test.ArrayTest",
//        "com.qtech.os.test.IfNullTest",
//        "com.qtech.os.vm.bytecode.BytecodeParser",
    };

    public static void main(String[] args) throws Exception {

        final String processorId = System.getProperty("cpu", "p5");
        final String dir = System.getProperty("classes.dir", ".");
        final String archName = System.getProperty("arch", "x86");

        final VmX86Architecture arch;
        if (archName.equals("x86_64")) {
            arch = new VmX86Architecture64();
        } else {
            arch = new VmX86Architecture32();
        }
        final VmSystemClassLoader cl = new VmSystemClassLoader(new java.net.URL[]{
            new File("./core/build/classes/").getCanonicalFile().toURI().toURL(),
            new File("./distr/build/classes/").getCanonicalFile().toURI().toURL(),
            new URL("jar:" + new File("./all/lib/classlib.jar").getCanonicalFile().toURI().toURL() + "!/"),
        }, arch);

        final VmImpl vm = new VmImpl("?", arch, cl.getSharedStatics(), false, cl, null);
        vm.toString();
        VmType.initializeForBootImage(cl);
        System.out.println("Architecture: " + arch.getFullName());

        //final ObjectResolver resolver = new DummyResolver();
        final X86CpuID cpuId = X86CpuID.createID(processorId);
        //NativeCodeCompiler c = cs[0];
        final NativeCodeCompiler[] cs = {
            //new X86Level1Compiler(),
            new X86Level1ACompiler(),
            new X86Level2Compiler()
        };
        for (int i = 0; i < cs.length; i++) {
            cs[i].initialize(cl);
        }
        long times[] = new long[cs.length];
        int counts[] = new int[cs.length];

        for (int ci = 0; ci < cs.length; ci++) {
            final long start = System.currentTimeMillis();
            for (int k = 0; k < clsNames.length; k++) {
                final String clsName = clsNames[k];
                System.out.println("Compiling " + clsName);
                final VmType type = cl.loadClass(clsName, true);
                final int cnt = type.getNoDeclaredMethods();
                for (int i = 0; i < cnt; i++) {
                    final VmMethod method = type.getDeclaredMethod(i);
                    if (
                        "<init>".equals(method.getName()) ||
                        "main".equals(method.getName()) ||
                            !X86Level2Compiler.canCompile(method)
                        )
                        continue;

                    System.out.println("Compiling method " + clsName + "#" + method.getName());
                    counts[ci]++;
                    try {
                        compile(method, arch, cs[ci], cpuId, ci + 1);
                    } catch (Exception x) {
                        x.printStackTrace();
                    }
                }
            }
            final long end = System.currentTimeMillis();
            times[ci] += end - start;
        }
        for (int ci = 0; ci < cs.length; ci++) {
            System.out.println("Compiled " + counts[ci] + " methods using "
                + cs[ci].getName() + " in " + times[ci] + "ms");
        }
    }

    static void compile(VmMethod method, BaseVmArchitecture arch, NativeCodeCompiler c, X86CpuID cpuId,
                        int level) throws IOException {
        final String cname = method.getDeclaringClass().getName();
        final String mname = method.getName();
        final String fname = cname + "#"
            + mname.replace('<', '_').replace('>', '_') + "." + c.getName()
            + ".method";
        final File outDir = new File("./core/build/compiler-test");
        outDir.mkdirs();
        final FileOutputStream out = new FileOutputStream(new File(outDir, fname));

        try {
//            if (!method.isAbstract()) {
//                final PrintStream ps = new PrintStream(out);
//                BytecodeViewer viewer = new BytecodeViewer(
//                    new ControlFlowGraph(method.getBytecode()), ps);
//                BytecodeParser.parse(method.getBytecode(), viewer);
//                ps.flush();
//            }

            X86TextAssembler os = new X86TextAssembler(new OutputStreamWriter(out),
                cpuId, ((VmX86Architecture) arch).getMode(), method.getMangledName());
//            X86BinaryAssembler os = new X86BinaryAssembler(cpuId, ((VmX86Architecture) arch).getMode(), 0);
            try {
                c.compileBootstrap(method, os, level);
                //c.compileRuntime(method, resolver, level, os);
            } finally {
//                out.write(os.getBytes());
                os.flush();
            }
        } catch (Throwable ex) {
            System.out.println("Error in ### Method " + mname + " -> " + fname);
            ex.printStackTrace();
            System.exit(1);
        } finally {
            out.close();
        }
    }

    static class DummyResolver extends ObjectResolver {

        /**
         * @see com.qtech.os.assembler.ObjectResolver#addressOf32(java.lang.Object)
         */
        public int addressOf32(Object object) {
            return 0;
        }

        /**
         * @see com.qtech.os.assembler.ObjectResolver#addressOf64(java.lang.Object)
         */
        public long addressOf64(Object object) {
            return 0L;
        }
    }
}
