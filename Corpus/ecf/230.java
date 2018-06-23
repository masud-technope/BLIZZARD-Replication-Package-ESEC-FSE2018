package org.eclipse.ecf.presence.ui.chatroom;

import java.util.Map;
import org.eclipse.ecf.presence.chatroom.IChatRoomContainer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;

public class ChatRoomViewerConfiguration extends TextSourceViewerConfiguration {

    private IChatRoomContainer container;

    private ChatRoomManagerView view;

    public  ChatRoomViewerConfiguration(IPreferenceStore preferenceStore, IChatRoomContainer container, ChatRoomManagerView view) {
        super(preferenceStore);
        this.container = container;
        this.view = view;
    }

    protected Map getHyperlinkDetectorTargets(ISourceViewer sourceViewer) {
        Map hyperlinkDetectorTargets = super.getHyperlinkDetectorTargets(sourceViewer);
        if (container != null)
            hyperlinkDetectorTargets.put(container.getClass().getPackage().getName(), view);
        return hyperlinkDetectorTargets;
    }
}
