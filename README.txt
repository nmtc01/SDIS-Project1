To build the project, run in this order and from directory scripts:
1. /bin/bash compile.sh - creates a directory build with the compiled java classes
2. /bin/bash setup.sh peer_id - creates a directory resources in the directory build
3. /bin/bash rmiregistry.sh port - sets up rmi

To run the project:
Use separate terminals for each peer, and another one for TestApp.

4./bin/bash peer.sh version(1.0 or 2.0) peer_id access_point MC MDB MDR(address port) - to lunch peer.
Example of a channel: 224.0.0.15 8000
This creates a PeerProtocol directory and a subdirectory for the Peer with id peer_id, where the files and chunks will be saved.

Examples of usage for each protocol:
- Backup
    /bin/bash test.sh access_point BACKUP file_absolute_path replication_deg
    In this protocol, script test.sh initially makes a copy of the specified file to resources directory inside build directory.
    Then the file is also exported to the Peer directory inside PeerProtocol folder.
- Restore
    /bin/bash test.sh access_point RESTORE file_name
- Delete
    /bin/bash test.sh access_point DELETE file_name
- Reclaim
    /bin/bash test.sh access_point RECLAIM 0
- State
    /bin/bash test.sh access_point STATE
Important!
file_name != file_absolute_path

To clean up:
/bin/bash cleanup.sh peer_id - deletes the directory of a peer inside PeerProtocol directory and also its directory in Storage.

JAVA SE version 11

T3G05

João Francisco de Pinho Brandão - up201705573@fe.up.pt
Nuno Miguel Teixeira Cardoso - up201706162@fe.up.pt