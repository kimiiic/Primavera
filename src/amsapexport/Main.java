
package amsapexport;


import com.primavera.integration.client.EnterpriseLoadManager;
import com.primavera.integration.client.Session;
import com.primavera.integration.client.bo.BOIterator;
import com.primavera.integration.client.bo.object.Activity;
import com.primavera.integration.client.bo.object.Project;
import com.primavera.integration.client.bo.object.ProjectCode;
import com.primavera.integration.client.bo.object.UDFValue;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JOptionPane;

public class Main
{
    public static void main(String[] args)
    {
       
        Session session = new DBConnection().getLoginSession();
        EnterpriseLoadManager elm = session.getEnterpriseLoadManager();
        try
        {
            Setting APIConnection = new Setting();
            String outputFileName = "";
            String errorLogFileName = "";
            Calendar currentDay = Calendar.getInstance();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

            //outputFileName = "C:\\Temp\\"+ sdf.format(currentDay.getTime()) +".txt";
            //outputFileName = "\\\\DC2WP11859\\primavera_data_in\\comm\\"+ sdf.format(currentDay.getTime()) +".txt";
            //errorLogFileName = "u:\\TEMP\\Major Project\\API Program Log\\" + sdf.format(currentDay.getTime()) + " Milestone Error Log.txt";
            
            outputFileName = APIConnection.getFilePath()+sdf.format(currentDay.getTime()) +".txt";
            errorLogFileName = APIConnection.getLogPath()+sdf.format(currentDay.getTime()) + " Milestone Error Log.txt";
                    
            File outPutFile = new File(outputFileName);
            File errorFile = new File(errorLogFileName);

            if (outPutFile.exists())
            {
                System.out.println("Exist and deleted");
                outPutFile.delete();
            }

            if (errorFile.exists())
            {
                System.out.println("Exist and deleted");
                outPutFile.delete();
            }

             FileWriter writer = new FileWriter(outPutFile);
             FileWriter errorWriter = new FileWriter(errorLogFileName);
             
             BOIterator<ProjectCode> aProjectCode = elm.loadProjectCodes(ProjectCode.getAllFields(), "CodeTypeName LIKE 'MP-PM Report' AND CodeValue LIKE 'Yes'", null);
             
         
             if (aProjectCode.hasNext())
             {
                 String condit = null;
                 //String condit = "Id = 'SI-09938'";
                 BOIterator<Project> allProjects = aProjectCode.next().loadProjects(new String[]{"Id", "Name","LastUpdateDate" }, condit, "Name");

                 //System.out.println(aProjectCode.next().getCodeTypeName());
                 int i=0;
                 while(allProjects.hasNext())
                 {                     
                     Project aProject = allProjects.next();
                     String projectId = aProject.getId();

                     i++;
                     System.out.println(i+ " " +projectId);

                     BOIterator<UDFValue> allUDFValues = aProject.loadAllUDFValues(new String[]{"ForeignObjectId" ,"Text" }, "UDFTypeSubjectArea LIKE 'TASK' AND UDFTypeTitle LIKE 'SAP WBS'", "UDFTypeTitle");

                     while(allUDFValues.hasNext())
                     {
                         UDFValue anUDFValue = allUDFValues.next();                         
                         String UDFTextValue = anUDFValue.getText();

                         if (UDFTextValue.length()>8)
                         {
                             UDFTextValue = UDFTextValue.substring(0, 8);
                         }                         

                         if (projectId.equals(UDFTextValue))
                         {
                             Activity anActivity = Activity.load(session, new String[]{ "Name", "Type", "EarlyStartDate", "EarlyFinishDate", "ActualStartDate", "ActualFinishDate" }, anUDFValue.getForeignObjectId());
                             String activityName = anActivity.getName();                            

                             if (anActivity.getType().getDescription().equals("Start Milestone"))
                             {
                                 String BLStartDate = "";
                                 String forecastStartDate = "";
                                 String actualStartDate = "";

                                 BOIterator<UDFValue> dateUDFs = anActivity.loadUDFValues(new String[]{ "StartDate", "FinishDate" }, "UDFTypeTitle LIKE 'SAP Basic Start Date' AND UDFTypeSubjectArea LIKE 'TASK'", null);
                                 if(dateUDFs.hasNext())
                                 {
                                    UDFValue aDateUDF = dateUDFs.next();
                                    BLStartDate = sdf.format(aDateUDF.getFinishDate());
                                 }
                                 if(null!=anActivity.getEarlyStartDate())
                                 {
                                    forecastStartDate = sdf.format(anActivity.getEarlyStartDate());
                                 }
                                 if(null!=anActivity.getActualStartDate())
                                 {
                                    actualStartDate = sdf.format(anActivity.getActualStartDate());
                                 }

                                 //System.out.println(projectId + "," + anUDFValue.getText() + "," + activityName + "," + BLStartDate + "," + forecastStartDate + "," + actualStartDate + "\n");
                                 String c = projectId + "\t" + anUDFValue.getText() + "\t" + activityName + "\t" + BLStartDate + "\t" + forecastStartDate + "\t" + actualStartDate + "\n";
                                 writer.append(c);
                             }

                             else if (anActivity.getType().getDescription().equals("Finish Milestone"))
                             {
                                 String BLFinishDate = "";
                                 String forecastFinishDate = "";
                                 String actualFinishDate = "";

                                 BOIterator<UDFValue> dateUDFs = anActivity.loadUDFValues(new String[]{ "StartDate", "FinishDate" }, "UDFTypeTitle LIKE 'SAP Basic Finish Date' AND UDFTypeSubjectArea LIKE 'TASK'", null);
                                 if(dateUDFs.hasNext())
                                 {
                                    UDFValue aDateUDF = dateUDFs.next();
                                    BLFinishDate = sdf.format(aDateUDF.getStartDate());
                                 }
                                 if(null!=anActivity.getEarlyFinishDate())
                                 {
                                    forecastFinishDate = sdf.format(anActivity.getEarlyFinishDate());
                                 }
                                 if(null!=anActivity.getActualFinishDate())
                                 {
                                    actualFinishDate = sdf.format(anActivity.getActualFinishDate());
                                 }
                                 
                                 //System.out.println(projectId + "," + anUDFValue.getText() + "," + activityName + "," + BLFinishDate + "," + forecastFinishDate + "," + actualFinishDate + "\n");
                                 String c = projectId + "\t" + anUDFValue.getText() + "\t" + activityName + "\t" + BLFinishDate + "\t" + forecastFinishDate + "\t" + actualFinishDate + "\n";
                                 writer.append(c);
                             }

                             else
                             {
                                 System.out.println("Error: This Activity is not a MileStone");                                

                                 String errorMSG = "Project Id: " + projectId + "\t" + "Activity Name: " + activityName + "\t" + "Error: This Activity is not a MileStone" + "\n";
                                 errorWriter.append(errorMSG);                                 
                             }
                         }

                         else
                         {
                             System.out.println("UDF within Activity UDF 'SAP-WBS' not equal to Project Id");

                             String ActivityName = Activity.load(session, new String[]{ "Name"}, anUDFValue.getForeignObjectId()).getName();                            

                             String errorMSG = "Project Id: " + projectId + "\t" + "Activity Name: " + ActivityName + "\t" + "Error: UDF within Activity UDF 'SAP-WBS' not equal to Project Id" + "\n";
                             errorWriter.append(errorMSG);                             
                         }
                     }
                 }
             }

             writer.flush();
             writer.close();
             errorWriter.flush();
             errorWriter.close();
            

             if (0==errorFile.length())
             {
                 System.out.println("Error Log empty, Delete it");
                 errorFile.delete();
             }
             
             if(session!=null)
             {
                 session.logout();
             }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            if(session!=null)
            {
                session.logout();
            }
            JOptionPane.showMessageDialog(null, e.toString());
            System.exit(0);
        }
    }

}
