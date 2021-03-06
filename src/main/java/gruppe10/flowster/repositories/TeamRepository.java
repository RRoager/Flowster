package gruppe10.flowster.repositories;

import gruppe10.flowster.models.teams.Team;
import gruppe10.flowster.viewModels.team.TeamViewModel;
import gruppe10.flowster.viewModels.user.PreviewUserViewModel;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;

@Repository
public class TeamRepository
{
    GeneralRepository generalRepository = new GeneralRepository();
    
    Connection organisationConnection;
    
    public ArrayList<Team> retrieveTeamsListFromUserId(String dbName, int userId)
    {
        ArrayList<Team> joinedTeamsList = null;
        
        organisationConnection = generalRepository.establishConnection(dbName);
        
        try
        {
            String sqlCommand = "SELECT id_team, team_name FROM teams_users " +
                                        "RIGHT JOIN teams ON f_id_team = id_team WHERE f_id_user = ?;";
            
            PreparedStatement preparedStatement = organisationConnection.prepareStatement(sqlCommand);
            
            preparedStatement.setInt(1, userId);
            
            ResultSet resultSet = preparedStatement.executeQuery();
            
            joinedTeamsList = createTeamListFromResultSet(resultSet);
            
        }
        catch(SQLException e)
        {
            System.err.println("ERROR in retrieveTeamsArrayListFromUserId: " + e.getMessage());
        }
        finally
        {
            try
            {
                organisationConnection.close();
            }
            catch(SQLException e)
            {
                System.err.println("ERROR in retrieveTeamsArrayListFromUserIdFinally: " + e.getMessage());
            }
        }
        
        return joinedTeamsList;
    }
    
    // TIL SIDEBAR
    public ArrayList<Team> createTeamListFromResultSet(ResultSet resultSet)
    {
        ArrayList<Team> teamList = new ArrayList<>();
        
        try
        {
            while(resultSet.next())
            {
                // til videreudvikling: ArrayList<Project> projectList = retrieveProjectListFromTeamId(teamId,
                // organisationConnection);
                
                Team team = new Team(resultSet.getInt("id_team"), resultSet.getString("team_name"));// projectList);
                
                teamList.add(team);
            }
            if(teamList.size() == 0)
            {
                teamList = null;
            }
            
        }
        catch(SQLException e)
        {
            System.err.println("ERROR in createJoinedTeamsListFromResultSet: " + e.getMessage());
        }
        
        return teamList;
    }
    
    // TODO TIL VIDEREUDVIKLING: HER MANGLER DER at vi henter de to lister som er attributter p?? Team-klasse
    /*
    public ArrayList<Project> retrieveProjectListFromTeamId(int teamId, Connection organisationConnection)
    {
        ArrayList<Project> projectList = null;
        
        try
        {
            
            String sqlCommand = "SELECT DISTINCT f_id_project FROM teams_projects WHERE f_id_team = ?";
        
            PreparedStatement preparedStatement = organisationConnection.prepareStatement(sqlCommand);
        
            preparedStatement.setInt(1, teamId);
        
            ResultSet resultSet = preparedStatement.executeQuery();
    
            projectList = createProjectListFromResultSet(resultSet);
        }
        catch(SQLException e)
        {
            System.err.println("ERROR in retrieveProjectListFromTeamId: " + e.getMessage());
        }
   
        return projectList;
        
    }
    
    
    public ArrayList<Project> createProjectListFromResultSet(ResultSet resultSet)
    {
    
        ArrayList<Project> projectList = new ArrayList<>();
    
        try
        {
            while(resultSet.next())
            {
                Project project = new Project(resultSet.getInt("f_id_project"));
    
                projectList.add(project);
            }
            if(projectList.size() == 0)
            {
                projectList = null;
            }
        
        }
        catch(SQLException e)
        {
            System.err.println("ERROR in createProjectListFromResultSet: " + e.getMessage());
        }
    
        return projectList;
    }
   
    public ResultSet retrieveTeamResultSetFromId(int teamId)
    {
        
        ResultSet resultSet = null;
    
        organisationConnection = generalRepository.establishConnection(dbName);
    
        try
        {
        
            int emailId = flowsterRepository.retrieveEmailIdFromEmail(logInViewModel.getEmail());
        
            String sqlCommand = "SELECT * FROM users WHERE f_id_email = ? and password = ?";
        
            PreparedStatement preparedStatement = organisationConnection.prepareStatement(sqlCommand);
        
            preparedStatement.setInt(1, emailId);
            preparedStatement.setString(2, logInViewModel.getPassword());
        
        
            ResultSet resultSet = preparedStatement.executeQuery();
        
            user = createUserFromResultSet(resultSet);
        
        }
        catch(SQLException e)
        {
            System.err.println("ERROR in retrieveUserFromDb: " + e.getMessage());
        }
        finally
        {
            try
            {
                organisationConnection.close();
            }
            catch(SQLException e)
            {
                System.err.println("ERROR in retrieveUserFromDbFinally: " + e.getMessage());
            }
        }
        return resultSet;
    }
    
     */
    
    public boolean retrieveTeamFromTeamName(String dbName, String teamName)
    {
        boolean teamNameIsAvailable = true;
        
        organisationConnection = generalRepository.establishConnection(dbName);
    
        try
        {
            String sqlCommand = "SELECT * FROM teams WHERE team_name = ?";
        
            PreparedStatement preparedStatement = organisationConnection.prepareStatement(sqlCommand);
        
            preparedStatement.setString(1, teamName);
            
            ResultSet resultSet = preparedStatement.executeQuery();
            
            // hvis der er noget i resultSet'et, er teamName alts?? IKKE ledig
            if(resultSet.next())
            {
                teamNameIsAvailable = false;
            }
         
        }
        catch(SQLException e)
        {
            System.err.println("ERROR in TeamRepository retrieveTeamFromTeamName: " + e.getMessage());
        }
        finally
        {
            try
            {
                organisationConnection.close();
            }
            catch(SQLException e)
            {
                System.err.println("ERROR in TeamRepository retrieveTeamFromTeamNameFinally: " + e.getMessage());
            }
        }
        
        
        return teamNameIsAvailable;
    }
    
    public void insertNewTeamIntoDb(String dbName, String teamName)
    {
        organisationConnection = generalRepository.establishConnection(dbName);
    
        try
        {
            String sqlCommand = "INSERT INTO teams (team_name) value (?)";
        
            PreparedStatement preparedStatement = organisationConnection.prepareStatement(sqlCommand);
        
            preparedStatement.setString(1, teamName);
        
            preparedStatement.executeUpdate();
        }
        catch(SQLException e)
        {
            System.err.println("ERROR in insertNewTeamIntoDb: " + e.getMessage());
        }
        finally
        {
            try
            {
                organisationConnection.close();
            }
            catch(SQLException e)
            {
                System.err.println("ERROR in insertNewTeamIntoDbFinally: " + e.getMessage());
            }
        }
    }
    
    public int retrieveTeamIdFromTeamName(String dbName, String teamName)
    {
        int teamId = 0;
        
        organisationConnection = generalRepository.establishConnection(dbName);
    
        try
        {
            String sqlCommand = "SELECT id_team FROM teams WHERE team_name = ?";
        
            PreparedStatement preparedStatement = organisationConnection.prepareStatement(sqlCommand);
        
            preparedStatement.setString(1, teamName);
        
            ResultSet resultSet = preparedStatement.executeQuery();
            
            teamId = createTeamIdFromResultSet(resultSet);
        }
        catch(SQLException e)
        {
            System.err.println("ERROR in retrieveTeamIdFromTeamName: " + e.getMessage());
        }
        finally
        {
            try
            {
                organisationConnection.close();
            }
            catch(SQLException e)
            {
                System.err.println("ERROR in retrieveTeamIdFromTeamNameFinally: " + e.getMessage());
            }
        }
        
        
        return teamId;
    }
    
    public int createTeamIdFromResultSet(ResultSet resultSet)
    {
        int teamId = 0;
    
        try
        {
            if(resultSet.next())
            {
                teamId = resultSet.getInt("id_team");
            }
        
        }
        catch(SQLException e)
        {
            System.err.println("ERROR in createTeamIdFromResultSet: " + e.getMessage());
        }
        
        return teamId;
    
    }
    
    public void insertRowIntoTeamsUsers(String dbName, int teamId, int userId)
    {
        organisationConnection = generalRepository.establishConnection(dbName);
    
        try
        {
            String sqlCommand = "INSERT INTO teams_users (f_id_team, f_id_user) values (?, ?)";
        
            PreparedStatement preparedStatement = organisationConnection.prepareStatement(sqlCommand);
        
            preparedStatement.setInt(1, teamId);
            preparedStatement.setInt(2, userId);
            
            preparedStatement.executeUpdate();
        }
        catch(SQLException e)
        {
            System.err.println("ERROR in insertNewRowIntoTeamsUsers: " + e.getMessage());
        }
        finally
        {
            try
            {
                organisationConnection.close();
            }
            catch(SQLException e)
            {
                System.err.println("ERROR in insertNewRowIntoTeamsUsersFinally: " + e.getMessage());
            }
        }
        
    }

    public void deleteRowFromTeamsUsers(String dbName, int teamId, int userId)
    {
        organisationConnection = generalRepository.establishConnection(dbName);

        try
        {
            String sqlCommand = "DELETE FROM teams_users WHERE f_id_team = ? AND f_id_user = ?";

            PreparedStatement preparedStatement = organisationConnection.prepareStatement(sqlCommand);

            preparedStatement.setInt(1, teamId);
            preparedStatement.setInt(2, userId);

            preparedStatement.executeUpdate();
        }
        catch(SQLException e)
        {
            System.err.println("ERROR in TeamRepository deleteRowFromTeamsUsers: " + e.getMessage());
        }
        finally
        {
            try
            {
                organisationConnection.close();
            }
            catch(SQLException e)
            {
                System.err.println("ERROR in TeamRepository deleteRowFromTeamsUsersFinally: " + e.getMessage());
            }
        }
        
    }

    public TeamViewModel retrieveAndCreateEditTeamViewModelFromId(String dbName, int teamId)
    {
        TeamViewModel teamViewModel = null;
        
        // metode til: select team_name
        String teamName = retrieveTeamNameFromTeamId(dbName, teamId);
        
        // hvis teamet findes
        if(teamName != null)
        {
            // laves en liste med alle brugere i org, som skal vises i viewet
            ArrayList<PreviewUserViewModel> previewUserViewModelList = createPreviewUserListWithAllOrgUsers(dbName, teamId);
    
            teamViewModel = new TeamViewModel(teamId, teamName, previewUserViewModelList);
        }
        
       
        
        return teamViewModel;
    }
    
    public TeamViewModel retrieveAndCreateViewTeamViewModelFromId(String dbName, int teamId)
    {
        TeamViewModel teamViewModel = null;
    
        // metode til: select team_name
        String teamName = retrieveTeamNameFromTeamId(dbName, teamId);
    
        // hvis teamet findes
        if(teamName != null)
        {
            // lav en liste med alle brugere i team, som skal vises i viewet
            ArrayList<PreviewUserViewModel> previewUserViewModelList = createPreviewUserListWithAllTeamUsers(dbName,
                    teamId);
        
            teamViewModel = new TeamViewModel(teamId, teamName, previewUserViewModelList);
        }
        
        return teamViewModel;
    }
    
    public String retrieveTeamNameFromTeamId(String dbName, int teamId)
    {
        String teamName = null;
        
        organisationConnection = generalRepository.establishConnection(dbName);
        
        try
        {
            String sqlCommand = "SELECT team_name FROM teams WHERE id_team = ?";
        
            PreparedStatement preparedStatement = organisationConnection.prepareStatement(sqlCommand);
        
            preparedStatement.setInt(1, teamId);
        
            ResultSet resultSet = preparedStatement.executeQuery();
    
            teamName = createTeamNameFromResultSet(resultSet);
        }
        catch(SQLException e)
        {
            System.err.println("ERROR in retrieveTeamNameFromTeamId: " + e.getMessage());
        }
        finally
        {
            try
            {
                organisationConnection.close();
            }
            catch(SQLException e)
            {
                System.err.println("ERROR in retrieveTeamNameFromTeamIdFinally: " + e.getMessage());
            }
        }
        
        return teamName;
    }
    
    public String createTeamNameFromResultSet(ResultSet resultSet)
    {
        String teamName = null;
    
        try
        {
            if(resultSet.next())
            {
                teamName = resultSet.getString("team_name");
            }
        
        }
        catch(SQLException e)
        {
            System.err.println("ERROR in createTeamNameFromResultSet: " + e.getMessage());
        }
    
        return teamName;
        
        
    }
    
    /**
     * Laver liste med ALLE organisationens users
     *
     *
     * */
    public ArrayList<PreviewUserViewModel> createPreviewUserListWithAllOrgUsers(String dbName, int teamId)
    {
        ArrayList<PreviewUserViewModel> previewUserViewModelList = null;
        
        organisationConnection = generalRepository.establishConnection(dbName);
    
        try
        {
      
            String sqlCommand = "SELECT id_user, profile_picture, firstname, surname, job_type FROM users " +
                                        "RIGHT JOIN flowster.job_types ON f_id_job_type = id_job_type";
        
            PreparedStatement preparedStatement = organisationConnection.prepareStatement(sqlCommand);
    
            // resultSet der indeholder ALLE org-brugere
            ResultSet resultSet = preparedStatement.executeQuery();
    
            previewUserViewModelList = createPreviewUserListFromResultSet(resultSet, teamId);
        }
        catch(SQLException e)
        {
            System.err.println("ERROR in createPreviewUserList: " + e.getMessage());
        }
        finally
        {
            try
            {
                organisationConnection.close();
            }
            catch(SQLException e)
            {
                System.err.println("ERROR in isUserOnTeamFinally: " + e.getMessage());
            }
        }
    
        return previewUserViewModelList;
    }
    
    public ArrayList<PreviewUserViewModel> createPreviewUserListWithAllTeamUsers(String dbName, int teamId)
    {
        ArrayList<PreviewUserViewModel> previewUserViewModelList = null;
    
        // resultSet der indeholder ALLE teamusers
        organisationConnection = generalRepository.establishConnection(dbName);
    
        try
        {
            String sqlCommand = "SELECT id_user, profile_picture, firstname, surname, job_type " +
                                        "FROM teams_users " +
                                        "RIGHT JOIN users ON f_id_user = id_user " +
                                        "RIGHT JOIN flowster.job_types ON f_id_job_type = id_job_type  " +
                                        "WHERE f_id_team = ?;";
        
            PreparedStatement preparedStatement = organisationConnection.prepareStatement(sqlCommand);
            
            preparedStatement.setInt(1, teamId);
        
            ResultSet resultSet = preparedStatement.executeQuery();
        
            previewUserViewModelList = createPreviewUserListFromResultSet(resultSet, teamId);
        }
        catch(SQLException e)
        {
            System.err.println("ERROR in createPreviewUserListWithAllTeamUsers: " + e.getMessage());
        }
        finally
        {
            try
            {
                organisationConnection.close();
            }
            catch(SQLException e)
            {
                System.err.println("ERROR in createPreviewUserListWithAllTeamUsersFinally: " + e.getMessage());
            }
        }
    
        return previewUserViewModelList;
        
    }
    
    public ArrayList<PreviewUserViewModel> createPreviewUserListFromResultSet(ResultSet resultSet, int teamId)
    {
        ArrayList<PreviewUserViewModel> previewUserViewModelList = new ArrayList<>();
        
        
        try
        {
            while(resultSet.next())
            {
                // lav nyt obj.
               
                if(resultSet.getInt("id_user") != 0)
                {
                    PreviewUserViewModel previewUser = new PreviewUserViewModel(
                            resultSet.getInt("id_user"),
                            convertBlobToByteArray(resultSet.getBlob("profile_picture")),
                            resultSet.getString("firstname") + " " + resultSet.getString("surname"),
                            resultSet.getString("job_type"));
                    
                    //tilf??j til liste
                    previewUserViewModelList.add(previewUser);
                }
            }
            
            if(previewUserViewModelList.size() > 0)
            {
                // g?? listen igennem og tjek om bruger er p?? team - hvis bruger er tilknyttet teamet: isOnTeam = true
                previewUserViewModelList = checkIfPreviewUserIsOnTeam(previewUserViewModelList, teamId);
            }
            else
            {
                previewUserViewModelList = null;
            }
            
        }
        catch(SQLException e)
        {
            System.err.println("ERROR in createPreviewUserListFromResultSet: " + e.getMessage());
        }
        
        
        
        return previewUserViewModelList;
    }
    
    public ArrayList<PreviewUserViewModel> checkIfPreviewUserIsOnTeam
            (ArrayList<PreviewUserViewModel> previewUserViewModelList, int teamId)
    {
        for(int i = 0; i < previewUserViewModelList.size(); i++)
        {
            // PreviewUserViewModel previewUser = previewUserViewModelList.get(i);
            
            // tjek om den er tilknyttet teamet
            boolean userIsOnTeam = isUserOnTeam(teamId, previewUserViewModelList.get(i).getId()); // previewUser
            // .getId();
            
            if(userIsOnTeam)
            {
                previewUserViewModelList.get(i).setOnTeam(true);
                // previewUser.setOnTeam(true);
            }
            else
            {
                previewUserViewModelList.get(i).setOnTeam(false);
                // previewUser.setOnTeam(false);
            }

        }
      
       
        return previewUserViewModelList;
    }
    
    public boolean isUserOnTeam(int teamId, int userId)
    {
        boolean userIsOnTeam = false;
        
        try
        {
            String sqlCommand = "SELECT * FROM teams_users WHERE f_id_team = ? AND f_id_user = ?";
            
            PreparedStatement preparedStatement = organisationConnection.prepareStatement(sqlCommand);
            
            preparedStatement.setInt(1, teamId);
            preparedStatement.setInt(2, userId);
            
            ResultSet resultSet = preparedStatement.executeQuery();
            
            if(resultSet.next())
            {
                userIsOnTeam = true;
            }
            
        }
        catch(SQLException e)
        {
            System.err.println("ERROR in isUserOnTeam: " + e.getMessage());
        }
        return userIsOnTeam;
    }
    
    public void updateTeamName(String dbName, int teamId, String newTeamName)
    {
        organisationConnection = generalRepository.establishConnection(dbName);
    
        try
        {
            String sqlCommand = "UPDATE teams SET team_name = ? WHERE id_team = ?";
        
            PreparedStatement preparedStatement = organisationConnection.prepareStatement(sqlCommand);
        
            preparedStatement.setString(1, newTeamName);
            preparedStatement.setInt(2, teamId);
            
            preparedStatement.executeUpdate();
        }
        catch(SQLException e)
        {
            System.err.println("ERROR in TeamRepository updateTeamName: " + e.getMessage());
        }
        finally
        {
            try
            {
                organisationConnection.close();
            }
            catch(SQLException e)
            {
                System.err.println("ERROR in updateTeamNameFinally: " + e.getMessage());
            }
        }
        
    }
    
    public boolean checkIfTeamIsOnProject(String dbName, int teamId, int projectId)
    {
        boolean teamIsOnProject = false;
    
        organisationConnection = generalRepository.establishConnection(dbName);
    
        try
        {
            String sqlCommand = "SELECT * FROM teams_projects WHERE f_id_team = ? AND f_id_project = ?";
        
            PreparedStatement preparedStatement = organisationConnection.prepareStatement(sqlCommand);
        
            preparedStatement.setInt(1, teamId);
            preparedStatement.setInt(2, projectId);
            
            ResultSet resultSet = preparedStatement.executeQuery();
        
            // hvis der er noget i resultSet'et, er teamName alts?? IKKE ledig
            if(resultSet.next())
            {
                teamIsOnProject = true;
            }
        
        }
        catch(SQLException e)
        {
            System.err.println("ERROR in TeamRepository checkIfTeamIsOnProject: " + e.getMessage());
        }
        finally
        {
            try
            {
                organisationConnection.close();
            }
            catch(SQLException e)
            {
                System.err.println("ERROR in TeamRepository checkIfTeamIsOnProjectFinally: " + e.getMessage());
            }
        }
        
        return teamIsOnProject;
    }
    
    /**
     * Konverterer blob til et byteArray
     *
     * @param blob Blob-obj som skal konverteres
     * @return byte[] konverterede blob
     * */
    public byte[] convertBlobToByteArray(Blob blob)
    {
        // byte[0] == null-value til byte[]
        byte[] profilePictureBytes = new byte[0];
        
        if(blob != null)
        {
            try
            {
                profilePictureBytes = blob.getBytes(1, (int) blob.length());
            }
            catch(SQLException e)
            {
                System.err.println("ERROR in convertBlobToByteArray: " + e.getMessage());
            }
        }
        
        return profilePictureBytes;
    }
    
    
    
}
