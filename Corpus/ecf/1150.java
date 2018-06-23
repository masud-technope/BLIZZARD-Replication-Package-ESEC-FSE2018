package org.eclipse.ecf.internal.provider.filetransfer.scp;

import org.eclipse.ecf.filetransfer.service.ISendFileTransfer;
import org.eclipse.ecf.filetransfer.service.ISendFileTransferFactory;

public class ScpSendFileTransferFactory implements ISendFileTransferFactory {

    public  ScpSendFileTransferFactory() {
    // 
    }

    public ISendFileTransfer newInstance() {
        return new ScpOutgoingFileTransfer();
    }
}
