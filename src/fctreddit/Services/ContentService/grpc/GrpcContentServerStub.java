package fctreddit.Services.ContentService.grpc;

import fctreddit.Services.ContentService.grpc.generated_java.ContentGrpc;
import fctreddit.Services.ContentService.grpc.generated_java.ContentProtoBuf;
import fctreddit.Services.ContentService.java.ContentClass;
import fctreddit.Services.grpc.util.DataModelAdaptor;
import fctreddit.api.Interfaces.Content;
import fctreddit.api.Interfaces.Result;
import fctreddit.api.Post;
import io.grpc.BindableService;
import io.grpc.ServerServiceDefinition;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.List;

public class GrpcContentServerStub implements ContentGrpc.AsyncService, BindableService {

    Content impl = new ContentClass();

    public GrpcContentServerStub() throws IOException {
    }

    @Override
    public ServerServiceDefinition bindService() {return ContentGrpc.bindService(this);}

    @Override
    public void createPost(ContentProtoBuf.CreatePostArgs request, StreamObserver<ContentProtoBuf.CreatePostResult> responseObserver) {
        //ContentGrpc.AsyncService.super.createPost(request, responseObserver);

        Result<String> res = impl.createPost( DataModelAdaptor.GrpcPost_to_Post(request.getPost()), request.getPassword());
        if( ! res.isOK() )
            responseObserver.onError(errorCodeToStatus(res.error()));
        else {
            responseObserver.onNext( ContentProtoBuf.CreatePostResult.newBuilder().setPostId( res.value() ).build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getPosts(ContentProtoBuf.GetPostsArgs request, StreamObserver<ContentProtoBuf.GetPostsResult> responseObserver) {
        //ContentGrpc.AsyncService.super.getPosts(request, responseObserver);

        Result<List<String>> res = impl.getPosts(request.getTimestamp(), request.getSortOrder());
        if( ! res.isOK() )
            responseObserver.onError(errorCodeToStatus(res.error()));
        else {
            ContentProtoBuf.GetPostsResult response = ContentProtoBuf.GetPostsResult.newBuilder()
                    .addAllPostId(res.value())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getPost(ContentProtoBuf.GetPostArgs request, StreamObserver<ContentProtoBuf.GrpcPost> responseObserver) {
        //ContentGrpc.AsyncService.super.getPost(request, responseObserver);

        Result<Post> res = impl.getPost(request.getPostId());
        if( ! res.isOK() )
            responseObserver.onError(errorCodeToStatus(res.error()));
        else {
            responseObserver.onNext( DataModelAdaptor.Post_to_GrpcPost(res.value()) );
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getPostAnswers(ContentProtoBuf.GetPostAnswersArgs request, StreamObserver<ContentProtoBuf.GetPostsResult> responseObserver) {
        //ContentGrpc.AsyncService.super.getPostAnswers(request, responseObserver);

        Result<List<String>> res = impl.getPostAnswers(request.getPostId(), request.getTimeout());
        if( ! res.isOK() )
            responseObserver.onError(errorCodeToStatus(res.error()));
        else {
            ContentProtoBuf.GetPostsResult response = ContentProtoBuf.GetPostsResult.newBuilder()
                    .addAllPostId(res.value())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void updatePost(ContentProtoBuf.UpdatePostArgs request, StreamObserver<ContentProtoBuf.GrpcPost> responseObserver) {
        //ContentGrpc.AsyncService.super.updatePost(request, responseObserver);

        Result<Post> res = impl.updatePost(request.getPostId(), request.getPassword(), DataModelAdaptor.GrpcPost_to_Post(request.getPost()));
        if( ! res.isOK() )
            responseObserver.onError(errorCodeToStatus(res.error()));
        else {
            responseObserver.onNext( DataModelAdaptor.Post_to_GrpcPost(res.value()) );
            responseObserver.onCompleted();
        }
    }

    @Override
    public void deletePost(ContentProtoBuf.DeletePostArgs request, StreamObserver<ContentProtoBuf.EmptyMessage> responseObserver) {
        //ContentGrpc.AsyncService.super.deletePost(request, responseObserver);

        Result<Void> res = impl.deletePost(request.getPostId(), request.getPassword());
        if( ! res.isOK() )
            responseObserver.onError(errorCodeToStatus(res.error()));
        else {
            responseObserver.onNext( ContentProtoBuf.EmptyMessage.newBuilder().build() );
            responseObserver.onCompleted();
        }
    }

    @Override
    public void upVotePost(ContentProtoBuf.ChangeVoteArgs request, StreamObserver<ContentProtoBuf.EmptyMessage> responseObserver) {
        //ContentGrpc.AsyncService.super.upVotePost(request, responseObserver);

        Result<Void> res = impl.upVotePost(request.getPostId(), request.getUserId(), request.getPassword());
        if( ! res.isOK() )
            responseObserver.onError(errorCodeToStatus(res.error()));
        else {
            responseObserver.onNext( ContentProtoBuf.EmptyMessage.newBuilder().build() );
            responseObserver.onCompleted();
        }
    }

    @Override
    public void removeUpVotePost(ContentProtoBuf.ChangeVoteArgs request, StreamObserver<ContentProtoBuf.EmptyMessage> responseObserver) {
        //ContentGrpc.AsyncService.super.removeUpVotePost(request, responseObserver);

        Result<Void> res = impl.removeUpVotePost(request.getPostId(), request.getUserId(), request.getPassword());
        if( ! res.isOK() )
            responseObserver.onError(errorCodeToStatus(res.error()));
        else {
            responseObserver.onNext( ContentProtoBuf.EmptyMessage.newBuilder().build() );
            responseObserver.onCompleted();
        }
    }

    @Override
    public void downVotePost(ContentProtoBuf.ChangeVoteArgs request, StreamObserver<ContentProtoBuf.EmptyMessage> responseObserver) {
        //ContentGrpc.AsyncService.super.downVotePost(request, responseObserver);

        Result<Void> res = impl.downVotePost(request.getPostId(), request.getUserId(), request.getPassword());
        if( ! res.isOK() )
            responseObserver.onError(errorCodeToStatus(res.error()));
        else {
            responseObserver.onNext( ContentProtoBuf.EmptyMessage.newBuilder().build() );
            responseObserver.onCompleted();
        }
    }

    @Override
    public void removeDownVotePost(ContentProtoBuf.ChangeVoteArgs request, StreamObserver<ContentProtoBuf.EmptyMessage> responseObserver) {
        //ContentGrpc.AsyncService.super.removeDownVotePost(request, responseObserver);

        Result<Void> res = impl.removeDownVotePost(request.getPostId(), request.getUserId(), request.getPassword());
        if( ! res.isOK() )
            responseObserver.onError(errorCodeToStatus(res.error()));
        else {
            responseObserver.onNext( ContentProtoBuf.EmptyMessage.newBuilder().build() );
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getUpVotes(ContentProtoBuf.GetPostArgs request, StreamObserver<ContentProtoBuf.VoteCountResult> responseObserver) {
        //ContentGrpc.AsyncService.super.getUpVotes(request, responseObserver);

        Result<Integer> res = impl.getupVotes(request.getPostId());
        if( ! res.isOK() )
            responseObserver.onError(errorCodeToStatus(res.error()));
        else {
            responseObserver.onNext( ContentProtoBuf.VoteCountResult.newBuilder().setCount(res.value()).build() );
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getDownVotes(ContentProtoBuf.GetPostArgs request, StreamObserver<ContentProtoBuf.VoteCountResult> responseObserver) {
        //ContentGrpc.AsyncService.super.getDownVotes(request, responseObserver);

        Result<Integer> res = impl.getDownVotes(request.getPostId());
        if( ! res.isOK() )
            responseObserver.onError(errorCodeToStatus(res.error()));
        else {
            responseObserver.onNext( ContentProtoBuf.VoteCountResult.newBuilder().setCount(res.value()).build() );
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
