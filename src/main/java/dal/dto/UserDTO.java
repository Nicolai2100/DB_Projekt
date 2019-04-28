package dal.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Standard implementation of IUserDTO
 */
public class UserDTO implements Serializable, IUserDTO {
    //Fields
    private int userId;
    private String userName;
    private String ini;
    private List<String> roles;
    private IUserDTO admin;
    private boolean active;

    //Constructor
    public UserDTO() {
        this.roles = new ArrayList<>();
    }

    //Getters and Setters
    @Override
    public int getUserId() {
        return userId;
    }

    @Override
    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String getIni() {
        return ini;
    }

    @Override
    public void setIni(String ini) {
        this.ini = ini;
    }

    @Override
    public List<String> getRoles() {
        return roles;
    }

    @Override
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    @Override
    public void addRole(String role) {
        this.roles.add(role);
    }

    /**
     * @param role
     * @return true if role existed, false if not
     */
    @Override
    public boolean removeRole(String role) {
        return this.roles.remove(role);
    }

    @Override
    public void setAdmin(IUserDTO userDTO) {
        this.admin = userDTO;
    }

    @Override
    public boolean getIsActive() {
        return active;
    }

    @Override
    public void setIsActive(boolean active) {
        this.active = active;
    }

    @Override
    public IUserDTO getAdmin() {
        return admin;
    }

    @Override
    public String toString() {
        if (!active){
            return "User is deactivated! \nUserDTO [userId=" + userId + ", userName=" + userName + ", ini=" + ini + ", roles=" + roles + "]";
        }
        else if (admin.getUserId() == userId) {
            return "UserDTO [userId=" + userId + ", userName=" + userName + ", ini=" + ini + ", roles=" + roles + ". Admin: Self]";
        } else {
            return "UserDTO [userId=" + userId + ", userName=" + userName + ", ini=" + ini + ", roles=" + roles + ". Admin: " + admin.getUserName() + ", userID: " + admin.getUserId() + "]";
        }
    }
}