package xyz.immortius.museumcurator.client.network;

import xyz.immortius.museumcurator.common.data.MuseumCollections;
import xyz.immortius.museumcurator.common.network.LogOnMessage;

/**
 * Processes the LogOnMessage to build the list of collections and set the initial checked items
 */
public class LogonReceiver {
    public static void receive(LogOnMessage msg) {
        MuseumCollections.setCollections(msg.getCollections());
        MuseumCollections.setCheckedItems(msg.getCheckedItems());
    }
}
