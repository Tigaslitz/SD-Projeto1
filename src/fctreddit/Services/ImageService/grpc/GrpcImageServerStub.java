package fctreddit.Services.ImageService.grpc;

import com.google.protobuf.ByteString;
import fctreddit.Services.ImageService.grpc.generated_java.ImageGrpc;
import fctreddit.Services.ImageService.grpc.generated_java.ImageProtoBuf;
import fctreddit.Services.ImageService.java.ImageClass;
import fctreddit.Services.grpc.util.DataModelAdaptor;
import fctreddit.api.Image;
import fctreddit.api.Interfaces.Images;
import fctreddit.api.Interfaces.Result;
import io.grpc.BindableService;
import io.grpc.ServerServiceDefinition;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

public class GrpcImageServerStub implements ImageGrpc.AsyncService, BindableService {

    Images impl = new ImageClass();

    public GrpcImageServerStub() throws IOException {
    }

    @Override
    public ServerServiceDefinition bindService() {return ImageGrpc.bindService(this);}

    @Override
    public void createImage(ImageProtoBuf.CreateImageArgs request, StreamObserver<ImageProtoBuf.CreateImageResult> responseObserver) {
        //ImageGrpc.AsyncService.super.createImage(request, responseObserver);

        Result<String> res = impl.createImage( request.getUserId(), request.getImageContents().toByteArray(), request.getPassword());
        if( ! res.isOK() )
            responseObserver.onError(errorCodeToStatus(res.error()));
        else {
            responseObserver.onNext( ImageProtoBuf.CreateImageResult.newBuilder().setImageId( res.value() ).build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getImage(ImageProtoBuf.GetImageArgs request, StreamObserver<ImageProtoBuf.GetImageResult> responseObserver) {
        //ImageGrpc.AsyncService.super.getImage(request, responseObserver);

        Result<byte[]> res = impl.getImage(request.getUserId(), request.getImageId());

        if (!res.isOK()) {
            responseObserver.onError(errorCodeToStatus(res.error()));
        } else {
            ImageProtoBuf.GetImageResult response = ImageProtoBuf.GetImageResult.newBuilder()
                    .setData(ByteString.copyFrom(res.value()))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void deleteImage(ImageProtoBuf.DeleteImageArgs request, StreamObserver<ImageProtoBuf.DeleteImageResult> responseObserver) {
        //ImageGrpc.AsyncService.super.deleteImage(request, responseObserver);

        Result<Void> res = impl.deleteImage(request.getUserId(), request.getImageId(), request.getPassword());
        if( ! res.isOK() )
            responseObserver.onError(errorCodeToStatus(res.error()));
        else {
            responseObserver.onNext( ImageProtoBuf.DeleteImageResult.newBuilder().build() );
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
