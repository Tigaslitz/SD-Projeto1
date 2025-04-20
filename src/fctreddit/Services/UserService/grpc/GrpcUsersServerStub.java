package fctreddit.Services.UserService.grpc;

import java.util.List;

import io.grpc.BindableService;
import io.grpc.ServerServiceDefinition;
import io.grpc.stub.StreamObserver;
import fctreddit.api.User;
import fctreddit.api.Interfaces.Result;
import fctreddit.api.Interfaces.Users;
import fctreddit.Services.grpc.util.DataModelAdaptor;
import fctreddit.Services.UserService.grpc.generated_java.UsersGrpc;
import fctreddit.Services.UserService.grpc.generated_java.UsersProtoBuf.CreateUserArgs;
import fctreddit.Services.UserService.grpc.generated_java.UsersProtoBuf.CreateUserResult;
import fctreddit.Services.UserService.grpc.generated_java.UsersProtoBuf.DeleteUserArgs;
import fctreddit.Services.UserService.grpc.generated_java.UsersProtoBuf.DeleteUserResult;
import fctreddit.Services.UserService.grpc.generated_java.UsersProtoBuf.GetUserArgs;
import fctreddit.Services.UserService.grpc.generated_java.UsersProtoBuf.GetUserResult;
import fctreddit.Services.UserService.grpc.generated_java.UsersProtoBuf.GrpcUser;
import fctreddit.Services.UserService.grpc.generated_java.UsersProtoBuf.SearchUserArgs;
import fctreddit.Services.UserService.grpc.generated_java.UsersProtoBuf.UpdateUserArgs;
import fctreddit.Services.UserService.grpc.generated_java.UsersProtoBuf.UpdateUserResult;
import fctreddit.Services.UserService.java.UsersClass;


public class GrpcUsersServerStub implements UsersGrpc.AsyncService, BindableService{

	Users impl = new UsersClass();
	
	 @Override 
	 public final ServerServiceDefinition bindService() {
	      return UsersGrpc.bindService(this);
	 }

	@Override
    public void createUser(CreateUserArgs request, StreamObserver<CreateUserResult> responseObserver) {
    	Result<String> res = impl.createUser( DataModelAdaptor.GrpcUser_to_User(request.getUser()));	
    	if( ! res.isOK() ) 
    		responseObserver.onError(errorCodeToStatus(res.error()));
    	else {
			responseObserver.onNext( CreateUserResult.newBuilder().setUserId( res.value() ).build());
			responseObserver.onCompleted();
    	}
    }

	@Override
    public void getUser(GetUserArgs request, StreamObserver<GetUserResult> responseObserver) {
		Result<User> res = impl.getUser(request.getUserId(), request.getPassword());
		if( ! res.isOK() )
			responseObserver.onError(errorCodeToStatus(res.error()));
		else {
			responseObserver.onNext( GetUserResult.newBuilder().setUser(DataModelAdaptor.User_to_GrpcUser(res.value())).build() );
			responseObserver.onCompleted();
		}
    }

	@Override
    public void updateUser(UpdateUserArgs request, StreamObserver<UpdateUserResult> responseObserver) {
		throw new RuntimeException("Not Implemented...");
   }

	@Override
    public void deleteUser(DeleteUserArgs request, StreamObserver<DeleteUserResult> responseObserver) {
		throw new RuntimeException("Not Implemented...");
    }

	@Override
    public void searchUsers(SearchUserArgs request, StreamObserver<GrpcUser> responseObserver) {
		Result<List<User>> res = impl.searchUsers(request.getPattern());
		
		if( ! res.isOK() )
			responseObserver.onError(errorCodeToStatus(res.error()));
		else {
			for(User u: res.value()) {
				responseObserver.onNext( DataModelAdaptor.User_to_GrpcUser(u));
			}
			responseObserver.onCompleted();
		}
	}
    
    protected static Throwable errorCodeToStatus( Result.ErrorCode error ) {
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
