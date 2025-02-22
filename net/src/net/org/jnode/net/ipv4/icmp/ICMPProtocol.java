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
 
package org.jnode.net.ipv4.icmp;

import java.net.DatagramSocketImplFactory;
import java.net.SocketException;
import java.net.SocketImplFactory;
import java.util.Vector;
import org.apache.log4j.Logger;
import org.jnode.net.SocketBuffer;
import org.jnode.net.ipv4.IPv4Constants;
import org.jnode.net.ipv4.IPv4Header;
import org.jnode.net.ipv4.IPv4Protocol;
import org.jnode.net.ipv4.IPv4Service;
import org.jnode.util.Queue;
import org.jnode.util.QueueProcessor;
import org.jnode.util.QueueProcessorThread;
import org.jnode.vm.objects.Statistics;

/**
 * Protocol handler of the ICMP protocol.
 *
 * @author Ewout Prangsma (epr@users.sourceforge.net)
 */
public class ICMPProtocol implements IPv4Protocol, IPv4Constants, ICMPConstants,
    QueueProcessor<SocketBuffer> {

    private static final String IPNAME_ICMP = "icmp";

    /**
     * My logger
     */
    private Logger log = Logger.getLogger(getClass());

    /**
     * The IP service we're a part of
     */
    private final IPv4Service ipService;

    /**
     * The statistics
     */
    private final ICMPStatistics stat = new ICMPStatistics();

    /**
     * Queue<SocketBuffer> for requests that need a reply
     */
    private final Queue<SocketBuffer> replyRequestQueue = new Queue<SocketBuffer>();

    private final QueueProcessorThread<SocketBuffer> replyRequestsThread;

    /**
     * ICMP packet listeners
     */
    private final Vector<ICMPListener> listeners = new Vector<ICMPListener>();

    /**
     * Create a new instance
     *
     * @param ipService
     */
    public ICMPProtocol(IPv4Service ipService) {
        this.ipService = ipService;
        this.replyRequestsThread =
            new QueueProcessorThread<SocketBuffer>("icmp-reply", replyRequestQueue, this);
        replyRequestsThread.start();
    }

    /**
     * @see org.jnode.net.ipv4.IPv4Protocol#getName()
     */
    public String getName() {
        return IPNAME_ICMP;
    }

    /**
     * @see org.jnode.net.ipv4.IPv4Protocol#getProtocolID()
     */
    public int getProtocolID() {
        return IPPROTO_ICMP;
    }

    /**
     * @see org.jnode.net.ipv4.IPv4Protocol#receive(org.jnode.net.SocketBuffer)
     */
    public void receive(SocketBuffer skbuf) throws SocketException {

        // Update statistics
        stat.ipackets.inc();

        try {
            final ICMPHeader hdr = ICMPHeaderFactory.createHeader(skbuf);
            skbuf.setTransportLayerHeader(hdr);
            skbuf.pull(hdr.getLength());

            if (!hdr.isChecksumOk()) {
                stat.badsum.inc();
                return;
            }

            // TODO Process ICMP messages

            switch (hdr.getType()) {
                case ICMP_ECHO:
                    postReplyRequest(skbuf);
                    break;
                case ICMP_ECHOREPLY:
                    notifyListeners(skbuf);
                    break;
                default:
                    log.debug("GOT ICMP type " + hdr.getType() + ", code " + hdr.getCode());
            }
        } catch (SocketException ex) {
            // TODO fix me
            // Ignore for now
        }
    }

    /**
     * Process an ICMP error message that has been received and matches this
     * protocol. The skbuf is position directly after the ICMP header (thus
     * contains the error IP header and error transport layer header). The
     * transportLayerHeader property of skbuf is set to the ICMP message header.
     *
     * @param skbuf
     * @throws SocketException
     */
    public void receiveError(SocketBuffer skbuf) throws SocketException {
        // Ignore errors here
    }

    /**
     * Gets the SocketImplFactory of this protocol.
     *
     * @throws SocketException If this protocol is not Socket based.
     */
    public SocketImplFactory getSocketImplFactory() throws SocketException {
        throw new SocketException("ICMP is packet based");
    }

    /**
     * Gets the DatagramSocketImplFactory of this protocol.
     *
     * @throws SocketException If this protocol is not DatagramSocket based.
     */
    public DatagramSocketImplFactory getDatagramSocketImplFactory() throws SocketException {
        throw new SocketException("Not implemented yet");
    }

    /**
     * Send an ICMP packet
     *
     * @param skbuf
     */
    protected void send(IPv4Header ipHdr, ICMPHeader icmpHdr, SocketBuffer skbuf)
        throws SocketException {
        stat.opackets.inc();
        skbuf.setTransportLayerHeader(icmpHdr);
        icmpHdr.prefixTo(skbuf);
        ipService.transmit(ipHdr, skbuf);
    }

    /**
     * Send a reply on an ICMP echo header.
     *
     * @param hdr
     * @param skbuf
     */
    private void sendEchoReply(ICMPEchoHeader hdr, SocketBuffer skbuf) throws SocketException {
        final IPv4Header ipHdr = (IPv4Header) skbuf.getNetworkLayerHeader();
        final IPv4Header ipReplyHdr = new IPv4Header(ipHdr);
        ipReplyHdr.swapAddresses();
        ipReplyHdr.setTtl(0xFF);
        send(ipReplyHdr, hdr.createReplyHeader(), new SocketBuffer(skbuf));
    }


    /**
     * @see org.jnode.net.ipv4.IPv4Protocol#getStatistics()
     */
    public Statistics getStatistics() {
        return stat;
    }

    /**
     * Post a request that needs a reply in the reply queue.
     *
     * @param skbuf
     */
    private void postReplyRequest(SocketBuffer skbuf) {
        replyRequestQueue.add(skbuf);
    }

    /**
     * Process a request that needs a reply
     *
     * @param skbuf
     */
    private void processReplyRequest(SocketBuffer skbuf) {
        final ICMPHeader hdr = (ICMPHeader) skbuf.getTransportLayerHeader();
        try {
            if (hdr.getType() == ICMPType.ICMP_ECHO) {
                sendEchoReply((ICMPEchoHeader) hdr, skbuf);
            }
        } catch (SocketException ex) {
            log.error("Error in ICMP reply", ex);
        }
    }

    /**
     * @see org.jnode.util.QueueProcessor#process(java.lang.Object)
     */
    public void process(SocketBuffer object) throws Exception {
        processReplyRequest(object);
    }

    /**
     * ICMP packet listeners methods
     */
    private void notifyListeners(SocketBuffer skbuf) {
        for (ICMPListener l : listeners) {
            l.packetReceived(skbuf);
        }
    }

    public void addListener(ICMPListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ICMPListener listener) {
        listeners.remove(listener);
    }
}
