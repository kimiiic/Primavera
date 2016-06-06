package amsapexport;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

public class Setting
{
    private String username = "";
    private String password = "";
    private int database = -1;
    private String APIServerIP = "";
    private int APIServerPort = -1;    

    private String filepath ="";
    private String logpath ="";
    File fXmlFile;
    
    public Setting() throws Exception
    {
        fXmlFile = new File("setting.xml");

        if(!fXmlFile.exists())
        {
            throw new Exception("Not able to locate setting.xml file, please check the setting file");
        }

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("detail");

        for (int temp = 0; temp < nList.getLength(); temp++)
        {
		   Node nNode = nList.item(temp);
		   if (nNode.getNodeType() == Node.ELEMENT_NODE)
           {
		      Element eElement = (Element) nNode;
		      username = getTagValue("username", eElement);
		      password = getTagValue("password", eElement);
	          database = Integer.parseInt(getTagValue("database", eElement));
		      APIServerIP = getTagValue("apiserverip", eElement);
              APIServerPort = Integer.parseInt(getTagValue("apiserverport", eElement));              
              filepath = getTagValue("filepath", eElement);
              logpath = getTagValue("logpath", eElement);
		   }
		}
    }

    private String getTagValue(String sTag, Element eElement) throws Exception
    {
        if(null==eElement.getElementsByTagName(sTag).item(0))
        {
            throw new Exception("Unable to located Tag: '"+sTag+"' in setting.xml file, please check the setting file");
        }
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        Node nValue = (Node) nlList.item(0);
        return nValue.getNodeValue();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getDatabase() {
        return database;
    }

    public String getAPIServerIP() {
        return APIServerIP;
    }

    public int getAPIServerPort() {
        return APIServerPort;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public void setAPIServerIP(String APIServerIP) {
        this.APIServerIP = APIServerIP;
    }

    public void setAPIServerPort(int APIServerPort) {
        this.APIServerPort = APIServerPort;
    }
    
    public String getFilePath(){
        return this.filepath;
    }
    
    public String getLogPath(){
        return this.logpath;
    }
}