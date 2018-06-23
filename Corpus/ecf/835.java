package org.eclipse.ecf.internal.provider.filetransfer.efs;

import org.eclipse.ecf.filetransfer.service.IRetrieveFileTransfer;
import org.eclipse.ecf.filetransfer.service.IRetrieveFileTransferFactory;

/**
 * EFS factory
 */
public class RetrieveFileTransferFactory implements IRetrieveFileTransferFactory {

    /**
	 * @see org.eclipse.ecf.filetransfer.service.IRetrieveFileTransferFactory#newInstance()
	 */
    public IRetrieveFileTransfer newInstance() {
        return new RetrieveFileTransfer();
    }
}
