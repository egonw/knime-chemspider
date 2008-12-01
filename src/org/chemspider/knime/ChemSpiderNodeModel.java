package org.chemspider.knime;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.StringValue;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.data.container.SingleCellFactory;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;


/**
 * This is the model implementation of ChemSpider.
 * Download Structures from ChemSpider.com.
 *
 * @TODO   should also work with InChI
 *
 * @author Egon Willighagen
 */
public class ChemSpiderNodeModel extends NodeModel {
    
	private String inchikeyColumnName = null;
	
    // the logger instance
    private static final NodeLogger logger = NodeLogger
            .getLogger(ChemSpiderNodeModel.class);
        
    /** the settings key which is used to retrieve and 
        store the settings (from the dialog or from a settings file)    
       (package visibility to be usable from the dialog). */
	static final String CFGKEY_COUNT = "Count";

	/** initial default count value. */
	static final int DEFAULT_COUNT = 100;

	// example value: the models count variable filled from the dialog 
	// and used in the models execution method. The default components of the
	// dialog work with "SettingsModels".
	private final SettingsModelIntegerBounded m_count =
		new SettingsModelIntegerBounded(ChemSpiderNodeModel.CFGKEY_COUNT,
                    ChemSpiderNodeModel.DEFAULT_COUNT,
                    Integer.MIN_VALUE, Integer.MAX_VALUE);
	

    /**
     * Constructor for the node model.
     */
    protected ChemSpiderNodeModel() {
    
        // TODO one incoming port and one outgoing port is assumed
        super(1, 1);
    }

    /**
     * @see org.knime.core.node.NodeModel #execute(BufferedDataTable[],
     *      ExecutionContext)
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {

    	ColumnRearranger rearranger = rearrangeColumns(inData[0].getDataTableSpec());
    	
    	BufferedDataTable someName = exec.createColumnRearrangeTable(
    		inData[0], rearranger, exec
    	);
    	
    	return new BufferedDataTable[] { someName };
    	
    }

    /**
     * @see org.knime.core.node.NodeModel#reset()
     */
    @Override
    protected void reset() {
        // TODO Code executed on reset.
        // Models build during execute are cleared here.
        // Also data handled in load/saveInternals will be erased here.
    }

    /**
     * @see org.knime.core.node.NodeModel
     *      #configure(org.knime.core.data.DataTableSpec[])
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
            throws InvalidSettingsException {
        
    	if (inSpecs[0].findColumnIndex(inchikeyColumnName) == -1)
    		throw new InvalidSettingsException("Cannot find the InChIKey column!");
    	
    	ColumnRearranger rearranger = rearrangeColumns(inSpecs[0]);
    	
        return new DataTableSpec[]{ rearranger.createSpec() };
    }

    private ColumnRearranger rearrangeColumns(final DataTableSpec inSpecs) {
    	ColumnRearranger bla = new ColumnRearranger(inSpecs);
    	DataColumnSpecCreator specCreator = new DataColumnSpecCreator("molFile", StringCell.TYPE);
    	bla.append(new SingleCellFactory(specCreator.createSpec()) {

			@Override
			public DataCell getCell(DataRow row) {
				String inchiKey = ((StringValue)row.getCell(
					inSpecs.findColumnIndex(inchikeyColumnName)
				)).getStringValue();
				String molFile = "";
				try {
					URL url = new URL("http://www.chemspider.com/InChIKey/" + inchiKey);
					BufferedReader reader = new BufferedReader(
						new InputStreamReader(
							url.openConnection().getInputStream()
						)
					);
					String line = reader.readLine();
					Pattern pattern = Pattern.compile("Chemical-Structure.(\\d*).html");
					String csid = "";
					while (line != null) {
						Matcher matcher = pattern.matcher(line);
						System.out.println("Line: " + line);
						if (matcher.find()) {
							csid = matcher.group(1);
							System.out.println("Found CSID: " + csid);
						}
						line = reader.readLine();
					}

					url = new URL("http://www.chemspider.com/mol/" + csid);
					reader = new BufferedReader(
						new InputStreamReader(
							url.openConnection().getInputStream()
						)
					);
					line = reader.readLine();
					while (line != null) {
						molFile += line;
						line = reader.readLine();
					}
				} catch (Exception exception) {
//					e.printStackTrace();
				}
				
				return new StringCell(molFile);
			}
    		
    	});
    	return bla;
    }
    
    /**
     * @see org.knime.core.node.NodeModel
     *      #saveSettingsTo(org.knime.core.node.NodeSettings)
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {

        settings.addString("inchikeyColumnName", inchikeyColumnName);

    }

    /**
     * @see org.knime.core.node.NodeModel
     *      #loadValidatedSettingsFrom(org.knime.core.node.NodeSettingsRO)
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
            
        inchikeyColumnName = settings.getString("inchikeyColumnName");

    }

    /**
     * @see org.knime.core.node.NodeModel
     *      #validateSettings(org.knime.core.node.NodeSettingsRO)
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
            
    	settings.getString("inchikeyColumnName");
    	
    }
    
    /**
     * @see org.knime.core.node.NodeModel #loadInternals(java.io.File,
     *      org.knime.core.node.ExecutionMonitor)
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        
		// TODO load internal data. 
		// Everything handed to output ports is loaded automatically (data
		// returned by the execute method, models loaded in loadModelContent,
		// and user settings set through loadSettingsFrom - is all taken care 
		// of). Load here only the other internals that need to be restored
		// (e.g. data used by the views).

    }
    
    /**
     * @see org.knime.core.node.NodeModel #saveInternals(java.io.File,
     *      org.knime.core.node.ExecutionMonitor)
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
       
       	// TODO save internal models. 
		// Everything written to output ports is saved automatically (data
		// returned by the execute method, models saved in the saveModelContent,
		// and user settings saved through saveSettingsTo - is all taken care 
		// of). Save here only the other internals that need to be preserved
		// (e.g. data used by the views).

    }

}
