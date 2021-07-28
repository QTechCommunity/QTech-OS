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
 
package com.qtech.os.test.threads;

import static com.qtech.os.test.threads.ThreadingUtils.print;
import static com.qtech.os.test.threads.ThreadingUtils.sleep;
import static com.qtech.os.test.threads.ThreadingUtils.trackEnter;
import static com.qtech.os.test.threads.ThreadingUtils.trackExecute;
import static com.qtech.os.test.threads.ThreadingUtils.trackExit;

/**
 * @author Levente S\u00e1ntha
 */
public class BasicTest {
    public static void main(String[] argv) throws Exception {
        print("Testing thread creation and starting...");
        ThreadingUtils.Forkable f = new ThreadingUtils.Forkable() {
            public void execute() throws Exception {
                aMethod();
            }
        };
        print("thread starting");
        f.fork();
        print("thread started");
        print("Testing join...");
        print("join in progress");
        f.join();
        print("join completed");

    }

    private static void aMethod() {
        trackEnter();
        try {
            trackExecute();
            sleep(3);
        } catch (Exception e) {
            e.printStackTrace();
        }
        trackExit();
    }
}
