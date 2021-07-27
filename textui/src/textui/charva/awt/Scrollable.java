/* interface Scrollable
 *
 * Copyright (C) 2001  R M Pitman
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package charva.awt;

import charva.awt.event.ScrollEvent;
import charva.awt.event.ScrollListener;

/**
 * The interface for objects that can be contained in a ScrollPane
 * and scrolled within a Viewport.
 */
public interface Scrollable {
    void addScrollListener(ScrollListener listener_);

    void removeScrollListener(ScrollListener listener_);

    void processScrollEvent(ScrollEvent evt_);

    Point getLocation();

    Dimension getPreferredScrollableViewportSize();
}
