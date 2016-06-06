package amsapexport;

import com.primavera.common.exceptions.InvalidCredentialsException;
import com.primavera.integration.client.Session;
import com.primavera.integration.client.RMIURL;
import com.primavera.integration.common.DatabaseInstance;
import javax.swing.JOptionPane;

public class DBConnection
{
    private Session session;

    //Constructor
    public DBConnection(){}

    //Function for connect to data base by Local Mode
    public Session getLoginSession()
    {
        try
        {
            Setting APIConnection = new Setting();

        	String loginName = APIConnection.getUsername();
        	String loginPassword = APIConnection.getPassword();
        	String serverAddress = APIConnection.getAPIServerIP();
        	int serverPort = APIConnection.getAPIServerPort();
            int databaseId = APIConnection.getDatabase();

            DatabaseInstance[] dbInstances = Session.getDatabaseInstances(
            RMIURL.getRmiUrl( RMIURL.STANDARD_RMI_SERVICE, serverAddress, serverPort ) );

            session = Session.login( RMIURL.getRmiUrl( RMIURL.STANDARD_RMI_SERVICE, serverAddress, serverPort ),
            dbInstances[databaseId].getDatabaseId(), loginName, loginPassword);

            System.out.println(session.getDatabaseName());
        }

        catch (InvalidCredentialsException e)
        {
            JOptionPane.showMessageDialog(null, "Login failed due to invalid user name or password. Program Exiting");
            e.printStackTrace();
            System.exit(0);
		}

        catch ( Exception e )
        {
            JOptionPane.showMessageDialog(null, "Connection Error with Database, Check the connection and Restart the program. Error message: "+e.getMessage());
            e.printStackTrace();
            System.exit(0);
        }

        return session;
    }
}
