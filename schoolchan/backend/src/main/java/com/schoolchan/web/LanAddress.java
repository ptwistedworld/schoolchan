package com.schoolchan.web;

import org.springframework.stereotype.Component;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Finds the address other devices on the same network can reach, so the
 * board can be opened on a phone. Only a local address is ever shown.
 */
@Component
public class LanAddress {

    /** The LAN address of this machine, or null when there is none. */
    public String address() {
        String fallback = null;
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface network : Collections.list(interfaces)) {
                if (!network.isUp() || network.isLoopback()) {
                    continue;
                }
                for (InetAddress address : Collections.list(network.getInetAddresses())) {
                    if (!(address instanceof Inet4Address) || address.isLoopbackAddress()) {
                        continue;
                    }
                    // A private address (192.168.x.x, 10.x.x.x) is the one a
                    // phone on the same Wi-Fi can actually reach.
                    if (address.isSiteLocalAddress()) {
                        return address.getHostAddress();
                    }
                    if (fallback == null && !network.isVirtual()) {
                        fallback = address.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {  // noqa: BLE001
            return null;
        }
        return fallback;
    }
}
