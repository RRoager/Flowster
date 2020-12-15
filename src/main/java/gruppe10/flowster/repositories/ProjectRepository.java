package gruppe10.flowster.repositories;

import gruppe10.flowster.models.project.Project;
import gruppe10.flowster.models.project.Subproject;
import gruppe10.flowster.models.project.Subtask;
import gruppe10.flowster.models.project.Task;
import gruppe10.flowster.models.teams.Team;
import gruppe10.flowster.models.users.ProjectManager;
import gruppe10.flowster.models.users.User;
import gruppe10.flowster.viewModels.project.CreateProjectViewModel;
import gruppe10.flowster.viewModels.user.CreatorViewModel;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Date;

@Repository
public class ProjectRepository
{
    
    GeneralRepository generalRepository = new GeneralRepository();
    // TODO ryk evt. metoden vi kalder via dette obj til generalRep
    OrganisationRepository organisationRepository = new OrganisationRepository();
    TeamRepository teamRepository = new TeamRepository();
    
    Connection organisationConnection;
    
    
    // find teams som bruger er i - og find projekter som disse teams er tilknyttet
    // teams_users --> f_id_team --> projects_teams --> f_id_project --> projects (INFO HERFRA)
    
    /*
    SELECT id_project, project_title, project_description, project_deadline, project_manhours
    FROM teams_users
    RIGHT JOIN teams_projects ON teams_users.f_id_team = teams_projects.f_id_team
    RIGHT JOIN projects ON teams_projects.f_id_project = projects.id_project
    WHERE f_id_user = ?
    
     */
    
    
    // id_user - find f_id_team i team_users --> ud fra f_id_team find id_project i projects_teans -->
    // opret et project-Obj ud fra hvert id --> find subproject-kols ud fra id_project i projects_subprojects -
    // via join --> opret subproject-obj ud i whileloop hvor vi tager ting ud fra subproject-kols --> osv.
    //
    // teams_users
    // SELECT id_project
    
    
    /*
    "SELECT id_user, profile_picture, firstname, surname, job_type " +
            "FROM teams_users " +
            "RIGHT JOIN users ON f_id_user = id_user " +
            "RIGHT JOIN flowster.job_types ON f_id_job_type = id_job_type  " +
            "WHERE f_id_team = ?;";
            
     */
    
    
    public ArrayList<Project> retrieveProjectListFromUserId(String dbName, int userId)
    {
        ArrayList<Project> joinedProjectList = null;

        organisationConnection = generalRepository.establishConnection(dbName);

        try
        {
            /* TODO: indsæt dette eksempel i rapporten
            Vi selecter alle relevante kolonner fra projekt-tabellen - disse skal vi bruge til at oprette et projekt
            Dette gøres ud fra userId'et
            
            teams_users --> f_id_team --> teams_projects --> f_id_project --> projects (INFO HERFRA)
            
            Vi starter fra teams_users-tabellen - herfra findes alle f_id_team ud fra f_id_user
            Så findes alle f_id_project ud fra f_id_team's i teams_projects-tabellen
            DERFRA findes SELECT'ed rækker fra projects - ud fra f_id_project'er
            */
            
            String sqlCommand =
                    "SELECT id_project, project_title, project_description, project_deadline, project_manhours " +
                        "FROM teams_users " +
                        "RIGHT JOIN teams_projects ON teams_users.f_id_team = teams_projects.f_id_team " +
                        "RIGHT JOIN projects ON teams_projects.f_id_project = projects.id_project " +
                        "WHERE f_id_user = ?";

            PreparedStatement preparedStatement = organisationConnection.prepareStatement(sqlCommand);

            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            joinedProjectList = createJoinedProjectListFromResultSet(resultSet);

        }
        catch(SQLException e)
        {
            System.err.println("ERROR in retrieveProjectArrayListFromUserId: " + e.getMessage());
        }
        finally
        {
            try
            {
                organisationConnection.close();
            }
            catch(SQLException e)
            {
                System.err.println("ERROR in retrieveProjectArrayListFromUserIdFinally: " + e.getMessage());
            }
        }

        return joinedProjectList;
    }
    
    public ArrayList<Project> createJoinedProjectListFromResultSet(ResultSet resultSet)
    {
        ArrayList<Project> joinedProjectList = new ArrayList<>();

        try
        {
            while(resultSet.next())
            {
                int projectId = resultSet.getInt("id_project");
                
                // opretter nyt projekt
                Project project = new Project
                      (projectId,
                        resultSet.getString("project_title"),
                        resultSet.getString("project_description"),
                        resultSet.getDate("project_deadline"),
                        resultSet.getDouble("project_manhours"),
                        retrieveCreator(projectId),
                        retrieveSubprojectList(projectId),
                        retrieveTeamList(projectId));

                // tilføjer nyoprettet projekt til liste
                joinedProjectList.add(project);
            }
            if(joinedProjectList.size() == 0)
            {
                joinedProjectList = null;
            }

        }
        catch(SQLException e)
        {
            System.err.println("ERROR in createJoinedProjectListFromResultSet: " + e.getMessage());
        }

        return joinedProjectList;
    }
    
    public User retrieveCreator(int projectId)
    {
        User creator = null;
        
        try
        {
  
            String sqlCommand = "SELECT * FROM projects_users " +
                                "RIGHT JOIN users ON f_id_user = id_user " +
                                "WHERE f_id_project = ?";
            
            PreparedStatement preparedStatement = organisationConnection.prepareStatement(sqlCommand);
            
            preparedStatement.setInt(1, projectId);
    
            ResultSet resultSet = preparedStatement.executeQuery();
            
            if(resultSet.next())
            {
                creator = organisationRepository.createUserFromResultSet(resultSet);
            }
            
        }
        catch(SQLException e)
        {
            System.err.println("ERROR in retrieveCreator: " + e.getMessage());
        }
        
        return creator;
    }
    
    public ArrayList<Subproject> retrieveSubprojectList(int projectId)
    {
        ArrayList<Subproject> subprojectList = null;
    
        try
        {
            String sqlCommand =
                    "SELECT * FROM projects_subprojects " +
                            "RIGHT JOIN subprojects ON f_id_subproject = id_subproject " +
                            "WHERE f_id_project = ?";
    
            PreparedStatement preparedStatement = organisationConnection.prepareStatement(sqlCommand);
    
            preparedStatement.setInt(1, projectId);
    
            ResultSet resultSet = preparedStatement.executeQuery();
    
            if(resultSet.next())
            {
                subprojectList = createSubprojectListFromResultSet(resultSet);
            }
        }
        catch(SQLException e)
        {
            System.err.println("ERROR in retrieveSubprojectList: " + e.getMessage());
        }
    
        return subprojectList;
    }
    
    public ArrayList<Subproject> createSubprojectListFromResultSet(ResultSet resultSet)
    {
        ArrayList<Subproject> subprojectList = null;
    
       try
       {
           while(resultSet.next())
           {
               int subprojectId = resultSet.getInt("id_subproject");
               
               // opretter nyt subprojekt
               Subproject subproject = new Subproject
                   (subprojectId,
                   resultSet.getString("subproject_title"),
                   resultSet.getString("subproject_description"),
                   resultSet.getDate("subproject_deadline"),
                   resultSet.getDouble("subproject_manhours"),
                   retrieveTaskList(subprojectId));
        
               // tilføjer nyoprettet projekt til liste
               subprojectList.add(subproject);
           }
           if(subprojectList.size() == 0)
           {
               subprojectList = null;
           }
       }
       catch(SQLException e)
       {
           System.err.println("ERROR in createSubprojectListFromResultSet: " + e.getMessage());
       }
       
       return subprojectList;
    }
    
    public ArrayList<Task> retrieveTaskList(int subprojectId)
    {
        ArrayList<Task> taskList = null;
    
        try
        {
            String sqlCommand =
                    "SELECT * FROM subprojects_tasks " +
                            "RIGHT JOIN tasks ON f_id_task = id_task " +
                            "WHERE f_id_subproject = ?";
        
            PreparedStatement preparedStatement = organisationConnection.prepareStatement(sqlCommand);
        
            preparedStatement.setInt(1, subprojectId);
        
            ResultSet resultSet = preparedStatement.executeQuery();
        
            if(resultSet.next())
            {
                taskList = createTaskListFromResultSet(resultSet);
            }
        }
        catch(SQLException e)
        {
            System.err.println("ERROR in retrieveTaskList: " + e.getMessage());
        }
    
        return taskList;
        
    }
    
    public ArrayList<Task> createTaskListFromResultSet(ResultSet resultSet)
    {
        ArrayList<Task> taskList = null;
        
        try
        {
            while(resultSet.next())
            {
                int taskId = resultSet.getInt("id_task");
                
                // opretter nyt subprojekt
                Task task = new Task
                       (taskId,
                        resultSet.getString("task_title"),
                        retrieveSubtaskList(taskId));
                
                // tilføjer nyoprettet projekt til liste
                taskList.add(task);
            }
            if(taskList.size() == 0)
            {
                taskList = null;
            }
        }
        catch(SQLException e)
        {
            System.err.println("ERROR in createTaskListFromResultSet: " + e.getMessage());
        }
        
        return taskList;
    }
    
    public ArrayList<Subtask> retrieveSubtaskList(int taskId)
    {
        ArrayList<Subtask> subtaskList = null;
        
        try
        {
            String sqlCommand =
                    "SELECT * FROM tasks_subtasks " +
                            "RIGHT JOIN subtasks ON f_id_subtask = id_subtask " +
                            "WHERE f_id_task = ?";
            
            PreparedStatement preparedStatement = organisationConnection.prepareStatement(sqlCommand);
            
            preparedStatement.setInt(1, taskId);
            
            ResultSet resultSet = preparedStatement.executeQuery();
            
            if(resultSet.next())
            {
                subtaskList = createSubtaskListFromResultSet(resultSet);
            }
        }
        catch(SQLException e)
        {
            System.err.println("ERROR in retrieveSubtaskList: " + e.getMessage());
        }
        
        return subtaskList;
    }
    
    public ArrayList<Subtask> createSubtaskListFromResultSet(ResultSet resultSet)
    {
        ArrayList<Subtask> subtaskList = null;
        
        try
        {
            while(resultSet.next())
            {
                
                // opretter nyt subprojekt
                Subtask subtask = new Subtask
                            (resultSet.getInt("id_subtask"),
                            resultSet.getString("subtask_title"));
                
                // tilføjer nyoprettet projekt til liste
                subtaskList.add(subtask);
            }
            if(subtaskList.size() == 0)
            {
                subtaskList = null;
            }
        }
        catch(SQLException e)
        {
            System.err.println("ERROR in createSubtaskListFromResultSet: " + e.getMessage());
        }
        
        return subtaskList;
    }
    
    
    
    
    
    
    
    
    
    
    public ArrayList<Team> retrieveTeamList(int projectId)
    {
        ArrayList<Team> teamList = new ArrayList<>();
    
        try
        {
            // henter alle teams
            String sqlCommand = "SELECT * FROM teams_projects " +
                                "RIGHT JOIN teams ON f_id_team = id_team " +
                                "WHERE f_id_project = ?";
    
            PreparedStatement preparedStatement = organisationConnection.prepareStatement(sqlCommand);
    
            preparedStatement.setInt(1, projectId);
    
            ResultSet resultSet = preparedStatement.executeQuery();
    
            if(resultSet.next())
            {
                // denne metoder gemmer reelt kun teamId og teamName
                teamList = teamRepository.createTeamListFromResultSet(resultSet);
            }
        
        }
        catch(SQLException e)
        {
            System.err.println("ERROR in retrieveTeamList: " + e.getMessage());
        }
    
        return teamList;
    }
    
    
    
    
    public boolean checkIfProjectTitleIsAvailable(String dbName, String title)
    {
        boolean projectTitleIsAvailable = true;
    
        organisationConnection = generalRepository.establishConnection(dbName);
    
        try
        {
            // TOD
            String sqlCommand = "SELECT id_project FROM projects WHERE project_title = ?";
        
            PreparedStatement preparedStatement = organisationConnection.prepareStatement(sqlCommand);
        
            preparedStatement.setString(1, title);
        
            ResultSet resultSet = preparedStatement.executeQuery();
        
            // hvis der er et projekt som allerede har titlen
            if(resultSet.next())
            {
                // så er titlen IKKE available
                projectTitleIsAvailable = false;
            }
        }
        catch(SQLException e)
        {
            System.err.println("ERROR in checkIfProjectTitleIsAvailable: " + e.getMessage());
        }
        finally
        {
            try
            {
                organisationConnection.close();
            }
            catch(SQLException e)
            {
                System.err.println("ERROR in checkIfProjectTitleIsAvailableFinally: " + e.getMessage());
            }
        }
        
        
        return projectTitleIsAvailable;
    }
    
    public void insertNewProjectIntoDb(String dbName, CreateProjectViewModel createProjectViewModel)
    {
        organisationConnection = generalRepository.establishConnection(dbName);
    
        try
        {
            String title = createProjectViewModel.getTitle();
            Date deadline = null;
            // hvis der ER noget gemt som deadline
            if(createProjectViewModel.getDeadline() != "")
            {
                // konverter String til Date
                deadline = Date.valueOf(createProjectViewModel.getDeadline());
            }
        
            String sqlCommand = "INSERT INTO projects (project_title, project_deadline) values (?, ?)";
        
            PreparedStatement preparedStatement = organisationConnection.prepareStatement(sqlCommand);
        
            preparedStatement.setString(1, title);
            preparedStatement.setDate(2, deadline);
        
            preparedStatement.executeUpdate();
        
        }
        catch(SQLException e)
        {
            System.err.println("ERROR in projectRepository insertNewProjectIntoDb: " + e.getMessage());
        }
        finally
        {
            try
            {
                organisationConnection.close();
            }
            catch(SQLException e)
            {
                System.err.println("ERROR in projectRepository insertNewProjectIntoDbFinally: " + e.getMessage());
            }
        }
    
    }
    
    
    public int retrieveProjectIdFromProjectTitle(String dbName, String projectTitle)
    {
        int projectId = 0;
    
        organisationConnection = generalRepository.establishConnection(dbName);
    
        try
        {
            // TOD
            String sqlCommand = "SELECT id_project FROM projects WHERE project_title = ?";
        
            PreparedStatement preparedStatement = organisationConnection.prepareStatement(sqlCommand);
        
            preparedStatement.setString(1, projectTitle);
        
            ResultSet resultSet = preparedStatement.executeQuery();
        
            if(resultSet.next())
            {
                projectId = resultSet.getInt("id_project");
            }
        
        }
        catch(SQLException e)
        {
            System.err.println("ERROR in projectRepository retrieveProjectIdFromProjectTitle: " + e.getMessage());
        }
        finally
        {
            try
            {
                organisationConnection.close();
            }
            catch(SQLException e)
            {
                System.err.println("ERROR in projectRepository retrieveProjectIdFromProjectTitleFinally: " + e.getMessage());
            }
        }
        
        
        return projectId;
    }
    
    public void insertRowIntoProjectsUsers(String dbName, int projectId, int userId)
    {
        organisationConnection = generalRepository.establishConnection(dbName);
    
        try
        {
            String sqlCommand = "INSERT INTO projects_users (f_id_project, f_id_user) values (?, ?)";
        
            PreparedStatement preparedStatement = organisationConnection.prepareStatement(sqlCommand);
        
            preparedStatement.setInt(1, projectId);
            preparedStatement.setInt(2, userId);
        
            preparedStatement.executeUpdate();
        }
        catch(SQLException e)
        {
            System.err.println("ERROR in insertRowIntoProjectsUsers: " + e.getMessage());
        }
        finally
        {
            try
            {
                organisationConnection.close();
            }
            catch(SQLException e)
            {
                System.err.println("ERROR in insertRowIntoProjectsUsersFinally: " + e.getMessage());
            }
        }
    }
    
    public int findMaxIdFromTable(String dbName, String tableName)
    {
        int maxId = 0;
    
        organisationConnection = generalRepository.establishConnection(dbName);
    
        try
        {
            String idColumnName = "id_" + tableName;
            // fjerner det sidste tegn fra db-namet (altså 's' fordi vores tabelnavne altid slutter på s)
            idColumnName = idColumnName.substring(0,idColumnName.length()-1);
            
            String sqlCommand = String.format("SELECT MAX(%s) FROM %s", idColumnName, tableName);
    
            PreparedStatement preparedStatement = organisationConnection.prepareStatement(sqlCommand);
            
            // preparedStatement.setString(1, tableName);
        
            ResultSet resultSet = preparedStatement.executeQuery();
        
            if(resultSet.next())
            {
                maxId = resultSet.getInt(String.format("MAX(%s)", idColumnName));
            }
        
        }
        catch(SQLException e)
        {
            System.err.println("ERROR in findMaxIdFromTable: " + e.getMessage());
        }
        finally
        {
            try
            {
                organisationConnection.close();
            }
            catch(SQLException e)
            {
                System.err.println("ERROR in findMaxIdFromTableFinally: " + e.getMessage());
            }
        }
    
    
        return maxId;
    }
    



}
