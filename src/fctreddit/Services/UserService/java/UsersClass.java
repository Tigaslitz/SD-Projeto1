package fctreddit.Services.UserService.java;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;
import java.util.List;
import java.util.logging.Logger;

import fctreddit.api.Interfaces.Result;
import fctreddit.api.Interfaces.Result.ErrorCode;
import fctreddit.api.User;
import fctreddit.api.Interfaces.Users;
import fctreddit.Hibernate;

public class UsersClass implements Users {

    private static Logger Log = Logger.getLogger(UsersClass.class.getName());
    private final Hibernate hibernate = Hibernate.getInstance();

    @Override
    public Result<String> createUser(User user) {
        Log.info("createUser : " + user);

        if (user == null || user.getUserId() == null || user.getPassword() == null ||
                user.getEmail() == null || user.getFullName() == null) {
            return Result.error(ErrorCode.BAD_REQUEST);
        }

        try {
            hibernate.persist(user);
            return Result.ok(user.getUserId());
        } catch (Exception e) {
            Log.info("User already exists.");
            return Result.error(ErrorCode.CONFLICT);
        }
    }

    @Override
    public Result<User> getUser(String userId, String password) {
        Log.info("getUser : " + userId);

        if (userId == null || password == null)
            return Result.error(ErrorCode.BAD_REQUEST);

        User user = null;
        try {
            user = hibernate.get(User.class, userId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
        }

        if (user == null)
            return Result.error(ErrorCode.NOT_FOUND);

        if (!user.getPassword().equals(password))
            return Result.error(ErrorCode.FORBIDDEN);

        return Result.ok(user);
    }

    @Override
    public Result<User> updateUser(String userId, String password, User userUpdate) {
        // Lógica semelhante, aplicando alterações apenas se os campos não forem null
        return Result.error(ErrorCode.NOT_IMPLEMENTED);
    }

    @Override
    public Result<User> deleteUser(String userId, String password) {
        // Lógica de apagar, limpar dados relacionados, etc.
        return Result.error(ErrorCode.NOT_IMPLEMENTED);
    }

    @Override
    public Result<List<User>> searchUsers(String pattern) {
        try {
            if (pattern == null) pattern = "";
            List<User> list = hibernate.jpql(
                    "SELECT u FROM User u WHERE LOWER(u.userId) LIKE LOWER('%" + pattern + "%')", User.class);
            list.forEach(u -> u.setPassword(""));
            return Result.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(ErrorCode.INTERNAL_ERROR);
        }
    }
}

