package fctreddit.Services.UserService.resources;

import java.util.List;
import java.util.logging.Logger;

import fctreddit.Services.UserService.java.UsersClass;
import fctreddit.api.Interfaces.Result;
import fctreddit.api.Interfaces.Users;
import jakarta.ws.rs.WebApplicationException;
import fctreddit.api.User;
import fctreddit.api.Rest.RestUsers;

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


    private static Throwable errorCodeToStatus( Result.ErrorCode error ) {
        var status =  switch( error) {
            case NOT_FOUND -> io.grpc.Status.NOT_FOUND;
            case CONFLICT -> io.grpc.Status.ALREADY_EXISTS;
            case FORBIDDEN -> io.grpc.Status.PERMISSION_DENIED;
            case NOT_IMPLEMENTED -> io.grpc.Status.UNIMPLEMENTED;
            case BAD_REQUEST -> io.grpc.Status.INVALID_ARGUMENT;
            default -> io.grpc.Status.INTERNAL;
        };

        return status.asException();
    }
}
