/*
 * $Id$
 *
 * Copyright (C) 2003-2013 QTech Community
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
 
package com.qtech.os.fs.hfsplus.command;

import com.qtech.os.fs.Formatter;
import com.qtech.os.fs.command.AbstractFormatCommand;
import com.qtech.os.fs.hfsplus.HFSPlusParams;
import com.qtech.os.fs.hfsplus.HfsPlusFileSystem;
import com.qtech.os.fs.hfsplus.HfsPlusFileSystemFormatter;
import com.qtech.os.shell.syntax.Argument;
import com.qtech.os.shell.syntax.StringArgument;

public class FormatHfsPlusCommand extends AbstractFormatCommand<HfsPlusFileSystem> {

    private final StringArgument ARG_VOLUME_NAME = new StringArgument("volumeName", Argument.OPTIONAL,
            "set volume name");

    public FormatHfsPlusCommand() {
        super("Format a block device with HFS+ filesystem");
        registerArguments(ARG_VOLUME_NAME);
    }

    public static void main(String[] args)
        throws Exception {
        new FormatHfsPlusCommand().execute(args);
    }

    @Override
    protected Formatter<HfsPlusFileSystem> getFormatter() {
        HFSPlusParams params = new HFSPlusParams();
        params.setVolumeName((ARG_VOLUME_NAME.isSet()) ? ARG_VOLUME_NAME.getValue() : "untitled");
        params.setBlockSize(HFSPlusParams.OPTIMAL_BLOCK_SIZE);
        params.setJournaled(false);
        params.setJournalSize(HFSPlusParams.DEFAULT_JOURNAL_SIZE);
        return new HfsPlusFileSystemFormatter(params);
    }
}
