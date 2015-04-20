package sag.is;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//represents one package instance
public class ISNode {
	
	public Boolean includeWMPackages = false;
	public String ISPath;
	public String pathToInstance;
	public String[] packages;
	public int packageCount;
	public ArrayList<String> nodes;
	public int java_svc;
	public int flow_svc;
	public int doc_svc;
	public int wsdl_svc;
	public int adapter_svc;
	public int webM_svc;
	public int misc_svc;
	
	//this represents one Integration server instance node
	public ISNode(String ISPath, String ISInstance) throws IOException{//integration server path
		
		this.doc_svc = this.flow_svc = this.wsdl_svc = this.adapter_svc = this.webM_svc = this.misc_svc = 0;
		this.ISPath = ISPath;
		this.includeWMPackages = true;
		if(ISInstance.equals("")){
			this.pathToInstance = this.ISPath+"/packages";
		} else {
			this.pathToInstance = this.ISPath+"/instances/"+ISInstance+"/packages";
		}
		
		
	}
	
	//start processing IS node
	public void processNode(ProgressBar progressBar,Text details){
		this.packages = this.getPackages();
		this.packageCount = this.getPackageCount();
		nodes = new ArrayList<String>();
		try {
			progressBar.setSelection(20);
			this.getNodesList(details);
			progressBar.setSelection(40);
			this.getAllSeviceTypes();
			progressBar.setSelection(100);
			//this.displayNodesList(details);
			this.displayServiceCount(details);
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	//validate IS settings
	public Boolean validate(){
		return true;
	}
	
	//get the no. of packages
	public int getPackageCount(){
		return this.packages.length;
	}
	
	//get all package names inside package folder of a particular instance
	public String[] getPackages(){
		
		File dir=new File(this.pathToInstance);		
		return dir.list();
		
	}
	//find nodes
	public void getNodesList(Text details) throws IOException{
		
		if(this.includeWMPackages){
			for(String node:this.packages){
				String pathToNode = this.pathToInstance+"/"+node+"/ns";
				this.getNodes(new File(pathToNode),details);
			}
		}
		else{
			for(String node:this.packages){
				if(node.startsWith("Wm")) continue;
				String pathToNode = this.pathToInstance+"/"+node+"/ns";
				//System.out.println(pathToNode);
				this.getNodes(new File(pathToNode),details);
			}
		}
	}
	//gets node.ndf files
	public void getNodes(File dir,Text details){
		File[] files = dir.listFiles();
		for(File f:files){
			if(f.isDirectory())
				this.getNodes(f,details);
			else if(f.toString().endsWith("node.ndf")){
				this.nodes.add(f.toString());
				details.append(f.toString()+"\n");
			}
		}
	}
	//print nodes
	public void displayNodesList(Text details){
		for(String s:this.nodes){
			details.append(s);
		}
	}
	
	//print service count
	public void displayServiceCount(Text details){
		
		details.append("\nDocument types:\t\t"+this.doc_svc);
		details.append("\nFlow Services:\t\t"+this.flow_svc);
		details.append("\nJava Services:\t\t"+this.java_svc);
		details.append("\nAdapter Services:\t"+this.adapter_svc);
		details.append("\nWSDL Services:\t\t"+this.wsdl_svc);
		details.append("\nWebM Services:\t\t"+this.webM_svc);
		details.append("\nMisc Services:\t\t"+this.misc_svc);
		
	}
	
	//gets service types in node.ndf files
	protected void getAllSeviceTypes(){
		
		int record,java,flow,wsdl,adapter,webM,misc;
		record=java=flow=wsdl=adapter=webM=misc=0;
		NodeList nodeResult = null;
		for(String node:this.nodes){
		
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			XPathFactory xpathFac = XPathFactory.newInstance();
			XPath xpath = xpathFac.newXPath();
			try {
				DocumentBuilder builder = factory.newDocumentBuilder();
		    	
				XPathExpression xPress = xpath.compile("//value[@name='svc_type']");
				Document doc = builder.parse(node);
				
				Object result = xPress.evaluate(doc, XPathConstants.NODESET);
				nodeResult = (NodeList) result;
			
			} catch(Exception e){
				e.printStackTrace();
			}	
			if(nodeResult.getLength()>0){
			switch(nodeResult.item(0).getTextContent()){
				case "java":
					java++;
					break;
				case "flow":
					flow++;
					break;
				case "webServiceDescriptor":
					wsdl++;
					break;
				case "AdapterService":
					adapter++;
					break;
				case "webMethods":
					webM++;
					break;
				case "record":
					record++;
					break;
				default:
					misc++;
					break;
				
				}
			}
	
	}
		this.java_svc = java;
		this.flow_svc = flow;
		this.wsdl_svc = wsdl;
		this.adapter_svc = adapter;
		this.doc_svc = record;
		this.misc_svc = misc;
		this.webM_svc = webM;
	}
}
