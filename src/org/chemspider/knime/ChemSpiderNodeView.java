package org.chemspider.knime;

import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeView;

/**
 * <code>NodeView</code> for the "ChemSpider" Node.
 * Download Structures from ChemSpider.com.
 *
 * @author Egon Willighagen
 */
public class ChemSpiderNodeView extends NodeView {

    /**
     * Creates a new view.
     * 
     * @param nodeModel The model (class: {@link ChemSpiderNodeModel})
     */
    protected ChemSpiderNodeView(final NodeModel nodeModel) {
        super(nodeModel);

        // TODO instantiate the components of the view here.

    }

    /**
     * @see org.knime.core.node.NodeView#modelChanged()
     */
    @Override
    protected void modelChanged() {

		// TODO retrieve the new model from your nodemodel and 
		// update the view.
		ChemSpiderNodeModel nodeModel = 
			(ChemSpiderNodeModel)getNodeModel();
		assert nodeModel != null;
		
		// be aware of a possibly not executed nodeModel! The data you retrieve
		// from your nodemodel could be null, emtpy, or invalid in any kind.
		
    }

    /**
     * @see org.knime.core.node.NodeView#onClose()
     */
    @Override
    protected void onClose() {
    
    	// TODO things to do when closing the view
    }

    /**
     * @see org.knime.core.node.NodeView#onOpen()
     */
    @Override
    protected void onOpen() {

    	// TODO things to do when opening the view
    }

}
