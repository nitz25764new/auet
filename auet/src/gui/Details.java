package gui;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.export.ToExcel;

import sag.bpm.*;
import sag.broker.*;
import sag.is.*;
import sag.tn.*;

public class Details extends AuetShell {

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			Details shell = new Details(display,"");
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private String SourcePath;
	/**
	 * Create the shell.
	 * @param display
	 */
	public Details(Display display, String SourcePath) {//sourcepath of softwareAG installation directory
		super(display);
		
		this.SourcePath = SourcePath;
		Button btnNext = new Button(this, SWT.NONE);
		btnNext.setBounds(277, 311, 75, 25);
		btnNext.setText("Next >");
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setBounds(212, 121, 203, 184);
		
		ExpandBar expandBar_3 = new ExpandBar(composite, SWT.NONE);
		expandBar_3.setBounds(10, 130, 180, 32);
		
		ExpandItem xpndtmTradingNetworkSettings = new ExpandItem(expandBar_3, SWT.NONE);
		xpndtmTradingNetworkSettings.setText("Trading Network Settings");
		
		ExpandBar expandBar_2 = new ExpandBar(composite, SWT.NONE);
		expandBar_2.setBounds(10, 10, 181, 32);
		
		ExpandItem xpndtmBroker = new ExpandItem(expandBar_2, SWT.NONE);
		xpndtmBroker.setText("Broker");
		
		ExpandBar expandBar_1 = new ExpandBar(composite, SWT.NONE);
		expandBar_1.setBounds(10, 50, 180, 32);
		
		ExpandItem xpndtmMwsSettings = new ExpandItem(expandBar_1, SWT.NONE);
		xpndtmMwsSettings.setText("MWS Settings");
		
		ExpandBar expandBar = new ExpandBar(composite, SWT.NONE);
		expandBar.setBounds(10, 90, 180, 32);
		
		ExpandItem xpndtmIntegrationServerSettings = new ExpandItem(expandBar, SWT.NONE);
		xpndtmIntegrationServerSettings.setText("Integration Server Settings");
		createContents();
			}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("SWT Application");
		setSize(665, 424);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	protected void processInformation() throws IOException{

		String ISPath = this.SourcePath+"/IntegrationServer";
		String BrokerPath = this.SourcePath+"/IntegrationServer";
		String BPMPath = this.SourcePath+"/IntegrationServer";
		String TNPath = this.SourcePath+"/IntegrationServer";
		
		ISNode isNode = new ISNode(ISPath,"Default");
		BrokerNode brokerNode = new BrokerNode(BrokerPath);
		BPMNode bpmNode = new BPMNode(BPMPath);
		TNNode tnNode = new TNNode(TNPath);
		
		Details dis = this;
		if(isNode.validate() && brokerNode.validate() && bpmNode.validate() && tnNode.validate()){//validate settings
			
			dis.getShell().setVisible(false);
			Display display = Display.getDefault();
			test114 shell = new test114(display);
			shell.setLocation(dis.getLocation());
			shell.open();
			ProgressBar progressBar = shell.progressBar;
			Label status = shell.status;
			Button btn = shell.btnCancel;
			status.setText("In Progress");
			Text details = shell.text;
			isNode.processNode(progressBar,details);
			status.setText("Completed");
			//final int max = progressBar.getMaximum();
			//save to excel sheet
			Map<String,Object[]> data = new TreeMap<String,Object[]>();
			data.put("1",new Object[]{"IS Related Artifacts","Present","Count"});
			data.put("2",new Object[]{"Number of IS Packages","",Integer.toString(isNode.packageCount)});
			data.put("3",new Object[]{"Flow services","Present",Integer.toString(isNode.flow_svc)});
			data.put("4",new Object[]{"Java Services","Present",Integer.toString(isNode.java_svc)});
			data.put("5",new Object[]{"Scheduler","",""});
			data.put("6",new Object[]{"Ports","","Count"});
			data.put("7",new Object[]{"Users (LDAP Configuration)","",""});
			data.put("8",new Object[]{"ACL's","",""});
			data.put("9",new Object[]{"Certificates","",""});
			data.put("10",new Object[]{"Remote Servers","",""});
			data.put("11",new Object[]{"Web Services","",""});
			data.put("12",new Object[]{"Extended Settings","",""});
			data.put("13",new Object[]{"Global Variables","",""});
			data.put("14",new Object[]{"Messaging","",""});
			ToExcel.exportToExcel(data, "wm_upgrade", "AssetsToBeMigrated.xlsx");
			btn.setText("Done");
			
		} else {
			//prompt for correct settings
		}

	}

}
