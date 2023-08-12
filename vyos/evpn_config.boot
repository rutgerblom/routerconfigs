interfaces {
    bridge br75001 {
        address 10.203.245.1/24
        description blue
        member {
            interface eth1.245 {
            }
            interface vxlan75001 {
            }
        }
        vrf blue
    }
    bridge br75002 {
        address 10.203.248.1/24
        description red
        member {
            interface eth1.248 {
            }
            interface vxlan75002 {
            }
        }
        vrf red
    }
    dummy dum0 {
        address 192.168.100.101/32
    }
    dummy dum1 {
        address 192.168.100.100/32
    }
    ethernet eth0 {
        address 10.203.0.240/24
        description "Router Uplink"
        hw-id 00:50:56:b3:5a:b7
        mtu 1500
    }
    ethernet eth1 {
        hw-id 00:50:56:b3:ce:86
        mtu 9000
        vif 240 {
            address 10.203.240.1/24
            description Management
            mtu 1500
        }
        vif 241 {
            address 10.203.241.1/24
            description vMotion
            mtu 9000
        }
        vif 242 {
            address 10.203.242.1/24
            description vSAN
            mtu 9000
        }
        vif 243 {
            address 10.203.243.1/24
            description "IP Storage"
            mtu 9000
        }
        vif 244 {
            address 10.203.244.1/24
            description "Overlay Transport"
            mtu 9000
        }
        vif 245 {
            description "Service VM Management"
            mtu 1500
        }
        vif 246 {
            address 10.203.246.1/24
            description "NSX Edge Uplink #1"
            mtu 1600
        }
        vif 247 {
            address 10.203.247.1/24
            description "NSX Edge Uplink #2"
            mtu 1600
        }
        vif 248 {
            description "RTEP Transport"
            mtu 1500
        }
        vif 249 {
            address 10.203.249.1/24
            description "VM Network"
            mtu 1500
        }
    }
    loopback lo {
    }
    vxlan vxlan75001 {
        mtu 1600
        parameters {
            nolearning
        }
        port 4789
        source-address 192.168.100.101
        vni 75001
    }
    vxlan vxlan75002 {
        mtu 1600
        parameters {
            nolearning
        }
        port 4789
        source-address 192.168.100.100
        vni 75002
    }
}
protocols {
    bgp {
        address-family {
            ipv4-unicast {
                network 192.168.100.100/32 {
                }
                network 192.168.100.101/32 {
                }
            }
            l2vpn-evpn {
                advertise {
                    ipv4 {
                        unicast {
                        }
                    }
                }
                advertise-all-vni
            }
        }
        neighbor 10.203.0.1 {
            address-family {
                ipv4-unicast {
                }
            }
            description Lab-Router
            remote-as 65000
        }
        neighbor 10.203.246.2 {
            address-family {
                ipv4-unicast {
                    default-originate {
                    }
                }
                l2vpn-evpn {
                }
            }
            description Pod-240-T0-EdgeVM-01-1
            ebgp-multihop 3
            remote-as 65241
        }
        neighbor 10.203.246.3 {
            address-family {
                ipv4-unicast {
                    default-originate {
                    }
                }
                l2vpn-evpn {
                }
            }
            description Pod-240-T0-EdgeVM-02-1
            ebgp-multihop 3
            remote-as 65241
        }
        neighbor 10.203.247.2 {
            address-family {
                ipv4-unicast {
                    default-originate {
                    }
                }
                l2vpn-evpn {
                }
            }
            description Pod-240-T0-EdgeVM-01-2
            ebgp-multihop 3
            remote-as 65241
        }
        neighbor 10.203.247.3 {
            address-family {
                ipv4-unicast {
                    default-originate {
                    }
                }
                l2vpn-evpn {
                }
            }
            description Pod-240-T0-EdgeVM-02-2
            ebgp-multihop 3
            remote-as 65241
        }
        parameters {
            router-id 10.203.240.1
        }
        system-as 65240
    }
    ospf {
        area 0 {
            network 10.203.0.0/24
        }
        area 240 {
            area-type {
                normal
            }
            network 10.203.246.0/24
            network 10.203.247.0/24
        }
        log-adjacency-changes {
        }
        parameters {
            router-id 10.203.0.240
        }
        redistribute {
            bgp {
            }
            connected {
                metric-type 2
            }
        }
    }
    static {
        route 0.0.0.0/0 {
            next-hop 10.203.0.1 {
                distance 250
            }
        }
    }
}
service {
    dhcp-server {
        shared-network-name vmnetwork {
            authoritative
            subnet 10.203.245.0/24 {
                default-router 10.203.245.1
                lease 300
                name-server 10.203.0.5
                range VMNetworkRange {
                    start 10.203.245.200
                    stop 10.203.245.254
                }
            }
            subnet 10.203.249.0/24 {
                default-router 10.203.249.1
                lease 300
                name-server 10.203.0.5
                range VMNetworkRange {
                    start 10.203.249.200
                    stop 10.203.249.254
                }
            }
        }
    }
    lldp {
        interface all {
        }
        legacy-protocols {
            cdp
        }
    }
    ntp {
        allow-client {
            address 0.0.0.0/0
            address ::/0
        }
        server time1.vyos.net {
        }
        server time2.vyos.net {
        }
        server time3.vyos.net {
        }
    }
    ssh {
        port 22
    }
}
system {
    config-management {
        commit-revisions 100
    }
    conntrack {
        modules {
            ftp
            h323
            nfs
            pptp
            sip
            sqlnet
            tftp
        }
    }
    console {
        device ttyS0 {
            speed 115200
        }
    }
    host-name Pod-240-Router
    login {
        banner {
            pre-login "********************\n*  Pod-240-Router  *\n*  Username: vyos  *\n********************\n"
        }
        user vyos {
            authentication {
                encrypted-password $6$rounds=656000$2IsRjwn51/XIOsz5$BpWOEtOpU1EAqwZh9Hdmy9qPIcg/5EVbNzsIiN4/wvnl3WDHtr9.Jrcdr55AlCOzszGR5N/VG24uchZAZh4.40
            }
        }
    }
    name-server 10.203.0.5
    syslog {
        global {
            facility all {
                level notice
            }
            facility local7 {
                level debug
            }
        }
        host 10.203.240.19 {
            facility all {
                level notice
            }
            protocol udp
        }
    }
    time-zone UTC
}
vrf {
    name blue {
        protocols {
            bgp {
                address-family {
                    ipv4-unicast {
                        redistribute {
                            connected {
                            }
                        }
                    }
                    l2vpn-evpn {
                        advertise {
                            ipv4 {
                                unicast {
                                }
                            }
                        }
                        rd 65240:1
                        route-target {
                            export 65240:1
                            import 65241:1
                        }
                    }
                }
                system-as 65240
            }
        }
        table 1001
        vni 75001
    }
    name red {
        protocols {
            bgp {
                address-family {
                    ipv4-unicast {
                        redistribute {
                            connected {
                            }
                        }
                    }
                    l2vpn-evpn {
                        advertise {
                            ipv4 {
                                unicast {
                                }
                            }
                        }
                        rd 65240:2
                        route-target {
                            export 65240:2
                            import 65241:2
                        }
                    }
                }
                system-as 65240
            }
        }
        table 1002
        vni 75002
    }
}


// Warning: Do not remove the following line.
// vyos-config-version: "bgp@4:broadcast-relay@1:cluster@1:config-management@1:conntrack@3:conntrack-sync@2:container@1:dhcp-relay@2:dhcp-server@6:dhcpv6-server@1:dns-dynamic@1:dns-forwarding@4:firewall@10:flow-accounting@1:https@4:ids@1:interfaces@29:ipoe-server@1:ipsec@12:isis@3:l2tp@4:lldp@1:mdns@1:monitoring@1:nat@5:nat66@1:ntp@3:openconnect@2:ospf@2:policy@5:pppoe-server@6:pptp@2:qos@2:quagga@11:rip@1:rpki@1:salt@1:snmp@3:ssh@2:sstp@4:system@26:vrf@3:vrrp@4:vyos-accel-ppp@2:wanloadbalance@3:webproxy@2"
// Release version: 1.4-rolling-202307270317
