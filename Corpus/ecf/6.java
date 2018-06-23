package org.eclipse.ecf.internal.provider.filetransfer.scp;

import org.eclipse.ecf.filetransfer.service.IRetrieveFileTransfer;
import org.eclipse.ecf.filetransfer.service.IRetrieveFileTransferFactory;

public class ScpRetrieveFileTransferFactory implements IRetrieveFileTransferFactory {

    public  ScpRetrieveFileTransferFactory() {
    // 
    }

    public IRetrieveFileTransfer newInstance() {
        return new ScpRetrieveFileTransfer();
    }
}
