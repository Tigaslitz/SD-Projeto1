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
                user.getPassword().isEmpty() || user.getEmail() == null || user.getFullName() == null) {
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


        if (userId == null || password == null) {
            Log.info("UserId or password null.");
            return Result.error(ErrorCode.FORBIDDEN);
        }

        User user;
        try {
            user = hibernate.get(User.class, userId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
        }

        if (user == null) {
            Log.info("User does not exist.");
            return Result.error(ErrorCode.NOT_FOUND);
        }

        if (!user.getPassword().equals(password)) {
            Log.info("Password is incorrect.");
            return Result.error(ErrorCode.FORBIDDEN);
        }

        return Result.ok(user);
    }

    @Override
    public Result<User> updateUser(String userId, String password, User userUpdate) {
        //TODO: Complete method
        Log.info("updateUser : user = " + userId + "; pwd = " + password + " ; userData = " + userUpdate);

        if (userUpdate == null) {
            Log.info("User Data is null.");
            return Result.error(ErrorCode.BAD_REQUEST);
        }

        Result<User> res = getUser(userId, password);

        if (res.isOK()){
            User user = res.value();

            if (userUpdate.getFullName() != null)
                user.setFullName(userUpdate.getFullName());

            if (userUpdate.getEmail() != null)
                user.setEmail(userUpdate.getEmail());

            if (userUpdate.getPassword() != null)
                user.setPassword(userUpdate.getPassword());

            if (userUpdate.getAvatarUrl() != null)
                user.setAvatarUrl(userUpdate.getAvatarUrl());

            try{
                hibernate.update(user);
            } catch (Exception e) {
                e.printStackTrace();
                throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
            }
            return Result.ok(user);
        }
        return res;

    }

    @Override
    public Result<User> deleteUser(String userId, String password) {
        // TODO: Complete method
        Log.info("deleteUser : user = " + userId + "; pwd = " + password);

        Result<User> res = getUser(userId, password);

        if (res.isOK()) {
            User userToDelete = res.value();

            // TODO: Lógica de delete (depois de content service)
            hibernate.delete(userToDelete);
            try {
                hibernate.delete(User.class, userId);
            } catch (Exception e) {
                e.printStackTrace();
                throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
            }
            return Result.ok(res.value());
        }
        return res;
    }

    @Override
    public Result<List<User>> searchUsers(String pattern) {
        Log.info("searchUsers : pattern = " + pattern);
        try {
            if (pattern == null) pattern = "";
            List<User> list = hibernate.jpql(
                    "SELECT u FROM User u WHERE LOWER(u.userId) LIKE LOWER('%" + pattern + "%')", User.class);
            return Result.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(ErrorCode.INTERNAL_ERROR);
        }
    }
}

