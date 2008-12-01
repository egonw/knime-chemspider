package org.chemspider.knime;

import org.knime.core.data.DataTableSpec;
import org.knime.core.data.StringValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.util.ColumnSelectionComboxBox;

/**
 * <code>NodeDialog</code> for the "ChemSpider" Node.
 * Download Structures from ChemSpider.com.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Egon Willighagen
 */
public class ChemSpiderNodeDialog extends NodeDialogPane {

	private ColumnSelectionComboxBox box;
	
    /**
     * New pane for configuring ChemSpider node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected ChemSpiderNodeDialog() {
        super();

        box = new ColumnSelectionComboxBox(StringValue.class);
        
        addTab("Column Name", box);
                    
    }

	@Override
	protected void loadSettingsFrom(NodeSettingsRO settings, DataTableSpec[] specs) throws NotConfigurableException {
		try {
			box.update(specs[0], settings.getString("inchikeyColumnName"));
		} catch (InvalidSettingsException e) {
			// OK, it defaults to ""
		}
		
	}

	@Override
	protected void saveSettingsTo(NodeSettingsWO settings) throws InvalidSettingsException {
		settings.addString("inchikeyColumnName", box.getSelectedColumn());
		
	}
}
