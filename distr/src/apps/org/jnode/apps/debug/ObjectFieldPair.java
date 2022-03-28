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
 
package org.jnode.apps.debug;

import java.lang.reflect.Field;


/**
 * Just a bean to store a Field and an object which has that field.
 *
 * @author blind
 */
class ObjectFieldPair {
    Object object;
    Field field;

    public ObjectFieldPair(Object object, Field field) {
        this.object = object;
        this.field = field;
    }

    public Field getField() {
        return field;
    }

    public Object getObject() {
        return object;
    }
}
