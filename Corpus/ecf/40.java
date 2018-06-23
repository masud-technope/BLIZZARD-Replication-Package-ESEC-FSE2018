package org.eclipse.ecf.internal.provider.bittorrent;

import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.filetransfer.service.IRetrieveFileTransfer;
import org.eclipse.ecf.filetransfer.service.IRetrieveFileTransferFactory;

public class BitTorrentRetrieveFileTransferFactory implements IRetrieveFileTransferFactory {

    public IRetrieveFileTransfer newInstance() {
        try {
            return new BitTorrentContainer();
        } catch (IDCreateException e) {
            return null;
        }
    }
}
