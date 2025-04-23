package fctreddit.Services.UserService.resources;

import java.util.List;
import java.util.logging.Logger;

import fctreddit.Services.UserService.java.UsersClass;
import fctreddit.api.Interfaces.Result;
import fctreddit.api.Interfaces.Users;
import jakarta.ws.rs.WebApplicationException;
import fctreddit.api.User;
import fctreddit.api.Rest.RestUsers;
import jakarta.ws.rs.core.Response.Status;

public class UsersResource implements RestUsers {

	private static Logger Log = Logger.getLogger(UsersResource.class.getName());
    private final Users impl = new UsersClass();



    @Override
    public String createUser(User user) {
        Result<String> res = impl.createUser(user);
        if (!res.isOK())
            throw new WebApplicationException(errorCodeToStatus(res.error()));
        return res.value();
    }

	@Override
	public User getUser(String userId, String password) {
		Result<User> res = impl.getUser(userId, password);
        if (!res.isOK())
            throw new WebApplicationException(errorCodeToStatus(res.error()));
        return res.value();
	}

	@Override
	public User updateUser(String userId, String password, User user) {
        Result<User> res = impl.updateUser(userId, password, user);
        if (!res.isOK())
            throw new WebApplicationException(errorCodeToStatus(res.error()));
        return res.value();

	}

	@Override
	public User deleteUser(String userId, String password) {
        Result<User> res = impl.deleteUser(userId, password);
        if (!res.isOK())
            throw new WebApplicationException(errorCodeToStatus(res.error()));
        return res.value();
	}

	@Override
	public List<User> searchUsers(String pattern) {
		Log.info("searchUsers : pattern = " + pattern);
        Result<List<User>> res = impl.searchUsers(pattern);
        if (!res.isOK())
            throw new WebApplicationException(errorCodeToStatus(res.error()));
        return res.value();
	}


    private static Status errorCodeToStatus( Result.ErrorCode error ) {
        return switch( error) {
            case NOT_FOUND -> Status.NOT_FOUND;         // 404
            case CONFLICT -> Status.CONFLICT;           // 409
            case FORBIDDEN -> Status.FORBIDDEN;         // 403
            case BAD_REQUEST -> Status.BAD_REQUEST;     // 400
            case NOT_IMPLEMENTED -> Status.NOT_IMPLEMENTED; // 501
            default -> Status.INTERNAL_SERVER_ERROR;    // 500
        };

    }
}
