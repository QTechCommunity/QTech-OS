/*
 * $Id$
 *
 * Copyright (C) 2020-2022 Ultreon Team
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
 
package org.jnode.test.fs.filesystem;

import org.jnode.test.fs.ext4.Ext4FileSystemTest;
import org.jnode.test.fs.filesystem.tests.BasicFSTest;
import org.jnode.test.fs.filesystem.tests.ConcurrentAccessFSTest;
import org.jnode.test.fs.filesystem.tests.FileFSTest;
import org.jnode.test.fs.filesystem.tests.TreeFSTest;
import org.jnode.test.fs.ntfs.NTFSFileSystemTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * This class runs a suite of functional tests on the UltreonOS file system
 * implementation.  The tests are designed to be run from the UltreonOS development
 * sandbox.  The UltreonOS core project needs to be on the bootclasspath to avoid
 * classloader security problems.
 *
 * @author Fabien DUMINY
 * @author crawley@jnode.org
 */
@RunWith(Suite.class)
@SuiteClasses({
    BasicFSTest.class,
    ConcurrentAccessFSTest.class,
    FileFSTest.class,
    TreeFSTest.class,
    /*FileSystemManagerTest.class,*/
    Ext4FileSystemTest.class,
    NTFSFileSystemTest.class,
}
)
public class FSTestSuite {

} 

