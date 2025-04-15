package fctreddit.Services.UserService.resources;

import java.util.List;
import java.util.logging.Logger;

import fctreddit.Services.UserService.java.UsersClass;
import fctreddit.api.Interfaces.Result;
import fctreddit.api.Interfaces.Users;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;
import fctreddit.api.User;
import fctreddit.api.Rest.RestUsers;
import fctreddit.Hibernate;

public class UsersResource implements RestUsers {

	private static Logger Log = Logger.getLogger(UsersResource.class.getName());
    private final Users core = new UsersClass();

	private Hibernate hibernate;

	public UsersResource() {
		hibernate = Hibernate.getInstance();
	}


    @Override
    public String createUser(User user) {
        Result<String> res = core.createUser(user);
        if (res.isOK())
            return res.value();
        else
            throw new WebApplicationException(mapStatus(res.error()));
    }

	@Override
	public User getUser(String userId, String password) {
		Log.info("getUser : user = " + userId + "; pwd = " + password);

		// Check if user is valid
		if (userId == null || password == null) {
			Log.info("UserId or password null.");
			throw new WebApplicationException(Status.BAD_REQUEST);
		}

		User user = null;
		try {
			user = hibernate.get(User.class, userId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}

		// Check if user exists
		if (user == null) {
			Log.info("User does not exist.");
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		// Check if the password is correct
		if (!user.getPassword().equals(password)) {
			Log.info("Password is incorrect.");
			throw new WebApplicationException(Status.FORBIDDEN);
		}

		return user;
	}

	@Override
	public User updateUser(String userId, String password, User user) {
		Log.info("updateUser : user = " + userId + "; pwd = " + password + " ; userData = " + user);
		// TODO: Complete method
		throw new WebApplicationException(Status.NOT_IMPLEMENTED);
	}

	@Override
	public User deleteUser(String userId, String password) {
		Log.info("deleteUser : user = " + userId + "; pwd = " + password);
		// TODO: Complete method
		throw new WebApplicationException(Status.NOT_IMPLEMENTED);
	}

	@Override
	public List<User> searchUsers(String pattern) {
		Log.info("searchUsers : pattern = " + pattern);
		
		try {
			List<User> list = hibernate.jpql("SELECT u FROM User u WHERE u.userId LIKE '%" + pattern +"%'", User.class);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
	}

    private static Status mapStatus(Result.ErrorCode code) {
        return switch (code) {
            case BAD_REQUEST -> Status.BAD_REQUEST;
            case CONFLICT -> Status.CONFLICT;
            case NOT_FOUND -> Status.NOT_FOUND;
            case FORBIDDEN -> Status.FORBIDDEN;
            default -> Status.INTERNAL_SERVER_ERROR;
        };
    }
}
