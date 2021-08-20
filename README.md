# KudesuNetwork
Native asynchronous network library with packet-based architecture

[![GitHub last commit](https://img.shields.io/github/last-commit/kudesunik/KudesuNetwork.svg)](https://github.com/Kudesunik/KudesuNetwork/commits)
[![GitHub](https://img.shields.io/github/license/mashape/apistatus.svg)](https://github.com/Kudesunik/KudesuNetwork/blob/master/LICENSE)

### Why?

Yes, I really know that there are other network libraries (Netty, for example), but I needed my own version of an asynchronous library with its own built-in exchange protocol and  with a packet abstraction layer, but at the same time with support for sending raw data. And so that all this was simple. Well, in general, I just want to invent the wheel :)

### Warning

Current (08.2021) network library state is unstable and low tested, so be careful, library will be updated and verified over time

### Usage

In general, I advise you to look at the examples in the tests section, it is simple. But here is some basics:

1. Create network parameters (```NetworkParameters```) instance;
2. Setup network parameters if you want (handshake sending, authorization, encrypt, compress etc.) or default will be applied;
3. Create your custom packets ```implements Packet``` and register it in your packet registrator class ```implements PacketRegistrator```;
4. Create your server listener ```implements NetworkServerListener```, which will be accept many events, such as ```onHandshake``` or ```onPacketReceive``` and others;
5. Create server (```KudesuNetworkServer server = KudesuNetwork.createServer(port, packetRegistrator, networkListener, networkParameters)```)
6. Launch server ```server.start()```;
7. Create your client listener ```implements NetworkClientListener```, which will be accept many events, such as ```onHandshake``` or ```onPacketReceive``` and others;
8. You can create both separate from server parameters class and packet registrator class, either reuse already created classes;
9. Create client (```KudesuNetworkClient client = KudesuNetwork.createClient(address, port, packetRegistrator, networkListener, networkParameters)```);
10. Lauch client ```client.connect()```;
11. Send your client packets by calling ```sendPacket(new <? extends Packet> yourPacket)``` method;
12. Send your server packets by calling ```sendPacket(port, new <? extends Packet> yourPacket)``` or ```sendBroadcast(new <? extends Packet> yourPacket)``` methods;
13. Handle packet recieving in your classes ```implements NetworkClientListener``` and ```implements NetworkServerListener```;
14. Enjoy!
15. You may disconnect client by calling ```client.disconnect(reason)``` or ```client.disconnect()```;
16. You !MUST! stop server by calling ```server.stop()``` or server will listen to port endlessly;

### Description

Protocols list and details

#### Transport layer
- [x] TCP
- [ ] UDP

#### Application protocol
- [x] Raw
- [x] Kudesu (custom)
- [ ] HTTP
- [ ] WebSocket

#### Send / Receive details 
![image](https://user-images.githubusercontent.com/3079145/130105989-4c9ba800-10bf-4b82-9e9a-72cc8e4bdbeb.png)

#### Kudesu custom protocol details
![image](https://user-images.githubusercontent.com/3079145/130106263-568dd07d-d139-487e-b7f2-d632b319c109.png)

### Build 

This is Gradle projects

#### Contributors

When you're ready to submit your code, just make a pull request.

#### Reporting bugs

1. Start by searching issue tracker for duplicates;
2. Create a new issue, explaining the problem in proper detail.

### License

MIT License. See LICENSE file for details.
